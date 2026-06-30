package com.antifraud.service;

import com.antifraud.model.User;
import com.antifraud.dto.UserDTO;
import java.util.List;

/**
 * 用户服务接口
 * 
 * 功能说明：
 * 1. 提供用户的增删改查功能
 * 2. 支持用户注册、登录认证
 * 3. 支持用户资料更新
 * 4. 支持用户权限管理
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
public interface UserService {
    
    /**
     * 用户注册
     * 
     * @param userDTO 用户数据传输对象
     * @return 注册成功的用户对象
     */
    User register(UserDTO userDTO);
    
    /**
     * 用户登录
     * 
     * @param nickname 昵称
     * @param password 密码
     * @return 登录成功的用户对象，失败返回null
     */
    User login(String nickname, String password);
    
    /**
     * 根据ID查询用户
     * 
     * @param id 用户ID
     * @return 用户对象，不存在返回null
     */
    User findById(Long id);
    
    /**
     * 根据昵称查询用户
     * 
     * @param nickname 昵称
     * @return 用户对象，不存在返回null
     */
    User findByNickname(String nickname);
    
    /**
     * 查询所有用户
     * 
     * @return 用户列表
     */
    List<User> findAll();
    
    /**
     * 更新用户资料
     * 
     * @param id 用户ID
     * @param userDTO 用户数据传输对象
     * @return 更新后的用户对象
     */
    User updateProfile(Long id, UserDTO userDTO);
    
    /**
     * 删除用户
     * 
     * @param id 用户ID
     */
    void delete(Long id);
    
    /**
     * 修改用户密码
     * 
     * @param id 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 修改成功返回true，失败返回false
     */
    boolean changePassword(Long id, String oldPassword, String newPassword);
    
    /**
     * 检查昵称是否已存在
     * 
     * @param nickname 昵称
     * @return true-已存在，false-不存在
     */
    boolean existsByNickname(String nickname);
    
    /**
     * 根据账号状态查询用户
     * 
     * @param accountStatus 账号状态
     * @return 用户列表
     */
    List<User> findByAccountStatus(String accountStatus);
    
    /**
     * 审核用户
     * 
     * @param userId 用户ID
     * @param auditorId 审核人ID
     * @param action 审核操作（APPROVE-通过,REJECT-拒绝）
     * @param remark 审核备注
     * @return 审核后的用户对象
     */
    User auditUser(Long userId, Long auditorId, String action, String remark);
    
    /**
     * 根据标识符查询用户（支持用户名、昵称、手机号、邮箱）
     * 
     * @param identifier 标识符
     * @return 用户对象，不存在返回null
     */
    User findByIdentifier(String identifier);
}
