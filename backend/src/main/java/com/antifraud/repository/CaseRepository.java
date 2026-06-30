package com.antifraud.repository;

import com.antifraud.model.Case;
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
 * 案例数据访问层接口
 * 
 * 功能说明：
 * 1. 提供案例数据的增删改查操作
 * 2. 支持按分类查询案例
 * 3. 支持浏览量、点赞数的统计更新
 * 
 * 数据表：cases
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@Mapper
public interface CaseRepository {

    /**
     * 插入新案例
     * 
     * @param c 案例对象
     * @return 影响的行数
     */
    @Insert("INSERT INTO cases(title, content, category, fraud_analysis, prevention_advice, cover_image, images, video_url, video_cover, author_id, view_count, like_count, created_at, updated_at) " +
            "VALUES(#{title}, #{content}, #{category}, #{fraudAnalysis}, #{preventionAdvice}, #{coverImage}, #{images}, #{videoUrl}, #{videoCover}, #{authorId}, 0, 0, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Case c);

    /**
     * 更新案例
     * 
     * @param c 案例对象
     * @return 影响的行数
     */
    @Update("UPDATE cases SET " +
            "title = #{title}, " +
            "content = #{content}, " +
            "category = #{category}, " +
            "fraud_analysis = #{fraudAnalysis}, " +
            "prevention_advice = #{preventionAdvice}, " +
            "cover_image = #{coverImage}, " +
            "images = #{images}, " +
            "video_url = #{videoUrl}, " +
            "video_cover = #{videoCover}, " +
            "updated_at = NOW() " +
            "WHERE id = #{id}")
    int update(Case c);

    /**
     * 查询所有案例（按创建时间倒序）
     * 
     * @return 案例列表
     */
    @Select("SELECT * FROM cases ORDER BY created_at DESC")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "title", column = "title"),
            @Result(property = "content", column = "content"),
            @Result(property = "category", column = "category"),
            @Result(property = "fraudAnalysis", column = "fraud_analysis"),
            @Result(property = "preventionAdvice", column = "prevention_advice"),
            @Result(property = "coverImage", column = "cover_image"),
            @Result(property = "images", column = "images"),
            @Result(property = "videoUrl", column = "video_url"),
            @Result(property = "videoCover", column = "video_cover"),
            @Result(property = "authorId", column = "author_id"),
            @Result(property = "viewCount", column = "view_count"),
            @Result(property = "likeCount", column = "like_count"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    List<Case> findAll();

    /**
     * 根据ID查询案例
     * 
     * @param id 案例ID
     * @return 案例对象，如果不存在返回null
     */
    @Select("SELECT * FROM cases WHERE id = #{id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "title", column = "title"),
            @Result(property = "content", column = "content"),
            @Result(property = "category", column = "category"),
            @Result(property = "fraudAnalysis", column = "fraud_analysis"),
            @Result(property = "preventionAdvice", column = "prevention_advice"),
            @Result(property = "coverImage", column = "cover_image"),
            @Result(property = "images", column = "images"),
            @Result(property = "videoUrl", column = "video_url"),
            @Result(property = "videoCover", column = "video_cover"),
            @Result(property = "authorId", column = "author_id"),
            @Result(property = "viewCount", column = "view_count"),
            @Result(property = "likeCount", column = "like_count"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    Case findById(Long id);

    /**
     * 根据分类查询案例（按创建时间倒序）
     * 
     * @param category 分类
     * @return 案例列表
     */
    @Select("SELECT * FROM cases WHERE category = #{category} ORDER BY created_at DESC")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "title", column = "title"),
            @Result(property = "content", column = "content"),
            @Result(property = "category", column = "category"),
            @Result(property = "fraudAnalysis", column = "fraud_analysis"),
            @Result(property = "preventionAdvice", column = "prevention_advice"),
            @Result(property = "coverImage", column = "cover_image"),
            @Result(property = "images", column = "images"),
            @Result(property = "videoUrl", column = "video_url"),
            @Result(property = "videoCover", column = "video_cover"),
            @Result(property = "authorId", column = "author_id"),
            @Result(property = "viewCount", column = "view_count"),
            @Result(property = "likeCount", column = "like_count"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    List<Case> findByCategory(String category);

    /**
     * 增加案例浏览量
     * 
     * @param id 案例ID
     */
    @Update("UPDATE cases SET view_count = view_count + 1 WHERE id = #{id}")
    void incrementViewCount(Long id);

    /**
     * 更新案例点赞数
     * 
     * @param id 案例ID
     * @param delta 增量（正数表示增加，负数表示减少）
     */
    @Update("UPDATE cases SET like_count = like_count + #{delta} WHERE id = #{id}")
    void updateLikeCount(@Param("id") Long id, @Param("delta") int delta);

    /**
     * 获取案例点赞数
     * 
     * @param id 案例ID
     * @return 点赞数
     */
    @Select("SELECT like_count FROM cases WHERE id = #{id}")
    Integer getLikeCount(Long id);

    /**
     * 根据ID删除案例
     * 
     * @param id 案例ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM cases WHERE id = #{id}")
    int deleteById(Long id);

    /**
     * 搜索案例（支持标题、内容、诈骗类型）
     * 
     * @param keyword 搜索关键词
     * @return 案例列表
     */
    @Select("SELECT * FROM cases WHERE " +
            "title LIKE CONCAT('%', #{keyword}, '%') OR " +
            "content LIKE CONCAT('%', #{keyword}, '%') OR " +
            "fraud_analysis LIKE CONCAT('%', #{keyword}, '%') " +
            "ORDER BY created_at DESC")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "title", column = "title"),
            @Result(property = "content", column = "content"),
            @Result(property = "category", column = "category"),
            @Result(property = "fraudAnalysis", column = "fraud_analysis"),
            @Result(property = "preventionAdvice", column = "prevention_advice"),
            @Result(property = "coverImage", column = "cover_image"),
            @Result(property = "images", column = "images"),
            @Result(property = "videoUrl", column = "video_url"),
            @Result(property = "videoCover", column = "video_cover"),
            @Result(property = "authorId", column = "author_id"),
            @Result(property = "viewCount", column = "view_count"),
            @Result(property = "likeCount", column = "like_count"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    List<Case> search(String keyword);

    /**
     * 分页搜索案例（支持标题、内容、诈骗类型）
     * 
     * @param keyword 搜索关键词
     * @param offset 偏移量
     * @param limit 每页数量
     * @return 案例列表
     */
    @Select("SELECT * FROM cases WHERE " +
            "title LIKE CONCAT('%', #{keyword}, '%') OR " +
            "content LIKE CONCAT('%', #{keyword}, '%') OR " +
            "fraud_analysis LIKE CONCAT('%', #{keyword}, '%') " +
            "ORDER BY created_at DESC " +
            "LIMIT #{limit} OFFSET #{offset}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "title", column = "title"),
            @Result(property = "content", column = "content"),
            @Result(property = "category", column = "category"),
            @Result(property = "fraudAnalysis", column = "fraud_analysis"),
            @Result(property = "preventionAdvice", column = "prevention_advice"),
            @Result(property = "coverImage", column = "cover_image"),
            @Result(property = "images", column = "images"),
            @Result(property = "videoUrl", column = "video_url"),
            @Result(property = "videoCover", column = "video_cover"),
            @Result(property = "authorId", column = "author_id"),
            @Result(property = "viewCount", column = "view_count"),
            @Result(property = "likeCount", column = "like_count"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    List<Case> searchWithPagination(@Param("keyword") String keyword, 
                                     @Param("offset") int offset, 
                                     @Param("limit") int limit);

    /**
     * 统计搜索结果数量
     * 
     * @param keyword 搜索关键词
     * @return 结果数量
     */
    @Select("SELECT COUNT(*) FROM cases WHERE " +
            "title LIKE CONCAT('%', #{keyword}, '%') OR " +
            "content LIKE CONCAT('%', #{keyword}, '%') OR " +
            "fraud_analysis LIKE CONCAT('%', #{keyword}, '%')")
    int countSearchResults(String keyword);
}