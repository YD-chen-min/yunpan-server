package com.yandan.yunstorage.controller;

import com.yandan.yunstorage.VO.ResultVO;
import com.yandan.yunstorage.configure.MyConfigure;
import com.yandan.yunstorage.data.UserInfo;
import com.yandan.yunstorage.util.Logger;
import com.yandan.yunstorage.util.ResultVOUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.net.URLEncoder;
import java.util.List;

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
    @Transactional
    @RequestMapping(value = "/log/user/download")
    public ResponseEntity<byte[]> download(@RequestParam(value = "user") String user) throws Exception {



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
}
