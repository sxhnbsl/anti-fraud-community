package com.antifraud.repository;

import com.antifraud.model.UserProfileChange;
import java.util.List;

/**
 * 用户资料变更记录仓库接口
 * 
 * 功能说明：
 * 1. 提供用户资料变更记录的CRUD操作
 * 2. 支持按用户ID和状态查询
 * 3. 支持批量操作
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
public interface UserProfileChangeRepository {
    
    /**
     * 插入用户资料变更记录
     * 
     * @param change 用户资料变更记录
     */
    void insert(UserProfileChange change);
    
    /**
     * 根据ID查询用户资料变更记录
     * 
     * @param id 变更记录ID
     * @return 用户资料变更记录
     */
    UserProfileChange findById(Long id);
    
    /**
     * 根据用户ID查询变更记录
     * 
     * @param userId 用户ID
     * @return 变更记录列表
     */
    List<UserProfileChange> findByUserId(Long userId);
    
    /**
     * 根据状态查询变更记录
     * 
     * @param status 审核状态
     * @return 变更记录列表
     */
    List<UserProfileChange> findByStatus(String status);
    
    /**
     * 更新审核状态
     * 
     * @param id 变更记录ID
     * @param status 审核状态
     */
    void updateStatus(Long id, String status);
    
    /**
     * 删除变更记录
     * 
     * @param id 变更记录ID
     */
    void deleteById(Long id);
}
