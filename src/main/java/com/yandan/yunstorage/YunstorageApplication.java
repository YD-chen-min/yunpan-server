package com.yandan.yunstorage;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Properties;

@SpringBootApplication
@MapperScan("com.yandan.yunstorage.dao")
public class YunstorageApplication {

    public static void main(String[] args) {
        Properties properties=System.getProperties();
        properties.setProperty("HADOOP_USER_NAME","root");
        SpringApplication.run(YunstorageApplication.class, args);
    }

}
