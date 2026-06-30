package com.antifraud.dto;

import lombok.Data;

/**
 * 案例数据传输对象
 * 
 * 功能说明：
 * 1. 用于创建案例的请求参数
 * 2. 不包含统计信息（浏览量、点赞数等）
 * 3. 用于前后端数据交互
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@Data
public class CaseDTO {
    /**
     * 案例标题
     */
    private String title;
    
    /**
     * 案例内容
     */
    private String content;
    
    /**
     * 分类
     */
    private String category;
    
    /**
     * 诈骗手法分析
     */
    private String fraudAnalysis;
    
    /**
     * 防范建议
     */
    private String preventionAdvice;
    
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