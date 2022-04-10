package com.yandan.yunstorage.controller;

import com.yandan.yunstorage.VO.MyFile;
import com.yandan.yunstorage.VO.Node;
import com.yandan.yunstorage.VO.ResultVO;
import com.yandan.yunstorage.configure.MyConfigure;
import com.yandan.yunstorage.converter.Converter;
import com.yandan.yunstorage.data.Dir;
import com.yandan.yunstorage.data.UserInfo;
import com.yandan.yunstorage.service.DirService;
import com.yandan.yunstorage.service.FileService;
import com.yandan.yunstorage.service.UserService;
import com.yandan.yunstorage.util.Logger;
import com.yandan.yunstorage.util.ResultVOUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

/**
 * Create by yandan
 * 2021/12/31  16:45
 */
@RestController
public class FileController {
    @Autowired
    private UserService userService;
    @Autowired
    private FileService fileService;
    @Autowired
    private MyConfigure myConfigure;
    @Autowired
    private Logger logger;
    @Autowired
    private DirService dirService;

    /**
     * 获取登录用户IP地址
     *
     * @param request
     * @return
     */

    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
//        if (ip.equals("0:0:0:0:0:0:0:1")) {
//            ip = "本地";
//        }
        return ip;
    }


    @GetMapping("/file/garbage/get")
    @ResponseBody
    public ResultVO<List<MyFile>> getGFileList(@RequestParam(value = "path", defaultValue = "") String path,
                                               HttpServletRequest request,
                                               @RequestParam(value = "user") String user) throws IOException {
        String ip = getIpAddr(request);
        UserInfo userInfo = userService.getUserInfoByUser(user);
        if (!ip.equals(userInfo.getIp())) {
            return ResultVOUtil.fail(1, "无权限访问");
        }
        List<MyFile> myFiles = fileService.getMyFiles("garbage/" + path);
        return ResultVOUtil.success(myFiles);
    }


    @GetMapping("/file/get")
    @ResponseBody
    public ResultVO<List<MyFile>> getFileList(@RequestParam(value = "path", defaultValue = "") String path,
                                              HttpServletRequest request,
                                              @RequestParam(value = "user") String user,
                                              @RequestParam(value = "shareDir",defaultValue = "")String shareDir) throws IOException {
        String ip = getIpAddr(request);
        UserInfo userInfo = userService.getUserInfoByUser(user);
        List<Dir> dirs=null;
        if(!"".equals(shareDir)){
            dirs=dirService.getLikeUrl(shareDir);
        }
        if(dirs!=null&&dirs.size()>0){

        }else{
            if (!ip.equals(userInfo.getIp())) {
                return ResultVOUtil.fail(1, "无权限访问");
            }
        }

        List<MyFile> myFiles = fileService.getMyFiles(path);
        return ResultVOUtil.success(myFiles);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @RequestMapping(value = "/file/download")
    public ResponseEntity<byte[]> download(HttpServletRequest request, @RequestParam("path") String path,
                                           @RequestParam(value = "user") String user,
                                           @RequestParam(value = "shareDir",defaultValue = "")String shareDir)
            throws Exception {
        String ip = getIpAddr(request);
        UserInfo userInfo = userService.getUserInfoByUser(user);
        List<Dir> dirs=null;
        if(!"".equals(shareDir)){
            dirs=dirService.getLikeUrl(shareDir);
        }

        if(dirs!=null&&dirs.size()>0){

        }else{
            if (!ip.equals(userInfo.getIp())) {
                return null;
            }
        }
        for(Dir dir:dirs){
            if(path.startsWith(dir.getUrl())){
                File file = fileService.getFile(path);
                String name = path.split("/")[path.split("/").length - 1];
                logger.userLogIn(user,"下载文件   '"+name+"'");
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDispositionFormData("attachment",
                        URLEncoder.encode(name, "UTF-8"));
                ResponseEntity<byte[]> responseEntity = new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
                file.delete();//删除本地文件
                return responseEntity;
            }
        }
        return null;

    }

    @Transactional(propagation = Propagation.REQUIRED)
    @RequestMapping(value = "/file/share/download")
    public ResponseEntity<byte[]> downloadShare(HttpServletRequest request, @RequestParam("path") String path,@RequestParam("user")String user
                                       )
            throws Exception {


        byte[] p=Base64.getDecoder().decode(path.replace(" ","+"));
        path=new String(p).substring(1);
        path=path.replace(myConfigure.getHdfsUrl(),"");
        File file = fileService.getFile(path);
        MyFile myFile=fileService.getMyFile(path);
        if(myFile.getIsShare()==1){
            String name = path.split("/")[path.split("/").length - 1];
            logger.userLogIn(user,"下载共享文件  '"+name+"'");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment",
                    URLEncoder.encode(name, "UTF-8"));
            ResponseEntity<byte[]> responseEntity = new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
            file.delete();//删除本地文件
            int count=fileService.getDownloadCount(myFile.getUrl());
            count++;
            fileService.setDownloadCount(myFile.getUrl(),count);
            return responseEntity;
        }else{
            return null;
        }

    }

    @Transactional(propagation = Propagation.REQUIRED)
    @PostMapping(value = "/file/upload")
    @ResponseBody
    public ResultVO upload(@RequestParam("file") MultipartFile file,
                           @RequestParam("path") String path,
                           HttpServletRequest request,
                           @RequestParam("user") String user) throws Exception {
        UserInfo userInfo = userService.getUserInfoByUser(user);
        String ip = getIpAddr(request);
        if (!ip.equals(userInfo.getIp())) {
            return  ResultVOUtil.fail(1, "无权限访问");
        }
//       for(MultipartFile file:files){
//           String fileName = file.getOriginalFilename();
//           fileName = fileName.replace(";", "");
//           String type = fileName.substring(fileName.indexOf(".") + 1, fileName.length());
//           String size = Converter.toSize(file.getSize());
//           MyFile myFile = new MyFile();
//           myFile.setName(fileName);
//           myFile.setSize(size);
//           myFile.setType(type);
//           String srcPath = userInfo.getHdfs() + path + fileName;
//           File hostFile = new File(srcPath);
//           File src = new File(userInfo.getHdfs() + path);
//           if (!src.exists()) src.mkdirs();
//           file.transferTo(hostFile);
//           String dstPath = userInfo.getHdfs() + path + fileName;
//           fileService.uploadFile(srcPath, dstPath);
//           hostFile.delete();
//           src.delete();
//       }
        String fileName = file.getOriginalFilename();
        fileName = fileName.replace(";", "");
        String type = fileName.substring(fileName.indexOf(".") + 1, fileName.length());
        String size = Converter.toSize(file.getSize());
        float realSize = file.getSize();
        float free = userInfo.getStore() - userInfo.getBusy();
        if (realSize > free)
            return ResultVOUtil.fail(1, "空闲空间不足");
        MyFile myFile = new MyFile();
        myFile.setName(fileName);
        myFile.setSize(size);
        myFile.setType(type);
        String srcPath = path + fileName;
        File hostFile = new File(myConfigure.getTemp() + srcPath);
        File src = new File(myConfigure.getTemp() + path);
        if (!src.exists()) src.mkdirs();
        file.transferTo(hostFile);
        String dstPath = path + fileName;
        myFile.setUrl(dstPath);
        myFile.setLastModifyTime(String.valueOf(new Date().getTime()));
        myFile.setRealSize(file.getSize());
        fileService.insertFile(userInfo.getUser() + "/", myFile);
        userService.setSize(userInfo.getBusy() + realSize, userInfo.getUser());
        fileService.uploadFile(srcPath, dstPath);
        hostFile.delete();
        src.delete();
        logger.userLogIn(user,"上传文件   '"+myFile.getName()+"'");
        return ResultVOUtil.success("上传成功！");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("/file/rename")
    @ResponseBody
    public ResultVO rename(@RequestParam("oldPath") String oldPath,
                           @RequestParam("newPath") String newPath,
                           HttpServletRequest request,
                           @RequestParam(value = "user") String user) throws IOException {
        String ip = getIpAddr(request);
        UserInfo userInfo = userService.getUserInfoByUser(user);
        if (!ip.equals(userInfo.getIp())) {
            return ResultVOUtil.fail(1, "无权限访问");
        }
        if (fileService.rename(oldPath, newPath)) {
            String[] newNames = newPath.split("/");
            String newName = newNames[newNames.length - 1];
            fileService.modifyFileName(oldPath, newPath, newName);
            logger.userLogIn(user,"文件路径修改 '"+oldPath.replace(user+"/","")+"' ---> '"+newPath.replace(user+"/","")+"'");
            File file = new File(myConfigure.getHostUrl() + oldPath);
            if (file.exists()) {
                file.renameTo(new File(myConfigure.getHostUrl() + newPath));
            }
            return ResultVOUtil.success(myConfigure.getHdfsUrl() + newPath);
        }
        return ResultVOUtil.fail(1, "该名称已存在");


    }

    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("/file/deleteFiles")
    @ResponseBody
    public ResultVO deleteFiles(HttpServletRequest request,
                                @RequestParam(value = "user") String user,
                                @RequestParam(value = "path") String path,
                                @RequestParam(value = "files") String files) throws IOException {
        String ip = getIpAddr(request);
        UserInfo userInfo = userService.getUserInfoByUser(user);
        if (!ip.equals(userInfo.getIp())) {
            return ResultVOUtil.fail(1, "无权限访问");
        }
        String[] file = files.split(";");
        int i = 0;
        int count = file.length;
        for (String f : file) {
            f=f.replace(myConfigure.getHdfsUrl(),"");
            if (fileService.rename(f , "garbage/"  + f))
                i++;
            logger.userLogIn(user,f+"'  ---> 回收站");
            fileService.setDeleteDatabaseFile( f, 1);
        }
        return ResultVOUtil.success(count + "个文件，其中" + i + "个文件被删除");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("/file/garbage/deleteFiles")
    @ResponseBody
    public ResultVO deleteGFiles(HttpServletRequest request,
                                 @RequestParam(value = "user") String user,
                                 @RequestParam(value = "path") String path,
                                 @RequestParam(value = "files") String files) throws IOException {
        String ip = getIpAddr(request);
        UserInfo userInfo = userService.getUserInfoByUser(user);
        if (!ip.equals(userInfo.getIp())) {
            return ResultVOUtil.fail(1, "无权限访问");
        }
        String[] file = files.split(";");
        int i = 0;
        int count = file.length;
        long size = 0;
        for (String f : file) {
            f = f.replace(myConfigure.getHdfsUrl(), "");
            if (fileService.deleteFile(f))
                i++;
            MyFile myFile = fileService.getMyFile(f.replace("garbage/", ""));
            if (myFile!=null){
                size += myFile.getRealSize();
                fileService.deleteDatabaseFile(f.replace("garbage/", ""));
                File file1 = new File(myConfigure.getHostUrl() + f.replace(myConfigure.getHdfsUrl() + "garbage/", ""));
                if (file1.exists()) {
                    file1.delete();
                }

            }
            logger.userLogIn(user,"'"+f+"'   彻底删除");
        }
        userService.setSize(userInfo.getBusy() - size, userInfo.getUser());
        return ResultVOUtil.success(count + "个文件，其中" + i + "个文件被删除");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("/file/garbage/deleteFile")
    @ResponseBody
    public ResultVO deleteGFile(HttpServletRequest request,
                                @RequestParam(value = "user") String user,
                                @RequestParam(value = "path") String path,
                                @RequestParam(value = "files") String files) throws IOException {
        String ip = getIpAddr(request);
        UserInfo userInfo = userService.getUserInfoByUser(user);
        if (!ip.equals(userInfo.getIp())) {
            return ResultVOUtil.fail(1, "无权限访问");
        }
        long size = 0;
        fileService.deleteFile("garbage/"+path+files);
        MyFile myFile = fileService.getMyFile(path+files);
        size += myFile.getRealSize();
        fileService.deleteDatabaseFile(path+files);
        File file1 = new File(myConfigure.getHostUrl() + path+files);
        if (file1.exists()) {
            file1.delete();
        }
        logger.userLogIn(user,"'"+files+"'  彻底删除");
        userService.setSize(userInfo.getBusy() - size, userInfo.getUser());
        return ResultVOUtil.success("删除成功！");
    }


    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("/file/garbage/deleteDri")
    @ResponseBody
    public ResultVO deleteGDir(HttpServletRequest request,
                               @RequestParam(value = "user") String user,
                               @RequestParam(value = "path") String path) throws IOException {
        UserInfo userInfo = userService.getUserInfoByUser(user);
        String ip = getIpAddr(request);
        if (!ip.equals(userInfo.getIp())) {
            return ResultVOUtil.fail(1, "无权限访问");
        }
        path=path.replace(myConfigure.getHdfsUrl(),"");
        Float size =fileService.getMyFilesByDir(path.replace("garbage/", "")+"%");
        if (size==null){
            size=new Float(0);
        }
        fileService.deleteFiles(path);
        File file = new File(myConfigure.getHostUrl() + path.replace("garbage/", ""));
        if (file.exists()) {
            file.delete();
        }
        userService.setSize(userInfo.getBusy() - size, userInfo.getUser());
        fileService.deleteDataBaseFilesByDir(path.replace("garbage/", "")+"%");
        logger.userLogIn(user,"目录  '"+path.replace(user+"/","")+"'   彻底删除");
        return ResultVOUtil.success("删除成功");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("/file/deleteDri")
    @ResponseBody
    public ResultVO deleteDir(HttpServletRequest request,
                              @RequestParam(value = "user") String user,
                              @RequestParam(value = "path") String path) throws IOException {
        String ip = getIpAddr(request);
        UserInfo userInfo=userService.getUserInfoByUser(user);
        if (!ip.equals(userInfo.getIp())) {
            return ResultVOUtil.fail(1, "无权限访问");
        }
        path=path.replace(myConfigure.getHdfsUrl(),"");
        fileService.setDeleteByDir(path+"%",1);
        fileService.rename(path, "garbage/" + path);
        logger.userLogIn(user,"目录  '"+path.replace(user+"/","")+"'   --->回收站");
        return ResultVOUtil.success("删除成功");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @PostMapping("/add/dir")
    @ResponseBody
    public ResultVO addDir(HttpServletRequest request,
                           @RequestParam(value = "user") String user,
                           @RequestParam(value = "path") String path) throws IOException {
        String ip = getIpAddr(request);
        UserInfo userInfo = userService.getUserInfoByUser(user);
        if (!ip.equals(userInfo.getIp())) {
            return ResultVOUtil.fail(1, "无权限访问");
        }
        if (fileService.mkDir(path)) {
            logger.userLogIn(user, "创建目录  '" + path.replace(user+"/","")+"'");
            return ResultVOUtil.success("创建成功！");
        }
        return ResultVOUtil.fail(1, "目录已存在");

    }

    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("/file/move")
    @ResponseBody
    public ResultVO move(HttpServletRequest request,
                         @RequestParam(value = "user") String user,
                         @RequestParam(value = "oldPath") String oldPath,
                         @RequestParam(value = "newPath") String newPath) throws IOException {
        String ip = getIpAddr(request);
        UserInfo userInfo = userService.getUserInfoByUser(user);
        if (!ip.equals(userInfo.getIp())) {
            return ResultVOUtil.fail(1, "无权限访问");
        }
        if (fileService.rename(oldPath, newPath)) {
            File file = new File(myConfigure.getHostUrl() + oldPath);
            if (file.exists()) {
                file.renameTo(new File(myConfigure.getHostUrl() + newPath));
                logger.userLogIn(user,"文件移动: '"+oldPath.replace(user+"/","")+"' ---> '"+newPath.replace(user+"/","")+"'");
            }
        }
        String[] newNames = newPath.split("/");
        String newName = newNames[newNames.length - 1];
        fileService.modifyFileName(oldPath, newPath, newName);
        return ResultVOUtil.success("移动成功");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("file/dir/get/all")
    @ResponseBody
    public ResultVO<Node[]> getAllDir(@RequestParam("user") String user,
                                      @RequestParam("path") String path,
                                      HttpServletRequest request) throws IOException {
        String ip = getIpAddr(request);
        UserInfo userInfo = userService.getUserInfoByUser(user);
        if (!ip.equals(userInfo.getIp())) {
            return ResultVOUtil.fail(1, "无权限访问");
        }
        Node[] node = new Node[1];
        node[0] = new Node(path.substring(0, path.length() - 1));
        fileService.getDir(path, node[0]);
        return ResultVOUtil.success(node);
    }

    @GetMapping("file/get/type")
    @ResponseBody
    public ResultVO<List<MyFile>> getFileListByType(@RequestParam("user") String user,
                                                    @RequestParam("type") String type,
                                                    HttpServletRequest request) throws IOException {
        String ip = getIpAddr(request);
        UserInfo userInfo = userService.getUserInfoByUser(user);
        if (!ip.equals(userInfo.getIp())) {
            return ResultVOUtil.fail(1, "无权限访问");
        }
        List<MyFile> files = fileService.getFilesByType(userInfo.getHdfs(), type);
        return ResultVOUtil.success(files);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("/file/deleteFile")
    @ResponseBody
    public ResultVO deleteFiles(HttpServletRequest request,
                                @RequestParam(value = "user") String user,
                                @RequestParam(value = "url") String url
    ) throws IOException {
        String ip = getIpAddr(request);
        UserInfo userInfo = userService.getUserInfoByUser(user);
        if (!ip.equals(userInfo.getIp())) {
            return ResultVOUtil.fail(1, "无权限访问");
        }
        String[] urls = url.split(";");
        int i = 0;
        int count = urls.length;
        for (String f : urls) {
            logger.userLogIn(user,"文件  '"+url.replace(user+"/","")+"'  --->回收站");
            if (fileService.rename(f, "garbage/" + f))
                i++;
            fileService.setDeleteDatabaseFile(f, 1);
        }
        return ResultVOUtil.success(count + "个文件，其中" + i + "个文件被删除");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("/restore/file")
    @ResponseBody
    public ResultVO restoreFile(HttpServletRequest request,
                                @RequestParam(value = "user") String user,
                                @RequestParam(value = "url") String url
    ) throws IOException {
        String ip = getIpAddr(request);
        UserInfo userInfo = userService.getUserInfoByUser(user);
        if (!ip.equals(userInfo.getIp())) {
            return ResultVOUtil.fail(1, "无权限访问");
        }
        url = url.replace(myConfigure.getHdfsUrl(), "");
        String newUrl = url.substring(url.indexOf("/") + 1);
        if (fileService.rename(url, newUrl)) {
            fileService.setDeleteDatabaseFile(newUrl, 0);
            logger.userLogIn(user,"还原文件  '"+newUrl.replace(user+"/","")+"'");
            return ResultVOUtil.success("还原成功！");
        }
        return ResultVOUtil.fail(1, "还原失败！");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("/restore/files")
    @ResponseBody
    public ResultVO restoreFiles(HttpServletRequest request,
                                 @RequestParam(value = "user") String user,
                                 @RequestParam(value = "url") String url
    ) throws IOException {
        String ip = getIpAddr(request);
        UserInfo userInfo = userService.getUserInfoByUser(user);
        if (!ip.equals(userInfo.getIp())) {
            return ResultVOUtil.fail(1, "无权限访问");
        }
        String[] urls = url.split(";");
        int i = 0;
        int count = urls.length;
        for (String u : urls) {
            u = u.replace(myConfigure.getHdfsUrl(), "");
            String newUrl = u.substring(u.indexOf("/") + 1);
            if (fileService.rename(u, newUrl)) {
                i++;
                logger.userLogIn(user,"还原文件   '"+newUrl.replace(user+"/","")+"'");
                fileService.setDeleteDatabaseFile(newUrl, 0);
            }
        }


        return ResultVOUtil.success(count + "个文件，其中" + i + "个文件被还原");
    }

    @PostMapping("file/online")
    @ResponseBody
    public ResultVO online(HttpServletRequest request, @RequestParam("path") String path,
                           @RequestParam(value = "user") String user) throws IOException {
        String ip = getIpAddr(request);
        UserInfo userInfo = userService.getUserInfoByUser(user);
        if (!ip.equals(userInfo.getIp())) {
            return ResultVOUtil.fail(1, "无权限访问");
        }
        return ResultVOUtil.success(fileService.fileLocaled(path));

    }
    @Transactional(propagation =Propagation.REQUIRED)
    @PostMapping("share/file")
    @ResponseBody
    public ResultVO shareFile(HttpServletRequest request,@RequestParam("urls[]") List<String> urls,
                              @RequestParam(value = "user") String user){
        String ip = getIpAddr(request);
        UserInfo userInfo = userService.getUserInfoByUser(user);
        if (!ip.equals(userInfo.getIp())) {
            return ResultVOUtil.fail(1, "无权限访问");
        }
        for(String url:urls){
            String shareCode="0"+url;
            shareCode=Base64.getEncoder().encodeToString(shareCode.getBytes());
            url=url.replace(myConfigure.getHdfsUrl(),"");
            fileService.setShare(url,1,shareCode);
            logger.userLogIn(user,"共享文件  '"+url.replace(user+"/","")+"'");
        }
        return ResultVOUtil.success("ok");
    }

    @GetMapping("share/getFileList")
    @ResponseBody
    public ResultVO<List<MyFile>> getShareFileList(HttpServletRequest request,
                                                   @RequestParam(value = "user") String user){
        String ip = getIpAddr(request);
        UserInfo userInfo = userService.getUserInfoByUser(user);
        if (!ip.equals(userInfo.getIp())) {
            return ResultVOUtil.fail(1, "无权限访问");
        }
        List<MyFile> myFiles=fileService.getShareFileList(user+"/");
        return ResultVOUtil.success(myFiles);
    }
    @Transactional(propagation = Propagation.REQUIRED)
    @PostMapping("share/cancel")
    @ResponseBody
    public ResultVO cancelShare(HttpServletRequest request,
                                @RequestParam(value = "user") String user,
                                @RequestParam(value = "url")String url){
        String ip = getIpAddr(request);
        UserInfo userInfo = userService.getUserInfoByUser(user);
        if (!ip.equals(userInfo.getIp())) {
            return ResultVOUtil.fail(1, "无权限访问");
        }
        fileService.setShare(url,0,"");
        logger.userLogIn(user,"文件  '"+url.replace(user+"/","")+"'  取消共享");
        return ResultVOUtil.success("取消成功！");
    }
    @Transactional(propagation = Propagation.REQUIRED)
    @PostMapping("share/dir")
    @ResponseBody
    public  ResultVO shareDir(HttpServletRequest request,
                              @RequestParam(value = "user") String user,
                              @RequestParam(value = "urls[]")List<String> urls){
        String ip = getIpAddr(request);
        UserInfo userInfo = userService.getUserInfoByUser(user);
        if (!ip.equals(userInfo.getIp())) {
            return ResultVOUtil.fail(1, "无权限访问");
        }
        ArrayList<Dir> dirs=new ArrayList<>();
        for(String url:urls){
            String shareCode="1"+url;
            shareCode=Base64.getEncoder().encodeToString(shareCode.getBytes());
            url=url.replace(myConfigure.getHdfsUrl(),"");
            Dir dir=new Dir();
            dir.setIsShare(1);
            dir.setRoot(user+"/");
            dir.setUrl(url);
            dir.setViewCount(0);
            dir.setShareCode(shareCode);
            dirs.add(dir);
        }

        int count=0;
        count+=dirService.addDir(dirs);
        for(String url:urls){
            logger.userLogIn(user,"共享目录  '"+url.replace(user+"/","")+"'");
        }

        return count==0? ResultVOUtil.fail(1,"分享失败"):ResultVOUtil.success("分享成功");

    }
    @Transactional(propagation = Propagation.REQUIRED)
    @PostMapping("share/dir/cancel")
    @ResponseBody
    public ResultVO cancelDirShare(HttpServletRequest request,
                                   @RequestParam(value = "user") String user,
                                   @RequestParam(value = "url")String url){
        String ip = getIpAddr(request);
        UserInfo userInfo = userService.getUserInfoByUser(user);
        if (!ip.equals(userInfo.getIp())) {
            return ResultVOUtil.fail(1, "无权限访问");
        }
        int count=0;
        count+=dirService.deleteByUrl(Arrays.asList(url));
        logger.userLogIn(user,"目录  '"+url.replace(user+"/","")+"'  取消共享");
        return count==0? ResultVOUtil.fail(1,"修改失败"):ResultVOUtil.success("修改成功");
    }
    @GetMapping("share/dir/getList")
    @ResponseBody
    public ResultVO<List<MyFile>> getShareDirList(HttpServletRequest request,
                                               @RequestParam(value = "user") String user){
        String ip = getIpAddr(request);
        UserInfo userInfo = userService.getUserInfoByUser(user);
        if (!ip.equals(userInfo.getIp())) {
            return ResultVOUtil.fail(1, "无权限访问");
        }
        List<Dir> list=null;
        list=dirService.getByRoot(user+"/");
        ArrayList<MyFile> myFiles=new ArrayList<>();
        for(Dir dir:list){
            MyFile myFile=new MyFile();
            myFile.setUrl(dir.getUrl());
            myFile.setName(dir.getUrl().substring(dir.getUrl().lastIndexOf('/')+1));
            myFile.setType("dir");
            myFile.setDownloadCount(dir.getViewCount());
            myFile.setShareCode(dir.getShareCode());
            myFiles.add(myFile);
        }
        return ResultVOUtil.success(myFiles);
    }
    @Transactional(propagation = Propagation.REQUIRED)
    @PostMapping("share/dir/get")
    @ResponseBody
    public ResultVO<List<MyFile>> getShareDir(@RequestParam("url")String url) throws IOException {
        url=url.replace(" ","+");
        byte[] p=Base64.getDecoder().decode(url);
        url=new String(p);
        url=url.replace("1"+myConfigure.getHdfsUrl(),"");
        Dir dir=dirService.getByUrl(url);
        if(dir==null)
            return ResultVOUtil.fail(1,"无权限访问");
        List<MyFile> myFiles=null;
        myFiles=fileService.getMyFiles(url);
        dirService.updateViewCount(url);
        return ResultVOUtil.success(myFiles);
    }
    @Transactional(propagation = Propagation.REQUIRED)
    @PostMapping("share/save")
    @ResponseBody
    public ResultVO saveShare(@RequestParam( "urls[]")List<String> urls,
                              @RequestParam("user")String user,
                              @RequestParam("dest")String dest,
                              @RequestParam("shareDir")String shareDir) throws IOException {
        Dir dir=dirService.getByUrl(shareDir);
        int count=urls.size();

        for(String url:urls){
            url= url.replace(myConfigure.getHdfsUrl(),"");
            if(url.startsWith(dir.getUrl())){
                count=fileService.saveToOther(url,dest+url.substring(url.lastIndexOf("/")+1),user+"/")? count-1:count;
            }
        }
        logger.userLogIn(user,"转存文件");
        return count==0? ResultVOUtil.success("转存成功！"):ResultVOUtil.success(count+"个文件转存失败");
    }




}
