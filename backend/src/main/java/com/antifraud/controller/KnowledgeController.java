package com.antifraud.controller;

import com.antifraud.model.Knowledge;
import com.antifraud.model.User;
import com.antifraud.dto.KnowledgeDTO;
import com.antifraud.service.KnowledgeService;
import com.antifraud.service.LikeService;
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
 * 防诈骗知识库管理控制器
 * 
 * 功能说明：
 * 1. 提供防诈骗知识的增删改查接口
 * 2. 支持按分类、标签等条件查询
 * 3. 支持点赞功能
 * 4. 自动增加知识浏览次数
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeController {

    @Autowired
    private KnowledgeService knowledgeService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 从请求中获取当前登录用户的ID
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
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                if (jwtUtil.validateToken(token)) {
                    String username = jwtUtil.getUsernameFromToken(token);
                    User user = userService.findByNickname(username);
                    if (user != null) {
                        return user.getId();
                    }
                }
            } catch (Exception e) {
                System.out.println("获取当前用户ID异常: " + e.getMessage());
            }
        }
        return null;
    }

    /**
     * 创建新的防诈骗知识
     * 
     * 功能说明：
     * - 接收前端提交的知识数据
     * - 创建知识记录并保存到数据库
     * - 返回创建结果
     * 
     * @param knowledgeDTO 知识数据传输对象，包含标题、内容、分类、标签等信息
     * @return ResponseEntity 包含操作结果的响应对象
     *         - message: 提示信息
     *         - data: 创建的知识对象
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody KnowledgeDTO knowledgeDTO) {
        try {
            Knowledge knowledge = knowledgeService.create(knowledgeDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "创建成功");
            response.put("data", knowledge);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("创建失败: " + e.getMessage());
        }
    }

    /**
     * 更新防诈骗知识
     * 
     * 功能说明：
     * - 接收前端提交的知识数据
     * - 更新知识记录并保存到数据库
     * - 返回更新结果
     * 
     * @param id 知识ID
     * @param knowledgeDTO 知识数据传输对象，包含标题、内容、分类、标签等信息
     * @return ResponseEntity 包含操作结果的响应对象
     *         - message: 提示信息
     *         - data: 更新的知识对象
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody KnowledgeDTO knowledgeDTO) {
        try {
            Knowledge knowledge = knowledgeService.findById(id);
            if (knowledge == null) {
                return ResponseEntity.badRequest().body("知识不存在");
            }
            
            knowledge.setTitle(knowledgeDTO.getTitle());
            knowledge.setContent(knowledgeDTO.getContent());
            knowledge.setCategory(knowledgeDTO.getCategory());
            knowledge.setTags(knowledgeDTO.getTags());
            knowledge.setCoverImage(knowledgeDTO.getCoverImage());
            knowledge.setImages(knowledgeDTO.getImages());
            knowledge.setVideoUrl(knowledgeDTO.getVideoUrl());
            knowledge.setVideoCover(knowledgeDTO.getVideoCover());
            
            knowledgeService.update(knowledge);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "更新成功");
            response.put("data", knowledge);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("更新失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有防诈骗知识列表
     * 
     * 功能说明：
     * - 返回所有知识记录
     * - 按创建时间倒序排列（最新的在前）
     * - 用于首页展示知识库
     * 
     * @return ResponseEntity 包含知识列表的响应对象
     *         - data: 知识对象列表
     */
    @GetMapping
    public ResponseEntity<?> findAll() {
        List<Knowledge> knowledgeList = knowledgeService.findAll();
        Map<String, Object> response = new HashMap<>();
        response.put("data", knowledgeList);
        return ResponseEntity.ok(response);
    }

    /**
     * 根据ID获取知识详情
     * 
     * 功能说明：
     * - 根据知识ID查询知识详情
     * - 自动增加知识的浏览次数
     * - 返回当前用户是否已点赞该知识
     * 
     * @param id 知识ID
     * @param request HTTP请求对象，用于获取当前用户ID
     * @return ResponseEntity 包含知识详情的响应对象
     *         - data: 知识对象
     *         - isLiked: 当前用户是否已点赞
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id, HttpServletRequest request) {
        Knowledge knowledge = knowledgeService.findById(id);
        if (knowledge != null) {
            knowledgeService.incrementViewCount(id);

            Long userId = getCurrentUserId(request);
            boolean isLiked = false;
            if (userId != null) {
                isLiked = likeService.checkLike(userId, id, "knowledge");
            }

            Map<String, Object> response = new HashMap<>();
            response.put("data", knowledge);
            response.put("isLiked", isLiked);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body("知识不存在");
    }

    /**
     * 根据分类获取知识列表
     * 
     * 功能说明：
     * - 按指定分类筛选知识
     * - 支持的分类：防骗知识、案例分享、预警信息等
     * - 按创建时间倒序排列
     * 
     * @param category 知识分类名称
     * @return ResponseEntity 包含指定分类知识列表的响应对象
     *         - data: 指定分类的知识列表
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<?> findByCategory(@PathVariable String category) {
        List<Knowledge> knowledgeList = knowledgeService.findByCategory(category);
        Map<String, Object> response = new HashMap<>();
        response.put("data", knowledgeList);
        return ResponseEntity.ok(response);
    }

    /**
     * 根据标签获取知识列表
     * 
     * 功能说明：
     * - 按指定标签筛选知识
     * - 支持多个标签查询（逗号分隔）
     * - 按创建时间倒序排列
     * 
     * @param tag 知识标签名称
     * @return ResponseEntity 包含指定标签知识列表的响应对象
     *         - data: 包含指定标签的知识列表
     */
    @GetMapping("/tag/{tag}")
    public ResponseEntity<?> findByTag(@PathVariable String tag) {
        List<Knowledge> knowledgeList = knowledgeService.findByTag(tag);
        Map<String, Object> response = new HashMap<>();
        response.put("data", knowledgeList);
        return ResponseEntity.ok(response);
    }

    /**
     * 点赞或取消点赞知识
     * 
     * 功能说明：
     * - 切换当前用户对指定知识的点赞状态
     * - 如果已点赞则取消点赞，未点赞则点赞
     * - 需要用户登录
     * 
     * @param id 知识ID
     * @param request HTTP请求对象，用于获取当前用户ID
     * @return ResponseEntity 包含点赞结果的响应对象
     *         - liked: 是否已点赞（true/false）
     *         - likeCount: 当前点赞数
     *         - message: 操作提示信息
     */
    @PostMapping("/{id}/like")
    public ResponseEntity<?> like(
            @PathVariable Long id,
            HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return ResponseEntity.status(401).body("未登录或token无效");
        }

        LikeService.LikeResult result = likeService.toggleLike(userId, id, "knowledge");
        Map<String, Object> response = new HashMap<>();
        response.put("liked", result.isLiked());
        response.put("likeCount", result.getLikeCount());
        response.put("message", result.isLiked() ? "点赞成功" : "取消点赞");
        return ResponseEntity.ok(response);
    }

    /**
     * 搜索知识（支持标题、内容、标签）
     * 
     * 功能说明：
     * - 在标题、内容、标签中搜索关键词
     * - 支持分页查询
     * - 按创建时间倒序排列
     * 
     * @param keyword 搜索关键词
     * @param page 页码（从1开始，默认为1）
     * @param size 每页数量（默认为10）
     * @return ResponseEntity 包含搜索结果的响应对象
     *         - data: 知识列表
     *         - total: 总数量
     *         - page: 当前页码
     *         - size: 每页数量
     */
    @GetMapping("/search")
    public ResponseEntity<?> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<Knowledge> knowledgeList = knowledgeService.searchWithPagination(keyword, page, size);
        int total = knowledgeService.countSearchResults(keyword);
        
        Map<String, Object> response = new HashMap<>();
        response.put("data", knowledgeList);
        response.put("total", total);
        response.put("page", page);
        response.put("size", size);
        return ResponseEntity.ok(response);
    }

    /**
     * 删除指定知识
     * 
     * 功能说明：
     * - 根据知识ID删除知识
     * - 注意：删除操作不可恢复
     * 
     * @param id 要删除的知识ID
     * @return ResponseEntity 包含删除结果的响应对象
     *         - message: 操作提示信息
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        knowledgeService.delete(id);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "删除成功");
        return ResponseEntity.ok(response);
    }
}
