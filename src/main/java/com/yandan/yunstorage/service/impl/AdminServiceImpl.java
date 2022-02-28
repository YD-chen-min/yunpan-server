package com.yandan.yunstorage.service.impl;

import com.yandan.yunstorage.dao.AdminDao;
import com.yandan.yunstorage.data.AdminInfo;
import com.yandan.yunstorage.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Create by yandan
 * 2022/2/23  18:12
 */
@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private AdminDao adminDao;
    @Override
    public AdminInfo getAdminInfoByUser(String user) {
        if(user!=null)
            return adminDao.getUserByUser(user);
        return null;
    }

    @Override
    public AdminInfo getAdminInfoByTel(String tel) {
        if (tel!=null)
            return adminDao.getUserByTel(tel);
        return null;
    }

    @Override
    public int modifyPassword(String user, String password) {
        if(adminDao.getUserByUser(user)!=null)
            return adminDao.modifyPassword(user,password);
        return 0;
    }

    @Override
    public int updateMac(String user, String ip) {
        if (adminDao.getUserByUser(user)!=null)
            return adminDao.updateMac(user,ip);
        return 0;
    }
}
