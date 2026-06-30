package com.antifraud.service;

import com.antifraud.model.UserProfileChange;
import java.util.List;

/**
 * 用户资料变更服务接口
 * 
 * 功能说明：
 * 1. 处理用户资料变更的创建和审核
 * 2. 提供变更记录的查询和管理
 * 3. 支持批量审核操作
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
public interface UserProfileChangeService {
    
    /**
     * 创建用户资料变更记录
     * 
     * @param userId 用户ID
     * @param changeType 变更类型
     * @param oldValue 变更前的值
     * @param newValue 变更后的值
     * @return 变更记录
     */
    UserProfileChange createChangeRecord(Long userId, String changeType, String oldValue, String newValue);
    
    /**
     * 审核用户资料变更
     * 
     * @param changeId 变更记录ID
     * @param action 审核操作（APPROVE-通过,REJECT-拒绝）
     * @return 审核后的变更记录
     */
    UserProfileChange auditChange(Long changeId, String action);
    
    /**
     * 根据ID查询变更记录
     * 
     * @param id 变更记录ID
     * @return 变更记录
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
     * 查询待审核的变更记录
     * 
     * @return 待审核变更记录列表
     */
    List<UserProfileChange> findPendingChanges();
    
    /**
     * 应用已通过的变更到用户资料
     * 
     * @param change 变更记录
     * @return 是否应用成功
     */
    boolean applyChange(UserProfileChange change);
}
