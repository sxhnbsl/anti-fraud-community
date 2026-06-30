package com.antifraud.model;

import lombok.Data;
import java.util.Date;

/**
 * 案例实体类
 * 
 * 功能说明：
 * 1. 存储诈骗案例信息
 * 2. 包含诈骗类型、受害信息和防范提示
 * 3. 包含统计信息（浏览量、点赞数）
 * 
 * 数据表：cases
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@Data
public class Case {
    /**
     * 案例ID（主键，自增）
     */
    private Long id;
    
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
    
    /**
     * 浏览量
     */
    private Integer viewCount;
    
    /**
     * 点赞数
     */
    private Integer likeCount;
    
    /**
     * 状态（published/deleted）
     */
    private String status;
    
    /**
     * 创建时间
     */
    private Date createdAt;
    
    /**
     * 更新时间
     */
    private Date updatedAt;
}