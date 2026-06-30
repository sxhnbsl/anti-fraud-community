package com.antifraud;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.antifraud.repository")  // 添加这一行
public class AntiFraudBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(AntiFraudBackendApplication.class, args);
    }
}