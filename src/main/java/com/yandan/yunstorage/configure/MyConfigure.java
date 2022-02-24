package com.yandan.yunstorage.configure;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Create by yandan
 * 2021/12/30  14:57
 */
@Component
@Data
public class MyConfigure {
    @Value("${my-config.hdfsURL}")
    private String hdfsUrl;
    @Value("${my-config.hostURL}")
    private String hostUrl;
    @Value("${my-config.temp}")
    private String temp;
    @Value("${my-config.log}")
    private String log;
    @Value("${my-config.hadoopJmxServerUrl}")
    private String hadoopJmxServerUrl;
    @Value("${my-config.smtp}")
    private String smtp;
    @Value("${my-config.code}")
    private String code;
    @Value("${my-config.hostEmail}")
    private String hostEmail;
    @Value("${my-config.userName}")
    private String userName;
}
