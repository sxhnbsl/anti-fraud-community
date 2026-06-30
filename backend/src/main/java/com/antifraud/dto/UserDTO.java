package com.antifraud.dto;

import lombok.Data;

/**
 * 用户数据传输对象
 * 
 * 功能说明：
 * 1. 用于用户注册和更新信息的请求参数
 * 2. 不包含敏感信息（如ID、OpenID、密码等）
 * 3. 用于前后端数据交互
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@Data
public class UserDTO {
    /**
     * 昵称（登录和显示使用）
     */
    private String nickname;
    
    /**
     * 密码
     */
    private String password;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 头像URL
     */
    private String avatar;
    
    /**
     * 用户角色
     * - USER: 普通用户
     * - ADMIN: 管理员
     */
    private String role;
}