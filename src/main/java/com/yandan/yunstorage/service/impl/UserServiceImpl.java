package com.yandan.yunstorage.service.impl;

import com.yandan.yunstorage.dao.UserDao;
import com.yandan.yunstorage.data.UserForm;
import com.yandan.yunstorage.data.UserInfo;
import com.yandan.yunstorage.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Create by yandan
 * 2021/12/30  16:09
 */
@Service
public class UserServiceImpl  implements UserService {
    @Autowired
    private UserDao userDao;
    @Override
    public UserInfo setUserInfo(UserForm userForm) {
        if (userForm.isEmpty())
            return null;
        if(userDao.insertUser(userForm)<=0)
            return null;
        userDao.insertUserHDFS(userForm.getUser(),userForm.getUser()+"/");
        UserInfo userInfo= new UserInfo();
        BeanUtils.copyProperties(userForm,userInfo);
        userInfo.setHdfs(userForm.getUser()+"/");
        return userInfo;
    }

    @Override
    public UserInfo getUserInfoByTel(String tel) {
        UserInfo userInfo=userDao.getUserByTel(tel);
        if (userInfo==null)
            return null;
        String hdfs=userDao.getUserHDFS(userInfo);
        userInfo.setHdfs(hdfs);
        return userInfo;
    }

    @Override
    public UserInfo getUserInfoByUser(String user) {
        UserInfo userInfo=userDao.getUserByUser(user);
        if (userInfo==null)
            return null;
        String hdfs=userDao.getUserHDFS(userInfo);
        userInfo.setHdfs(hdfs);
        return userInfo;
    }

    @Override
    public boolean deleteUser(String user) {
       if(userDao.deleteUser(user)>0&&userDao.deleteUserHDFS(user)>0)
           return true;
        return false;
    }

    @Override
    public UserInfo modifyUserInfo(UserForm userForm) {
        if(userDao.updateUserInfo(userForm)>0)
            return userDao.getUserByUser(userForm.getUser());
        return null;
    }

    @Override
    public boolean modifyPassword(String user, String password) {
        if(userDao.modifyPassword(user,password)>0)
            return true;
        return false;
    }

    @Override
    public boolean modifyMac(String user, String mac) {
        if(userDao.updateUserMac(user,mac)>0)
            return true;
        return false;
    }

    @Override
    public boolean userExist(String user) {
        UserInfo userInfo=userDao.getUserByUser(user);
        if (userInfo==null)
            return false;
        return true;
    }

    @Override
    public void setSize(float size, String user) {
        userDao.setUserBusy(user,size);
    }

    @Override
    public List<UserInfo> getUsers(String user,int start,int size) {
        if("".equals(user)){
            return userDao.getUsers(start,size);
        }else{
            return userDao.getUsersLikeUser(user+"%",start,size);
        }
    }

    @Override
    public int deleteUserByUser(String user) {
        return userDao.deleteByUser(user)+userDao.deleteUserHDFS(user);
    }
}
