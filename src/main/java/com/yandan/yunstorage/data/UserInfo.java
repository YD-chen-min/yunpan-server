package com.yandan.yunstorage.data;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Create by yandan
 * 2021/12/30  15:19
 */
@Entity
@Data
public class UserInfo {
    @Id
    private String user;
    private String name;
    private String tel;
    private String email;
    private String password;
    private String hdfs;
    private String ip;
    private float store;
    private float busy;

}
