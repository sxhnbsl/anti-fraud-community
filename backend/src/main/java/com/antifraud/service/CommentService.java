package com.antifraud.service;

import com.antifraud.model.Comment;
import com.antifraud.dto.CommentDTO;
import java.util.List;

/**
 * 评论服务接口
 * 
 * 功能说明：
 * 1. 提供评论的增删改查功能
 * 2. 支持内容审核功能
 * 3. 支持按帖子、父评论查询
 * 4. 支持点赞数统计
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
public interface CommentService {
    
    /**
     * 创建评论（默认审核通过）
     * 
     * @param commentDTO 评论数据传输对象
     * @return 创建的评论对象
     */
    Comment create(CommentDTO commentDTO);
    
    /**
     * 创建评论并指定审核状态
     * 
     * @param commentDTO 评论数据传输对象
     * @param auditStatus 审核状态（APPROVED/PENDING/REJECTED）
     * @return 创建的评论对象
     */
    Comment createWithAudit(CommentDTO commentDTO, String auditStatus);
    
    /**
     * 根据ID查询评论
     * 
     * @param id 评论ID
     * @return 评论对象，不存在返回null
     */
    Comment findById(Long id);
    
    /**
     * 根据帖子ID查询评论列表
     * 
     * @param postId 帖子ID
     * @return 该帖子的评论列表
     */
    List<Comment> findByPostId(Long postId);
    
    /**
     * 根据父评论ID查询回复列表
     * 
     * @param parentId 父评论ID
     * @return 回复列表
     */
    List<Comment> findByParentId(Long parentId);
    
    /**
     * 查询所有评论（管理员使用）
     * 
     * @return 所有评论列表
     */
    List<Comment> findAll();
    
    /**
     * 根据审核状态查询评论
     * 
     * @param auditStatus 审核状态
     * @return 指定状态的评论列表
     */
    List<Comment> findByAuditStatus(String auditStatus);
    
    /**
     * 增加评论点赞数
     * 
     * @param id 评论ID
     */
    void incrementLikeCount(Long id);
    
    /**
     * 更新评论审核状态
     * 
     * @param id 评论ID
     * @param auditStatus 审核状态
     * @param auditRemark 审核备注
     */
    void updateAuditStatus(Long id, String auditStatus, String auditRemark);
    
    /**
     * 删除评论
     * 
     * @param id 评论ID
     */
    void delete(Long id);
}
