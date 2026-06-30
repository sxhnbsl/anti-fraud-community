package com.antifraud.controller;

import com.antifraud.model.User;
import com.antifraud.dto.UserDTO;
import com.antifraud.service.UserService;
import com.antifraud.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户认证控制器
 * 
 * 功能说明：
 * 1. 提供用户注册接口
 * 2. 提供用户登录接口
 * 3. 生成和返回 JWT token
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户注册
     * 
     * 功能说明：
     * - 接收用户提交的注册信息
     * - 创建新用户账户
     * - 返回注册结果和用户信息
     * 
     * @param userDTO 用户数据传输对象，包含昵称、密码等信息
     * @return ResponseEntity 包含注册结果的响应对象
     *         - message: 提示信息
     *         - user: 创建的用户对象
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDTO userDTO) {
        System.out.println("收到注册请求: " + userDTO.getNickname());
        try {
            User user = userService.register(userDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "注册成功，请等待管理员审核");
            response.put("user", user);
            response.put("needAudit", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("注册失败: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("注册失败: " + e.getMessage());
        }
    }

    /**
     * 用户登录
     * 
     * 功能说明：
     * - 验证用户名/昵称/手机号/邮箱和密码
     * - 生成 JWT token 用于后续请求的身份验证
     * - 返回 token 和用户信息
     * 
     * @param credentials 登录凭证
     *         - username: 用户名/昵称/手机号/邮箱
     *         - password: 密码
     * @return ResponseEntity 包含登录结果的响应对象
     *         - token: JWT 认证令牌
     *         - user: 用户对象
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String identifier = credentials.get("username");
        String password = credentials.get("password");

        System.out.println("登录请求: identifier=" + identifier);

        try {
            User user = userService.login(identifier, password);
            if (user != null) {
                String token = jwtUtil.generateToken(user.getNickname());
                Map<String, Object> response = new HashMap<>();
                response.put("token", token);
                response.put("user", user);
                return ResponseEntity.ok(response);
            }

            return ResponseEntity.badRequest().body("账号或密码错误");
        } catch (Exception e) {
            System.out.println("登录失败: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("登录失败: " + e.getMessage());
        }
    }
}
