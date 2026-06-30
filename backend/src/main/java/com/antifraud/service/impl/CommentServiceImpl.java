package com.antifraud.service.impl;

import com.antifraud.model.Comment;
import com.antifraud.dto.CommentDTO;
import com.antifraud.repository.CommentRepository;
import com.antifraud.service.CommentService;
import com.antifraud.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 评论服务实现类
 * 
 * 功能说明：
 * 1. 实现评论的增删改查功能
 * 2. 支持内容审核功能
 * 3. 支持按帖子、父评论查询
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private PostService postService;

    /**
     * 创建评论（默认审核通过）
     * 
     * @param commentDTO 评论数据传输对象
     * @return 创建的评论对象
     */
    @Override
    public Comment create(CommentDTO commentDTO) {
        return createWithAudit(commentDTO, Comment.AUDIT_APPROVED);
    }

    /**
     * 创建评论并指定审核状态
     * 
     * 功能说明：
     * - 创建评论对象
     * - 设置帖子ID、作者ID、作者名称、内容等信息
     * - 设置审核状态
     * - 保存到数据库
     * - 如果审核通过，增加帖子评论数
     * 
     * @param commentDTO 评论数据传输对象
     * @param auditStatus 审核状态
     * @return 创建的评论对象
     */
    @Override
    public Comment createWithAudit(CommentDTO commentDTO, String auditStatus) {
        Comment comment = new Comment();
        comment.setPostId(commentDTO.getPostId());
        comment.setAuthorId(commentDTO.getAuthorId());
        comment.setAuthorName(commentDTO.getAuthorName());
        comment.setContent(commentDTO.getContent());
        comment.setParentId(commentDTO.getParentId());
        comment.setAuditStatus(auditStatus);
        commentRepository.insert(comment);
        
        // 如果审核通过，增加帖子评论数
        if (Comment.AUDIT_APPROVED.equals(auditStatus)) {
            postService.incrementCommentCount(commentDTO.getPostId());
        }
        
        return comment;
    }

    /**
     * 根据ID查询评论
     * 
     * @param id 评论ID
     * @return 评论对象，不存在返回null
     */
    @Override
    public Comment findById(Long id) {
        return commentRepository.findById(id);
    }

    /**
     * 根据帖子ID查询评论列表
     * 
     * @param postId 帖子ID
     * @return 该帖子的评论列表
     */
    @Override
    public List<Comment> findByPostId(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    /**
     * 根据父评论ID查询回复列表
     * 
     * @param parentId 父评论ID
     * @return 回复列表
     */
    @Override
    public List<Comment> findByParentId(Long parentId) {
        return commentRepository.findByParentId(parentId);
    }

    /**
     * 查询所有评论（管理员使用）
     * 
     * @return 所有评论列表
     */
    @Override
    public List<Comment> findAll() {
        return commentRepository.findAll();
    }

    /**
     * 根据审核状态查询评论
     * 
     * @param auditStatus 审核状态
     * @return 指定状态的评论列表
     */
    @Override
    public List<Comment> findByAuditStatus(String auditStatus) {
        return commentRepository.findByAuditStatus(auditStatus);
    }

    /**
     * 增加评论点赞数
     * 
     * @param id 评论ID
     */
    @Override
    public void incrementLikeCount(Long id) {
        commentRepository.incrementLikeCount(id);
    }

    /**
     * 更新评论审核状态
     * 
     * @param id 评论ID
     * @param auditStatus 审核状态
     * @param auditRemark 审核备注
     */
    @Override
    public void updateAuditStatus(Long id, String auditStatus, String auditRemark) {
        // 获取原评论信息，检查是否是从待审核状态变为审核通过
        Comment comment = commentRepository.findById(id);
        if (comment != null && Comment.AUDIT_PENDING.equals(comment.getAuditStatus()) && Comment.AUDIT_APPROVED.equals(auditStatus)) {
            // 从待审核变为审核通过，增加帖子评论数
            postService.incrementCommentCount(comment.getPostId());
        }
        commentRepository.updateAuditStatus(id, auditStatus, auditRemark);
    }

    /**
     * 删除评论
     * 
     * @param id 评论ID
     */
    @Override
    public void delete(Long id) {
        commentRepository.deleteById(id);
    }
}
