package com.antifraud.controller;

import com.antifraud.model.Report;
import com.antifraud.dto.ReportDTO;
import com.antifraud.service.ReportService;
import com.antifraud.util.JwtUtil;
import com.antifraud.model.User;
import com.antifraud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 举报管理控制器
 * 
 * 功能说明：
 * 1. 提供举报的创建、查询、删除接口
 * 2. 支持按举报人、举报状态等条件查询
 * 3. 支持更新举报状态
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    /**
     * 从请求中获取当前登录用户
     * 
     * @param request HTTP请求对象
     * @return 当前登录用户，未登录返回null
     */
    private User getCurrentUser(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                if (jwtUtil.validateToken(token)) {
                    String username = jwtUtil.getUsernameFromToken(token);
                    return userService.findByNickname(username);
                }
            } catch (Exception e) {
                System.out.println("获取当前用户异常: " + e.getMessage());
            }
        }
        return null;
    }

    /**
     * 创建新的举报
     * 
     * 功能说明：
     * - 接收前端提交的举报数据
     * - 创建举报记录并保存到数据库
     * - 返回创建结果
     * 
     * @param reportDTO 举报数据传输对象，包含举报类型、被举报对象ID、举报原因等信息
     * @return ResponseEntity 包含操作结果的响应对象
     *         - message: 提示信息
     *         - data: 创建的举报对象
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody ReportDTO reportDTO) {
        System.out.println("创建举报: " + reportDTO.getReportedType());
        try {
            Report report = reportService.create(reportDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "举报成功");
            response.put("data", report);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("创建失败: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("创建失败: " + e.getMessage());
        }
    }

    /**
     * 获取举报列表
     * 
     * 功能说明：
     * - 管理员：返回所有举报记录
     * - 普通用户：只返回自己的举报记录
     * - 按创建时间倒序排列（最新的在前）
     * 
     * @param request HTTP请求对象
     * @return ResponseEntity 包含举报列表的响应对象
     *         - data: 举报对象列表
     */
    @GetMapping
    public ResponseEntity<?> findAll(HttpServletRequest request) {
        User currentUser = getCurrentUser(request);
        if (currentUser == null) {
            return ResponseEntity.status(401).body("未登录或token无效");
        }
        
        List<Report> reports;
        if ("ADMIN".equals(currentUser.getRole())) {
            // 管理员可以查看所有举报
            reports = reportService.findAll();
        } else {
            // 普通用户只能查看自己的举报
            reports = reportService.findByReporterId(currentUser.getId());
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("data", reports);
        return ResponseEntity.ok(response);
    }

    /**
     * 根据ID获取举报详情
     * 
     * 功能说明：
     * - 根据举报ID查询举报详情
     * - 管理员：可以查看所有举报详情
     * - 普通用户：只能查看自己的举报详情
     * - 用于查看单条举报的完整信息
     * 
     * @param id 举报ID
     * @param request HTTP请求对象
     * @return ResponseEntity 包含举报详情的响应对象
     *         - data: 举报对象
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id, HttpServletRequest request) {
        User currentUser = getCurrentUser(request);
        if (currentUser == null) {
            return ResponseEntity.status(401).body("未登录或token无效");
        }
        
        Report report = reportService.findById(id);
        if (report != null) {
            // 检查权限：管理员或举报本人
            if ("ADMIN".equals(currentUser.getRole()) || report.getReporterId().equals(currentUser.getId())) {
                Map<String, Object> response = new HashMap<>();
                response.put("data", report);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(403).body("无权限查看此举报");
            }
        }
        return ResponseEntity.badRequest().body("举报不存在");
    }

    /**
     * 根据举报人ID获取举报列表
     * 
     * 功能说明：
     * - 查询指定用户提交的所有举报
     * - 管理员：可以查看任何用户的举报
     * - 普通用户：只能查看自己的举报
     * - 按创建时间倒序排列
     * - 用于用户查看自己的举报记录
     * 
     * @param reporterId 举报人ID
     * @param request HTTP请求对象
     * @return ResponseEntity 包含举报列表的响应对象
     *         - data: 该用户的举报列表
     */
    @GetMapping("/reporter/{reporterId}")
    public ResponseEntity<?> findByReporterId(@PathVariable Long reporterId, HttpServletRequest request) {
        User currentUser = getCurrentUser(request);
        if (currentUser == null) {
            return ResponseEntity.status(401).body("未登录或token无效");
        }
        
        // 检查权限：管理员或查看自己的举报
        if (!"ADMIN".equals(currentUser.getRole()) && !reporterId.equals(currentUser.getId())) {
            return ResponseEntity.status(403).body("无权限查看其他用户的举报");
        }
        
        List<Report> reports = reportService.findByReporterId(reporterId);
        Map<String, Object> response = new HashMap<>();
        response.put("data", reports);
        return ResponseEntity.ok(response);
    }

    /**
     * 根据状态获取举报列表
     * 
     * 功能说明：
     * - 按指定状态筛选举报
     * - 管理员：可以查看所有状态的举报
     * - 普通用户：只能查看自己的指定状态举报
     * - 支持的状态：PENDING（待处理）、PROCESSING（处理中）、RESOLVED（已解决）
     * - 用于管理员快速查看待处理的举报
     * 
     * @param status 举报状态
     * @param request HTTP请求对象
     * @return ResponseEntity 包含指定状态举报列表的响应对象
     *         - data: 指定状态的举报列表
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<?> findByStatus(@PathVariable String status, HttpServletRequest request) {
        User currentUser = getCurrentUser(request);
        if (currentUser == null) {
            return ResponseEntity.status(401).body("未登录或token无效");
        }
        
        List<Report> reports;
        if ("ADMIN".equals(currentUser.getRole())) {
            // 管理员可以查看所有状态的举报
            reports = reportService.findByStatus(status);
        } else {
            // 普通用户只能查看自己的指定状态举报
            // 这里需要在ReportService中添加相应的方法
            // 暂时返回空列表，后续需要实现
            reports = reportService.findByReporterIdAndStatus(currentUser.getId(), status);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("data", reports);
        return ResponseEntity.ok(response);
    }

    /**
     * 更新举报状态
     * 
     * 功能说明：
     * - 更新指定举报的处理状态
     * - 只有管理员可以更新举报状态
     * - 支持的状态：PENDING、PROCESSING、RESOLVED
     * 
     * @param id 举报ID
     * @param body 请求体，包含新的状态
     *         - status: 新的举报状态
     *         - handleResult: 处理结果说明
     * @param request HTTP请求对象
     * @return ResponseEntity 包含更新结果的响应对象
     *         - message: 操作提示信息
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body, HttpServletRequest request) {
        User currentUser = getCurrentUser(request);
        if (currentUser == null) {
            return ResponseEntity.status(401).body("未登录或token无效");
        }
        
        if (!"ADMIN".equals(currentUser.getRole())) {
            return ResponseEntity.status(403).body("无权限更新举报状态");
        }
        
        String status = body.get("status");
        String handleResult = body.get("handleResult");
        reportService.updateStatus(id, status, handleResult);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "状态更新成功");
        return ResponseEntity.ok(response);
    }

    /**
     * 删除指定举报
     * 
     * 功能说明：
     * - 根据举报ID删除举报
     * - 只有管理员可以删除举报
     * - 注意：删除操作不可恢复
     * 
     * @param id 要删除的举报ID
     * @param request HTTP请求对象
     * @return ResponseEntity 包含删除结果的响应对象
     *         - message: 操作提示信息
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, HttpServletRequest request) {
        User currentUser = getCurrentUser(request);
        if (currentUser == null) {
            return ResponseEntity.status(401).body("未登录或token无效");
        }
        
        if (!"ADMIN".equals(currentUser.getRole())) {
            return ResponseEntity.status(403).body("无权限删除举报");
        }
        
        reportService.delete(id);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "删除成功");
        return ResponseEntity.ok(response);
    }
}
