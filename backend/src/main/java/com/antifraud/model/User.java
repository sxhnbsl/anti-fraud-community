package com.antifraud.model;

import lombok.Data;
import java.util.Date;

/**
 * 用户实体类
 * 
 * 功能说明：
 * 1. 存储用户基本信息
 * 2. 支持用户名登录和微信OpenID登录
 * 3. 包含用户角色信息（普通用户/管理员）
 * 
 * 数据表：users
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@Data
public class User {
    /**
     * 用户ID（主键，自增）
     */
    private Long id;
    
    /**
     * 微信OpenID（微信登录使用）
     */
    private String openid;
    
    /**
     * 昵称（登录和显示使用）
     */
    private String nickname;
    
    /**
     * 密码（加密存储）
     */
    private String password;
    
    /**
     * 头像URL
     */
    private String avatar;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 用户角色
     * - USER: 普通用户
     * - ADMIN: 管理员
     */
    private String role;
    
    /**
     * 账号状态
     * - PENDING: 待审核
     * - ACTIVE: 已激活
     * - SUSPENDED: 已停用
     * - REJECTED: 已拒绝
     */
    private String accountStatus;
    
    /**
     * 审核人ID
     */
    private Long auditorId;
    
    /**
     * 审核备注
     */
    private String auditRemark;
    
    /**
     * 审核时间
     */
    private Date auditTime;
    
    /**
     * 创建时间
     */
    private Date createdAt;
    
    /**
     * 更新时间
     */
    private Date updatedAt;
    
    /**
     * 账号状态常量：待审核
     */
    public static final String ACCOUNT_PENDING = "PENDING";
    
    /**
     * 账号状态常量：已激活
     */
    public static final String ACCOUNT_ACTIVE = "ACTIVE";
    
    /**
     * 账号状态常量：已停用
     */
    public static final String ACCOUNT_SUSPENDED = "SUSPENDED";
    
    /**
     * 账号状态常量：已拒绝
     */
    public static final String ACCOUNT_REJECTED = "REJECTED";
}