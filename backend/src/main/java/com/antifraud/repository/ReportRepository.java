package com.antifraud.repository;

import com.antifraud.model.Report;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 举报数据访问层接口
 * 
 * 功能说明：
 * 1. 提供举报数据的增删改查操作
 * 2. 支持按举报人ID、状态查询举报
 * 3. 支持举报状态更新
 * 
 * 数据表：reports
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@Mapper
public interface ReportRepository {

    /**
     * 插入新举报
     * 
     * @param report 举报对象
     * @return 影响的行数
     */
    @Insert("INSERT INTO reports(reporter_id, reported_type, reported_id, reason, description, status) " +
            "VALUES(#{reporterId}, #{reportedType}, #{reportedId}, #{reason}, #{description}, 'pending')")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Report report);

    /**
     * 根据ID查询举报
     * 
     * @param id 举报ID
     * @return 举报对象，如果不存在返回null
     */
    @Select("SELECT r.*, COALESCE(u.nickname, '未知用户') as reporter_name " +
            "FROM reports r " +
            "LEFT JOIN users u ON r.reporter_id = u.id " +
            "WHERE r.id = #{id}")
    Report findById(Long id);

    /**
     * 查询所有举报（按创建时间倒序）
     * 
     * @return 举报列表
     */
    @Select("SELECT r.*, COALESCE(u.nickname, '未知用户') as reporter_name " +
            "FROM reports r " +
            "LEFT JOIN users u ON r.reporter_id = u.id " +
            "ORDER BY r.created_at DESC")
    List<Report> findAll();

    /**
     * 根据举报人ID查询举报（按创建时间倒序）
     * 
     * @param reporterId 举报人ID
     * @return 举报列表
     */
    @Select("SELECT r.*, COALESCE(u.nickname, '未知用户') as reporter_name " +
            "FROM reports r " +
            "LEFT JOIN users u ON r.reporter_id = u.id " +
            "WHERE r.reporter_id = #{reporterId} " +
            "ORDER BY r.created_at DESC")
    List<Report> findByReporterId(Long reporterId);

    /**
     * 根据状态查询举报（按创建时间倒序）
     * 
     * @param status 状态
     * @return 举报列表
     */
    @Select("SELECT r.*, COALESCE(u.nickname, '未知用户') as reporter_name " +
            "FROM reports r " +
            "LEFT JOIN users u ON r.reporter_id = u.id " +
            "WHERE r.status = #{status} " +
            "ORDER BY r.created_at DESC")
    List<Report> findByStatus(String status);

    /**
     * 根据举报人和状态查询举报（按创建时间倒序）
     * 
     * @param reporterId 举报人ID
     * @param status 状态
     * @return 举报列表
     */
    @Select("SELECT r.*, COALESCE(u.nickname, '未知用户') as reporter_name " +
            "FROM reports r " +
            "LEFT JOIN users u ON r.reporter_id = u.id " +
            "WHERE r.reporter_id = #{reporterId} AND r.status = #{status} " +
            "ORDER BY r.created_at DESC")
    List<Report> findByReporterIdAndStatus(@Param("reporterId") Long reporterId, @Param("status") String status);

    /**
     * 更新举报状态
     * 
     * @param id 举报ID
     * @param status 状态
     * @param handleResult 处理结果说明
     * @return 影响的行数
     */
    @Update("UPDATE reports SET status = #{status}, handle_result = #{handleResult} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status, @Param("handleResult") String handleResult);

    /**
     * 根据ID删除举报
     * 
     * @param id 举报ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM reports WHERE id = #{id}")
    int deleteById(Long id);
}