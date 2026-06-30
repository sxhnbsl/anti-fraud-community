package com.antifraud.controller;

import com.antifraud.model.Comment;
import com.antifraud.model.User;
import com.antifraud.dto.CommentDTO;
import com.antifraud.dto.ContentCheckResult;
import com.antifraud.service.CommentService;
import com.antifraud.service.ContentSecurityService;
import com.antifraud.service.LikeService;
import com.antifraud.service.UserService;
import com.antifraud.service.PostService;
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
 * 评论管理控制器
 * 
 * 功能说明：
 * 1. 提供评论的增删改查接口
 * 2. 集成内容安全审核功能，自动检测敏感内容
 * 3. 支持点赞功能
 * 4. 提供管理端审核接口
 * 5. 支持按帖子ID、审核状态等条件查询
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private ContentSecurityService contentSecurityService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    /**
     * 从请求头中获取当前登录用户的ID
     * 
     * 功能说明：
     * - 从HTTP请求头的Authorization字段中提取JWT token
     * - 验证token的有效性
     * - 从token中解析出用户名
     * - 通过用户名查询用户对象，获取用户ID
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
     * 创建新评论
     * 
     * 功能说明：
     * - 接收前端提交的评论数据
     * - 自动调用内容安全审核服务检测敏感内容
     * - 根据审核结果设置评论审核状态：
     *   - 审核通过：直接发布（APPROVED）
     *   - 风险较高：进入待审核状态（PENDING）
     *   - 严重违规：拒绝发布（REJECT）
     * - 返回创建结果和审核状态
     * 
     * @param commentDTO 评论数据传输对象，包含内容、帖子ID、作者ID等信息
     * @return ResponseEntity 包含操作结果的响应对象
     *         - success: 操作是否成功
     *         - message: 提示信息
     *         - data: 创建的评论对象
     *         - auditStatus: 审核状态（APPROVED/PENDING）
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody CommentDTO commentDTO) {
        System.out.println("创建评论: " + commentDTO.getContent());
        try {
            String contentToCheck = commentDTO.getContent();
            ContentCheckResult checkResult = contentSecurityService.checkText(contentToCheck, "comment");
            
            String auditStatus;
            if (!checkResult.isPassed()) {
                if (checkResult.getRiskLevel() == ContentCheckResult.RiskLevel.REJECT) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "评论内容包含违规信息，禁止发布");
                    response.put("data", checkResult);
                    return ResponseEntity.ok(response);
                } else {
                    auditStatus = Comment.AUDIT_PENDING;
                }
            } else {
                auditStatus = Comment.AUDIT_APPROVED;
            }

            Comment comment = commentService.createWithAudit(commentDTO, auditStatus);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            if (Comment.AUDIT_PENDING.equals(auditStatus)) {
                response.put("message", "评论已提交，等待审核");
            } else {
                response.put("message", "评论成功");
            }
            response.put("data", comment);
            response.put("auditStatus", auditStatus);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("创建失败: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("创建失败: " + e.getMessage());
        }
    }

    /**
     * 根据帖子ID获取该帖子的所有评论
     * 
     * 功能说明：
     * - 查询指定帖子的所有评论
     * - 只返回审核通过的评论
     * - 按创建时间正序排列（最早的在前）
     * - 检查当前用户是否已点赞
     * 
     * @param postId 帖子ID
     * @param request HTTP请求对象，用于获取当前用户ID
     * @return ResponseEntity 包含评论列表的响应对象
     *         - data: 该帖子的评论列表
     */
    @GetMapping("/post/{postId}")
    public ResponseEntity<?> findByPostId(
            @PathVariable Long postId,
            HttpServletRequest request) {
        List<Comment> comments = commentService.findByPostId(postId);
        
        // 获取当前用户ID
        Long userId = getCurrentUserId(request);
        
        // 构建评论响应列表，包含点赞状态
        List<Map<String, Object>> commentResponses = new ArrayList<>();
        for (Comment comment : comments) {
            Map<String, Object> commentMap = new HashMap<>();
            commentMap.put("id", comment.getId());
            commentMap.put("postId", comment.getPostId());
            commentMap.put("authorId", comment.getAuthorId());
            commentMap.put("authorName", comment.getAuthorName());
            commentMap.put("content", comment.getContent());
            commentMap.put("parentId", comment.getParentId());
            commentMap.put("likeCount", comment.getLikeCount());
            commentMap.put("status", comment.getStatus());
            commentMap.put("auditStatus", comment.getAuditStatus());
            commentMap.put("auditRemark", comment.getAuditRemark());
            commentMap.put("auditTime", comment.getAuditTime());
            commentMap.put("createdAt", comment.getCreatedAt());
            commentMap.put("updatedAt", comment.getUpdatedAt());
            
            // 检查是否已点赞
            boolean isLiked = false;
            if (userId != null) {
                isLiked = likeService.checkLike(userId, comment.getId(), "comment");
            }
            commentMap.put("isLiked", isLiked);
            
            commentResponses.add(commentMap);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("data", commentResponses);
        return ResponseEntity.ok(response);
    }

    /**
     * 根据ID获取评论详情
     * 
     * 功能说明：
     * - 根据评论ID查询评论详情
     * - 用于查看单条评论的完整信息
     * 
     * @param id 评论ID
     * @return ResponseEntity 包含评论详情的响应对象
     *         - data: 评论对象
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        Comment comment = commentService.findById(id);
        if (comment != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("data", comment);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body("评论不存在");
    }

    /**
     * 点赞或取消点赞评论
     * 
     * 功能说明：
     * - 切换当前用户对指定评论的点赞状态
     * - 如果已点赞则取消点赞，未点赞则点赞
     * - 需要用户登录
     * 
     * @param id 评论ID
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

        LikeService.LikeResult result = likeService.toggleLike(userId, id, "comment");
        Map<String, Object> response = new HashMap<>();
        response.put("liked", result.isLiked());
        response.put("likeCount", result.getLikeCount());
        response.put("message", result.isLiked() ? "点赞成功" : "取消点赞");
        return ResponseEntity.ok(response);
    }

    /**
     * 删除指定评论
     * 
     * 功能说明：
     * - 根据评论ID删除评论
     * - 删除后减少帖子的评论数
     * - 注意：删除操作不可恢复
     * 
     * @param id 要删除的评论ID
     * @return ResponseEntity 包含删除结果的响应对象
     *         - message: 操作提示信息
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        // 先获取评论信息，用于减少帖子评论数
        Comment comment = commentService.findById(id);
        if (comment != null) {
            Long postId = comment.getPostId();
            commentService.delete(id);
            // 减少帖子评论数
            postService.decrementCommentCount(postId);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "删除成功");
        return ResponseEntity.ok(response);
    }

    /**
     * 内容安全审核接口
     * 
     * 功能说明：
     * - 用于前端在发布前预检测评论内容
     * - 不实际创建评论，仅返回审核结果
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

        ContentCheckResult result = contentSecurityService.checkText(content, "comment");
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", result);
        return ResponseEntity.ok(response);
    }

    /**
     * 管理端：获取所有评论（包括未审核的）
     * 
     * 功能说明：
     * - 返回所有评论，不受审核状态限制
     * - 用于管理员审核评论
     * - 按创建时间倒序排列
     * 
     * @return ResponseEntity 包含所有评论列表的响应对象
     *         - data: 所有评论列表
     */
    @GetMapping("/admin/all")
    public ResponseEntity<?> findAllForAdmin() {
        List<Comment> comments = commentService.findAll();
        Map<String, Object> response = new HashMap<>();
        response.put("data", comments);
        return ResponseEntity.ok(response);
    }

    /**
     * 管理端：根据审核状态查询评论
     * 
     * 功能说明：
     * - 按审核状态筛选评论
     * - 支持的状态：PENDING（待审核）、APPROVED（已通过）、REJECTED（已拒绝）
     * - 用于管理员快速查看待审核的评论
     * 
     * @param status 审核状态（PENDING/APPROVED/REJECTED）
     * @return ResponseEntity 包含指定状态的评论列表的响应对象
     *         - data: 指定审核状态的评论列表
     */
    @GetMapping("/admin/status/{status}")
    public ResponseEntity<?> findByAuditStatus(@PathVariable String status) {
        List<Comment> comments = commentService.findByAuditStatus(status);
        Map<String, Object> response = new HashMap<>();
        response.put("data", comments);
        return ResponseEntity.ok(response);
    }

    /**
     * 管理端：审核评论
     * 
     * 功能说明：
     * - 管理员对评论进行审核操作
     * - 可以通过或拒绝评论
     * - 可以添加审核备注
     * - 自动记录审核时间
     * 
     * @param id 评论ID
     * @param request 请求体，包含审核信息
     *         - auditStatus: 审核状态（APPROVED/REJECTED）
     *         - auditRemark: 审核备注（可选）
     * @return ResponseEntity 包含审核结果的响应对象
     *         - success: 操作是否成功
     *         - message: 操作提示信息
     */
    @PostMapping("/admin/{id}/audit")
    public ResponseEntity<?> auditComment(
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
        
        commentService.updateAuditStatus(id, auditStatus, auditRemark);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "审核完成");
        return ResponseEntity.ok(response);
    }
}
