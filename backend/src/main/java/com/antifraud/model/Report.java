package com.antifraud.model;

import lombok.Data;
import java.util.Date;

/**
 * 举报实体类
 * 
 * 功能说明：
 * 1. 存储用户举报信息
 * 2. 支持多种举报类型（帖子、评论、用户等）
 * 3. 包含举报状态跟踪
 * 
 * 数据表：reports
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@Data
public class Report {
    /**
     * 举报ID（主键，自增）
     */
    private Long id;
    
    /**
     * 举报人ID
     */
    private Long reporterId;
    
    /**
     * 举报人名称
     */
    private String reporterName;
    
    /**
     * 被举报类型（POST/COMMENT/USER等）
     */
    private String reportedType;
    
    /**
     * 被举报对象ID
     */
    private Long reportedId;
    
    /**
     * 举报原因
     */
    private String reason;
    
    /**
     * 举报描述
     */
    private String description;
    
    /**
     * 举报状态
     * - PENDING: 待处理
     * - PROCESSING: 处理中
     * - RESOLVED: 已解决
     * - REJECTED: 已拒绝
     */
    private String status;
    
    /**
     * 处理结果说明
     */
    private String handleResult;
    
    /**
     * 处理人ID
     */
    private Long handlerId;
    
    /**
     * 处理时间
     */
    private Date handleTime;
    
    /**
     * 证据图片URL列表（JSON数组字符串）
     */
    private String evidenceImages;
    
    /**
     * 证据视频URL
     */
    private String evidenceVideo;
    
    /**
     * 创建时间
     */
    private Date createdAt;
    
    /**
     * 更新时间
     */
    private Date updatedAt;
    
    /**
     * 举报状态常量：待处理
     */
    public static final String STATUS_PENDING = "PENDING";
    
    /**
     * 举报状态常量：处理中
     */
    public static final String STATUS_PROCESSING = "PROCESSING";
    
    /**
     * 举报状态常量：已解决
     */
    public static final String STATUS_RESOLVED = "RESOLVED";
    
    /**
     * 举报状态常量：已拒绝
     */
    public static final String STATUS_REJECTED = "REJECTED";
}