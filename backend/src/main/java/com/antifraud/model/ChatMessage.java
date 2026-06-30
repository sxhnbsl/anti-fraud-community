package com.antifraud.model;

import lombok.Data;
import java.util.Date;

/**
 * 聊天消息实体类
 * 
 * 功能说明：
 * 1. 存储AI对话的消息
 * 2. 支持用户和AI两种角色
 * 3. 用于对话历史管理
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@Data
public class ChatMessage {
    
    /**
     * 消息ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 会话ID（用于区分不同对话）
     */
    private String sessionId;
    
    /**
     * 角色：user-用户，assistant-AI助手
     */
    private String role;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 创建时间
     */
    private Date createdAt;
    
    /**
     * 构造函数
     */
    public ChatMessage() {
    }
    
    /**
     * 构造函数
     * 
     * @param role 角色
     * @param content 消息内容
     */
    public ChatMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }
    
    /**
     * 构造函数
     * 
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @param role 角色
     * @param content 消息内容
     */
    public ChatMessage(Long userId, String sessionId, String role, String content) {
        this.userId = userId;
        this.sessionId = sessionId;
        this.role = role;
        this.content = content;
    }
}
