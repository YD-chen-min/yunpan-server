package com.yandan.yunstorage.controller;

import com.yandan.yunstorage.VO.ResultVO;
import com.yandan.yunstorage.configure.MyConfigure;
import com.yandan.yunstorage.util.Logger;
import com.yandan.yunstorage.util.ResultVOUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.net.URLEncoder;

/**
 * Create by yandan
 * 2022/2/22  14:07
 */
@RestController
public class LogController {
    @Autowired
    private Logger logger;
    @Autowired
    private MyConfigure myConfigure;
    @GetMapping("/log/get/user")
    @ResponseBody
    public ResultVO getUserLog(@RequestParam("user") String user){
        return ResultVOUtil.success(logger.userLogOut(user));
    }

    @GetMapping("/log/get/admin")
    @ResponseBody
    public ResultVO getAdminLog(){
        return ResultVOUtil.success(logger.adminLogOut());
    }
    @GetMapping("/log/get/error")
    @ResponseBody
    public  ResultVO getErrorLog(){
        return ResultVOUtil.success(logger.errorLogOut());
    }
    @Transactional
    @RequestMapping(value = "/log/user/download")
    public ResponseEntity<byte[]> downloadUserLog(@RequestParam(value = "user") String user) throws Exception {



       File file=new File(myConfigure.getLog()+user+".log");
       if (!file.exists()){
           return null;

       }
       HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment",
                URLEncoder.encode(user+".log", "UTF-8"));
        ResponseEntity<byte[]> responseEntity = new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
        logger.userLogIn(user,"下载日志 ");
        return responseEntity;
    }
    @Transactional
    @RequestMapping("/log/admin/download")
    public ResponseEntity<byte[]> downloadAdminLog() throws Exception {



        File file=new File(myConfigure.getLog()+"admin.log");
        if (!file.exists()){
            return null;

        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment",
                URLEncoder.encode("admin.log", "UTF-8"));
        ResponseEntity<byte[]> responseEntity = new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
        logger.adminLogIn("下载日志 ");
        return responseEntity;
    }
    @Transactional
    @RequestMapping("/log/error/download")
    public ResponseEntity<byte[]> downloadErrorLog() throws Exception {



        File file=new File(myConfigure.getLog()+"error.log");
        if (!file.exists()){
            return null;

        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment",
                URLEncoder.encode("error.log", "UTF-8"));
        ResponseEntity<byte[]> responseEntity = new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
        logger.adminLogIn("下载错误日志");
        return responseEntity;
    }
}
