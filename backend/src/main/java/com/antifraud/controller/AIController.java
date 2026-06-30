package com.antifraud.controller;

import com.antifraud.model.ChatMessage;
import com.antifraud.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * AI问答控制器
 * 
 * 功能说明：
 * 1. 提供AI防诈骗问答接口
 * 2. 支持多轮对话
 * 3. 管理对话历史
 * 4. 提供清除历史功能
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@RestController
@RequestMapping("/api/ai")
public class AIController {

    @Autowired
    private AIService aiService;

    /**
     * 从请求中获取当前用户ID
     * 
     * @param request HTTP请求对象
     * @return 用户ID，未登录返回null
     */
    private Long getCurrentUserId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                return 1L;
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    /**
     * AI问答接口
     * 
     * 功能说明：
     * - 用户提问，AI回答防诈骗相关问题
     * - 支持多轮对话，保持上下文
     * - 自动保存对话历史
     * 
     * @param requestBody 请求体
     *         - question: 用户问题
     *         - sessionId: 会话ID（可选）
     * @param request HTTP请求对象
     * @return AI回答
     */
    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestBody Map<String, String> requestBody, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            userId = 1L;
        }

        String question = requestBody.get("question");
        String sessionId = requestBody.get("sessionId");

        if (question == null || question.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("问题不能为空");
        }

        String response = aiService.askAntiFraudQuestion(question, userId, sessionId);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", response);
        result.put("sessionId", sessionId);

        System.out.println("AI Chat API called: question=" + question + ", sessionId=" + sessionId);
        System.out.println("AI Response: " + response);

        return ResponseEntity.ok(result);
    }

    /**
     * 获取对话历史
     * 
     * 功能说明：
     * - 获取用户的对话历史记录
     * - 支持按会话ID查询
     * - 按时间倒序排列
     * 
     * @param sessionId 会话ID（可选）
     * @param limit 限制数量（默认10）
     * @param request HTTP请求对象
     * @return 对话历史列表
     */
    @GetMapping("/history")
    public ResponseEntity<?> getHistory(
            @RequestParam(required = false) String sessionId,
            @RequestParam(defaultValue = "10") int limit,
            HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return ResponseEntity.status(401).body("未登录");
        }

        List<ChatMessage> history = aiService.getChatHistory(userId, sessionId, limit);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", history);

        return ResponseEntity.ok(result);
    }

    /**
     * 清除对话历史
     * 
     * 功能说明：
     * - 清除用户的对话历史
     * - 支持清除指定会话或所有会话
     * 
     * @param sessionId 会话ID（可选，为空则清除所有）
     * @param request HTTP请求对象
     * @return 操作结果
     */
    @DeleteMapping("/history")
    public ResponseEntity<?> clearHistory(
            @RequestParam(required = false) String sessionId,
            HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return ResponseEntity.status(401).body("未登录");
        }

        aiService.clearChatHistory(userId, sessionId);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "清除成功");
        result.put("data", null);

        return ResponseEntity.ok(result);
    }

    /**
     * 创建新会话
     * 
     * 功能说明：
     * - 生成新的会话ID
     * - 用于开始新的对话
     * 
     * @return 新会话ID
     */
    @PostMapping("/session")
    public ResponseEntity<?> createSession() {
        String sessionId = UUID.randomUUID().toString();

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", sessionId);

        return ResponseEntity.ok(result);
    }
}
