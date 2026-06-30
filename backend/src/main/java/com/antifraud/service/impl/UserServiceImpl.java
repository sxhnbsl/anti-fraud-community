package com.antifraud.service.impl;

import com.antifraud.model.User;
import com.antifraud.dto.UserDTO;
import com.antifraud.service.UserService;
import com.antifraud.repository.UserRepository;
import com.antifraud.service.UserProfileChangeService;
import com.antifraud.model.UserProfileChange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 用户服务实现类
 * 
 * 功能说明：
 * 1. 实现用户的增删改查功能
 * 2. 实现用户注册、登录认证
 * 3. 实现用户资料更新
 * 4. 密码加密处理
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    @Lazy
    private UserProfileChangeService userProfileChangeService;

    /**
     * 用户注册
     * 
     * 功能说明：
     * - 检查昵称是否已存在
     * - 对密码进行加密
     * - 创建用户对象并保存到数据库
     * - 对昵称创建审核记录
     * 
     * @param userDTO 用户数据传输对象
     * @return 注册成功的用户对象
     */
    @Override
    public User register(UserDTO userDTO) {
        // 检查昵称是否已存在
        if (userRepository.findByNickname(userDTO.getNickname()) != null) {
            throw new RuntimeException("昵称已存在");
        }

        User user = new User();
        user.setNickname(userDTO.getNickname());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setRole("USER");
        user.setAccountStatus("PENDING");

        userRepository.insert(user);
        
        // 对昵称创建审核记录
        if (userDTO.getNickname() != null) {
            userProfileChangeService.createChangeRecord(
                user.getId(), 
                UserProfileChange.CHANGE_TYPE_NICKNAME, 
                null, 
                userDTO.getNickname()
            );
        }
        
        return user;
    }

    /**
     * 用户登录
     * 
     * 功能说明：
     * - 根据标识符（昵称、手机号、邮箱）查询用户
     * - 验证密码是否正确
     * - 检查账号状态是否为激活状态
     * - 返回登录结果
     * 
     * @param identifier 标识符（昵称、手机号、邮箱）
     * @param password 密码
     * @return 登录成功的用户对象，失败返回null
     */
    @Override
    public User login(String identifier, String password) {
        User user = userRepository.findByIdentifier(identifier);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            // 检查账号状态
            if ("ACTIVE".equals(user.getAccountStatus())) {
                return user;
            } else if ("PENDING".equals(user.getAccountStatus())) {
                throw new RuntimeException("账号待审核，请等待管理员审核");
            } else if ("REJECTED".equals(user.getAccountStatus())) {
                throw new RuntimeException("账号已被拒绝，请联系管理员");
            } else if ("SUSPENDED".equals(user.getAccountStatus())) {
                throw new RuntimeException("账号已被停用，请联系管理员");
            }
        }
        return null;
    }

    /**
     * 根据ID查询用户
     * 
     * @param id 用户ID
     * @return 用户对象，不存在返回null
     */
    @Override
    public User findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * 根据昵称查询用户
     * 
     * @param nickname 昵称
     * @return 用户对象，不存在返回null
     */
    @Override
    public User findByNickname(String nickname) {
        return userRepository.findByNickname(nickname);
    }

    /**
     * 查询所有用户
     * 
     * @return 用户列表
     */
    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * 更新用户资料
     * 
     * 功能说明：
     * - 更新用户昵称、邮箱、手机号等信息
     * - 如果提供了新密码，则更新密码
     * - 对昵称和头像的变更创建审核记录
     * 
     * @param id 用户ID
     * @param userDTO 用户数据传输对象
     * @return 更新后的用户对象
     */
    @Override
    public User updateProfile(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id);
        if (user != null) {
            // 检查昵称变更
            if (userDTO.getNickname() != null && !userDTO.getNickname().equals(user.getNickname())) {
                userProfileChangeService.createChangeRecord(
                    id, 
                    UserProfileChange.CHANGE_TYPE_NICKNAME, 
                    user.getNickname(), 
                    userDTO.getNickname()
                );
            }
            
            // 检查头像变更
            if (userDTO.getAvatar() != null && !userDTO.getAvatar().equals(user.getAvatar())) {
                userProfileChangeService.createChangeRecord(
                    id, 
                    UserProfileChange.CHANGE_TYPE_AVATAR, 
                    user.getAvatar(), 
                    userDTO.getAvatar()
                );
            }
            
            // 更新其他字段（昵称和头像不立即更新，等待审核通过后再更新）
            if (userDTO.getEmail() != null) {
                user.setEmail(userDTO.getEmail());
            }
            if (userDTO.getPhone() != null) {
                user.setPhone(userDTO.getPhone());
            }
            if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            }
            userRepository.updateById(user);
        }
        return user;
    }

    /**
     * 删除用户
     * 
     * @param id 用户ID
     */
    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
    
    /**
     * 修改用户密码
     * 
     * 功能说明：
     * - 验证旧密码是否正确
     * - 对新密码进行加密
     * - 更新用户密码
     * 
     * @param id 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 修改成功返回true，失败返回false
     */
    @Override
    public boolean changePassword(Long id, String oldPassword, String newPassword) {
        User user = userRepository.findById(id);
        if (user != null) {
            // 验证旧密码
            if (passwordEncoder.matches(oldPassword, user.getPassword())) {
                // 对新密码进行加密
                user.setPassword(passwordEncoder.encode(newPassword));
                // 更新密码
                userRepository.updatePassword(id, user.getPassword());
                return true;
            }
        }
        return false;
    }

    /**
     * 检查昵称是否已存在
     * 
     * @param nickname 昵称
     * @return true-已存在，false-不存在
     */
    @Override
    public boolean existsByNickname(String nickname) {
        return userRepository.findByNickname(nickname) != null;
    }
    
    /**
     * 根据账号状态查询用户
     * 
     * @param accountStatus 账号状态
     * @return 用户列表
     */
    @Override
    public List<User> findByAccountStatus(String accountStatus) {
        return userRepository.findByAccountStatus(accountStatus);
    }
    
    /**
     * 审核用户
     * 
     * @param userId 用户ID
     * @param auditorId 审核人ID
     * @param action 审核操作（APPROVE-通过,REJECT-拒绝）
     * @param remark 审核备注
     * @return 审核后的用户对象
     */
    @Override
    public User auditUser(Long userId, Long auditorId, String action, String remark) {
        User user = userRepository.findById(userId);
        if (user != null) {
            if ("APPROVE".equals(action)) {
                user.setAccountStatus("ACTIVE");
            } else if ("REJECT".equals(action)) {
                user.setAccountStatus("REJECTED");
            }
            user.setAuditorId(auditorId);
            user.setAuditRemark(remark);
            user.setAuditTime(new java.util.Date());
            userRepository.updateAuditInfo(user);
        }
        return user;
    }
    
    /**
     * 根据标识符查询用户（支持用户名、昵称、手机号、邮箱）
     * 
     * @param identifier 标识符
     * @return 用户对象，不存在返回null
     */
    @Override
    public User findByIdentifier(String identifier) {
        return userRepository.findByIdentifier(identifier);
    }
}
