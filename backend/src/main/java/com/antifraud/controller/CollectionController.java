package com.antifraud.controller;

import com.antifraud.model.UserCollection;
import com.antifraud.model.User;
import com.antifraud.dto.CollectionDTO;
import com.antifraud.service.CollectionService;
import com.antifraud.service.UserService;
import com.antifraud.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户收藏管理控制器
 * 
 * 功能说明：
 * 1. 提供收藏的创建、查询、删除接口
 * 2. 支持切换收藏状态（收藏/取消收藏）
 * 3. 支持检查用户是否已收藏指定内容
 * 4. 支持获取用户的所有收藏列表
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@RestController
@RequestMapping("/api/collections")
public class CollectionController {

    private final CollectionService collectionService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Autowired
    public CollectionController(CollectionService collectionService, UserService userService, JwtUtil jwtUtil) {
        this.collectionService = collectionService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 从请求中获取当前用户ID
     * 
     * 功能说明：
     * - 从请求头的 Authorization 字段中提取 JWT token
     * - 解析 token 获取用户名
     * - 根据用户名查询用户信息
     * - 返回用户ID
     * 
     * @param request HTTP请求对象，包含请求头信息
     * @return 当前登录用户的ID，如果未登录或token无效则返回null
     */
    private Long getCurrentUserId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        System.out.println("Authorization Header: " + authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            System.out.println("Token: " + token);

            try {
                boolean isValid = jwtUtil.validateToken(token);
                System.out.println("Token valid: " + isValid);

                if (isValid) {
                        String username = jwtUtil.getUsernameFromToken(token);
                        System.out.println("Username from token: " + username);

                        User user = userService.findByNickname(username);
                        if (user != null) {
                            System.out.println("User ID: " + user.getId());
                            return user.getId();
                        } else {
                            System.out.println("User not found for username: " + username);
                        }
                    }
            } catch (Exception e) {
                System.out.println("Error: " + e.getClass().getName() + " - " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Authorization header is missing or invalid format");
        }
        return null;
    }

    /**
     * 切换收藏状态
     * 
     * 功能说明：
     * - 如果用户已收藏该内容，则取消收藏
     * - 如果用户未收藏该内容，则添加收藏
     * - 支持收藏类型：post（帖子）、knowledge（知识）、case（案例）
     * - 需要用户登录
     * 
     * @param collectionDTO 收藏数据传输对象
     *         - userId: 用户ID（自动从token获取）
     *         - targetType: 收藏类型（post/knowledge/case）
     *         - targetId: 目标内容ID
     * @param request HTTP请求对象，用于获取当前用户ID
     * @return ResponseEntity 包含操作结果的响应对象
     *         - 返回"收藏成功"或"取消收藏"
     */
    @PostMapping("/toggle")
    public ResponseEntity<?> toggleCollection(
            @RequestBody CollectionDTO collectionDTO,
            HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return ResponseEntity.status(401).body("未登录或token无效");
        }

        boolean isCollected = collectionService.toggleCollection(userId, collectionDTO.getTargetId(), collectionDTO.getType(), collectionDTO.getTitle(), collectionDTO.getSummary());
        if (isCollected) {
            return ResponseEntity.ok().body("收藏成功");
        } else {
            return ResponseEntity.ok().body("取消收藏");
        }
    }

    /**
     * 检查用户是否已收藏指定内容
     * 
     * 功能说明：
     * - 查询用户是否已收藏指定的内容
     * - 用于前端显示收藏按钮的状态
     * - 需要用户登录
     * 
     * @param type 收藏类型（post/knowledge/case）
     * @param targetId 目标内容ID
     * @param request HTTP请求对象，用于获取当前用户ID
     * @return ResponseEntity 包含收藏状态的响应对象
     *         - true: 已收藏
     *         - false: 未收藏
     */
    @GetMapping("/check/{type}/{targetId}")
    public ResponseEntity<?> checkCollection(
            @PathVariable String type,
            @PathVariable Long targetId,
            HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return ResponseEntity.status(401).body("未登录或token无效");
        }

        boolean isCollected = collectionService.checkCollection(userId, targetId, type);
        return ResponseEntity.ok().body(isCollected);
    }

    /**
     * 获取用户的所有收藏列表
     * 
     * 功能说明：
     * - 查询当前用户的所有收藏记录
     * - 按收藏时间倒序排列（最新的在前）
     * - 需要用户登录
     * 
     * @param request HTTP请求对象，用于获取当前用户ID
     * @return ResponseEntity 包含收藏列表的响应对象
     *         - 返回用户的收藏列表
     */
    @GetMapping
    public ResponseEntity<List<UserCollection>> getUserCollections(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return ResponseEntity.status(401).body(null);
        }

        List<UserCollection> collections = collectionService.findByUserId(userId);
        return ResponseEntity.ok(collections);
    }

    /**
     * 删除指定收藏
     * 
     * 功能说明：
     * - 根据收藏ID删除收藏记录
     * - 注意：删除操作不可恢复
     * 
     * @param id 要删除的收藏ID
     * @return ResponseEntity 包含删除结果的响应对象
     *         - 返回"删除成功"
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCollection(@PathVariable Long id) {
        collectionService.delete(id);
        return ResponseEntity.ok().body("删除成功");
    }
}
