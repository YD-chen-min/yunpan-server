package com.yandan.yunstorage.util;

import com.yandan.yunstorage.configure.MyConfigure;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Base64;

/**
 * Create by yandan
 * 2022/2/24  17:55
 */
@Component
public class EmailUtil {
    @Autowired
    private MyConfigure myConfigure;
    @Autowired
    private Logger logger;
    private HtmlEmail htmlEmail=null;

    public void setHtmlEmail(){
        htmlEmail=new HtmlEmail();
        htmlEmail.setHostName(myConfigure.getSmtp());
        htmlEmail.setCharset("utf-8");
        try {
            htmlEmail.setFrom(myConfigure.getHostEmail(),myConfigure.getUserName());
            byte[] b=Base64.getDecoder().decode(myConfigure.getCode());
            String code=new String(b);
            htmlEmail.setAuthentication(myConfigure.getHostEmail(), code);
        } catch (EmailException e) {
            logger.errorLogIn(e.getMessage(),e.getStackTrace());
        }

    }
    public boolean sendCode(String code,String userEmail){

        setHtmlEmail();
        htmlEmail.setSubject("验证码");
        try {
            htmlEmail.addTo(userEmail);
            htmlEmail.setMsg("验证码是"+code+"  ;  如果不是本人操作请忽略");

            htmlEmail.send();

            return true;
        } catch (EmailException e) {
            logger.errorLogIn(e.getMessage(),e.getStackTrace());
            return false;
        }

    }

}
