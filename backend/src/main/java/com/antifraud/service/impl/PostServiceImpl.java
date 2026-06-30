package com.antifraud.service.impl;

import com.antifraud.model.Post;
import com.antifraud.dto.PostDTO;
import com.antifraud.service.PostService;
import com.antifraud.repository.PostRepository;
import com.antifraud.repository.CommentRepository;
import com.antifraud.repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 帖子服务实现类
 * 
 * 功能说明：
 * 1. 实现帖子的增删改查功能
 * 2. 支持内容审核功能
 * 3. 支持按作者、审核状态查询
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private LikeRepository likeRepository;

    /**
     * 创建帖子（默认审核通过）
     * 
     * @param postDTO 帖子数据传输对象
     * @return 创建的帖子对象
     */
    @Override
    public Post create(PostDTO postDTO) {
        return createWithAudit(postDTO, Post.AUDIT_APPROVED);
    }

    /**
     * 创建帖子并指定审核状态
     * 
     * 功能说明：
     * - 创建帖子对象
     * - 设置标题、内容、作者等信息
     * - 设置审核状态
     * - 保存到数据库
     * 
     * @param postDTO 帖子数据传输对象
     * @param auditStatus 审核状态
     * @return 创建的帖子对象
     */
    @Override
    public Post createWithAudit(PostDTO postDTO, String auditStatus) {
        Post post = new Post();
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        post.setAuthorId(postDTO.getAuthorId());
        post.setAuthorName(postDTO.getAuthorName());
        post.setCategory(postDTO.getCategory());
        post.setAuditStatus(auditStatus);

        postRepository.insert(post);
        return post;
    }

    /**
     * 根据ID查询帖子
     * 
     * @param id 帖子ID
     * @return 帖子对象，不存在返回null
     */
    @Override
    public Post findById(Long id) {
        return postRepository.findById(id);
    }

    /**
     * 查询所有帖子（只返回审核通过的）
     * 
     * @return 帖子列表
     */
    @Override
    public List<Post> findAll() {
        return postRepository.findAll();
    }
    
    /**
     * 查询所有帖子（只返回审核通过的），支持排序
     * 
     * @param sort 排序方式：latest（最新）、hot（热门）
     * @return 帖子列表
     */
    @Override
    public List<Post> findAll(String sort) {
        return postRepository.findAllWithSort(sort);
    }
    
    /**
     * 根据分类查询帖子（只返回审核通过的），支持排序
     * 
     * @param category 帖子分类
     * @param sort 排序方式：latest（最新）、hot（热门）
     * @return 帖子列表
     */
    @Override
    public List<Post> findByCategory(String category, String sort) {
        return postRepository.findByCategory(category, sort);
    }

    /**
     * 查询所有帖子（管理员使用）
     * 
     * @return 所有帖子列表
     */
    @Override
    public List<Post> findAllForAdmin() {
        return postRepository.findAllForAdmin();
    }

    /**
     * 根据作者ID查询帖子
     * 
     * @param authorId 作者ID
     * @return 该作者的帖子列表
     */
    @Override
    public List<Post> findByAuthorId(Long authorId) {
        return postRepository.findByAuthorId(authorId);
    }

    /**
     * 根据审核状态查询帖子
     * 
     * @param auditStatus 审核状态
     * @return 指定状态的帖子列表
     */
    @Override
    public List<Post> findByAuditStatus(String auditStatus) {
        return postRepository.findByAuditStatus(auditStatus);
    }

    /**
     * 增加帖子浏览量
     * 
     * @param id 帖子ID
     */
    @Override
    public void incrementViewCount(Long id) {
        postRepository.incrementViewCount(id);
    }

    /**
     * 增加帖子评论数
     * 
     * @param id 帖子ID
     */
    @Override
    public void incrementCommentCount(Long id) {
        postRepository.incrementCommentCount(id);
    }

    /**
     * 减少帖子评论数
     * 
     * @param id 帖子ID
     */
    @Override
    public void decrementCommentCount(Long id) {
        postRepository.decrementCommentCount(id);
    }

    /**
     * 更新帖子审核状态
     * 
     * @param id 帖子ID
     * @param auditStatus 审核状态
     * @param auditRemark 审核备注
     */
    @Override
    public void updateAuditStatus(Long id, String auditStatus, String auditRemark) {
        postRepository.updateAuditStatus(id, auditStatus, auditRemark);
    }

    /**
     * 删除帖子（级联删除关联的评论和点赞）
     * 
     * @param id 帖子ID
     */
    @Override
    public void delete(Long id) {
        commentRepository.deleteByPostId(id);
        likeRepository.deleteByTargetIdAndTargetType(id, "post");
        postRepository.deleteById(id);
    }
}
