package com.antifraud.controller;

import com.antifraud.model.User;
import com.antifraud.dto.UserDTO;
import com.antifraud.service.UserService;
import com.antifraud.service.UserProfileChangeService;
import com.antifraud.model.UserProfileChange;
import com.antifraud.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户管理控制器
 * 
 * 功能说明：
 * 1. 提供用户信息查询接口
 * 2. 提供用户信息更新接口
 * 3. 基于 JWT token 进行身份验证
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserProfileChangeService userProfileChangeService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 获取当前登录用户信息
     * 
     * 功能说明：
     * - 从请求头的 Authorization 字段中提取 JWT token
     * - 解析 token 获取用户名
     * - 根据用户名查询完整的用户信息
     * - 用于个人中心展示用户资料
     * 
     * @param request HTTP请求对象，包含Authorization头
     * @return ResponseEntity 包含用户信息的响应对象
     *         - data: 用户对象（包含id、username、nickname、avatar等）
     */
    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                    token = token.substring(7);
                    String username = jwtUtil.getUsernameFromToken(token);
                    User user = userService.findByIdentifier(username);
                    if (user != null) {
                        return ResponseEntity.ok(Map.of("data", user));
                    }
                }
            return ResponseEntity.badRequest().body("获取用户信息失败");
        } catch (Exception e) {
            System.out.println("获取用户信息失败: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("获取用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取当前用户待审核的资料变更
     */
    @GetMapping("/profile-change-status")
    public ResponseEntity<?> getProfileChangeStatus(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                String username = jwtUtil.getUsernameFromToken(token);
                User user = userService.findByIdentifier(username);
                if (user != null) {
                    List<UserProfileChange> changes = userProfileChangeService.findByUserId(user.getId());
                    List<Map<String, Object>> pendingList = new java.util.ArrayList<>();
                    if (changes != null) {
                        for (UserProfileChange c : changes) {
                            if ("PENDING".equals(c.getStatus())) {
                                Map<String, Object> item = new HashMap<>();
                                item.put("changeType", c.getChangeType());
                                item.put("oldValue", c.getOldValue());
                                item.put("newValue", c.getNewValue());
                                item.put("status", c.getStatus());
                                pendingList.add(item);
                            }
                        }
                    }
                    Map<String, Object> response = new HashMap<>();
                    response.put("hasPending", !pendingList.isEmpty());
                    response.put("pendingChanges", pendingList);
                    return ResponseEntity.ok(response);
                }
            }
            return ResponseEntity.badRequest().body("获取失败");
        } catch (Exception e) {
            System.out.println("获取资料变更状态失败: " + e.getMessage());
            return ResponseEntity.badRequest().body("获取失败: " + e.getMessage());
        }
    }

    /**
     * 更新当前登录用户信息
     * 
     * 功能说明：
     * - 从 JWT token 中获取当前用户ID
     * - 接收前端提交的用户信息更新数据
     * - 更新用户的昵称、头像等信息
     * - 用户名和密码不允许修改
     * 
     * @param userDTO 用户数据传输对象，包含要更新的字段
     *               - nickname: 昵称（可选）
     *               - avatar: 头像URL（可选）
     *               - phone: 手机号（可选）
     * @param request HTTP请求对象，包含Authorization头
     * @return ResponseEntity 包含更新结果的响应对象
     *         - success: 操作是否成功
     *         - message: 提示信息
     *         - data: 更新后的用户对象
     */
    @PostMapping("/update")
    public ResponseEntity<?> updateProfile(@RequestBody UserDTO userDTO, HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                    token = token.substring(7);
                    String username = jwtUtil.getUsernameFromToken(token);
                    User user = userService.findByIdentifier(username);
                    if (user != null) {
                        User updatedUser = userService.updateProfile(user.getId(), userDTO);
                        if (updatedUser != null) {
                            Map<String, Object> response = new HashMap<>();
                            response.put("success", true);
                            
                            // 检查是否修改了昵称或头像
                            boolean hasNicknameChange = userDTO.getNickname() != null && !userDTO.getNickname().equals(user.getNickname());
                            boolean hasAvatarChange = userDTO.getAvatar() != null && !userDTO.getAvatar().equals(user.getAvatar());
                            
                            if (hasNicknameChange || hasAvatarChange) {
                                response.put("message", "更新成功，昵称和头像变更需要审核");
                            } else {
                                response.put("message", "更新成功");
                            }
                            
                            response.put("data", updatedUser);
                            return ResponseEntity.ok(response);
                        }
                    }
                }
            return ResponseEntity.badRequest().body("更新失败");
        } catch (Exception e) {
            System.out.println("更新用户信息失败: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "更新失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 修改密码
     * 
     * 功能说明：
     * - 从 JWT token 中获取当前用户ID
     * - 接收旧密码和新密码
     * - 验证旧密码是否正确
     * - 更新用户密码
     * 
     * @param requestBody 请求体，包含旧密码和新密码
     *               - oldPassword: 旧密码
     *               - newPassword: 新密码
     * @param request HTTP请求对象，包含Authorization头
     * @return ResponseEntity 包含修改结果的响应对象
     *         - success: 操作是否成功
     *         - message: 提示信息
     */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> requestBody, HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                    token = token.substring(7);
                    String username = jwtUtil.getUsernameFromToken(token);
                    User user = userService.findByIdentifier(username);
                    if (user != null) {
                        String oldPassword = requestBody.get("oldPassword");
                        String newPassword = requestBody.get("newPassword");
                        
                        if (oldPassword == null || newPassword == null) {
                            Map<String, Object> response = new HashMap<>();
                            response.put("success", false);
                            response.put("message", "旧密码和新密码不能为空");
                            return ResponseEntity.badRequest().body(response);
                        }
                        
                        boolean success = userService.changePassword(user.getId(), oldPassword, newPassword);
                        if (success) {
                            Map<String, Object> response = new HashMap<>();
                            response.put("success", true);
                            response.put("message", "密码修改成功");
                            return ResponseEntity.ok(response);
                        } else {
                            Map<String, Object> response = new HashMap<>();
                            response.put("success", false);
                            response.put("message", "旧密码错误");
                            return ResponseEntity.badRequest().body(response);
                        }
                    }
                }
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败");
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            System.out.println("修改密码失败: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "修改失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
