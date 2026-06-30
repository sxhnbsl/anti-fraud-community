package com.antifraud.service.impl;

import com.antifraud.model.Report;
import com.antifraud.dto.ReportDTO;
import com.antifraud.service.ReportService;
import com.antifraud.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 举报服务实现类
 * 
 * 功能说明：
 * 1. 实现举报的创建和查询功能
 * 2. 实现举报状态管理
 * 3. 支持按用户、状态查询举报记录
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportRepository reportRepository;

    /**
     * 创建举报
     * 
     * 功能说明：
     * - 创建举报对象
     * - 设置举报人、被举报内容、举报原因等信息
     * - 设置初始状态为待处理
     * - 保存到数据库
     * 
     * @param reportDTO 举报数据传输对象
     * @return 创建的举报对象
     */
    @Override
    public Report create(ReportDTO reportDTO) {
        Report report = new Report();
        report.setReporterId(reportDTO.getReporterId());
        report.setReportedId(reportDTO.getReportedId());
        report.setReportedType(reportDTO.getReportedType());
        report.setReason(reportDTO.getReason());
        report.setDescription(reportDTO.getDescription());
        report.setStatus(Report.STATUS_PENDING);

        reportRepository.insert(report);
        return report;
    }

    /**
     * 根据ID查询举报
     * 
     * @param id 举报ID
     * @return 举报对象，不存在返回null
     */
    @Override
    public Report findById(Long id) {
        return reportRepository.findById(id);
    }

    /**
     * 查询所有举报
     * 
     * @return 举报列表
     */
    @Override
    public List<Report> findAll() {
        return reportRepository.findAll();
    }

    /**
     * 根据举报人ID查询举报列表
     * 
     * @param reporterId 举报人ID
     * @return 该用户的举报列表
     */
    @Override
    public List<Report> findByReporterId(Long reporterId) {
        return reportRepository.findByReporterId(reporterId);
    }

    /**
     * 根据状态查询举报列表
     * 
     * @param status 举报状态
     * @return 指定状态的举报列表
     */
    @Override
    public List<Report> findByStatus(String status) {
        return reportRepository.findByStatus(status);
    }

    @Override
    public List<Report> findByReporterIdAndStatus(Long reporterId, String status) {
        return reportRepository.findByReporterIdAndStatus(reporterId, status);
    }

    /**
     * 更新举报状态
     * 
     * @param id 举报ID
     * @param status 新状态
     * @param handleResult 处理结果说明
     */
    @Override
    public void updateStatus(Long id, String status, String handleResult) {
        reportRepository.updateStatus(id, status, handleResult);
    }

    /**
     * 删除举报
     * 
     * @param id 举报ID
     */
    @Override
    public void delete(Long id) {
        reportRepository.deleteById(id);
    }
}
