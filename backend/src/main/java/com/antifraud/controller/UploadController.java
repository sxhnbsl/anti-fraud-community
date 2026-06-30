package com.antifraud.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传控制器
 * 
 * 功能说明：
 * 1. 处理图片和视频文件的上传
 * 2. 生成唯一的文件名称
 * 3. 保存文件到服务器目录
 * 4. 返回文件访问URL
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@RestController
@RequestMapping("/api/upload")
public class UploadController {
    
    // 文件存储目录
    private static final String UPLOAD_DIR = "uploads";
    
    // 支持的图片格式
    private static final String[] IMAGE_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif", ".webp"};
    
    // 支持的视频格式
    private static final String[] VIDEO_EXTENSIONS = {".mp4", ".avi", ".mov", ".wmv", ".flv"};
    
    // 图片最大大小（10MB）
    private static final long MAX_IMAGE_SIZE = 10 * 1024 * 1024;
    
    // 视频最大大小（200MB）
    private static final long MAX_VIDEO_SIZE = 200 * 1024 * 1024;
    
    /**
     * 上传文件
     * 
     * @param file 上传的文件
     * @param type 文件类型 (image/video/avatar)
     * @return ResponseEntity 包含文件URL的响应
     */
    @PostMapping
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("type") String type) {
        try {
            // 验证文件是否为空
            if (file.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "文件不能为空");
                return ResponseEntity.status(400).body(response);
            }
            
            // 验证文件大小
            long fileSize = file.getSize();
            if ("image".equals(type) || "avatar".equals(type)) {
                if (fileSize > MAX_IMAGE_SIZE) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "图片大小不能超过 10MB");
                    return ResponseEntity.status(400).body(response);
                }
            } else if ("video".equals(type)) {
                if (fileSize > MAX_VIDEO_SIZE) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "视频大小不能超过 200MB");
                    return ResponseEntity.status(400).body(response);
                }
            }
            
            // 验证文件类型
            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            
            if ("image".equals(type) || "avatar".equals(type)) {
                if (!isImageFile(extension)) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "只支持上传图片文件 (jpg, jpeg, png, gif, webp)");
                    return ResponseEntity.status(400).body(response);
                }
            } else if ("video".equals(type)) {
                if (!isVideoFile(extension)) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "只支持上传视频文件 (mp4, avi, mov, wmv, flv)");
                    return ResponseEntity.status(400).body(response);
                }
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "文件类型参数错误，只支持 image、video 或 avatar");
                return ResponseEntity.status(400).body(response);
            }
            
            // 获取当前工作目录
            String currentDir = System.getProperty("user.dir");
            
            // 创建上传目录（使用绝对路径）
            File uploadDir = new File(currentDir, UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            // 生成唯一文件名
            String fileName = UUID.randomUUID().toString() + extension;
            File destFile = new File(uploadDir, fileName);
            
            // 保存文件
            file.transferTo(destFile);
            
            // 构建文件URL
            String fileUrl = "/uploads/" + fileName;
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "上传成功");
            
            Map<String, Object> data = new HashMap<>();
            data.put("url", fileUrl);
            response.put("data", data);
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "上传失败：" + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return filename.substring(lastDotIndex).toLowerCase();
        }
        return "";
    }
    
    /**
     * 判断是否为图片文件
     */
    private boolean isImageFile(String extension) {
        for (String ext : IMAGE_EXTENSIONS) {
            if (ext.equals(extension)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 判断是否为视频文件
     */
    private boolean isVideoFile(String extension) {
        for (String ext : VIDEO_EXTENSIONS) {
            if (ext.equals(extension)) {
                return true;
            }
        }
        return false;
    }
}