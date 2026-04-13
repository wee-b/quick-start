package com.quickstart.client;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.quickstart")
@MapperScan("com.quickstart.client.module.**.mapper")
public class QsClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(QsClientApplication.class, args);
    }

}
