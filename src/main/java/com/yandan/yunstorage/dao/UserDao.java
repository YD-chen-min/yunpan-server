package com.yandan.yunstorage.dao;

import com.yandan.yunstorage.data.UserForm;
import com.yandan.yunstorage.data.UserInfo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Create by yandan
 * 2021/12/30  15:56
 */
@Repository
@Mapper
public interface UserDao {
    @Insert("insert into user(user,name,tel,email,password,ip) values(#{user},#{name}," +
            "#{tel},#{email},#{password},#{ip})")
    int insertUser(UserForm userForm);

    @Select("select * from user where tel=#{tel}")
    UserInfo getUserByTel(@Param("tel") String tel);

    @Select("select * from user where user=#{user}")
    UserInfo getUserByUser(@Param("user") String user);

    @Select("select hdfs from userAndhdfsUrl where user=#{user}")
    String getUserHDFS(UserInfo userInfo);

    @Insert("insert into userAndhdfsUrl(user,hdfs) values(#{user},#{hdfs})")
    int insertUserHDFS(@Param("user") String user, @Param("hdfs") String hdfs);
    @Delete("delete from user where user=#{user}")
    int deleteUser(@Param("user")String user);
    @Update("update user set name=#{name},tel=#{tel},email=#{email} where user=#{user}")
    int updateUserInfo(UserForm userForm);
    @Update("update user set password=#{password} where user=#{user}")
    int modifyPassword(@Param("user")String user,@Param("password")String password);
    @Delete("delete from userAndhdfsUrl where user=#{user} ")
    int deleteUserHDFS(@Param("user")String user);
    @Update("update user set ip=#{ip} where user=#{user}")
    int updateUserMac(@Param("user")String user,@Param("ip")String ip);
    @Update("update user set busy=#{size} where user=#{user}")
    int setUserBusy(@Param("user")String user,@Param("size")float size);
    @Select("select * from user")
    List<UserInfo> getUsers();
    @Select("select * from user where user like #{user}")
    List<UserInfo> getUsersLikeUser(@Param("user")String user);
    @Delete("delete from user where user = #{user}")
    int deleteByUser(@Param("user")String user);

}
