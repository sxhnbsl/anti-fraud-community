package com.antifraud.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置类
 * 
 * 功能说明：
 * 1. 配置静态资源访问路径
 * 2. 支持上传文件的HTTP访问
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    /**
     * 配置静态资源处理
     * 
     * 将 /uploads/** 路径映射到项目根目录下的 uploads 文件夹
     * 这样上传的文件就可以通过 http://localhost:8080/uploads/文件名 访问
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 获取当前工作目录
        String currentDir = System.getProperty("user.dir");
        
        // 配置上传文件的访问路径（使用绝对路径）
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + currentDir + "/uploads/");
    }
}