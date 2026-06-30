package com.antifraud.model;

import lombok.Data;
import java.util.Date;

/**
 * 知识库实体类
 * 
 * 功能说明：
 * 1. 存储反诈骗知识库条目
 * 2. 支持分类和标签管理
 * 3. 包含统计信息（浏览量、点赞数）
 * 
 * 数据表：knowledge
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@Data
public class Knowledge {
    /**
     * 知识库ID（主键，自增）
     */
    private Long id;
    
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