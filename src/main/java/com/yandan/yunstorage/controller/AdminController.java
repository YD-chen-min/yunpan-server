package com.yandan.yunstorage.controller;

import com.yandan.yunstorage.VO.ResultVO;
import com.yandan.yunstorage.data.AdminInfo;
import com.yandan.yunstorage.data.UserInfo;
import com.yandan.yunstorage.service.AdminService;
import com.yandan.yunstorage.util.Logger;
import com.yandan.yunstorage.util.ResultVOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Create by yandan
 * 2022/2/23  18:15
 */
@RestController
public class AdminController {
    @Autowired
    private AdminService adminService;
    @Autowired
    private Logger logger;

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
    @Transactional
    @PostMapping("/admin/login")
    @ResponseBody
    public ResultVO<UserInfo> login(@RequestParam(value = "user",defaultValue = "") String user, HttpServletRequest request,
                                    @RequestParam(value = "password",defaultValue = "")String password) {
        AdminInfo userInfo;
        String ip = getIpAddr(request);
        if ("".equals(user)) {
            return ResultVOUtil.fail(1, "参数错误");
        }
        userInfo = adminService.getAdminInfoByTel(user);
        if (userInfo == null) {
            userInfo = adminService.getAdminInfoByUser(user);
            if (userInfo == null)
                return ResultVOUtil.fail(1, "账号或密码错误");
            else if (userInfo.getPassword().equals(password)) {
                adminService.updateMac(userInfo.getUser(), ip);
                return ResultVOUtil.success(userInfo);
            }
        } else {
            if(!userInfo.getPassword().equals(password)){
                userInfo=adminService.getAdminInfoByUser(user);
                if(userInfo==null)
                    return ResultVOUtil.fail(1, "账号或密码错误");
                else if (userInfo.getPassword().equals(password)){
                    adminService.updateMac(userInfo.getUser(), ip);
                    return ResultVOUtil.success(userInfo);
                }
            }else{
                adminService.updateMac(userInfo.getUser(), ip);
                return ResultVOUtil.success(userInfo);
            }
        }
        return ResultVOUtil.fail(1,"账号或密码错误");
    }
    @Transactional
    @PostMapping("/admin/modify/password")
    @ResponseBody
    public ResultVO modifyPassword(@RequestParam(value = "user",defaultValue="")String user,@RequestParam(value = "password",defaultValue = "")String password){
        if("".equals(password)||"".equals(password)){
            return ResultVOUtil.fail(1,"参数错误");
        }
        adminService.modifyPassword(user,password);
        logger.adminLogIn(user,"修改密码");
        return ResultVOUtil.success("密码修改成功");
    }
}
