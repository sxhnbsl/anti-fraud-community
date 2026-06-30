package com.antifraud.repository;

import com.antifraud.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 用户数据访问层接口
 * 
 * 功能说明：
 * 1. 提供用户数据的增删改查操作
 * 2. 支持通过用户名、OpenID、ID查询用户
 * 3. 支持用户信息更新和删除
 * 
 * 数据表：users
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@Mapper
public interface UserRepository {

    /**
     * 插入新用户
     * 
     * @param user 用户对象
     * @return 影响的行数
     */
    @Insert("INSERT INTO users(nickname, password, phone, email, role, account_status, created_at, updated_at) " +
            "VALUES(#{nickname}, #{password}, #{phone}, #{email}, #{role}, #{accountStatus}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    /**
     * 根据昵称查询用户
     * 
     * @param nickname 昵称
     * @return 用户对象，如果不存在返回null
     */
    @Select("SELECT * FROM users WHERE nickname = #{nickname}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "openid", column = "openid"),
            @Result(property = "nickname", column = "nickname"),
            @Result(property = "password", column = "password"),
            @Result(property = "avatar", column = "avatar"),
            @Result(property = "phone", column = "phone"),
            @Result(property = "email", column = "email"),
            @Result(property = "role", column = "role"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    User findByNickname(String nickname);

    /**
     * 根据OpenID查询用户（微信登录使用）
     * 
     * @param openid 微信OpenID
     * @return 用户对象，如果不存在返回null
     */
    @Select("SELECT * FROM users WHERE openid = #{openid}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "openid", column = "openid"),
            @Result(property = "nickname", column = "nickname"),
            @Result(property = "password", column = "password"),
            @Result(property = "avatar", column = "avatar"),
            @Result(property = "phone", column = "phone"),
            @Result(property = "email", column = "email"),
            @Result(property = "role", column = "role"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    User findByOpenid(String openid);

    /**
     * 根据ID查询用户
     * 
     * @param id 用户ID
     * @return 用户对象，如果不存在返回null
     */
    @Select("SELECT * FROM users WHERE id = #{id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "openid", column = "openid"),
            @Result(property = "nickname", column = "nickname"),
            @Result(property = "password", column = "password"),
            @Result(property = "avatar", column = "avatar"),
            @Result(property = "phone", column = "phone"),
            @Result(property = "email", column = "email"),
            @Result(property = "role", column = "role"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    User findById(Long id);

    /**
     * 查询所有用户（按创建时间倒序）
     * 
     * @return 用户列表
     */
    @Select("SELECT * FROM users ORDER BY created_at DESC")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "openid", column = "openid"),
            @Result(property = "nickname", column = "nickname"),
            @Result(property = "password", column = "password"),
            @Result(property = "avatar", column = "avatar"),
            @Result(property = "phone", column = "phone"),
            @Result(property = "email", column = "email"),
            @Result(property = "role", column = "role"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    List<User> findAll();

    /**
     * 根据ID更新用户信息
     * 
     * @param user 用户对象（包含ID和要更新的字段）
     * @return 影响的行数
     */
    @Update("UPDATE users SET nickname = #{nickname}, password = #{password}, avatar = #{avatar}, phone = #{phone}, email = #{email}, " +
            "role = #{role}, updated_at = NOW() WHERE id = #{id}")
    int updateById(User user);
    
    /**
     * 根据账号状态查询用户
     * 
     * @param accountStatus 账号状态
     * @return 用户列表
     */
    @Select("SELECT * FROM users WHERE account_status = #{accountStatus} ORDER BY created_at DESC")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "openid", column = "openid"),
            @Result(property = "nickname", column = "nickname"),
            @Result(property = "password", column = "password"),
            @Result(property = "avatar", column = "avatar"),
            @Result(property = "phone", column = "phone"),
            @Result(property = "email", column = "email"),
            @Result(property = "role", column = "role"),
            @Result(property = "accountStatus", column = "account_status"),
            @Result(property = "auditorId", column = "auditor_id"),
            @Result(property = "auditRemark", column = "audit_remark"),
            @Result(property = "auditTime", column = "audit_time"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    List<User> findByAccountStatus(String accountStatus);
    
    /**
     * 更新用户审核信息
     * 
     * @param user 用户对象（包含审核信息）
     * @return 影响的行数
     */
    @Update("UPDATE users SET account_status = #{accountStatus}, auditor_id = #{auditorId}, " +
            "audit_remark = #{auditRemark}, audit_time = #{auditTime}, updated_at = NOW() WHERE id = #{id}")
    int updateAuditInfo(User user);
    
    /**
     * 根据ID删除用户
     * 
     * @param id 用户ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM users WHERE id = #{id}")
    int deleteById(Long id);
    
    /**
     * 根据昵称、手机号、邮箱或历史昵称查询用户
     * 
     * @param identifier 标识符（昵称、手机号、邮箱或历史昵称）
     * @return 用户对象，如果不存在返回null
     */
    @Select("SELECT u.* FROM users u LEFT JOIN user_profile_changes upc ON u.id = upc.user_id WHERE u.nickname = #{identifier} OR u.phone = #{identifier} OR u.email = #{identifier} OR upc.old_value = #{identifier} GROUP BY u.id")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "openid", column = "openid"),
            @Result(property = "nickname", column = "nickname"),
            @Result(property = "password", column = "password"),
            @Result(property = "avatar", column = "avatar"),
            @Result(property = "phone", column = "phone"),
            @Result(property = "email", column = "email"),
            @Result(property = "role", column = "role"),
            @Result(property = "accountStatus", column = "account_status"),
            @Result(property = "auditorId", column = "auditor_id"),
            @Result(property = "auditRemark", column = "audit_remark"),
            @Result(property = "auditTime", column = "audit_time"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    User findByIdentifier(String identifier);
    
    /**
     * 根据ID更新用户密码
     * 
     * @param id 用户ID
     * @param password 加密后的密码
     * @return 影响的行数
     */
    @Update("UPDATE users SET password = #{password}, updated_at = NOW() WHERE id = #{id}")
    int updatePassword(@Param("id") Long id, @Param("password") String password);
}