package com.antifraud.repository;

import com.antifraud.model.ChatMessage;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 聊天历史数据访问层接口
 * 
 * 功能说明：
 * 1. 提供聊天消息的增删改查操作
 * 2. 支持按用户ID查询
 * 3. 支持按会话ID查询
 * 4. 支持分页查询
 * 
 * 数据表：chat_history
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@Mapper
public interface ChatHistoryRepository {
    
    /**
     * 插入聊天消息
     * 
     * @param chatMessage 聊天消息对象
     * @return 影响的行数
     */
    @Insert("INSERT INTO chat_history(user_id, session_id, role, content, created_at) " +
            "VALUES(#{userId}, #{sessionId}, #{role}, #{content}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ChatMessage chatMessage);
    
    /**
     * 根据用户ID查询聊天历史
     * 
     * @param userId 用户ID
     * @return 聊天消息列表
     */
    @Select("SELECT * FROM chat_history WHERE user_id = #{userId} ORDER BY created_at DESC")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "sessionId", column = "session_id"),
            @Result(property = "role", column = "role"),
            @Result(property = "content", column = "content"),
            @Result(property = "createdAt", column = "created_at")
    })
    List<ChatMessage> findByUserId(Long userId);
    
    /**
     * 根据用户ID和会话ID查询聊天历史
     * 
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @return 聊天消息列表
     */
    @Select("SELECT * FROM chat_history WHERE user_id = #{userId} AND session_id = #{sessionId} ORDER BY created_at DESC")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "sessionId", column = "session_id"),
            @Result(property = "role", column = "role"),
            @Result(property = "content", column = "content"),
            @Result(property = "createdAt", column = "created_at")
    })
    List<ChatMessage> findByUserIdAndSessionId(Long userId, String sessionId);
    
    /**
     * 根据用户ID和会话ID查询聊天历史（限制数量）
     * 
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @param limit 限制数量
     * @return 聊天消息列表
     */
    @Select("SELECT * FROM chat_history WHERE user_id = #{userId} AND session_id = #{sessionId} ORDER BY created_at DESC LIMIT #{limit}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "sessionId", column = "session_id"),
            @Result(property = "role", column = "role"),
            @Result(property = "content", column = "content"),
            @Result(property = "createdAt", column = "created_at")
    })
    List<ChatMessage> findByUserIdAndSessionIdWithLimit(Long userId, String sessionId, int limit);
    
    /**
     * 删除用户的所有聊天历史
     * 
     * @param userId 用户ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM chat_history WHERE user_id = #{userId}")
    int deleteByUserId(Long userId);
    
    /**
     * 删除指定会话的聊天历史
     * 
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM chat_history WHERE user_id = #{userId} AND session_id = #{sessionId}")
    int deleteByUserIdAndSessionId(Long userId, String sessionId);
}
