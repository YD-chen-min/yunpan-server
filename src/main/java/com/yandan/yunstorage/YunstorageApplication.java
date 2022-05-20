package com.yandan.yunstorage;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;

import java.util.Properties;

@SpringBootApplication
@MapperScan("com.yandan.yunstorage.dao")
public class YunstorageApplication {

    public static void main(String[] args) {
        Properties properties=System.getProperties();
        properties.setProperty("HADOOP_USER_NAME","root");
        SpringApplication.run(YunstorageApplication.class, args);
    }
    @Bean
    public TomcatServletWebServerFactory tomcatServletWebServerFactory (){
        // 修改内置的 tomcat 容器配置
        TomcatServletWebServerFactory tomcatServlet = new TomcatServletWebServerFactory();
        tomcatServlet .addConnectorCustomizers(
                (TomcatConnectorCustomizer) connector -> connector.setProperty("relaxedQueryChars", "[]|{}^&#x5c;&#x60;&quot;&lt;&gt;")
        );
        return tomcatServlet ;
    }


}
