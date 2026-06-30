package com.antifraud.service;

import com.antifraud.model.ChatMessage;
import java.util.List;

/**
 * AI服务接口
 * 
 * 功能说明：
 * 1. 提供防诈骗问答功能
 * 2. 支持多轮对话
 * 3. 支持流式响应
 * 4. 集成DeepSeek大模型
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
public interface AIService {
    
    /**
     * 防诈骗问答
     * 
     * 功能说明：
     * - 用户提问，AI回答防诈骗相关问题
     * - 支持多轮对话，保持上下文
     * - 返回结构化的回答
     * 
     * @param question 用户问题
     * @param userId 用户ID（用于保存对话历史）
     * @param sessionId 会话ID（可选，用于区分不同对话）
     * @return AI回答
     */
    String askAntiFraudQuestion(String question, Long userId, String sessionId);
    
    /**
     * 防诈骗问答（带历史记录）
     * 
     * 功能说明：
     * - 基于对话历史进行回答
     * - 保持多轮对话的上下文
     * - 提供更连贯的回答
     * 
     * @param question 用户问题
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @param chatHistory 对话历史
     * @return AI回答
     */
    String askAntiFraudQuestionWithHistory(String question, Long userId, String sessionId, List<ChatMessage> chatHistory);
    
    /**
     * 获取用户对话历史
     * 
     * @param userId 用户ID
     * @param sessionId 会话ID（可选）
     * @param limit 限制数量
     * @return 对话历史列表
     */
    List<ChatMessage> getChatHistory(Long userId, String sessionId, int limit);
    
    /**
     * 清除用户对话历史
     * 
     * @param userId 用户ID
     * @param sessionId 会话ID（可选，为空则清除所有）
     */
    void clearChatHistory(Long userId, String sessionId);
}
