package com.antifraud.dto;

import lombok.Data;

/**
 * 知识库数据传输对象
 * 
 * 功能说明：
 * 1. 用于创建知识库条目的请求参数
 * 2. 不包含统计信息（浏览量、点赞数等）
 * 3. 用于前后端数据交互
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@Data
public class KnowledgeDTO {
    /**
     * 知识库标题
     */
    private String title;
    
    /**
     * 知识库内容
     */
    private String content;
    
    /**
     * 分类
     */
    private String category;
    
    /**
     * 标签（逗号分隔）
     */
    private String tags;
    
    /**
     * 封面图片URL
     */
    private String coverImage;
    
    /**
     * 图片URL列表（JSON数组字符串）
     */
    private String images;
    
    /**
     * 视频URL
     */
    private String videoUrl;
    
    /**
     * 视频封面URL
     */
    private String videoCover;
    
    /**
     * 作者ID
     */
    private Long authorId;
}