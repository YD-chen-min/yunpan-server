package com.yandan.yunstorage.dao;

import com.yandan.yunstorage.data.AdminInfo;
import com.yandan.yunstorage.data.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

/**
 * Create by yandan
 * 2022/2/23  18:03
 */
@Component
@Mapper
public interface AdminDao {
    @Select("select * from admin where tel=#{tel}")
    AdminInfo getUserByTel(@Param("tel") String tel);
    @Select("select * from admin where user=#{user}")
    AdminInfo getUserByUser(@Param("user") String user);
    @Update("update admin set password=#{password} where user=#{user}")
    int modifyPassword(@Param("user")String user,@Param("password")String password);
    @Update("update admin set ip=#{ip} where user=#{user}")
    int updateMac(@Param("user")String user,@Param("ip")String ip);
}
