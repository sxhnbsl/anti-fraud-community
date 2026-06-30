package com.antifraud.service;

import com.antifraud.model.Report;
import com.antifraud.dto.ReportDTO;
import java.util.List;

/**
 * 举报服务接口
 * 
 * 功能说明：
 * 1. 提供举报的创建和查询功能
 * 2. 支持举报状态管理
 * 3. 支持按用户、状态查询举报记录
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
public interface ReportService {
    
    /**
     * 创建举报
     * 
     * @param reportDTO 举报数据传输对象
     * @return 创建的举报对象
     */
    Report create(ReportDTO reportDTO);
    
    /**
     * 根据ID查询举报
     * 
     * @param id 举报ID
     * @return 举报对象，不存在返回null
     */
    Report findById(Long id);
    
    /**
     * 查询所有举报
     * 
     * @return 举报列表
     */
    List<Report> findAll();
    
    /**
     * 根据举报人ID查询举报列表
     * 
     * @param reporterId 举报人ID
     * @return 该用户的举报列表
     */
    List<Report> findByReporterId(Long reporterId);
    
    /**
     * 根据状态查询举报列表
     * 
     * @param status 举报状态
     * @return 指定状态的举报列表
     */
    List<Report> findByStatus(String status);
    
    /**
     * 根据举报人和状态查询举报列表
     * 
     * @param reporterId 举报人ID
     * @param status 举报状态
     * @return 指定举报人和状态的举报列表
     */
    List<Report> findByReporterIdAndStatus(Long reporterId, String status);
    
    /**
     * 更新举报状态
     * 
     * @param id 举报ID
     * @param status 新状态
     * @param handleResult 处理结果说明
     */
    void updateStatus(Long id, String status, String handleResult);
    
    /**
     * 删除举报
     * 
     * @param id 举报ID
     */
    void delete(Long id);
}
