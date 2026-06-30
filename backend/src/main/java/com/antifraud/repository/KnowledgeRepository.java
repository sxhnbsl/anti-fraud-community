package com.antifraud.repository;

import com.antifraud.model.Knowledge;
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
 * 知识库数据访问层接口
 * 
 * 功能说明：
 * 1. 提供知识库数据的增删改查操作
 * 2. 支持按分类、标签查询知识库
 * 3. 支持浏览量、点赞数的统计更新
 * 
 * 数据表：knowledge
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@Mapper
public interface KnowledgeRepository {

    /**
     * 插入新知识库条目
     * 
     * @param knowledge 知识库对象
     * @return 影响的行数
     */
    @Insert("INSERT INTO knowledge(title, content, category, tags, author_id, cover_image, images, video_url, video_cover, view_count, like_count, created_at, updated_at) " +
            "VALUES(#{title}, #{content}, #{category}, #{tags}, #{authorId}, #{coverImage}, #{images}, #{videoUrl}, #{videoCover}, 0, 0, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Knowledge knowledge);

    /**
     * 更新知识库条目
     * 
     * @param knowledge 知识库对象
     * @return 影响的行数
     */
    @Update("UPDATE knowledge SET " +
            "title = #{title}, " +
            "content = #{content}, " +
            "category = #{category}, " +
            "tags = #{tags}, " +
            "cover_image = #{coverImage}, " +
            "images = #{images}, " +
            "video_url = #{videoUrl}, " +
            "video_cover = #{videoCover}, " +
            "updated_at = NOW() " +
            "WHERE id = #{id}")
    int update(Knowledge knowledge);

    /**
     * 查询所有知识库条目（按创建时间倒序）
     * 
     * @return 知识库列表
     */
    @Select("SELECT * FROM knowledge ORDER BY created_at DESC")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "title", column = "title"),
            @Result(property = "content", column = "content"),
            @Result(property = "category", column = "category"),
            @Result(property = "tags", column = "tags"),
            @Result(property = "authorId", column = "author_id"),
            @Result(property = "coverImage", column = "cover_image"),
            @Result(property = "images", column = "images"),
            @Result(property = "videoUrl", column = "video_url"),
            @Result(property = "videoCover", column = "video_cover"),
            @Result(property = "viewCount", column = "view_count"),
            @Result(property = "likeCount", column = "like_count"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    List<Knowledge> findAll();

    /**
     * 根据ID查询知识库条目
     * 
     * @param id 知识库ID
     * @return 知识库对象，如果不存在返回null
     */
    @Select("SELECT * FROM knowledge WHERE id = #{id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "title", column = "title"),
            @Result(property = "content", column = "content"),
            @Result(property = "category", column = "category"),
            @Result(property = "tags", column = "tags"),
            @Result(property = "authorId", column = "author_id"),
            @Result(property = "coverImage", column = "cover_image"),
            @Result(property = "images", column = "images"),
            @Result(property = "videoUrl", column = "video_url"),
            @Result(property = "videoCover", column = "video_cover"),
            @Result(property = "viewCount", column = "view_count"),
            @Result(property = "likeCount", column = "like_count"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    Knowledge findById(Long id);

    /**
     * 根据分类查询知识库条目（按创建时间倒序）
     * 
     * @param category 分类
     * @return 知识库列表
     */
    @Select("SELECT * FROM knowledge WHERE category = #{category} ORDER BY created_at DESC")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "title", column = "title"),
            @Result(property = "content", column = "content"),
            @Result(property = "category", column = "category"),
            @Result(property = "tags", column = "tags"),
            @Result(property = "authorId", column = "author_id"),
            @Result(property = "coverImage", column = "cover_image"),
            @Result(property = "images", column = "images"),
            @Result(property = "videoUrl", column = "video_url"),
            @Result(property = "videoCover", column = "video_cover"),
            @Result(property = "viewCount", column = "view_count"),
            @Result(property = "likeCount", column = "like_count"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    List<Knowledge> findByCategory(String category);

    /**
     * 根据标签查询知识库条目（按创建时间倒序）
     * 
     * @param tag 标签
     * @return 知识库列表
     */
    @Select("SELECT * FROM knowledge WHERE tags LIKE CONCAT('%', #{tag}, '%') ORDER BY created_at DESC")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "title", column = "title"),
            @Result(property = "content", column = "content"),
            @Result(property = "category", column = "category"),
            @Result(property = "tags", column = "tags"),
            @Result(property = "authorId", column = "author_id"),
            @Result(property = "coverImage", column = "cover_image"),
            @Result(property = "images", column = "images"),
            @Result(property = "videoUrl", column = "video_url"),
            @Result(property = "videoCover", column = "video_cover"),
            @Result(property = "viewCount", column = "view_count"),
            @Result(property = "likeCount", column = "like_count"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    List<Knowledge> findByTag(String tag);

    /**
     * 增加知识库浏览量
     * 
     * @param id 知识库ID
     */
    @Update("UPDATE knowledge SET view_count = view_count + 1 WHERE id = #{id}")
    void incrementViewCount(Long id);

    /**
     * 更新知识库点赞数
     * 
     * @param id 知识库ID
     * @param delta 增量（正数表示增加，负数表示减少）
     */
    @Update("UPDATE knowledge SET like_count = like_count + #{delta} WHERE id = #{id}")
    void updateLikeCount(@Param("id") Long id, @Param("delta") int delta);

    /**
     * 获取知识库点赞数
     * 
     * @param id 知识库ID
     * @return 点赞数
     */
    @Select("SELECT like_count FROM knowledge WHERE id = #{id}")
    Integer getLikeCount(Long id);

    /**
     * 根据ID删除知识库条目
     * 
     * @param id 知识库ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM knowledge WHERE id = #{id}")
    int deleteById(Long id);

    /**
     * 搜索知识库（支持标题、内容、标签）
     * 
     * @param keyword 搜索关键词
     * @return 知识库列表
     */
    @Select("SELECT * FROM knowledge WHERE " +
            "title LIKE CONCAT('%', #{keyword}, '%') OR " +
            "content LIKE CONCAT('%', #{keyword}, '%') OR " +
            "tags LIKE CONCAT('%', #{keyword}, '%') " +
            "ORDER BY created_at DESC")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "title", column = "title"),
            @Result(property = "content", column = "content"),
            @Result(property = "category", column = "category"),
            @Result(property = "tags", column = "tags"),
            @Result(property = "authorId", column = "author_id"),
            @Result(property = "coverImage", column = "cover_image"),
            @Result(property = "images", column = "images"),
            @Result(property = "videoUrl", column = "video_url"),
            @Result(property = "videoCover", column = "video_cover"),
            @Result(property = "viewCount", column = "view_count"),
            @Result(property = "likeCount", column = "like_count"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    List<Knowledge> search(String keyword);

    /**
     * 分页搜索知识库（支持标题、内容、标签）
     * 
     * @param keyword 搜索关键词
     * @param offset 偏移量
     * @param limit 每页数量
     * @return 知识库列表
     */
    @Select("SELECT * FROM knowledge WHERE " +
            "title LIKE CONCAT('%', #{keyword}, '%') OR " +
            "content LIKE CONCAT('%', #{keyword}, '%') OR " +
            "tags LIKE CONCAT('%', #{keyword}, '%') " +
            "ORDER BY created_at DESC " +
            "LIMIT #{limit} OFFSET #{offset}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "title", column = "title"),
            @Result(property = "content", column = "content"),
            @Result(property = "category", column = "category"),
            @Result(property = "tags", column = "tags"),
            @Result(property = "authorId", column = "author_id"),
            @Result(property = "coverImage", column = "cover_image"),
            @Result(property = "images", column = "images"),
            @Result(property = "videoUrl", column = "video_url"),
            @Result(property = "videoCover", column = "video_cover"),
            @Result(property = "viewCount", column = "view_count"),
            @Result(property = "likeCount", column = "like_count"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    List<Knowledge> searchWithPagination(@Param("keyword") String keyword, 
                                         @Param("offset") int offset, 
                                         @Param("limit") int limit);

    /**
     * 统计搜索结果数量
     * 
     * @param keyword 搜索关键词
     * @return 结果数量
     */
    @Select("SELECT COUNT(*) FROM knowledge WHERE " +
            "title LIKE CONCAT('%', #{keyword}, '%') OR " +
            "content LIKE CONCAT('%', #{keyword}, '%') OR " +
            "tags LIKE CONCAT('%', #{keyword}, '%')")
    int countSearchResults(String keyword);
}