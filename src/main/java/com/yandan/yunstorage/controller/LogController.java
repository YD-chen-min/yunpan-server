package com.yandan.yunstorage.controller;

import com.yandan.yunstorage.VO.ResultVO;
import com.yandan.yunstorage.util.Logger;
import com.yandan.yunstorage.util.ResultVOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Create by yandan
 * 2022/2/22  14:07
 */
@RestController
public class LogController {
    @Autowired
    private Logger logger;
    @GetMapping("/log/get/user")
    @ResponseBody
    public ResultVO getUserLog(@RequestParam("user") String user){
        return ResultVOUtil.success(logger.userLogOut(user));
    }
}
