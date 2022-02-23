package com.yandan.yunstorage.service;

import com.yandan.yunstorage.data.AdminInfo;

/**
 * Create by yandan
 * 2022/2/23  18:09
 */
public interface AdminService {
    /**
     * 通过账号获取管理员信息
     * @param user
     * @return
     */
    AdminInfo getAdminInfoByUser(String user);

    /**
     * 通过手机号获取用户信息
     * @param tel
     * @return
     */
    AdminInfo getAdminInfoByTel(String tel);

    /**
     * 更改密码
     * @param user
     * @param password
     * @return
     */
    int modifyPassword(String user,String password);

    /**
     * 根据账号修改admin登录的ip
     * @param user
     * @param ip
     * @return
     */
    int  updateMac(String user,String ip);
}
