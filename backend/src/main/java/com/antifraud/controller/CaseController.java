package com.antifraud.controller;

import com.antifraud.model.Case;
import com.antifraud.model.User;
import com.antifraud.dto.CaseDTO;
import com.antifraud.service.CaseService;
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
 * 诈骗案例管理控制器
 * 
 * 功能说明：
 * 1. 提供诈骗案例的增删改查接口
 * 2. 支持按分类等条件查询
 * 3. 支持点赞功能
 * 4. 自动增加案例浏览次数
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@RestController
@RequestMapping("/api/cases")
public class CaseController {

    @Autowired
    private CaseService caseService;

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
     * 创建新的诈骗案例
     * 
     * 功能说明：
     * - 接收前端提交的案例数据
     * - 创建案例记录并保存到数据库
     * - 返回创建结果
     * 
     * @param caseDTO 案例数据传输对象，包含标题、内容、分类等信息
     * @return ResponseEntity 包含操作结果的响应对象
     *         - message: 提示信息
     *         - data: 创建的案例对象
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody CaseDTO caseDTO) {
        try {
            Case c = caseService.create(caseDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "创建成功");
            response.put("data", c);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("创建失败: " + e.getMessage());
        }
    }

    /**
     * 更新诈骗案例
     * 
     * 功能说明：
     * - 接收前端提交的案例数据
     * - 更新案例记录并保存到数据库
     * - 返回更新结果
     * 
     * @param id 案例ID
     * @param caseDTO 案例数据传输对象，包含标题、内容、分类等信息
     * @return ResponseEntity 包含操作结果的响应对象
     *         - message: 提示信息
     *         - data: 更新的案例对象
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody CaseDTO caseDTO) {
        try {
            Case c = caseService.findById(id);
            if (c == null) {
                return ResponseEntity.badRequest().body("案例不存在");
            }
            
            c.setTitle(caseDTO.getTitle());
            c.setContent(caseDTO.getContent());
            c.setCategory(caseDTO.getCategory());
            c.setFraudAnalysis(caseDTO.getFraudAnalysis());
            c.setPreventionAdvice(caseDTO.getPreventionAdvice());
            c.setCoverImage(caseDTO.getCoverImage());
            c.setImages(caseDTO.getImages());
            c.setVideoUrl(caseDTO.getVideoUrl());
            c.setVideoCover(caseDTO.getVideoCover());
            
            caseService.update(c);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "更新成功");
            response.put("data", c);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("更新失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有诈骗案例列表
     * 
     * 功能说明：
     * - 返回所有案例记录
     * - 按创建时间倒序排列（最新的在前）
     * - 用于首页展示案例库
     * 
     * @return ResponseEntity 包含案例列表的响应对象
     *         - data: 案例对象列表
     */
    @GetMapping
    public ResponseEntity<?> findAll() {
        List<Case> cases = caseService.findAll();
        Map<String, Object> response = new HashMap<>();
        response.put("data", cases);
        return ResponseEntity.ok(response);
    }

    /**
     * 根据ID获取案例详情
     * 
     * 功能说明：
     * - 根据案例ID查询案例详情
     * - 自动增加案例的浏览次数
     * - 返回当前用户是否已点赞该案例
     * 
     * @param id 案例ID
     * @param request HTTP请求对象，用于获取当前用户ID
     * @return ResponseEntity 包含案例详情的响应对象
     *         - data: 案例对象
     *         - isLiked: 当前用户是否已点赞
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id, HttpServletRequest request) {
        Case c = caseService.findById(id);
        if (c != null) {
            caseService.incrementViewCount(id);

            Long userId = getCurrentUserId(request);
            boolean isLiked = false;
            if (userId != null) {
                isLiked = likeService.checkLike(userId, id, "case");
            }

            Map<String, Object> response = new HashMap<>();
            response.put("data", c);
            response.put("isLiked", isLiked);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body("案例不存在");
    }

    /**
     * 根据分类获取案例列表
     * 
     * 功能说明：
     * - 按指定分类筛选案例
     * - 支持的分类：网络诈骗、电信诈骗、金融诈骗等
     * - 按创建时间倒序排列
     * 
     * @param category 案例分类名称
     * @return ResponseEntity 包含指定分类案例列表的响应对象
     *         - data: 指定分类的案例列表
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<?> findByCategory(@PathVariable String category) {
        List<Case> cases = caseService.findByCategory(category);
        Map<String, Object> response = new HashMap<>();
        response.put("data", cases);
        return ResponseEntity.ok(response);
    }

    /**
     * 点赞或取消点赞案例
     * 
     * 功能说明：
     * - 切换当前用户对指定案例的点赞状态
     * - 如果已点赞则取消点赞，未点赞则点赞
     * - 需要用户登录
     * 
     * @param id 案例ID
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

        LikeService.LikeResult result = likeService.toggleLike(userId, id, "case");
        Map<String, Object> response = new HashMap<>();
        response.put("liked", result.isLiked());
        response.put("likeCount", result.getLikeCount());
        response.put("message", result.isLiked() ? "点赞成功" : "取消点赞");
        return ResponseEntity.ok(response);
    }

    /**
     * 搜索案例（支持标题、内容、诈骗类型）
     * 
     * 功能说明：
     * - 在标题、内容、诈骗类型中搜索关键词
     * - 支持分页查询
     * - 按创建时间倒序排列
     * 
     * @param keyword 搜索关键词
     * @param page 页码（从1开始，默认为1）
     * @param size 每页数量（默认为10）
     * @return ResponseEntity 包含搜索结果的响应对象
     *         - data: 案例列表
     *         - total: 总数量
     *         - page: 当前页码
     *         - size: 每页数量
     */
    @GetMapping("/search")
    public ResponseEntity<?> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<Case> cases = caseService.searchWithPagination(keyword, page, size);
        int total = caseService.countSearchResults(keyword);
        
        Map<String, Object> response = new HashMap<>();
        response.put("data", cases);
        response.put("total", total);
        response.put("page", page);
        response.put("size", size);
        return ResponseEntity.ok(response);
    }

    /**
     * 删除指定案例
     * 
     * 功能说明：
     * - 根据案例ID删除案例
     * - 注意：删除操作不可恢复
     * 
     * @param id 要删除的案例ID
     * @return ResponseEntity 包含删除结果的响应对象
     *         - message: 操作提示信息
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        caseService.delete(id);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "删除成功");
        return ResponseEntity.ok(response);
    }
}
