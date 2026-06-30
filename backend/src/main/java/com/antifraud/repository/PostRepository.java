package com.antifraud.repository;

import com.antifraud.model.Post;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 帖子数据访问层接口
 * 
 * 功能说明：
 * 1. 提供帖子数据的增删改查操作
 * 2. 支持按作者ID、审核状态查询帖子
 * 3. 支持浏览量、点赞数、评论数的统计更新
 * 4. 支持帖子审核状态更新
 * 
 * 数据表：posts
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@Mapper
public interface PostRepository {

    /**
     * 插入新帖子
     * 
     * @param post 帖子对象
     * @return 影响的行数
     */
    @Insert("INSERT INTO posts(title, content, author_id, author_name, category, view_count, like_count, comment_count, audit_status, created_at, updated_at) " +
            "VALUES(#{title}, #{content}, #{authorId}, #{authorName}, #{category}, 0, 0, 0, #{auditStatus}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Post post);

    /**
     * 查询所有已审核通过的帖子（按创建时间倒序）
     * 用于前端展示
     * 关联users表获取最新的用户名
     * 
     * @return 帖子列表
     */
    @Select("SELECT p.*, COALESCE(u.nickname, p.author_name) as author_name " +
            "FROM posts p " +
            "LEFT JOIN users u ON p.author_id = u.id " +
            "WHERE p.audit_status = 'APPROVED' " +
            "ORDER BY p.created_at DESC")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "title", column = "title"),
            @Result(property = "content", column = "content"),
            @Result(property = "authorId", column = "author_id"),
            @Result(property = "authorName", column = "author_name"),
            @Result(property = "category", column = "category"),
            @Result(property = "viewCount", column = "view_count"),
            @Result(property = "likeCount", column = "like_count"),
            @Result(property = "commentCount", column = "comment_count"),
            @Result(property = "auditStatus", column = "audit_status"),
            @Result(property = "auditRemark", column = "audit_remark"),
            @Result(property = "auditTime", column = "audit_time"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    List<Post> findAll();

    /**
     * 查询所有帖子（包括未审核的，按创建时间倒序）
     * 用于管理端展示
     * 关联users表获取最新的用户名
     * 
     * @return 帖子列表
     */
    @Select("SELECT p.*, COALESCE(u.nickname, p.author_name) as author_name " +
            "FROM posts p " +
            "LEFT JOIN users u ON p.author_id = u.id " +
            "ORDER BY p.created_at DESC")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "title", column = "title"),
            @Result(property = "content", column = "content"),
            @Result(property = "authorId", column = "author_id"),
            @Result(property = "authorName", column = "author_name"),
            @Result(property = "category", column = "category"),
            @Result(property = "viewCount", column = "view_count"),
            @Result(property = "likeCount", column = "like_count"),
            @Result(property = "commentCount", column = "comment_count"),
            @Result(property = "auditStatus", column = "audit_status"),
            @Result(property = "auditRemark", column = "audit_remark"),
            @Result(property = "auditTime", column = "audit_time"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    List<Post> findAllForAdmin();

    /**
     * 根据ID查询帖子
     * 关联users表获取最新的用户名
     * 
     * @param id 帖子ID
     * @return 帖子对象，如果不存在返回null
     */
    @Select("SELECT p.*, COALESCE(u.nickname, p.author_name) as author_name " +
            "FROM posts p " +
            "LEFT JOIN users u ON p.author_id = u.id " +
            "WHERE p.id = #{id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "title", column = "title"),
            @Result(property = "content", column = "content"),
            @Result(property = "authorId", column = "author_id"),
            @Result(property = "authorName", column = "author_name"),
            @Result(property = "category", column = "category"),
            @Result(property = "viewCount", column = "view_count"),
            @Result(property = "likeCount", column = "like_count"),
            @Result(property = "commentCount", column = "comment_count"),
            @Result(property = "auditStatus", column = "audit_status"),
            @Result(property = "auditRemark", column = "audit_remark"),
            @Result(property = "auditTime", column = "audit_time"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    Post findById(Long id);

    /**
     * 根据作者ID查询帖子（按创建时间倒序）
     * 关联users表获取最新的用户名
     * 
     * @param authorId 作者ID
     * @return 帖子列表
     */
    @Select("SELECT p.*, COALESCE(u.nickname, p.author_name) as author_name " +
            "FROM posts p " +
            "LEFT JOIN users u ON p.author_id = u.id " +
            "WHERE p.author_id = #{authorId} " +
            "ORDER BY p.created_at DESC")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "title", column = "title"),
            @Result(property = "content", column = "content"),
            @Result(property = "authorId", column = "author_id"),
            @Result(property = "authorName", column = "author_name"),
            @Result(property = "category", column = "category"),
            @Result(property = "viewCount", column = "view_count"),
            @Result(property = "likeCount", column = "like_count"),
            @Result(property = "commentCount", column = "comment_count"),
            @Result(property = "auditStatus", column = "audit_status"),
            @Result(property = "auditRemark", column = "audit_remark"),
            @Result(property = "auditTime", column = "audit_time"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    List<Post> findByAuthorId(Long authorId);

    /**
     * 根据审核状态查询帖子（按创建时间倒序）
     * 关联users表获取最新的用户名
     * 
     * @param auditStatus 审核状态
     * @return 帖子列表
     */
    @Select("SELECT p.*, COALESCE(u.nickname, p.author_name) as author_name " +
            "FROM posts p " +
            "LEFT JOIN users u ON p.author_id = u.id " +
            "WHERE p.audit_status = #{auditStatus} " +
            "ORDER BY p.created_at DESC")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "title", column = "title"),
            @Result(property = "content", column = "content"),
            @Result(property = "authorId", column = "author_id"),
            @Result(property = "authorName", column = "author_name"),
            @Result(property = "category", column = "category"),
            @Result(property = "viewCount", column = "view_count"),
            @Result(property = "likeCount", column = "like_count"),
            @Result(property = "commentCount", column = "comment_count"),
            @Result(property = "auditStatus", column = "audit_status"),
            @Result(property = "auditRemark", column = "audit_remark"),
            @Result(property = "auditTime", column = "audit_time"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    List<Post> findByAuditStatus(String auditStatus);

    /**
     * 增加帖子浏览量
     * 
     * @param id 帖子ID
     */
    @Update("UPDATE posts SET view_count = view_count + 1 WHERE id = #{id}")
    void incrementViewCount(Long id);

    /**
     * 更新帖子点赞数
     * 
     * @param id 帖子ID
     * @param delta 增量（正数表示增加，负数表示减少）
     */
    @Update("UPDATE posts SET like_count = like_count + #{delta} WHERE id = #{id}")
    void updateLikeCount(@Param("id") Long id, @Param("delta") int delta);

    /**
     * 获取帖子点赞数
     * 
     * @param id 帖子ID
     * @return 点赞数
     */
    @Select("SELECT like_count FROM posts WHERE id = #{id}")
    Integer getLikeCount(Long id);

    /**
     * 增加帖子评论数
     * 
     * @param id 帖子ID
     */
    @Update("UPDATE posts SET comment_count = comment_count + 1 WHERE id = #{id}")
    void incrementCommentCount(Long id);

    /**
     * 减少帖子评论数
     * 
     * @param id 帖子ID
     */
    @Update("UPDATE posts SET comment_count = comment_count - 1 WHERE id = #{id}")
    void decrementCommentCount(Long id);

    /**
     * 更新帖子审核状态
     * 
     * @param id 帖子ID
     * @param auditStatus 审核状态
     * @param auditRemark 审核备注
     * @return 影响的行数
     */
    @Update("UPDATE posts SET audit_status = #{auditStatus}, audit_remark = #{auditRemark}, audit_time = NOW() WHERE id = #{id}")
    int updateAuditStatus(@Param("id") Long id, @Param("auditStatus") String auditStatus, @Param("auditRemark") String auditRemark);

    /**
     * 根据ID删除帖子
     * 
     * @param id 帖子ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM posts WHERE id = #{id}")
    int deleteById(Long id);
    
    /**
     * 查询所有已审核通过的帖子，支持排序
     * 关联users表获取最新的用户名
     * 
     * @param sort 排序方式：latest（最新）、hot（热门）
     * @return 帖子列表
     */
    @Select("SELECT p.*, COALESCE(u.nickname, p.author_name) as author_name " +
            "FROM posts p " +
            "LEFT JOIN users u ON p.author_id = u.id " +
            "WHERE p.audit_status = 'APPROVED' AND (p.like_count > 10 OR #{sort} != 'hot') " +
            "ORDER BY CASE WHEN #{sort} = 'latest' THEN p.created_at END DESC, " +
            "CASE WHEN #{sort} = 'hot' THEN p.like_count END DESC, p.created_at DESC")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "title", column = "title"),
            @Result(property = "content", column = "content"),
            @Result(property = "authorId", column = "author_id"),
            @Result(property = "authorName", column = "author_name"),
            @Result(property = "category", column = "category"),
            @Result(property = "viewCount", column = "view_count"),
            @Result(property = "likeCount", column = "like_count"),
            @Result(property = "commentCount", column = "comment_count"),
            @Result(property = "auditStatus", column = "audit_status"),
            @Result(property = "auditRemark", column = "audit_remark"),
            @Result(property = "auditTime", column = "audit_time"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    List<Post> findAllWithSort(String sort);
    
    /**
     * 根据分类查询已审核通过的帖子，支持排序
     * 关联users表获取最新的用户名
     * 
     * @param category 帖子分类
     * @param sort 排序方式：latest（最新）、hot（热门）
     * @return 帖子列表
     */
    @Select("SELECT p.*, COALESCE(u.nickname, p.author_name) as author_name " +
            "FROM posts p " +
            "LEFT JOIN users u ON p.author_id = u.id " +
            "WHERE p.audit_status = 'APPROVED' AND p.category = #{category} AND (p.like_count > 10 OR #{sort} != 'hot') " +
            "ORDER BY CASE WHEN #{sort} = 'latest' THEN p.created_at END DESC, " +
            "CASE WHEN #{sort} = 'hot' THEN p.like_count END DESC, p.created_at DESC")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "title", column = "title"),
            @Result(property = "content", column = "content"),
            @Result(property = "authorId", column = "author_id"),
            @Result(property = "authorName", column = "author_name"),
            @Result(property = "category", column = "category"),
            @Result(property = "viewCount", column = "view_count"),
            @Result(property = "likeCount", column = "like_count"),
            @Result(property = "commentCount", column = "comment_count"),
            @Result(property = "auditStatus", column = "audit_status"),
            @Result(property = "auditRemark", column = "audit_remark"),
            @Result(property = "auditTime", column = "audit_time"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    List<Post> findByCategory(@Param("category") String category, @Param("sort") String sort);
}