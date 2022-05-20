package com.yandan.yunstorage.controller;

import com.yandan.yunstorage.VO.ResultVO;
import com.yandan.yunstorage.dao.AdminDao;
import com.yandan.yunstorage.data.AdminInfo;
import com.yandan.yunstorage.data.UserForm;
import com.yandan.yunstorage.data.UserInfo;
import com.yandan.yunstorage.service.FileService;
import com.yandan.yunstorage.service.UserService;
import com.yandan.yunstorage.util.Logger;
import com.yandan.yunstorage.util.ResultVOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Create by yandan
 * 2021/12/30  18:16
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private FileService fileService;
    @Autowired
    private Logger logger;
    @Autowired
    private AdminDao adminDao;

    @Transactional
    @PostMapping("/user/register")
    @ResponseBody
    public ResultVO<UserInfo> register(@Validated UserForm userForm, BindingResult bindingResult, HttpServletRequest request) throws IOException {
        String ip = getIpAddr(request);
        if (bindingResult.hasErrors()) {
            return ResultVOUtil.fail(1, "参数错误");
        }
        userForm.setIp(ip);
        UserInfo userInfo = userService.setUserInfo(userForm);
        fileService.mkDir(userInfo.getHdfs());
        fileService.mkGDir("garbage/" + userInfo.getHdfs());
        return ResultVOUtil.success(userInfo);
    }

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
    @PostMapping("/user/login")
    @ResponseBody
    public ResultVO<UserInfo> login(@RequestParam(value = "user", defaultValue = "") String user, HttpServletRequest request,
                                    @RequestParam(value = "password", defaultValue = "") String password) {
        UserInfo userInfo;
        String ip = getIpAddr(request);
        if ("".equals(user)) {
            return ResultVOUtil.fail(1, "参数错误");
        }
        userInfo = userService.getUserInfoByTel(user);
        if (userInfo == null) {
            userInfo = userService.getUserInfoByUser(user);
            if (userInfo == null)
                return ResultVOUtil.fail(1, "账号或密码错误");
            else if (userInfo.getPassword().equals(password)) {
                userService.modifyMac(userInfo.getUser(), ip);
                return ResultVOUtil.success(userInfo);
            }
        } else {
            if (!userInfo.getPassword().equals(password)) {
                userInfo = userService.getUserInfoByUser(user);
                if (userInfo == null)
                    return ResultVOUtil.fail(1, "账号或密码错误");
                else if (userInfo.getPassword().equals(password)) {
                    userService.modifyMac(userInfo.getUser(), ip);
                    return ResultVOUtil.success(userInfo);
                }
            } else {
                userService.modifyMac(userInfo.getUser(), ip);
                return ResultVOUtil.success(userInfo);
            }
        }
        return ResultVOUtil.fail(1, "账号或密码错误");
    }

    @Transactional
    @PostMapping("/user/modify/baseInfo")
    @ResponseBody
    public ResultVO<UserInfo> modifyUserInfo(@Validated UserForm userForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResultVOUtil.fail(1, "参数错误");
        }
        UserInfo userInfo = userService.modifyUserInfo(userForm);
        logger.userLogIn(userForm.getUser(), "修改用户信息");
        return ResultVOUtil.success(userInfo);
    }

    @Transactional
    @PostMapping("/user/modify/password")
    @ResponseBody
    public ResultVO modifyPassword(@RequestParam(value = "user", defaultValue = "") String user, @RequestParam(value = "password", defaultValue = "") String password) {
        if ("".equals(password) || "".equals(password)) {
            return ResultVOUtil.fail(1, "参数错误");
        }
        userService.modifyPassword(user, password);
        logger.userLogIn(user, "修改密码");
        return ResultVOUtil.success("密码修改成功");
    }



    @GetMapping("/user/exist")
    @ResponseBody
    public ResultVO exist(@RequestParam("user") String user) {
        if (userService.userExist(user))
            return ResultVOUtil.success("该账号已存在");
        return ResultVOUtil.fail(1, "");
    }

    @GetMapping("/user/get/free")
    @ResponseBody
    public ResultVO getUserFree(@RequestParam("user") String user) {
        UserInfo userInfo = userService.getUserInfoByUser(user);
        if (userInfo == null) return ResultVOUtil.fail(1, "no user");
        double f = userInfo.getBusy() / userInfo.getStore() * 100.0;
        return ResultVOUtil.success(f);
    }

    @GetMapping("/user/get/info")
    @ResponseBody
    public ResultVO getUserInfo(@RequestParam("user") String user) {
        UserInfo userInfo = userService.getUserInfoByUser(user);
        if (userInfo==null)
            userInfo=userService.getUserInfoByTel(user);
        if (userInfo==null){
            return ResultVOUtil.fail(1,"用户不存在");
        }
        return ResultVOUtil.success(userInfo);
    }

    @GetMapping("/user/getList")
    @ResponseBody
    public ResultVO getUsers(@RequestParam(value = "user", defaultValue = "") String user,@RequestParam(value = "start", defaultValue = "") int start) {
        return ResultVOUtil.success(userService.getUsers(user,start,15));
    }

    @PostMapping("/user/delete")
    @ResponseBody
    public ResultVO deleteUserByUser(HttpServletRequest httpServletRequest, @RequestParam("user") String user, @RequestParam("admin") String admin) throws IOException {
        String ip = getIpAddr(httpServletRequest);
        AdminInfo adminInfo = adminDao.getUserByUser(admin);
        if (adminInfo == null) {
            return ResultVOUtil.fail(1, "信息错误");
        }
        if (!ip.equals(adminInfo.getIp())) {
            return ResultVOUtil.fail(1, "无权限访问");
        }
        int count = userService.deleteUserByUser(user);
        String message = "";
        message += "删除用户 <账号>" + user + ";  影响数据库表<users>结果数目 : " + count;
        count = fileService.deleteDataBaseFileByRoot(user + "/");
        message += ";  影响数据库表<users>结果数目 : " + count;
        fileService.deleteFiles(user );
        fileService.deleteFiles("garbage/"+user);
        userService.deleteUserByUser(user);
        logger.deleteLog(user);
        logger.adminLogIn(message);
        return ResultVOUtil.success("success");
    }
}
