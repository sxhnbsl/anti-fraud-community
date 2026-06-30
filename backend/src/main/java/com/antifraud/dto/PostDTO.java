package com.antifraud.dto;

import lombok.Data;

/**
 * 帖子数据传输对象
 * 
 * 功能说明：
 * 1. 用于创建帖子的请求参数
 * 2. 不包含统计信息（浏览量、点赞数等）
 * 3. 用于前后端数据交互
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@Data
public class PostDTO {
    /**
     * 帖子标题
     */
    private String title;
    
    /**
     * 帖子内容
     */
    private String content;
    
    /**
     * 作者ID
     */
    private Long authorId;
    
    /**
     * 作者名称
     */
    private String authorName;
    
    /**
     * 帖子分类
     */
    private String category;
}