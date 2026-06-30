package com.antifraud.repository.impl;

import com.antifraud.model.UserProfileChange;
import com.antifraud.repository.UserProfileChangeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * 用户资料变更记录仓库实现类
 * 
 * 功能说明：
 * 1. 使用JdbcTemplate实现数据库操作
 * 2. 提供用户资料变更记录的CRUD操作
 * 3. 支持按用户ID和状态查询
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@Repository
public class UserProfileChangeRepositoryImpl implements UserProfileChangeRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 插入用户资料变更记录
     * 
     * @param change 用户资料变更记录
     */
    @Override
    public void insert(UserProfileChange change) {
        String sql = "INSERT INTO user_profile_changes (user_id, change_type, old_value, new_value, status) " +
                "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, change.getUserId(), change.getChangeType(), 
                change.getOldValue(), change.getNewValue(), change.getStatus());
    }

    /**
     * 根据ID查询用户资料变更记录
     * 
     * @param id 变更记录ID
     * @return 用户资料变更记录
     */
    @Override
    public UserProfileChange findById(Long id) {
        String sql = "SELECT * FROM user_profile_changes WHERE id = ?";
        List<UserProfileChange> changes = jdbcTemplate.query(sql, 
                new BeanPropertyRowMapper<>(UserProfileChange.class), id);
        return changes.isEmpty() ? null : changes.get(0);
    }

    /**
     * 根据用户ID查询变更记录
     * 
     * @param userId 用户ID
     * @return 变更记录列表
     */
    @Override
    public List<UserProfileChange> findByUserId(Long userId) {
        String sql = "SELECT * FROM user_profile_changes WHERE user_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, 
                new BeanPropertyRowMapper<>(UserProfileChange.class), userId);
    }

    /**
     * 根据状态查询变更记录
     * 
     * @param status 审核状态
     * @return 变更记录列表
     */
    @Override
    public List<UserProfileChange> findByStatus(String status) {
        String sql = "SELECT * FROM user_profile_changes WHERE status = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, 
                new BeanPropertyRowMapper<>(UserProfileChange.class), status);
    }

    /**
     * 更新审核状态
     * 
     * @param id 变更记录ID
     * @param status 审核状态
     */
    @Override
    public void updateStatus(Long id, String status) {
        String sql = "UPDATE user_profile_changes SET status = ? WHERE id = ?";
        jdbcTemplate.update(sql, status, id);
    }

    /**
     * 删除变更记录
     * 
     * @param id 变更记录ID
     */
    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM user_profile_changes WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
