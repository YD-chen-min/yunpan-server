package com.yandan.yunstorage.data;

import lombok.Data;

/**
 * Create by yandan
 * 2021/12/30  15:22
 */
@Data
public class UserForm {
    private String user;
    private String name;
    private String password;
    private String tel;
    private String email;
    private String ip;
    public boolean isEmpty(){
        if (user==null||user.equals(""))
            return true;
        if (name==null||name.equals(""))
            return true;
        if (password==null||password.equals(""))
            return true;
        if((email==null||email.equals(""))&&(tel==null||tel.equals("")))
            return true;
        return false;

    }

}
