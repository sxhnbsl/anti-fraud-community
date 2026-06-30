package com.antifraud.controller;

import com.antifraud.model.LearningRecord;
import com.antifraud.service.LearningRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 浏览记录控制器
 * 
 * 功能说明：
 * 1. 提供浏览记录的增删改查接口
 * 2. 支持按用户ID查询浏览记录
 * 3. 支持按内容类型查询浏览记录
 * 4. 支持统计浏览次数
 * 
 * 接口列表：
 * - POST /api/learning-records - 创建浏览记录
 * - GET /api/learning-records/user/{userId} - 获取用户的所有浏览记录
 * - GET /api/learning-records/user/{userId}/page - 分页获取用户的浏览记录
 * - GET /api/learning-records/user/{userId}/type/{contentType} - 按内容类型获取用户的浏览记录
 * - GET /api/learning-records/user/{userId}/recent - 获取用户最近浏览的记录
 * - GET /api/learning-records/user/{userId}/count - 统计用户的浏览次数
 * - GET /api/learning-records/user/{userId}/count/content - 统计用户对指定内容的浏览次数
 * - DELETE /api/learning-records/{id} - 删除浏览记录
 * - DELETE /api/learning-records/user/{userId} - 删除用户的所有浏览记录
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@RestController
@RequestMapping("/api/learning-records")
public class LearningRecordController {

    @Autowired
    private LearningRecordService learningRecordService;

    /**
     * 创建浏览记录
     * 
     * 功能说明：
     * - 创建新的浏览记录
     * - 需要提供用户ID、内容类型、内容ID等信息
     * - 返回创建的浏览记录对象
     * 
     * @param record 浏览记录对象
     * @return 创建的浏览记录
     */
    @PostMapping
    public Map<String, Object> createLearningRecord(@RequestBody LearningRecord record) {
        LearningRecord createdRecord = learningRecordService.create(record);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "创建成功");
        response.put("data", createdRecord);
        return response;
    }

    /**
     * 获取用户的所有浏览记录
     * 
     * 功能说明：
     * - 根据用户ID查询所有浏览记录
     * - 按浏览时间倒序排列
     * 
     * @param userId 用户ID
     * @return 浏览记录列表
     */
    @GetMapping("/user/{userId}")
    public Map<String, Object> getLearningRecordsByUserId(@PathVariable Long userId) {
        List<LearningRecord> records = learningRecordService.findByUserId(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "获取成功");
        response.put("data", records);
        return response;
    }

    /**
     * 分页获取用户的浏览记录
     * 
     * 功能说明：
     * - 根据用户ID分页查询浏览记录
     * - 按浏览时间倒序排列
     * - 支持分页参数：page（页码，从1开始）、size（每页数量）
     * 
     * @param userId 用户ID
     * @param page 页码（从1开始）
     * @param size 每页数量
     * @return 浏览记录列表
     */
    @GetMapping("/user/{userId}/page")
    public Map<String, Object> getLearningRecordsByUserIdWithPagination(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<LearningRecord> records = learningRecordService.findByUserIdWithPagination(userId, page, size);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "获取成功");
        response.put("data", records);
        return response;
    }

    /**
     * 按内容类型获取用户的浏览记录
     * 
     * 功能说明：
     * - 根据用户ID和内容类型查询浏览记录
     * - 按浏览时间倒序排列
     * - 内容类型：knowledge-知识，case-案例，post-帖子
     * 
     * @param userId 用户ID
     * @param contentType 内容类型
     * @return 浏览记录列表
     */
    @GetMapping("/user/{userId}/type/{contentType}")
    public Map<String, Object> getLearningRecordsByUserIdAndContentType(
            @PathVariable Long userId,
            @PathVariable String contentType) {
        List<LearningRecord> records = learningRecordService.findByUserIdAndContentType(userId, contentType);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "获取成功");
        response.put("data", records);
        return response;
    }

    /**
     * 获取用户最近浏览的记录
     * 
     * 功能说明：
     * - 根据用户ID查询最近浏览的记录
     * - 按浏览时间倒序排列
     * - 支持限制返回数量：limit（默认10条）
     * 
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 浏览记录列表
     */
    @GetMapping("/user/{userId}/recent")
    public Map<String, Object> getRecentLearningRecordsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        List<LearningRecord> records = learningRecordService.findRecentByUserId(userId, limit);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "获取成功");
        response.put("data", records);
        return response;
    }

    /**
     * 统计用户的浏览次数
     * 
     * 功能说明：
     * - 根据用户ID统计浏览次数
     * - 返回总浏览次数
     * 
     * @param userId 用户ID
     * @return 浏览次数
     */
    @GetMapping("/user/{userId}/count")
    public Map<String, Object> countLearningRecordsByUserId(@PathVariable Long userId) {
        int count = learningRecordService.countByUserId(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "获取成功");
        response.put("data", count);
        return response;
    }

    /**
     * 统计用户对指定内容的浏览次数
     * 
     * 功能说明：
     * - 根据用户ID、内容类型和内容ID统计浏览次数
     * - 返回指定内容的浏览次数
     * 
     * @param userId 用户ID
     * @param contentType 内容类型
     * @param contentId 内容ID
     * @return 浏览次数
     */
    @GetMapping("/user/{userId}/count/content")
    public Map<String, Object> countLearningRecordsByUserAndContent(
            @PathVariable Long userId,
            @RequestParam String contentType,
            @RequestParam Long contentId) {
        int count = learningRecordService.countByUserAndContent(userId, contentType, contentId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "获取成功");
        response.put("data", count);
        return response;
    }

    /**
     * 删除浏览记录
     * 
     * 功能说明：
     * - 根据浏览记录ID删除浏览记录
     * - 删除操作不可恢复
     * - 如果浏览记录不存在，返回404错误
     * 
     * @param id 浏览记录ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteLearningRecord(@PathVariable Long id) {
        learningRecordService.delete(id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "删除成功");
        response.put("data", null);
        return response;
    }

    /**
     * 删除用户的所有浏览记录
     * 
     * 功能说明：
     * - 根据用户ID删除所有浏览记录
     * - 删除操作不可恢复
     * 
     * @param userId 用户ID
     * @return 操作结果
     */
    @DeleteMapping("/user/{userId}")
    public Map<String, Object> deleteLearningRecordsByUserId(@PathVariable Long userId) {
        learningRecordService.deleteByUserId(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "删除成功");
        response.put("data", null);
        return response;
    }
}
