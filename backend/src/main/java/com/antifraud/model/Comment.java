package com.antifraud.model;

import lombok.Data;
import java.util.Date;

/**
 * 评论实体类
 * 
 * 功能说明：
 * 1. 存储评论基本信息
 * 2. 支持评论审核功能
 * 3. 支持评论回复（通过parentId实现）
 * 4. 包含点赞数统计
 * 
 * 数据表：comments
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@Data
public class Comment {
    /**
     * 评论ID（主键，自增）
     */
    private Long id;
    
    /**
     * 帖子ID
     */
    private Long postId;
    
    /**
     * 评论者ID
     */
    private Long authorId;
    
    /**
     * 评论内容
     */
    private String content;
    
    /**
     * 父评论ID（用于评论回复，null表示顶级评论）
     */
    private Long parentId;
    
    /**
     * 点赞数
     */
    private Integer likeCount;
    
    /**
     * 评论者名称
     */
    private String authorName;
    
    /**
     * 关联帖子标题
     */
    private String postTitle;
    
    /**
     * 评论状态（published/deleted）
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