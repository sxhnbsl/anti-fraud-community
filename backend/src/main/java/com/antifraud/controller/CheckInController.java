package com.antifraud.controller;

import com.antifraud.model.User;
import com.antifraud.service.CheckInService;
import com.antifraud.service.UserService;
import com.antifraud.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 签到控制器
 * 
 * 功能说明：
 * 1. 处理用户签到相关的请求
 * 2. 提供签到、获取签到统计等接口
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@RestController
@RequestMapping("/api/check-in")
public class CheckInController {
    
    @Autowired
    private CheckInService checkInService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserService userService;
    
    /**
     * 用户签到
     * 
     * @param request HttpServletRequest
     * @return 签到结果
     */
    @PostMapping("/do-check-in")
    public ResponseEntity<Map<String, Object>> doCheckIn(HttpServletRequest request) {
        try {
            // 从JWT中获取用户ID
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                String username = jwtUtil.getUsernameFromToken(token);
                User user = userService.findByNickname(username);
                if (user == null) {
                    return ResponseEntity.ok(Map.of(
                        "success", false,
                        "message", "未登录"
                    ));
                }
                
                // 执行签到
                Map<String, Object> result = checkInService.checkIn(user.getId());
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "未登录"
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 检查是否是重复签到的异常
            String errorMessage = e.getMessage();
            if (errorMessage != null && (errorMessage.contains("Duplicate entry") || errorMessage.contains("重复") || errorMessage.contains("已签"))) {
                return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "今日已签到"
                ));
            }
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "签到失败"
            ));
        }
    }
    
    /**
     * 获取用户签到统计信息
     * 
     * @param request HttpServletRequest
     * @return 签到统计信息
     */
    @GetMapping("/get-stat")
    public ResponseEntity<Map<String, Object>> getCheckInStat(HttpServletRequest request) {
        try {
            // 从JWT中获取用户ID
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                String username = jwtUtil.getUsernameFromToken(token);
                User user = userService.findByNickname(username);
                if (user == null) {
                    return ResponseEntity.ok(Map.of(
                        "success", false,
                        "message", "未登录"
                    ));
                }
                
                // 获取签到统计
                var stat = checkInService.getUserCheckInStat(user.getId());
                if (stat == null) {
                    return ResponseEntity.ok(Map.of(
                        "success", true,
                        "data", Map.of(
                            "totalCheckInDays", 0,
                            "currentConsecutiveDays", 0,
                            "maxConsecutiveDays", 0,
                            "totalPoints", 0
                        )
                    ));
                }
                
                // 检查今日是否已签到
                boolean checkedInToday = checkInService.isCheckedInToday(user.getId());
                
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", Map.of(
                        "totalCheckInDays", stat.getTotalCheckInDays(),
                        "currentConsecutiveDays", stat.getCurrentConsecutiveDays(),
                        "maxConsecutiveDays", stat.getMaxConsecutiveDays(),
                        "totalPoints", stat.getTotalPoints(),
                        "checkedInToday", checkedInToday
                    )
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "未登录"
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "获取签到统计失败"
            ));
        }
    }
    
    /**
     * 获取用户签到历史记录
     * 
     * @param request HttpServletRequest
     * @return 签到历史记录
     */
    @GetMapping("/get-history")
    public ResponseEntity<Map<String, Object>> getCheckInHistory(HttpServletRequest request) {
        try {
            // 从JWT中获取用户ID
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                String username = jwtUtil.getUsernameFromToken(token);
                User user = userService.findByNickname(username);
                if (user == null) {
                    return ResponseEntity.ok(Map.of(
                        "success", false,
                        "message", "未登录"
                    ));
                }
                
                // 获取签到历史
                var history = checkInService.getCheckInHistory(user.getId());
                
                // 格式化签到历史数据
                java.util.List<Map<String, Object>> formattedHistory = new java.util.ArrayList<>();
                for (var record : history) {
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                    formattedHistory.add(Map.of(
                        "date", sdf.format(record.getCheckInDate()),
                        "points", record.getPoints(),
                        "consecutiveDays", record.getConsecutiveDays()
                    ));
                }
                
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", formattedHistory
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "未登录"
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "获取签到历史失败"
            ));
        }
    }
}
