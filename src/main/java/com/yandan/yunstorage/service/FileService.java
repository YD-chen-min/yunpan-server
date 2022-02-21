package com.yandan.yunstorage.service;

import com.yandan.yunstorage.VO.MyFile;
import com.yandan.yunstorage.VO.Node;

import java.io.File;
import java.util.List;

/**
 * Create by yandan
 * 2021/12/31  12:40
 */
public interface FileService {
    /**
     * 根据路径获取实体文件
     * @param path
     * @return file
     */
    File getFile(String path);

    /**
     * 根据目录获取目录下的文件属性
     * @param path
     * @return List<MyFile>
     */
    List<MyFile> getMyFiles(String path);

    /**
     * 上传文件,上传完后记得删除本地文件
     * @param srcUrl
     * @param distUrl
     * @return
     */
    boolean uploadFile(String srcUrl,String distUrl);

    /**
     * 创建目录
     * @param dir
     * @return
     */
    boolean mkDir(String dir);

    /**
     * 创建垃圾箱目录
     * @param dir
     * @return
     */
    boolean mkGDir(String dir);

    /**
     * 根据路径删除单个文件
     * @param path
     * @return
     */
    boolean deleteFile(String path);

    /**
     * 根据路径删除目录下的文件
     * @param path
     * @return
     */
    boolean deleteFiles(String path);

    /**
     * 将路径对应的文件或目录修改为新名称
     * @param path
     * @param newName
     * @return
     */
    boolean rename(String path,String newName);

    /**
     * 根据用户根目录的路径返回所有目录（树形）
     * @param path
     * node
     */
    void getDir(String path,Node node);

    /**
     * 根据用户路径和文件类型返回文件
     * @param rootPath
     * @param type
     * @return
     */
    List<MyFile> getFilesByType(String rootPath,String type);

    /**
     * 往数据库插入数据
     * @param rootPath
     * @param myFile
     * @return
     */
    boolean insertFile(String rootPath,MyFile myFile);

    /**
     * 根据url删除数据库中的文件数据
     * @param url
     * @return
     */
    int deleteDatabaseFile(String url);

    /**
     * 根据url设置删除标志
     * @param url
     * @return
     */
    int setDeleteDatabaseFile(String url,int delete);
    /**
     *
     * @param oldUrl
     * @param newName
     * @return
     */
    int modifyFileName(String oldUrl,String newUrl,String newName);

    /**
     * 根据url删除文件
     * @param url
     * @return
     */
    boolean deleteFileByUrl(String url);

    /**
     * 将文件本地化便于预览
     * @param url
     * @return
     */
    String fileLocaled(String url);

    /**
     * 获取数据库中的文件
     * @param url
     * @return
     */
    MyFile getMyFile(String url);

    /**
     * 获取符合路径的所有文件总大小
     * @param url
     * @return
     */
    float getMyFilesByDir(String url);

    /**
     * 获取下载次数
     * @param url
     * @return
     */
    int getDownloadCount(String url);

    /**
     * 更新下载次数
     * @param url
     * @param count
     */
    void setDownloadCount(String url,int count);

    /**
     * 设置share
     * @param url
     * @param share
     */
    void setShare(String url,int share);

    /**
     * 获取分享文件列表
     * @param root
     * @return
     */
    List<MyFile> getShareFileList(String root);


}
