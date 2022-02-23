package com.yandan.yunstorage.data;

import lombok.Data;

import javax.persistence.Id;

/**
 * Create by yandan
 * 2022/2/23  18:07
 */
@Data
public class AdminInfo {
    @Id
    private String user;
    private String name;
    private String tel;
    private String email;
    private String password;
    private String ip;

}
