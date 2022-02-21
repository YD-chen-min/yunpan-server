package com.yandan.yunstorage.service;

import com.yandan.yunstorage.data.UserForm;
import com.yandan.yunstorage.data.UserInfo;

/**
 * Create by yandan
 * 2021/12/30  15:37
 */
public interface UserService {
    /**
     * 注册时将用户表单里的内容写入数据库中
     * @param userForm
     * @return UserInfo
     */
    UserInfo setUserInfo(UserForm userForm);

    /**
     * 根据tel查询用户信息
     * @param tel
     * @return UserInfo
     */
    UserInfo getUserInfoByTel(String tel);

    /**
     * 根据user查询用户信息
     * @param user
     * @return UserInfo
     */
    UserInfo getUserInfoByUser(String user);

    /**
     * 根据user删除用户及用户文件
     * @param user
     * @return
     */
    boolean deleteUser(String user);

    /**
     * 修改用户信息（user字段不能修改）
     * @param userForm
     * @return
     */
    UserInfo modifyUserInfo(UserForm userForm);

    /**
     * 根据用户账号修改密码
     * @param user
     * @param password
     * @return
     */
    boolean modifyPassword(String user,String password);

    /**
     * 根据user修改mac
     * @param user
     * @param mac
     * @return
     */
    boolean modifyMac(String user,String mac);

    /**
     * 检测user是否存在
     * @param user
     * @return
     */
    boolean userExist(String user);

    /**
     * 更新用户存储空间
     * @param size
     */
    void setSize(float size,String user);


}
