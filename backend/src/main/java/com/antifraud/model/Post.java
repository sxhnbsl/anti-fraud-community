package com.antifraud.model;

import lombok.Data;
import java.util.Date;

/**
 * 帖子实体类
 * 
 * 功能说明：
 * 1. 存储帖子基本信息
 * 2. 支持帖子审核功能
 * 3. 包含统计信息（浏览量、点赞数、评论数）
 * 
 * 数据表：posts
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@Data
public class Post {
    /**
     * 帖子ID（主键，自增）
     */
    private Long id;
    
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
     * 浏览量
     */
    private Integer viewCount;
    
    /**
     * 点赞数
     */
    private Integer likeCount;
    
    /**
     * 评论数
     */
    private Integer commentCount;
    
    /**
     * 帖子状态（已废弃，使用auditStatus）
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
    
    /**
     * 审核状态
     * - PENDING: 待审核
     * - APPROVED: 已通过
     * - REJECTED: 已拒绝
     */
    private String auditStatus;
    
    /**
     * 审核备注
     */
    private String auditRemark;
    
    /**
     * 审核时间
     */
    private Date auditTime;
    
    /**
     * 审核状态常量：待审核
     */
    public static final String AUDIT_PENDING = "PENDING";
    
    /**
     * 审核状态常量：已通过
     */
    public static final String AUDIT_APPROVED = "APPROVED";
    
    /**
     * 审核状态常量：已拒绝
     */
    public static final String AUDIT_REJECTED = "REJECTED";
}