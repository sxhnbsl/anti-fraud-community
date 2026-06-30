package com.antifraud.model;

import lombok.Data;
import java.util.Date;

/**
 * 用户资料变更记录实体类
 * 
 * 功能说明：
 * 1. 存储用户资料变更信息
 * 2. 支持审核流程
 * 3. 记录变更前后的值
 * 
 * 数据表：user_profile_changes
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@Data
public class UserProfileChange {
    /**
     * 变更记录ID（主键，自增）
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 变更类型
     * - nickname: 昵称
     * - avatar: 头像
     */
    private String changeType;
    
    /**
     * 变更前的值
     */
    private String oldValue;
    
    /**
     * 变更后的值
     */
    private String newValue;
    
    /**
     * 审核状态
     * - PENDING: 待审核
     * - APPROVED: 已通过
     * - REJECTED: 已拒绝
     */
    private String status;
    
    /**
     * 创建时间
     */
    private Date createdAt;
    
    /**
     * 更新时间
     */
    private Date updatedAt;
    
    /**
     * 状态常量：待审核
     */
    public static final String STATUS_PENDING = "PENDING";
    
    /**
     * 状态常量：已通过
     */
    public static final String STATUS_APPROVED = "APPROVED";
    
    /**
     * 状态常量：已拒绝
     */
    public static final String STATUS_REJECTED = "REJECTED";
    
    /**
     * 变更类型常量：昵称
     */
    public static final String CHANGE_TYPE_NICKNAME = "nickname";
    
    /**
     * 变更类型常量：头像
     */
    public static final String CHANGE_TYPE_AVATAR = "avatar";
}
