package com.antifraud.model;

import lombok.Data;
import java.util.Date;

/**
 * 浏览记录实体类
 * 
 * 功能说明：
 * 1. 存储用户浏览记录
 * 2. 支持多种内容类型（知识、案例、帖子）
 * 
 * 数据表：learning_records
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@Data
public class LearningRecord {
    /**
     * 浏览记录ID（主键，自增）
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 内容类型
     * - knowledge: 知识
     * - case: 案例
     * - post: 帖子
     */
    private String contentType;
    
    /**
     * 内容ID
     */
    private Long contentId;
    
    /**
     * 内容标题（冗余字段，方便查询）
     */
    private String title;
    
    /**
     * 浏览时间
     */
    private Date createdAt;
}
