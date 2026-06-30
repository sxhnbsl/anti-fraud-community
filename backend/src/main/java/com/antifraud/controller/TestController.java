package com.antifraud.controller;

import com.antifraud.dto.SubmitTestDTO;
import com.antifraud.model.TestQuestion;
import com.antifraud.model.TestRecord;
import com.antifraud.model.User;
import com.antifraud.service.TestService;
import com.antifraud.service.UserService;
import com.antifraud.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试控制器
 */
@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private TestService testService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 获取测试题目
     */
    @GetMapping("/questions")
    public ResponseEntity<?> getQuestions(@RequestParam(defaultValue = "20") int count) {
        try {
            List<TestQuestion> questions = testService.getRandomQuestions(count);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", questions);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取题目失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 提交测试
     */
    @PostMapping("/submit")
    public ResponseEntity<?> submitTest(@RequestBody SubmitTestDTO dto, HttpServletRequest request) {
        try {
            // 获取当前用户
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "未登录"));
            }
            
            token = token.substring(7);
            String username = jwtUtil.getUsernameFromToken(token);
            User user = userService.findByNickname(username);
            
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "用户不存在"));
            }

            // 提交测试
            TestRecord record = testService.submitTest(user.getId(), dto.getAnswers(), dto.getTimeUsed());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", record);
            response.put("message", "提交成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "提交失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取用户测试记录
     */
    @GetMapping("/records")
    public ResponseEntity<?> getUserRecords(HttpServletRequest request) {
        try {
            // 获取当前用户
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "未登录"));
            }
            
            token = token.substring(7);
            String username = jwtUtil.getUsernameFromToken(token);
            User user = userService.findByNickname(username);
            
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "用户不存在"));
            }

            List<TestRecord> records = testService.getUserTestRecords(user.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", records);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取记录失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取排行榜
     */
    @GetMapping("/ranking")
    public ResponseEntity<?> getRanking(@RequestParam(defaultValue = "10") int limit) {
        try {
            List<TestRecord> ranking = testService.getRankingList(limit);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", ranking);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取排行榜失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取测试详情
     */
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getDetail(@PathVariable Long id) {
        try {
            TestRecord record = testService.getTestDetail(id);
            
            if (record == null) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "记录不存在"));
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", record);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取详情失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
