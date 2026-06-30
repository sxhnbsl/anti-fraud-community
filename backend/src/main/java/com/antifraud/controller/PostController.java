package com.antifraud.controller;

import com.antifraud.model.Post;
import com.antifraud.model.User;
import com.antifraud.dto.PostDTO;
import com.antifraud.dto.ContentCheckResult;
import com.antifraud.service.PostService;
import com.antifraud.service.LikeService;
import com.antifraud.service.UserService;
import com.antifraud.service.ContentSecurityService;
import com.antifraud.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 帖子管理控制器
 * 
 * 功能说明：
 * 1. 提供帖子的增删改查接口
 * 2. 集成内容安全审核功能，自动检测敏感内容
 * 3. 支持点赞功能
 * 4. 提供管理端审核接口
 * 5. 支持按作者、审核状态等条件查询
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ContentSecurityService contentSecurityService;

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
     * 创建新帖子
     * 
     * 功能说明：
     * - 接收前端提交的帖子数据
     * - 自动调用内容安全审核服务检测敏感内容
     * - 根据审核结果设置帖子审核状态：
     *   - 审核通过：直接发布（APPROVED）
     *   - 风险较高：进入待审核状态（PENDING）
     *   - 严重违规：拒绝发布（REJECT）
     * - 返回创建结果和审核状态
     * 
     * @param postDTO 帖子数据传输对象，包含标题、内容、作者ID等信息
     * @return ResponseEntity 包含操作结果的响应对象
     *         - success: 操作是否成功
     *         - message: 提示信息
     *         - data: 创建的帖子对象
     *         - auditStatus: 审核状态（APPROVED/PENDING）
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody PostDTO postDTO) {
        System.out.println("创建帖子: " + postDTO.getTitle());
        try {
            String contentToCheck = postDTO.getTitle() + " " + postDTO.getContent();
            ContentCheckResult checkResult = contentSecurityService.checkText(contentToCheck, "post");
            
            String auditStatus;
            if (!checkResult.isPassed()) {
                if (checkResult.getRiskLevel() == ContentCheckResult.RiskLevel.REJECT) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "内容包含违规信息，禁止发布");
                    response.put("data", checkResult);
                    return ResponseEntity.ok(response);
                } else {
                    auditStatus = Post.AUDIT_PENDING;
                }
            } else {
                auditStatus = Post.AUDIT_APPROVED;
            }

            Post post = postService.createWithAudit(postDTO, auditStatus);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            if (Post.AUDIT_PENDING.equals(auditStatus)) {
                response.put("message", "帖子已提交，等待审核");
            } else {
                response.put("message", "发布成功");
            }
            response.put("data", post);
            response.put("auditStatus", auditStatus);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.out.println("创建失败: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "创建失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取所有已审核通过的帖子列表
     * 
     * 功能说明：
     * - 只返回审核状态为 APPROVED 的帖子
     * - 支持按分类筛选
     * - 支持排序：latest（最新）、hot（热门，点赞数>10）
     * - 同时查询当前用户对每个帖子的点赞状态
     * 
     * @param request HTTP请求对象，用于获取当前用户ID
     * @param category 帖子分类
     * @param sort 排序方式：latest（最新）、hot（热门）
     * @return ResponseEntity 包含帖子列表的响应对象
     *         - data: 帖子对象列表
     */
    @GetMapping
    public ResponseEntity<?> findAll(HttpServletRequest request, 
                                   @RequestParam(value = "category", required = false) String category, 
                                   @RequestParam(value = "sort", required = false, defaultValue = "latest") String sort) {
        List<Post> posts;
        
        if (category != null) {
            posts = postService.findByCategory(category, sort);
        } else {
            posts = postService.findAll(sort);
        }

        Long userId = getCurrentUserId(request);

        // 构建帖子响应列表，包含作者头像
        List<Map<String, Object>> postResponses = new ArrayList<>();
        for (Post post : posts) {
            boolean isLiked = false;
            if (userId != null) {
                isLiked = likeService.checkLike(userId, post.getId(), "post");
            }
            
            // 创建帖子响应对象
            Map<String, Object> postResponse = new HashMap<>();
            postResponse.put("id", post.getId());
            postResponse.put("title", post.getTitle());
            postResponse.put("content", post.getContent());
            postResponse.put("authorId", post.getAuthorId());
            postResponse.put("authorName", post.getAuthorName());
            postResponse.put("category", post.getCategory());
            postResponse.put("coverImage", post.getCoverImage());
            postResponse.put("images", post.getImages());
            postResponse.put("videoUrl", post.getVideoUrl());
            postResponse.put("videoCover", post.getVideoCover());
            postResponse.put("viewCount", post.getViewCount());
            postResponse.put("likeCount", post.getLikeCount());
            postResponse.put("commentCount", post.getCommentCount());
            postResponse.put("createdAt", post.getCreatedAt());
            postResponse.put("isLiked", isLiked);
            
            // 查询作者信息，获取头像
            if (post.getAuthorId() != null) {
                User author = userService.findById(post.getAuthorId());
                if (author != null) {
                    postResponse.put("authorAvatar", author.getAvatar());
                }
            }
            
            postResponses.add(postResponse);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("data", postResponses);
        return ResponseEntity.ok(response);
    }

    /**
     * 根据ID获取帖子详情
     * 
     * 功能说明：
     * - 根据帖子ID查询帖子详情
     * - 自动增加帖子的浏览次数
     * - 返回当前用户是否已点赞该帖子
     * 
     * @param id 帖子ID
     * @param request HTTP请求对象，用于获取当前用户ID
     * @return ResponseEntity 包含帖子详情的响应对象
     *         - data: 帖子对象
     *         - isLiked: 当前用户是否已点赞
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id, HttpServletRequest request) {
        Post post = postService.findById(id);
        if (post != null) {
            postService.incrementViewCount(id);

            Long userId = getCurrentUserId(request);
            boolean isLiked = false;
            if (userId != null) {
                isLiked = likeService.checkLike(userId, id, "post");
            }

            Map<String, Object> response = new HashMap<>();
            response.put("data", post);
            response.put("isLiked", isLiked);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body("帖子不存在");
    }

    /**
     * 获取当前登录用户发布的所有帖子
     * 
     * 功能说明：
     * - 从JWT token中解析当前用户信息
     * - 查询该用户发布的所有帖子（包括未审核的）
     * - 用于个人中心展示"我的帖子"
     * 
     * @param request HTTP请求对象，包含Authorization头
     * @return ResponseEntity 包含用户帖子列表的响应对象
     *         - data: 当前用户的帖子列表
     */
    @GetMapping("/my")
    public ResponseEntity<?> findMyPosts(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                    token = token.substring(7);
                    String username = jwtUtil.getUsernameFromToken(token);
                    User user = userService.findByNickname(username);
                    if (user != null) {
                        List<Post> posts = postService.findByAuthorId(user.getId());
                        Map<String, Object> response = new HashMap<>();
                        response.put("data", posts);
                        return ResponseEntity.ok(response);
                    }
                }
            return ResponseEntity.badRequest().body("获取我的帖子失败");
        } catch (Exception e) {
            System.out.println("获取我的帖子失败: " + e.getMessage());
            return ResponseEntity.badRequest().body("获取我的帖子失败: " + e.getMessage());
        }
    }

    /**
     * 根据作者ID获取该作者发布的所有帖子
     * 
     * 功能说明：
     * - 查询指定作者的所有帖子
     * - 用于查看某个用户的帖子列表
     * 
     * @param authorId 作者ID
     * @return ResponseEntity 包含该作者帖子列表的响应对象
     *         - data: 该作者的帖子列表
     */
    @GetMapping("/author/{authorId}")
    public ResponseEntity<?> findByAuthorId(@PathVariable Long authorId) {
        List<Post> posts = postService.findByAuthorId(authorId);
        Map<String, Object> response = new HashMap<>();
        response.put("data", posts);
        return ResponseEntity.ok(response);
    }

    /**
     * 点赞或取消点赞帖子
     * 
     * 功能说明：
     * - 切换当前用户对指定帖子的点赞状态
     * - 如果已点赞则取消点赞，未点赞则点赞
     * - 需要用户登录
     * 
     * @param id 帖子ID
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

        LikeService.LikeResult result = likeService.toggleLike(userId, id, "post");
        Map<String, Object> response = new HashMap<>();
        response.put("liked", result.isLiked());
        response.put("likeCount", result.getLikeCount());
        response.put("message", result.isLiked() ? "点赞成功" : "取消点赞");
        return ResponseEntity.ok(response);
    }

    /**
     * 删除指定帖子
     * 
     * 功能说明：
     * - 根据帖子ID删除帖子
     * - 同时删除相关的评论和点赞记录
     * - 注意：删除操作不可恢复
     * 
     * @param id 要删除的帖子ID
     * @return ResponseEntity 包含删除结果的响应对象
     *         - message: 操作提示信息
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        postService.delete(id);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "删除成功");
        return ResponseEntity.ok(response);
    }

    /**
     * 内容安全审核接口
     * 
     * 功能说明：
     * - 用于前端在发布前预检测内容
     * - 不实际创建帖子，仅返回审核结果
     * - 帮助用户提前了解内容是否合规
     * 
     * @param request 请求体，包含要检测的内容
     *         - content: 待检测的文本内容
     * @return ResponseEntity 包含审核结果的响应对象
     *         - success: 操作是否成功
     *         - data: 审核结果对象
     *             - passed: 是否通过审核
     *             - riskLevel: 风险等级
     *             - suggestion: 处理建议
     */
    @PostMapping("/check-content")
    public ResponseEntity<?> checkContent(@RequestBody Map<String, String> request) {
        String content = request.get("content");
        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("内容不能为空");
        }

        ContentCheckResult result = contentSecurityService.checkText(content, "post");
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", result);
        return ResponseEntity.ok(response);
    }

    /**
     * 管理端：获取所有帖子（包括未审核的）
     * 
     * 功能说明：
     * - 返回所有帖子，不受审核状态限制
     * - 用于管理员审核帖子
     * - 按创建时间倒序排列
     * 
     * @return ResponseEntity 包含所有帖子列表的响应对象
     *         - data: 所有帖子列表
     */
    @GetMapping("/admin/all")
    public ResponseEntity<?> findAllForAdmin() {
        List<Post> posts = postService.findAllForAdmin();
        Map<String, Object> response = new HashMap<>();
        response.put("data", posts);
        return ResponseEntity.ok(response);
    }

    /**
     * 管理端：根据审核状态查询帖子
     * 
     * 功能说明：
     * - 按审核状态筛选帖子
     * - 支持的状态：PENDING（待审核）、APPROVED（已通过）、REJECTED（已拒绝）
     * - 用于管理员快速查看待审核的帖子
     * 
     * @param status 审核状态（PENDING/APPROVED/REJECTED）
     * @return ResponseEntity 包含指定状态的帖子列表的响应对象
     *         - data: 指定审核状态的帖子列表
     */
    @GetMapping("/admin/status/{status}")
    public ResponseEntity<?> findByAuditStatus(@PathVariable String status) {
        List<Post> posts = postService.findByAuditStatus(status);
        Map<String, Object> response = new HashMap<>();
        response.put("data", posts);
        return ResponseEntity.ok(response);
    }

    /**
     * 管理端：审核帖子
     * 
     * 功能说明：
     * - 管理员对帖子进行审核操作
     * - 可以通过或拒绝帖子
     * - 可以添加审核备注
     * - 自动记录审核时间
     * 
     * @param id 帖子ID
     * @param request 请求体，包含审核信息
     *         - auditStatus: 审核状态（APPROVED/REJECTED）
     *         - auditRemark: 审核备注（可选）
     * @return ResponseEntity 包含审核结果的响应对象
     *         - success: 操作是否成功
     *         - message: 操作提示信息
     */
    @PostMapping("/admin/{id}/audit")
    public ResponseEntity<?> auditPost(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String auditStatus = request.get("auditStatus");
        String auditRemark = request.get("auditRemark");
        
        if (auditStatus == null || (!auditStatus.equals("APPROVED") && !auditStatus.equals("REJECTED"))) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "审核状态无效");
            return ResponseEntity.badRequest().body(response);
        }
        
        postService.updateAuditStatus(id, auditStatus, auditRemark);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "审核完成");
        return ResponseEntity.ok(response);
    }
}
