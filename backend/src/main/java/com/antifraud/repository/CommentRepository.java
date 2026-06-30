package com.antifraud.repository;

import com.antifraud.model.Comment;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 评论数据访问层接口
 * 
 * 功能说明：
 * 1. 提供评论数据的增删改查操作
 * 2. 支持按帖子ID、父评论ID、审核状态查询评论
 * 3. 支持评论点赞数更新
 * 4. 支持评论审核状态更新
 * 
 * 数据表：comments
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@Mapper
public interface CommentRepository {

    /**
     * 插入新评论
     * 
     * @param comment 评论对象
     * @return 影响的行数
     */
    @Insert("INSERT INTO comments(post_id, author_id, author_name, content, parent_id, like_count, status, audit_status, created_at, updated_at) " +
            "VALUES(#{postId}, #{authorId}, #{authorName}, #{content}, #{parentId}, 0, 'published', #{auditStatus}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Comment comment);

    /**
     * 根据ID查询评论
     * 关联users表获取最新的用户名
     * 
     * @param id 评论ID
     * @return 评论对象，如果不存在返回null
     */
    @Select("SELECT c.*, COALESCE(u.nickname, c.author_name) as author_name " +
            "FROM comments c " +
            "LEFT JOIN users u ON c.author_id = u.id " +
            "WHERE c.id = #{id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "postId", column = "post_id"),
            @Result(property = "authorId", column = "author_id"),
            @Result(property = "authorName", column = "author_name"),
            @Result(property = "content", column = "content"),
            @Result(property = "parentId", column = "parent_id"),
            @Result(property = "likeCount", column = "like_count"),
            @Result(property = "status", column = "status"),
            @Result(property = "auditStatus", column = "audit_status"),
            @Result(property = "auditRemark", column = "audit_remark"),
            @Result(property = "auditTime", column = "audit_time"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    Comment findById(Long id);

    /**
     * 根据帖子ID查询已审核通过的评论（按创建时间正序）
     * 用于前端展示
     * 关联users表获取最新的用户名
     * 
     * @param postId 帖子ID
     * @return 评论列表
     */
    @Select("SELECT c.*, COALESCE(u.nickname, c.author_name) as author_name " +
            "FROM comments c " +
            "LEFT JOIN users u ON c.author_id = u.id " +
            "WHERE c.post_id = #{postId} AND c.audit_status = 'APPROVED' " +
            "ORDER BY c.created_at ASC")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "postId", column = "post_id"),
            @Result(property = "authorId", column = "author_id"),
            @Result(property = "authorName", column = "author_name"),
            @Result(property = "content", column = "content"),
            @Result(property = "parentId", column = "parent_id"),
            @Result(property = "likeCount", column = "like_count"),
            @Result(property = "status", column = "status"),
            @Result(property = "auditStatus", column = "audit_status"),
            @Result(property = "auditRemark", column = "audit_remark"),
            @Result(property = "auditTime", column = "audit_time"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    List<Comment> findByPostId(Long postId);

    /**
     * 根据父评论ID查询子评论（按创建时间正序）
     * 用于显示评论回复
     * 关联users表获取最新的用户名
     * 
     * @param parentId 父评论ID
     * @return 子评论列表
     */
    @Select("SELECT c.*, COALESCE(u.nickname, c.author_name) as author_name " +
            "FROM comments c " +
            "LEFT JOIN users u ON c.author_id = u.id " +
            "WHERE c.parent_id = #{parentId} " +
            "ORDER BY c.created_at ASC")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "postId", column = "post_id"),
            @Result(property = "authorId", column = "author_id"),
            @Result(property = "authorName", column = "author_name"),
            @Result(property = "content", column = "content"),
            @Result(property = "parentId", column = "parent_id"),
            @Result(property = "likeCount", column = "like_count"),
            @Result(property = "status", column = "status"),
            @Result(property = "auditStatus", column = "audit_status"),
            @Result(property = "auditRemark", column = "audit_remark"),
            @Result(property = "auditTime", column = "audit_time"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    List<Comment> findByParentId(Long parentId);

    /**
     * 查询所有评论（按创建时间倒序）
     * 用于管理端展示
     * 
     * @return 评论列表
     */
    @Select("SELECT c.*, p.title as post_title, COALESCE(u.nickname, c.author_name, '未知用户') as author_name " +
            "FROM comments c " +
            "LEFT JOIN posts p ON c.post_id = p.id " +
            "LEFT JOIN users u ON c.author_id = u.id " +
            "ORDER BY c.created_at DESC")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "postId", column = "post_id"),
            @Result(property = "authorId", column = "author_id"),
            @Result(property = "authorName", column = "author_name"),
            @Result(property = "content", column = "content"),
            @Result(property = "parentId", column = "parent_id"),
            @Result(property = "likeCount", column = "like_count"),
            @Result(property = "status", column = "status"),
            @Result(property = "auditStatus", column = "audit_status"),
            @Result(property = "auditRemark", column = "audit_remark"),
            @Result(property = "auditTime", column = "audit_time"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at"),
            @Result(property = "postTitle", column = "post_title")
    })
    List<Comment> findAll();

    /**
     * 根据审核状态查询评论（按创建时间倒序）
     * 
     * @param auditStatus 审核状态
     * @return 评论列表
     */
    @Select("SELECT c.*, p.title as post_title, COALESCE(u.nickname, c.author_name, '未知用户') as author_name " +
            "FROM comments c " +
            "LEFT JOIN posts p ON c.post_id = p.id " +
            "LEFT JOIN users u ON c.author_id = u.id " +
            "WHERE c.audit_status = #{auditStatus} " +
            "ORDER BY c.created_at DESC")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "postId", column = "post_id"),
            @Result(property = "authorId", column = "author_id"),
            @Result(property = "authorName", column = "author_name"),
            @Result(property = "content", column = "content"),
            @Result(property = "parentId", column = "parent_id"),
            @Result(property = "likeCount", column = "like_count"),
            @Result(property = "status", column = "status"),
            @Result(property = "auditStatus", column = "audit_status"),
            @Result(property = "auditRemark", column = "audit_remark"),
            @Result(property = "auditTime", column = "audit_time"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at"),
            @Result(property = "postTitle", column = "post_title")
    })
    List<Comment> findByAuditStatus(String auditStatus);

    /**
     * 增加评论点赞数
     * 
     * @param id 评论ID
     * @return 影响的行数
     */
    @Update("UPDATE comments SET like_count = like_count + 1 WHERE id = #{id}")
    int incrementLikeCount(Long id);

    /**
     * 更新评论点赞数
     * 
     * @param id 评论ID
     * @param delta 变化量（+1或-1）
     * @return 影响的行数
     */
    @Update("UPDATE comments SET like_count = like_count + #{delta} WHERE id = #{id}")
    int updateLikeCount(@Param("id") Long id, @Param("delta") int delta);

    /**
     * 获取评论点赞数
     * 
     * @param id 评论ID
     * @return 点赞数
     */
    @Select("SELECT like_count FROM comments WHERE id = #{id}")
    Integer getLikeCount(Long id);

    /**
     * 更新评论审核状态
     * 
     * @param id 评论ID
     * @param auditStatus 审核状态
     * @param auditRemark 审核备注
     * @return 影响的行数
     */
    @Update("UPDATE comments SET audit_status = #{auditStatus}, audit_remark = #{auditRemark}, audit_time = NOW() WHERE id = #{id}")
    int updateAuditStatus(@Param("id") Long id, @Param("auditStatus") String auditStatus, @Param("auditRemark") String auditRemark);

    /**
     * 根据ID删除评论
     * 
     * @param id 评论ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM comments WHERE id = #{id}")
    int deleteById(Long id);

    /**
     * 根据帖子ID删除该帖子的所有评论
     * 
     * @param postId 帖子ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM comments WHERE post_id = #{postId}")
    int deleteByPostId(Long postId);
}