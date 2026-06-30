package com.antifraud.dto;

import lombok.Data;

/**
 * 评论数据传输对象
 * 
 * 功能说明：
 * 1. 用于创建评论的请求参数
 * 2. 不包含统计信息（点赞数等）
 * 3. 支持评论回复（通过parentId）
 * 4. 用于前后端数据交互
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@Data
public class CommentDTO {
    /**
     * 帖子ID
     */
    private Long postId;
    
    /**
     * 评论者ID
     */
    private Long authorId;
    
    /**
     * 评论者名称
     */
    private String authorName;
    
    /**
     * 评论内容
     */
    private String content;
    
    /**
     * 父评论ID（用于评论回复，null表示顶级评论）
     */
    private Long parentId;
}