package com.antifraud.controller;

import com.antifraud.model.*;
import com.antifraud.dto.*;
import com.antifraud.service.*;
import com.antifraud.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员控制器
 * 
 * 功能说明：
 * 1. 提供管理员登录接口
 * 2. 提供用户管理接口（查询、更新、删除）
 * 3. 提供知识管理接口（查询、创建、删除）
 * 4. 提供案例管理接口（查询、创建、删除）
 * 5. 提供帖子管理接口（查询、删除）
 * 6. 提供举报管理接口（查询、更新状态）
 * 7. 提供统计数据接口
 * 
 * 所有接口都需要管理员权限验证
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private KnowledgeService knowledgeService;

    @Autowired
    private CaseService caseService;

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserProfileChangeService userProfileChangeService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 从请求中获取当前管理员ID
     * 
     * 功能说明：
     * - 从请求头的 Authorization 字段中提取 JWT token
     * - 解析 token 获取用户名
     * - 验证用户是否具有管理员权限
     * - 返回管理员ID
     * 
     * @param request HTTP请求对象，包含请求头信息
     * @return 管理员ID，如果未登录或没有管理员权限则返回null
     */
    private Long getCurrentAdminId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                if (jwtUtil.validateToken(token)) {
                        String username = jwtUtil.getUsernameFromToken(token);
                        User user = userService.findByNickname(username);
                        if (user != null && ("ADMIN".equals(user.getRole()) || "ROLE_ADMIN".equals(user.getRole()))) {
                            return user.getId();
                        }
                    }
            } catch (Exception e) {
                System.out.println("获取管理员ID异常: " + e.getMessage());
            }
        }
        return null;
    }

    /**
     * 管理员登录
     * 
     * 功能说明：
     * - 验证管理员用户名和密码
     * - 检查用户是否具有管理员权限
     * - 生成 JWT token 用于后续请求的身份验证
     * - 返回 token 和管理员信息
     * 
     * @param credentials 登录凭证
     *         - username: 用户名
     *         - password: 密码
     * @return ResponseEntity 包含登录结果的响应对象
     *         - token: JWT 认证令牌
     *         - user: 管理员对象
     */
    @PostMapping("/login")
    public ResponseEntity<?> adminLogin(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        System.out.println("管理员登录请求: username=" + username);

        try {
            User user = userService.login(username, password);
            if (user != null) {
                if ("ADMIN".equals(user.getRole()) || "ROLE_ADMIN".equals(user.getRole())) {
                    String token = jwtUtil.generateToken(user.getNickname());
                    Map<String, Object> response = new HashMap<>();
                    response.put("token", token);
                    response.put("user", user);
                    return ResponseEntity.ok(response);
                } else {
                    return ResponseEntity.badRequest().body("该账户没有管理员权限");
                }
            }
            return ResponseEntity.badRequest().body("用户名或密码错误");
        } catch (Exception e) {
            System.out.println("管理员登录失败: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("登录失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有用户列表
     * 
     * 功能说明：
     * - 查询所有用户信息
     * - 用于管理端用户管理
     * - 需要管理员权限
     * 
     * @param request HTTP请求对象，用于权限验证
     * @return ResponseEntity 包含用户列表的响应对象
     *         - data: 用户对象列表
     */
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(HttpServletRequest request) {
        Long adminId = getCurrentAdminId(request);
        if (adminId == null) {
            return ResponseEntity.status(401).body("未登录或没有管理员权限");
        }
        List<User> users = userService.findAll();
        Map<String, Object> response = new HashMap<>();
        response.put("data", users);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取待审核用户列表
     * 
     * 功能说明：
     * - 查询所有待审核的用户
     * - 用于管理端用户审核
     * - 需要管理员权限
     * 
     * @param request HTTP请求对象，用于权限验证
     * @return ResponseEntity 包含待审核用户列表的响应对象
     *         - data: 用户对象列表
     */
    @GetMapping("/users/pending")
    public ResponseEntity<?> getPendingUsers(HttpServletRequest request) {
        Long adminId = getCurrentAdminId(request);
        if (adminId == null) {
            return ResponseEntity.status(401).body("未登录或没有管理员权限");
        }
        List<User> pendingUsers = userService.findByAccountStatus("PENDING");
        Map<String, Object> response = new HashMap<>();
        response.put("data", pendingUsers);
        return ResponseEntity.ok(response);
    }

    /**
     * 审核用户
     * 
     * 功能说明：
     * - 审核用户注册申请
     * - 可以通过或拒绝用户注册
     * - 需要管理员权限
     * 
     * @param id 用户ID
     * @param body 请求体，包含审核信息
     *         - action: 审核操作（APPROVE-通过,REJECT-拒绝）
     *         - remark: 审核备注
     * @param request HTTP请求对象，用于权限验证
     * @return ResponseEntity 包含审核结果的响应对象
     *         - message: 提示信息
     */
    @PostMapping("/users/{id}/audit")
    public ResponseEntity<?> auditUser(@PathVariable Long id, @RequestBody Map<String, String> body, HttpServletRequest request) {
        Long adminId = getCurrentAdminId(request);
        if (adminId == null) {
            return ResponseEntity.status(401).body("未登录或没有管理员权限");
        }
        
        String action = body.get("action");
        String remark = body.get("remark");
        
        if (!"APPROVE".equals(action) && !"REJECT".equals(action)) {
            return ResponseEntity.badRequest().body("无效的审核操作");
        }
        
        User user = userService.auditUser(id, adminId, action, remark);
        if (user != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "APPROVE".equals(action) ? "审核通过" : "审核拒绝");
            response.put("data", user);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body("用户不存在");
    }

    /**
     * 批量审核用户
     * 
     * 功能说明：
     * - 批量审核用户注册申请
     * - 可以通过或拒绝多个用户
     * - 需要管理员权限
     * 
     * @param body 请求体，包含审核信息
     *         - userIds: 用户ID列表
     *         - action: 审核操作（APPROVE-通过,REJECT-拒绝）
     *         - remark: 审核备注
     * @param request HTTP请求对象，用于权限验证
     * @return ResponseEntity 包含审核结果的响应对象
     *         - message: 提示信息
     */
    @PostMapping("/users/batch-audit")
    public ResponseEntity<?> batchAuditUsers(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        Long adminId = getCurrentAdminId(request);
        if (adminId == null) {
            return ResponseEntity.status(401).body("未登录或没有管理员权限");
        }
        
        @SuppressWarnings("unchecked")
        List<Long> userIds = (List<Long>) body.get("userIds");
        String action = (String) body.get("action");
        String remark = (String) body.get("remark");
        
        if (!"APPROVE".equals(action) && !"REJECT".equals(action)) {
            return ResponseEntity.badRequest().body("无效的审核操作");
        }
        
        int successCount = 0;
        for (Long userId : userIds) {
            if (userService.auditUser(userId, adminId, action, remark) != null) {
                successCount++;
            }
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", String.format("批量审核完成，成功处理%d个用户", successCount));
        response.put("successCount", successCount);
        return ResponseEntity.ok(response);
    }

    /**
     * 根据ID获取用户信息
     * 
     * 功能说明：
     * - 查询指定用户的详细信息
     * - 用于管理端查看用户详情
     * - 需要管理员权限
     * 
     * @param id 用户ID
     * @param request HTTP请求对象，用于权限验证
     * @return ResponseEntity 包含用户信息的响应对象
     *         - data: 用户对象
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id, HttpServletRequest request) {
        Long adminId = getCurrentAdminId(request);
        if (adminId == null) {
            return ResponseEntity.status(401).body("未登录或没有管理员权限");
        }
        User user = userService.findById(id);
        if (user != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("data", user);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body("用户不存在");
    }

    /**
     * 更新用户信息
     * 
     * 功能说明：
     * - 更新指定用户的信息
     * - 用于管理端用户管理
     * - 需要管理员权限
     * 
     * @param id 用户ID
     * @param userDTO 用户数据传输对象，包含要更新的字段
     * @param request HTTP请求对象，用于权限验证
     * @return ResponseEntity 包含更新结果的响应对象
     *         - message: 提示信息
     *         - data: 更新后的用户对象
     */
    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO, HttpServletRequest request) {
        Long adminId = getCurrentAdminId(request);
        if (adminId == null) {
            return ResponseEntity.status(401).body("未登录或没有管理员权限");
        }
        User user = userService.updateProfile(id, userDTO);
        if (user != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "更新成功");
            response.put("data", user);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body("更新失败");
    }
    
    /**
     * 获取待审核的用户资料变更记录
     * 
     * 功能说明：
     * - 获取所有待审核的用户资料变更记录
     * - 需要管理员权限
     * 
     * @param request HTTP请求对象，用于权限验证
     * @return ResponseEntity 包含待审核变更记录的响应对象
     *         - data: 待审核变更记录列表
     */
    @GetMapping("/profile-changes/pending")
    public ResponseEntity<?> getPendingProfileChanges(HttpServletRequest request) {
        Long adminId = getCurrentAdminId(request);
        if (adminId == null) {
            return ResponseEntity.status(401).body("未登录或没有管理员权限");
        }
        
        List<UserProfileChange> pendingChanges = userProfileChangeService.findPendingChanges();
        Map<String, Object> response = new HashMap<>();
        response.put("data", pendingChanges);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 审核用户资料变更
     * 
     * 功能说明：
     * - 审核用户资料变更记录
     * - 需要管理员权限
     * 
     * @param id 变更记录ID
     * @param body 请求体，包含审核信息
     *         - action: 审核操作（APPROVE-通过,REJECT-拒绝）
     * @param request HTTP请求对象，用于权限验证
     * @return ResponseEntity 包含审核结果的响应对象
     *         - message: 提示信息
     *         - data: 审核后的变更记录
     */
    @PostMapping("/profile-changes/{id}/audit")
    public ResponseEntity<?> auditProfileChange(@PathVariable Long id, @RequestBody Map<String, String> body, HttpServletRequest request) {
        Long adminId = getCurrentAdminId(request);
        if (adminId == null) {
            return ResponseEntity.status(401).body("未登录或没有管理员权限");
        }
        
        String action = body.get("action");
        if (!"APPROVE".equals(action) && !"REJECT".equals(action)) {
            return ResponseEntity.badRequest().body("无效的审核操作");
        }
        
        UserProfileChange change = userProfileChangeService.auditChange(id, action);
        if (change != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "APPROVE".equals(action) ? "审核通过" : "审核拒绝");
            response.put("data", change);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body("变更记录不存在");
    }

    /**
     * 删除用户
     * 
     * 功能说明：
     * - 删除指定用户
     * - 注意：删除操作不可恢复
     * - 需要管理员权限
     * 
     * @param id 要删除的用户ID
     * @param request HTTP请求对象，用于权限验证
     * @return ResponseEntity 包含删除结果的响应对象
     *         - message: 提示信息
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, HttpServletRequest request) {
        Long adminId = getCurrentAdminId(request);
        if (adminId == null) {
            return ResponseEntity.status(401).body("未登录或没有管理员权限");
        }
        userService.delete(id);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "删除成功");
        return ResponseEntity.ok(response);
    }

    /**
     * 重置用户密码
     * 
     * 功能说明：
     * - 重置指定用户的密码为默认密码123456
     * - 需要管理员权限
     * 
     * @param id 用户ID
     * @param body 请求体，包含新密码
     *         - password: 新密码，默认为123456
     * @param request HTTP请求对象，用于权限验证
     * @return ResponseEntity 包含重置结果的响应对象
     *         - message: 提示信息
     */
    @PostMapping("/users/{id}/reset-password")
    public ResponseEntity<?> resetUserPassword(@PathVariable Long id, @RequestBody Map<String, String> body, HttpServletRequest request) {
        Long adminId = getCurrentAdminId(request);
        if (adminId == null) {
            return ResponseEntity.status(401).body("未登录或没有管理员权限");
        }
        
        String newPassword = body.get("password");
        if (newPassword == null || newPassword.trim().isEmpty()) {
            newPassword = "123456";
        }
        
        User user = userService.findById(id);
        if (user == null) {
            return ResponseEntity.badRequest().body("用户不存在");
        }
        
        UserDTO userDTO = new UserDTO();
        userDTO.setPassword(newPassword);
        userService.updateProfile(id, userDTO);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "密码重置成功");
        return ResponseEntity.ok(response);
    }

    /**
     * 获取所有知识列表
     * 
     * 功能说明：
     * - 查询所有防诈骗知识
     * - 用于管理端知识管理
     * - 需要管理员权限
     * 
     * @param request HTTP请求对象，用于权限验证
     * @return ResponseEntity 包含知识列表的响应对象
     *         - data: 知识对象列表
     */
    @GetMapping("/knowledge")
    public ResponseEntity<?> getAllKnowledge(HttpServletRequest request) {
        Long adminId = getCurrentAdminId(request);
        if (adminId == null) {
            return ResponseEntity.status(401).body("未登录或没有管理员权限");
        }
        List<Knowledge> knowledgeList = knowledgeService.findAll();
        Map<String, Object> response = new HashMap<>();
        response.put("data", knowledgeList);
        return ResponseEntity.ok(response);
    }

    /**
     * 创建新知识
     * 
     * 功能说明：
     * - 创建新的防诈骗知识
     * - 用于管理端知识管理
     * - 需要管理员权限
     * 
     * @param knowledgeDTO 知识数据传输对象
     * @param request HTTP请求对象，用于权限验证
     * @return ResponseEntity 包含创建结果的响应对象
     *         - message: 提示信息
     *         - data: 创建的知识对象
     */
    @PostMapping("/knowledge")
    public ResponseEntity<?> createKnowledge(@RequestBody KnowledgeDTO knowledgeDTO, HttpServletRequest request) {
        Long adminId = getCurrentAdminId(request);
        if (adminId == null) {
            return ResponseEntity.status(401).body("未登录或没有管理员权限");
        }
        Knowledge knowledge = knowledgeService.create(knowledgeDTO);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "创建成功");
        response.put("data", knowledge);
        return ResponseEntity.ok(response);
    }

    /**
     * 删除知识
     * 
     * 功能说明：
     * - 删除指定的知识
     * - 注意：删除操作不可恢复
     * - 需要管理员权限
     * 
     * @param id 要删除的知识ID
     * @param request HTTP请求对象，用于权限验证
     * @return ResponseEntity 包含删除结果的响应对象
     *         - message: 提示信息
     */
    @DeleteMapping("/knowledge/{id}")
    public ResponseEntity<?> deleteKnowledge(@PathVariable Long id, HttpServletRequest request) {
        Long adminId = getCurrentAdminId(request);
        if (adminId == null) {
            return ResponseEntity.status(401).body("未登录或没有管理员权限");
        }
        knowledgeService.delete(id);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "删除成功");
        return ResponseEntity.ok(response);
    }

    /**
     * 获取所有案例列表
     * 
     * 功能说明：
     * - 查询所有诈骗案例
     * - 用于管理端案例管理
     * - 需要管理员权限
     * 
     * @param request HTTP请求对象，用于权限验证
     * @return ResponseEntity 包含案例列表的响应对象
     *         - data: 案例对象列表
     */
    @GetMapping("/cases")
    public ResponseEntity<?> getAllCases(HttpServletRequest request) {
        Long adminId = getCurrentAdminId(request);
        if (adminId == null) {
            return ResponseEntity.status(401).body("未登录或没有管理员权限");
        }
        List<Case> cases = caseService.findAll();
        Map<String, Object> response = new HashMap<>();
        response.put("data", cases);
        return ResponseEntity.ok(response);
    }

    /**
     * 创建新案例
     * 
     * 功能说明：
     * - 创建新的诈骗案例
     * - 用于管理端案例管理
     * - 需要管理员权限
     * 
     * @param caseDTO 案例数据传输对象
     * @param request HTTP请求对象，用于权限验证
     * @return ResponseEntity 包含创建结果的响应对象
     *         - message: 提示信息
     *         - data: 创建的案例对象
     */
    @PostMapping("/cases")
    public ResponseEntity<?> createCase(@RequestBody CaseDTO caseDTO, HttpServletRequest request) {
        Long adminId = getCurrentAdminId(request);
        if (adminId == null) {
            return ResponseEntity.status(401).body("未登录或没有管理员权限");
        }
        Case c = caseService.create(caseDTO);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "创建成功");
        response.put("data", c);
        return ResponseEntity.ok(response);
    }

    /**
     * 删除案例
     * 
     * 功能说明：
     * - 删除指定的案例
     * - 注意：删除操作不可恢复
     * - 需要管理员权限
     * 
     * @param id 要删除的案例ID
     * @param request HTTP请求对象，用于权限验证
     * @return ResponseEntity 包含删除结果的响应对象
     *         - message: 提示信息
     */
    @DeleteMapping("/cases/{id}")
    public ResponseEntity<?> deleteCase(@PathVariable Long id, HttpServletRequest request) {
        Long adminId = getCurrentAdminId(request);
        if (adminId == null) {
            return ResponseEntity.status(401).body("未登录或没有管理员权限");
        }
        caseService.delete(id);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "删除成功");
        return ResponseEntity.ok(response);
    }

    /**
     * 获取所有帖子列表
     * 
     * 功能说明：
     * - 查询所有帖子
     * - 用于管理端帖子管理
     * - 需要管理员权限
     * 
     * @param request HTTP请求对象，用于权限验证
     * @return ResponseEntity 包含帖子列表的响应对象
     *         - data: 帖子对象列表
     */
    @GetMapping("/posts")
    public ResponseEntity<?> getAllPosts(HttpServletRequest request) {
        Long adminId = getCurrentAdminId(request);
        if (adminId == null) {
            return ResponseEntity.status(401).body("未登录或没有管理员权限");
        }
        List<Post> posts = postService.findAll();
        Map<String, Object> response = new HashMap<>();
        response.put("data", posts);
        return ResponseEntity.ok(response);
    }

    /**
     * 删除帖子
     * 
     * 功能说明：
     * - 删除指定的帖子
     * - 注意：删除操作不可恢复
     * - 需要管理员权限
     * 
     * @param id 要删除的帖子ID
     * @param request HTTP请求对象，用于权限验证
     * @return ResponseEntity 包含删除结果的响应对象
     *         - message: 提示信息
     */
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id, HttpServletRequest request) {
        Long adminId = getCurrentAdminId(request);
        if (adminId == null) {
            return ResponseEntity.status(401).body("未登录或没有管理员权限");
        }
        postService.delete(id);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "删除成功");
        return ResponseEntity.ok(response);
    }

    /**
     * 获取所有举报列表
     * 
     * 功能说明：
     * - 查询所有举报记录
     * - 用于管理端举报管理
     * - 需要管理员权限
     * 
     * @param request HTTP请求对象，用于权限验证
     * @return ResponseEntity 包含举报列表的响应对象
     *         - data: 举报对象列表
     */
    @GetMapping("/reports")
    public ResponseEntity<?> getAllReports(HttpServletRequest request) {
        Long adminId = getCurrentAdminId(request);
        if (adminId == null) {
            return ResponseEntity.status(401).body("未登录或没有管理员权限");
        }
        List<Report> reports = reportService.findAll();
        Map<String, Object> response = new HashMap<>();
        response.put("data", reports);
        return ResponseEntity.ok(response);
    }

    /**
     * 根据ID获取举报详情
     * 
     * 功能说明：
     * - 查询指定举报的详细信息
     * - 用于管理端查看举报详情
     * - 需要管理员权限
     * 
     * @param id 举报ID
     * @param request HTTP请求对象，用于权限验证
     * @return ResponseEntity 包含举报详情的响应对象
     *         - data: 举报对象
     */
    @GetMapping("/reports/{id}")
    public ResponseEntity<?> getReportById(@PathVariable Long id, HttpServletRequest request) {
        Long adminId = getCurrentAdminId(request);
        if (adminId == null) {
            return ResponseEntity.status(401).body("未登录或没有管理员权限");
        }
        Report report = reportService.findById(id);
        if (report != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("data", report);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body("举报不存在");
    }

    /**
     * 更新举报状态
     * 
     * 功能说明：
     * - 更新指定举报的处理状态
     * - 用于管理端处理举报
     * - 需要管理员权限
     * 
     * @param id 举报ID
     * @param body 请求体，包含新的状态
     *         - status: 新的举报状态
     *         - handleResult: 处理结果说明
     * @param request HTTP请求对象，用于权限验证
     * @return ResponseEntity 包含更新结果的响应对象
     *         - message: 提示信息
     */
    @PutMapping("/reports/{id}/status")
    public ResponseEntity<?> updateReportStatus(@PathVariable Long id, @RequestBody Map<String, String> body, HttpServletRequest request) {
        Long adminId = getCurrentAdminId(request);
        if (adminId == null) {
            return ResponseEntity.status(401).body("未登录或没有管理员权限");
        }
        String status = body.get("status");
        String handleResult = body.get("handleResult");
        reportService.updateStatus(id, status, handleResult);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "状态更新成功");
        return ResponseEntity.ok(response);
    }

    /**
     * 获取平台统计数据
     * 
     * 功能说明：
     * - 统计平台各项数据
     * - 包括用户数、知识数、案例数、帖子数、举报数
     * - 用于管理端数据看板
     * - 需要管理员权限
     * 
     * @param request HTTP请求对象，用于权限验证
     * @return ResponseEntity 包含统计数据的响应对象
     *         - data: 统计数据对象
     *             - userCount: 用户总数
     *             - knowledgeCount: 知识总数
     *             - caseCount: 案例总数
     *             - postCount: 帖子总数
     *             - reportCount: 举报总数
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getStats(HttpServletRequest request) {
        Long adminId = getCurrentAdminId(request);
        if (adminId == null) {
            return ResponseEntity.status(401).body("未登录或没有管理员权限");
        }
        Map<String, Object> stats = new HashMap<>();
        stats.put("userCount", userService.findAll().size());
        stats.put("knowledgeCount", knowledgeService.findAll().size());
        stats.put("caseCount", caseService.findAll().size());
        stats.put("postCount", postService.findAll().size());
        stats.put("reportCount", reportService.findAll().size());
        stats.put("commentCount", commentService.findAll().size());
        stats.put("pendingUsers", userService.findByAccountStatus("PENDING").size());
        
        int totalViews = 0;
        for (Post post : postService.findAll()) {
            totalViews += post.getViewCount() != null ? post.getViewCount() : 0;
        }
        stats.put("totalViews", totalViews);

        Map<String, Object> response = new HashMap<>();
        response.put("data", stats);
        return ResponseEntity.ok(response);
    }
}
