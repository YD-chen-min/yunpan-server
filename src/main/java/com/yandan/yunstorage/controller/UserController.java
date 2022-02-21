package com.yandan.yunstorage.controller;

import com.yandan.yunstorage.VO.ResultVO;
import com.yandan.yunstorage.data.UserForm;
import com.yandan.yunstorage.data.UserInfo;
import com.yandan.yunstorage.service.FileService;
import com.yandan.yunstorage.service.UserService;
import com.yandan.yunstorage.util.ResultVOUtil;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Create by yandan
 * 2021/12/30  18:16
 */
@Log4j
@RestController
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private FileService fileService;
    @Transactional
    @PostMapping("/user/register")
    @ResponseBody
    public ResultVO<UserInfo> register(@Validated UserForm userForm, BindingResult bindingResult, HttpServletRequest request){
        String ip =getIpAddr(request);
        if(bindingResult.hasErrors()){
            log.error("【注册】：参数错误");
            return ResultVOUtil.fail(1,"参数错误");
        }
        userForm.setIp(ip);
        UserInfo userInfo=userService.setUserInfo(userForm);
        fileService.mkDir(userInfo.getHdfs());
        fileService.mkGDir("garbage/"+userInfo.getHdfs());
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
    public ResultVO<UserInfo> login(@RequestParam(value = "user",defaultValue = "") String user, HttpServletRequest request,
                                  @RequestParam(value = "password",defaultValue = "")String password) {
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
            if(!userInfo.getPassword().equals(password)){
                userInfo=userService.getUserInfoByUser(user);
                if(userInfo==null)
                    return ResultVOUtil.fail(1, "账号或密码错误");
                else if (userInfo.getPassword().equals(password)){
                    userService.modifyMac(userInfo.getUser(), ip);
                    return ResultVOUtil.success(userInfo);
                }
            }else{
                userService.modifyMac(userInfo.getUser(), ip);
                return ResultVOUtil.success(userInfo);
            }
        }
        return ResultVOUtil.fail(1,"账号或密码错误");
    }
    @Transactional
    @PostMapping("/user/modify/baseInfo")
    @ResponseBody
    public ResultVO<UserInfo> modifyUserInfo(@Validated UserForm userForm, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            log.error("【登录】：参数错误");
            return ResultVOUtil.fail(1,"参数错误");
        }
        UserInfo userInfo = userService.modifyUserInfo(userForm);
        return ResultVOUtil.success(userInfo);
    }
    @Transactional
    @PostMapping("/user/modify/password")
    @ResponseBody
    public ResultVO modifyPassword(@RequestParam(value = "user",defaultValue="")String user,@RequestParam(value = "password",defaultValue = "")String password){
        if("".equals(password)||"".equals(password)){
            log.error("【修改密码】：参数错误");
            return ResultVOUtil.fail(1,"参数错误");
        }
        userService.modifyPassword(user,password);
        return ResultVOUtil.success("密码修改成功");
    }
    @Transactional
    @PostMapping("/user/delete")
    @ResponseBody
    public ResultVO deleteUser(@RequestParam("users")String users){
       String[] user= users.split(";");
       int count=user.length;
       int i=0;
       for(String u:user){
           UserInfo userInfo=userService.getUserInfoByUser(u);
           fileService.deleteFiles(userInfo.getHdfs());
           userService.deleteUser(u);
           i++;
       }
       return ResultVOUtil.success(count+"条数据中，有"+i+"条数据及关联数据被彻底删除");
    }

    @GetMapping("/user/exist")
    @ResponseBody
    public ResultVO exist(@RequestParam("user")String user){
        if (userService.userExist(user))
            return ResultVOUtil.success("该账号已存在");
        return ResultVOUtil.fail(1,"");
    }

    @GetMapping("/user/get/free")
    @ResponseBody
    public ResultVO getUserFree(@RequestParam("user")String user){
        UserInfo userInfo=userService.getUserInfoByUser(user);
        if (userInfo==null) return ResultVOUtil.fail(1,"no user");
        double f=userInfo.getBusy()/userInfo.getStore()*100.0;
        return ResultVOUtil.success(f);
    }


}