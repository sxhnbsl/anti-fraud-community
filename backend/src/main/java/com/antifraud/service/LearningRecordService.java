package com.antifraud.service;

import com.antifraud.model.LearningRecord;
import java.util.List;

/**
 * 浏览记录服务接口
 * 
 * 功能说明：
 * 1. 提供浏览记录的增删改查功能
 * 2. 支持按用户ID查询浏览记录
 * 3. 支持按内容类型查询浏览记录
 * 4. 支持统计浏览次数
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
public interface LearningRecordService {
    
    /**
     * 创建浏览记录
     * 
     * @param record 浏览记录对象
     * @return 创建的浏览记录对象
     */
    LearningRecord create(LearningRecord record);
    
    /**
     * 查询用户的所有浏览记录
     * 
     * @param userId 用户ID
     * @return 浏览记录列表
     */
    List<LearningRecord> findByUserId(Long userId);
    
    /**
     * 分页查询用户的浏览记录
     * 
     * @param userId 用户ID
     * @param page 页码（从1开始）
     * @param size 每页数量
     * @return 浏览记录列表
     */
    List<LearningRecord> findByUserIdWithPagination(Long userId, int page, int size);
    
    /**
     * 按内容类型查询用户的浏览记录
     * 
     * @param userId 用户ID
     * @param contentType 内容类型
     * @return 浏览记录列表
     */
    List<LearningRecord> findByUserIdAndContentType(Long userId, String contentType);
    
    /**
     * 查询用户最近浏览的记录
     * 
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 浏览记录列表
     */
    List<LearningRecord> findRecentByUserId(Long userId, int limit);
    
    /**
     * 统计用户的浏览次数
     * 
     * @param userId 用户ID
     * @return 浏览次数
     */
    int countByUserId(Long userId);
    
    /**
     * 统计用户对指定内容的浏览次数
     * 
     * @param userId 用户ID
     * @param contentType 内容类型
     * @param contentId 内容ID
     * @return 浏览次数
     */
    int countByUserAndContent(Long userId, String contentType, Long contentId);
    
    /**
     * 删除浏览记录
     * 
     * @param id 浏览记录ID
     */
    void delete(Long id);
    
    /**
     * 删除用户的所有浏览记录
     * 
     * @param userId 用户ID
     */
    void deleteByUserId(Long userId);
}
