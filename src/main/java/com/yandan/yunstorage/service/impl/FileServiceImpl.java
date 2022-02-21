package com.yandan.yunstorage.service.impl;

import com.yandan.yunstorage.VO.MyFile;
import com.yandan.yunstorage.VO.Node;
import com.yandan.yunstorage.configure.MyConfigure;
import com.yandan.yunstorage.converter.Converter;
import com.yandan.yunstorage.dao.FileDao;
import com.yandan.yunstorage.service.FileService;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Create by yandan
 * 2021/12/31  12:58
 */
@Service
public class FileServiceImpl implements FileService {
    @Autowired
    private MyConfigure myConfigure;
    private FileSystem fileSystem;
    private List<String> picType;
    private List<String> videoType;
    private List<String> docType;
    private List<String> musicType;
    private boolean loadType = false;//是否加载文件类型

    @Autowired
    private FileDao fileDao;

    public void setTypes() {
        InputStream inputStream = this.getClass().getResourceAsStream("/static/types.txt");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String line = bufferedReader.readLine();
            picType = Arrays.asList(line.split("-"));
            line = bufferedReader.readLine();
            videoType = Arrays.asList(line.split("-"));
            line = bufferedReader.readLine();
            docType = Arrays.asList(line.split("-"));
            line = bufferedReader.readLine();
            musicType = Arrays.asList(line.split("-"));
            this.loadType = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setFileSystem() {
        Configuration configuration = new Configuration();
        try {
            this.fileSystem = FileSystem.get(URI.create(myConfigure.getHdfsUrl()), configuration);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public File getFile(String path) {
        if (fileSystem == null) setFileSystem();
        String distFile = myConfigure.getTemp() + path.replace("/", "\\");
        String srcFile = myConfigure.getHdfsUrl() + path;
        Path srcPath = new Path(srcFile);
        Path distPath = new Path(distFile);
        File file = null;
        try {
            fileSystem.copyToLocalFile(srcPath, distPath);
            file = new File(distFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    @Override
    public List<MyFile> getMyFiles(String path) {
        if (fileSystem == null) setFileSystem();
        try {
            FileStatus[] fileStatuses = fileSystem.listStatus(new Path(myConfigure.getHdfsUrl() + path));
            List<MyFile> myFiles = new ArrayList<MyFile>();
            for (FileStatus fileStatus : fileStatuses) {
                myFiles.add(Converter.fileStatus2File(fileStatus, myConfigure.getHdfsUrl() + path));
            }
            myFiles.sort(new Comparator<MyFile>() {
                @Override
                public int compare(MyFile o1, MyFile o2) {
                    if (o1.getType().equals("dir") && o2.getType().equals("dir"))
                        return 0;
                    if (o1.getType().equals("dir"))
                        return -1;
                    if (o2.getType().equals("dir"))
                        return 1;
                    return 0;
                }
            });
            return myFiles;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean uploadFile(String srcUrl, String distUrl) {
        if (fileSystem == null) setFileSystem();
        try {
            fileSystem.copyFromLocalFile(new Path(myConfigure.getTemp() + srcUrl), new Path(myConfigure.getHdfsUrl() + distUrl));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean mkDir(String dir) {
        if (fileSystem == null) setFileSystem();
        File file = new File(myConfigure.getTemp() + dir);
        if (!file.exists())
            file.mkdir();
        try {
            if (fileSystem.exists(new Path(myConfigure.getHdfsUrl() + dir))) {
                return false;
            }
            fileSystem.mkdirs(new Path(myConfigure.getHdfsUrl() + dir));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteFile(String path) {
        if (fileSystem == null) setFileSystem();
        try {
            if (fileSystem.exists(new Path(myConfigure.getHdfsUrl() + path))) {
                fileSystem.delete(new Path(myConfigure.getHdfsUrl() + path));
                return true;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean mkGDir(String dir) {
        if (fileSystem == null) setFileSystem();
        try {
            if (fileSystem.exists(new Path(myConfigure.getHdfsUrl() + dir))) {
                return false;
            }
            fileSystem.mkdirs(new Path(myConfigure.getHdfsUrl() + dir));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void getDir(String path, Node node) {
        if (fileSystem == null) setFileSystem();
        try {
            if (fileSystem.exists(new Path(myConfigure.getHdfsUrl() + path))) {
                FileStatus[] fileStatuses = fileSystem.listStatus(new Path(myConfigure.getHdfsUrl() + path));
                for (FileStatus fs : fileStatuses) {
                    if (fs.isDirectory()) {
                        if (node.getChildren() == null)
                            node.setChildren(new ArrayList<Node>());
                        MyFile myFile = Converter.fileStatus2File(fs, myConfigure.getHdfsUrl() + path);
                        Node node1 = new Node(myFile.getName());
                        node.getChildren().add(node1);
                        getDir(path + myFile.getName() + "/", node1);
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean deleteFiles(String path) {
        if (fileSystem == null) setFileSystem();
        try {
            if (fileSystem.exists(new Path(myConfigure.getHdfsUrl() + path))) {
                fileSystem.delete(new Path(myConfigure.getHdfsUrl() + path), true);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean rename(String path, String newName) {
        if (fileSystem == null) setFileSystem();
        Path oldPath = new Path(myConfigure.getHdfsUrl() + path);
        Path newPath = new Path((myConfigure.getHdfsUrl() + newName));
        String dir=newName.substring(0,newName.lastIndexOf("/"));
        Path dirPath=new Path(myConfigure.getHdfsUrl()+dir);
        try {
            if (fileSystem.exists(oldPath) && !fileSystem.exists(newPath)) {
                if(!fileSystem.exists(dirPath)&&!fileSystem.isDirectory(dirPath)){
                    fileSystem.mkdirs(dirPath);

                }
                fileSystem.rename(oldPath, newPath);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<MyFile> getFilesByType(String rootPath, String type) {
        if (this.loadType == false) setTypes();
        List<MyFile> myFiles = fileDao.getFilesByType(type, rootPath);
        return myFiles;
    }

    @Override
    public boolean insertFile(String rootPath, MyFile myFile) {
        if (this.loadType == false) setTypes();
        String rootType = "unknown";
        if (picType.contains(myFile.getType()))
            rootType = "pic";
        else if (docType.contains(myFile.getType()))
            rootType = "doc";
        else if (videoType.contains(myFile.getType()))
            rootType = "video";
        else if (musicType.contains(myFile.getType()))
            rootType = "music";
        fileDao.insertFile(myFile, rootPath, rootType);
        return true;
    }

    @Override
    public int deleteDatabaseFile(String url) {
        if (this.loadType == false) setTypes();
        return fileDao.deleteFiles(url);
    }

    @Override
    public int setDeleteDatabaseFile(String url,int delete) {
        if (this.loadType == false) setTypes();
        return fileDao.setDelete(url,delete);
    }

    @Override
    public int modifyFileName(String oldUrl, String newUrl, String newName) {
        if (this.loadType == false) setTypes();
        return fileDao.modifyFilesName(oldUrl, newUrl, newName);
    }

    @Override
    public MyFile getMyFile(String url) {
        return fileDao.getFile(url);
    }

    @Override
    public boolean deleteFileByUrl(String url) {
        if (fileSystem == null) setFileSystem();
        try {
            if (fileSystem.exists(new Path(myConfigure.getHdfsUrl() + url))) {
                fileSystem.delete(new Path(myConfigure.getHdfsUrl() + url));
                return true;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String fileLocaled(String url) {
        if (fileSystem == null) setFileSystem();
        try {
            String distFile = myConfigure.getHostUrl() + url.replace(myConfigure.getHdfsUrl(), "").replace("/", "\\");
            String srcFile = url;
            String dir = distFile.substring(0, distFile.lastIndexOf("\\"));
            Path srcPath = new Path(srcFile);
            Path distPath = new Path(distFile);
            File file = new File(dir);
            if (!file.exists()) file.mkdirs();
            if (fileSystem.exists(new Path(url))) {
                File file1 = new File(distFile);
                if (!file1.exists()) {
                    fileSystem.copyToLocalFile(srcPath, distPath);
                }
                return url.replace(myConfigure.getHdfsUrl(), "");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public float getMyFilesByDir(String url) {
        return fileDao.getFilesSizeByDir(url+"%%");
    }

    @Override
    public int getDownloadCount(String url) {
        return fileDao.getDownCount(url);
    }

    @Override
    public void setDownloadCount(String url, int count) {
        fileDao.updateCount(url,count);
    }

    @Override
    public void setShare(String url, int share) {
        fileDao.updateIsShare(url,share);
    }

    @Override
    public List<MyFile> getShareFileList(String root) {
        return fileDao.getShareFiles(root);
    }
}
