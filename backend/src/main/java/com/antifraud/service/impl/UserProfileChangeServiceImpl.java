package com.antifraud.service.impl;

import com.antifraud.model.User;
import com.antifraud.model.UserProfileChange;
import com.antifraud.dto.UserDTO;
import com.antifraud.repository.UserProfileChangeRepository;
import com.antifraud.repository.UserRepository;
import com.antifraud.service.UserProfileChangeService;
import com.antifraud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 用户资料变更服务实现类
 * 
 * 功能说明：
 * 1. 处理用户资料变更的创建和审核
 * 2. 提供变更记录的查询和管理
 * 3. 支持批量审核操作
 * 4. 应用已通过的变更到用户资料
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@Service
public class UserProfileChangeServiceImpl implements UserProfileChangeService {

    @Autowired
    private UserProfileChangeRepository userProfileChangeRepository;

    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;

    /**
     * 创建用户资料变更记录
     * 
     * @param userId 用户ID
     * @param changeType 变更类型
     * @param oldValue 变更前的值
     * @param newValue 变更后的值
     * @return 变更记录
     */
    @Override
    public UserProfileChange createChangeRecord(Long userId, String changeType, String oldValue, String newValue) {
        UserProfileChange change = new UserProfileChange();
        change.setUserId(userId);
        change.setChangeType(changeType);
        change.setOldValue(oldValue);
        change.setNewValue(newValue);
        change.setStatus(UserProfileChange.STATUS_PENDING);
        
        userProfileChangeRepository.insert(change);
        return change;
    }

    /**
     * 审核用户资料变更
     * 
     * @param changeId 变更记录ID
     * @param action 审核操作（APPROVE-通过,REJECT-拒绝）
     * @return 审核后的变更记录
     */
    @Override
    public UserProfileChange auditChange(Long changeId, String action) {
        UserProfileChange change = userProfileChangeRepository.findById(changeId);
        if (change != null) {
            if ("APPROVE".equals(action)) {
                change.setStatus(UserProfileChange.STATUS_APPROVED);
                applyChange(change);
            } else if ("REJECT".equals(action)) {
                change.setStatus(UserProfileChange.STATUS_REJECTED);
                User user = userService.findById(change.getUserId());
                if (user != null) {
                    if (change.getOldValue() != null && !change.getOldValue().isEmpty()) {
                        user.setNickname(change.getOldValue());
                    } else {
                        user.setNickname("未知用户");
                    }
                    userRepository.updateById(user);
                }
            }
            userProfileChangeRepository.updateStatus(changeId, change.getStatus());
        }
        return change;
    }

    /**
     * 根据ID查询变更记录
     * 
     * @param id 变更记录ID
     * @return 变更记录
     */
    @Override
    public UserProfileChange findById(Long id) {
        return userProfileChangeRepository.findById(id);
    }

    /**
     * 根据用户ID查询变更记录
     * 
     * @param userId 用户ID
     * @return 变更记录列表
     */
    @Override
    public List<UserProfileChange> findByUserId(Long userId) {
        return userProfileChangeRepository.findByUserId(userId);
    }

    /**
     * 查询待审核的变更记录
     * 
     * @return 待审核变更记录列表
     */
    @Override
    public List<UserProfileChange> findPendingChanges() {
        return userProfileChangeRepository.findByStatus(UserProfileChange.STATUS_PENDING);
    }

    /**
     * 应用已通过的变更到用户资料
     * 
     * @param change 变更记录
     * @return 是否应用成功
     */
    @Override
    public boolean applyChange(UserProfileChange change) {
        User user = userService.findById(change.getUserId());
        if (user != null) {
            if (UserProfileChange.CHANGE_TYPE_NICKNAME.equals(change.getChangeType())) {
                user.setNickname(change.getNewValue());
            } else if (UserProfileChange.CHANGE_TYPE_AVATAR.equals(change.getChangeType())) {
                user.setAvatar(change.getNewValue());
            }
            userRepository.updateById(user);
            return true;
        }
        return false;
    }
}
