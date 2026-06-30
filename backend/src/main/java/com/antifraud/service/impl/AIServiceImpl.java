package com.antifraud.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.antifraud.model.ChatMessage;
import com.antifraud.repository.ChatHistoryRepository;
import com.antifraud.service.AIService;
import com.antifraud.util.PromptTemplate;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * AI服务实现类
 * 
 * 功能说明：
 * 1. 实现防诈骗问答功能
 * 2. 集成DeepSeek大模型
 * 3. 管理对话历史
 * 4. 使用Prompt模板优化回答
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@Slf4j
@Service
public class AIServiceImpl implements AIService {

    @Value("${deepseek.api-key}")
    private String apiKey;

    @Value("${deepseek.base-url}")
    private String baseUrl;

    @Value("${deepseek.model}")
    private String model;

    @Value("${deepseek.max-tokens}")
    private int maxTokens;

    @Value("${deepseek.temperature}")
    private double temperature;

    private final ChatHistoryRepository chatHistoryRepository;

    private final OkHttpClient httpClient;

    public AIServiceImpl(ChatHistoryRepository chatHistoryRepository) {
        this.chatHistoryRepository = chatHistoryRepository;
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    /**
     * 防诈骗问答
     * 
     * @param question 用户问题
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @return AI回答
     */
    @Override
    public String askAntiFraudQuestion(String question, Long userId, String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            sessionId = UUID.randomUUID().toString();
        }

        List<ChatMessage> history = getChatHistory(userId, sessionId, 10);
        return askAntiFraudQuestionWithHistory(question, userId, sessionId, history);
    }

    /**
     * 防诈骗问答（带历史记录）
     * 
     * @param question 用户问题
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @param chatHistory 对话历史
     * @return AI回答
     */
    @Override
    public String askAntiFraudQuestionWithHistory(String question, Long userId, String sessionId, List<ChatMessage> chatHistory) {
        try {
            saveUserMessage(userId, sessionId, question);

            String response = callDeepSeekAPI(question, chatHistory);

            saveAssistantMessage(userId, sessionId, response);

            return response;
        } catch (Exception e) {
            log.error("调用DeepSeek API失败: {}", e.getMessage(), e);
            return "抱歉，AI助手暂时无法回答，请稍后再试。如果情况紧急，请立即拨打110报警。";
        }
    }

    /**
     * 调用DeepSeek API
     * 
     * @param question 用户问题
     * @param history 对话历史
     * @return AI回答
     */
    private String callDeepSeekAPI(String question, List<ChatMessage> history) throws IOException {
        List<JSONObject> messages = new ArrayList<>();

        messages.add(createSystemMessage());

        if (history != null && !history.isEmpty()) {
            for (int i = history.size() - 1; i >= 0; i--) {
                ChatMessage msg = history.get(i);
                JSONObject message = new JSONObject();
                message.put("role", msg.getRole());
                message.put("content", msg.getContent());
                messages.add(message);
            }
        }

        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", question);
        messages.add(userMessage);

        JSONObject requestBody = new JSONObject();
        requestBody.put("model", model);
        requestBody.put("messages", messages);
        requestBody.put("max_tokens", maxTokens);
        requestBody.put("temperature", temperature);

        Request request = new Request.Builder()
                .url(baseUrl + "/v1/chat/completions")
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(requestBody.toJSONString(), MediaType.parse("application/json")))
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("DeepSeek API调用失败: {}", response.code());
                throw new IOException("API调用失败: " + response.code());
            }

            String responseBody = response.body().string();
            JSONObject jsonResponse = JSON.parseObject(responseBody);
            JSONArray choices = jsonResponse.getJSONArray("choices");
            
            if (choices != null && !choices.isEmpty()) {
                JSONObject choice = choices.getJSONObject(0);
                JSONObject message = choice.getJSONObject("message");
                return message.getString("content");
            }

            return "抱歉，AI助手暂时无法回答。";
        }
    }

    /**
     * 创建系统消息
     * 
     * @return 系统消息JSON对象
     */
    private JSONObject createSystemMessage() {
        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", PromptTemplate.SYSTEM_PROMPT);
        return systemMessage;
    }

    /**
     * 保存用户消息
     * 
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @param content 消息内容
     */
    private void saveUserMessage(Long userId, String sessionId, String content) {
        ChatMessage message = new ChatMessage(userId, sessionId, "user", content);
        chatHistoryRepository.insert(message);
    }

    /**
     * 保存AI助手消息
     * 
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @param content 消息内容
     */
    private void saveAssistantMessage(Long userId, String sessionId, String content) {
        ChatMessage message = new ChatMessage(userId, sessionId, "assistant", content);
        chatHistoryRepository.insert(message);
    }

    /**
     * 获取用户对话历史
     * 
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @param limit 限制数量
     * @return 对话历史列表
     */
    @Override
    public List<ChatMessage> getChatHistory(Long userId, String sessionId, int limit) {
        if (sessionId == null || sessionId.isEmpty()) {
            return new ArrayList<>();
        }
        return chatHistoryRepository.findByUserIdAndSessionIdWithLimit(userId, sessionId, limit);
    }

    /**
     * 清除用户对话历史
     * 
     * @param userId 用户ID
     * @param sessionId 会话ID
     */
    @Override
    public void clearChatHistory(Long userId, String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            chatHistoryRepository.deleteByUserId(userId);
        } else {
            chatHistoryRepository.deleteByUserIdAndSessionId(userId, sessionId);
        }
    }
}
