package com.antifraud.repository;

import com.antifraud.model.LearningRecord;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 浏览记录数据访问层接口
 * 
 * 功能说明：
 * 1. 提供浏览记录数据的增删改查操作
 * 2. 支持按用户ID查询浏览记录
 * 3. 支持按内容类型查询浏览记录
 * 4. 支持统计浏览次数
 * 
 * 数据表：learning_records
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@Mapper
public interface LearningRecordRepository {

    /**
     * 插入新浏览记录
     * 
     * @param record 浏览记录对象
     * @return 影响的行数
     */
    @Insert("INSERT INTO learning_records(user_id, content_type, content_id, title, created_at) " +
            "VALUES(#{userId}, #{contentType}, #{contentId}, #{title}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(LearningRecord record);

    /**
     * 查询用户的所有浏览记录（按时间倒序）
     * 
     * @param userId 用户ID
     * @return 浏览记录列表
     */
    @Select("SELECT * FROM learning_records WHERE user_id = #{userId} ORDER BY created_at DESC")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "contentType", column = "content_type"),
            @Result(property = "contentId", column = "content_id"),
            @Result(property = "title", column = "title"),
            @Result(property = "createdAt", column = "created_at")
    })
    List<LearningRecord> findByUserId(Long userId);

    /**
     * 分页查询用户的浏览记录（按时间倒序）
     * 
     * @param userId 用户ID
     * @param offset 偏移量
     * @param limit 每页数量
     * @return 浏览记录列表
     */
    @Select("SELECT * FROM learning_records WHERE user_id = #{userId} " +
            "ORDER BY created_at DESC LIMIT #{limit} OFFSET #{offset}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "contentType", column = "content_type"),
            @Result(property = "contentId", column = "content_id"),
            @Result(property = "title", column = "title"),
            @Result(property = "createdAt", column = "created_at")
    })
    List<LearningRecord> findByUserIdWithPagination(@Param("userId") Long userId, 
                                                   @Param("offset") int offset, 
                                                   @Param("limit") int limit);

    /**
     * 按内容类型查询用户的浏览记录（按时间倒序）
     * 
     * @param userId 用户ID
     * @param contentType 内容类型
     * @return 浏览记录列表
     */
    @Select("SELECT * FROM learning_records WHERE user_id = #{userId} AND content_type = #{contentType} " +
            "ORDER BY created_at DESC")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "contentType", column = "content_type"),
            @Result(property = "contentId", column = "content_id"),
            @Result(property = "title", column = "title"),
            @Result(property = "createdAt", column = "created_at")
    })
    List<LearningRecord> findByUserIdAndContentType(@Param("userId") Long userId, 
                                                   @Param("contentType") String contentType);

    /**
     * 统计用户的浏览次数
     * 
     * @param userId 用户ID
     * @return 浏览次数
     */
    @Select("SELECT COUNT(*) FROM learning_records WHERE user_id = #{userId}")
    int countByUserId(Long userId);

    /**
     * 统计用户对指定内容的浏览次数
     * 
     * @param userId 用户ID
     * @param contentType 内容类型
     * @param contentId 内容ID
     * @return 浏览次数
     */
    @Select("SELECT COUNT(*) FROM learning_records WHERE user_id = #{userId} " +
            "AND content_type = #{contentType} AND content_id = #{contentId}")
    int countByUserAndContent(@Param("userId") Long userId, 
                              @Param("contentType") String contentType, 
                              @Param("contentId") Long contentId);

    /**
     * 查询用户最近浏览的记录（限制数量）
     * 
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 浏览记录列表
     */
    @Select("SELECT * FROM learning_records WHERE user_id = #{userId} " +
            "ORDER BY created_at DESC LIMIT #{limit}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "contentType", column = "content_type"),
            @Result(property = "contentId", column = "content_id"),
            @Result(property = "title", column = "title"),
            @Result(property = "createdAt", column = "created_at")
    })
    List<LearningRecord> findRecentByUserId(@Param("userId") Long userId, @Param("limit") int limit);

    /**
     * 根据ID删除浏览记录
     * 
     * @param id 浏览记录ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM learning_records WHERE id = #{id}")
    int deleteById(Long id);

    /**
     * 删除用户的所有浏览记录
     * 
     * @param userId 用户ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM learning_records WHERE user_id = #{userId}")
    int deleteByUserId(Long userId);
}
