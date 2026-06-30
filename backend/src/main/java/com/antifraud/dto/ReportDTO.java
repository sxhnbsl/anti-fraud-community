package com.antifraud.dto;

import lombok.Data;

/**
 * 举报数据传输对象
 * 
 * 功能说明：
 * 1. 用于创建举报的请求参数
 * 2. 支持多种举报类型（帖子、评论、用户等）
 * 3. 用于前后端数据交互
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@Data
public class ReportDTO {
    /**
     * 举报人ID
     */
    private Long reporterId;
    
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
}