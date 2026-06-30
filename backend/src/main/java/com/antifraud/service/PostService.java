package com.antifraud.service;

import com.antifraud.model.Post;
import com.antifraud.dto.PostDTO;
import java.util.List;

/**
 * 帖子服务接口
 * 
 * 功能说明：
 * 1. 提供帖子的增删改查功能
 * 2. 支持内容审核功能
 * 3. 支持按作者、审核状态查询
 * 4. 支持浏览量、评论数统计
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
public interface PostService {
    
    /**
     * 创建帖子（默认审核通过）
     * 
     * @param postDTO 帖子数据传输对象
     * @return 创建的帖子对象
     */
    Post create(PostDTO postDTO);
    
    /**
     * 创建帖子并指定审核状态
     * 
     * @param postDTO 帖子数据传输对象
     * @param auditStatus 审核状态（APPROVED/PENDING/REJECTED）
     * @return 创建的帖子对象
     */
    Post createWithAudit(PostDTO postDTO, String auditStatus);
    
    /**
     * 根据ID查询帖子
     * 
     * @param id 帖子ID
     * @return 帖子对象，不存在返回null
     */
    Post findById(Long id);
    
    /**
     * 查询所有帖子（只返回审核通过的）
     * 
     * @return 帖子列表
     */
    List<Post> findAll();
    
    /**
     * 查询所有帖子（只返回审核通过的），支持排序
     * 
     * @param sort 排序方式：latest（最新）、hot（热门）
     * @return 帖子列表
     */
    List<Post> findAll(String sort);
    
    /**
     * 根据分类查询帖子（只返回审核通过的），支持排序
     * 
     * @param category 帖子分类
     * @param sort 排序方式：latest（最新）、hot（热门）
     * @return 帖子列表
     */
    List<Post> findByCategory(String category, String sort);
    
    /**
     * 查询所有帖子（管理员使用，包括未审核的）
     * 
     * @return 所有帖子列表
     */
    List<Post> findAllForAdmin();
    
    /**
     * 根据作者ID查询帖子
     * 
     * @param authorId 作者ID
     * @return 该作者的帖子列表
     */
    List<Post> findByAuthorId(Long authorId);
    
    /**
     * 根据审核状态查询帖子
     * 
     * @param auditStatus 审核状态
     * @return 指定状态的帖子列表
     */
    List<Post> findByAuditStatus(String auditStatus);
    
    /**
     * 增加帖子浏览量
     * 
     * @param id 帖子ID
     */
    void incrementViewCount(Long id);
    
    /**
     * 增加帖子评论数
     * 
     * @param id 帖子ID
     */
    void incrementCommentCount(Long id);
    
    /**
     * 减少帖子评论数
     * 
     * @param id 帖子ID
     */
    void decrementCommentCount(Long id);
    
    /**
     * 更新帖子审核状态
     * 
     * @param id 帖子ID
     * @param auditStatus 审核状态
     * @param auditRemark 审核备注
     */
    void updateAuditStatus(Long id, String auditStatus, String auditRemark);
    
    /**
     * 删除帖子
     * 
     * @param id 帖子ID
     */
    void delete(Long id);
}
