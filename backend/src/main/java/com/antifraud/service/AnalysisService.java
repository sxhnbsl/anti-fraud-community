package com.antifraud.service;

import java.util.Map;

/**
 * 数据分析服务接口
 * 
 * 功能说明：
 * 1. 提供各种数据分析功能
 * 2. 支持诈骗类型分析、举报数据分析、用户行为分析等
 * 3. 为管理端提供数据可视化所需的数据
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
public interface AnalysisService {
    
    /**
     * 获取诈骗类型分析数据
     * 
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 诈骗类型分析数据
     */
    Map<String, Object> getFraudTypeAnalysis(String startDate, String endDate);
    
    /**
     * 获取举报数据分析
     * 
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 举报分析数据
     */
    Map<String, Object> getReportAnalysis(String startDate, String endDate);
    
    /**
     * 获取用户行为分析
     * 
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 用户行为分析数据
     */
    Map<String, Object> getUserBehaviorAnalysis(String startDate, String endDate);
    
    /**
     * 获取内容效果分析
     * 
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 内容效果分析数据
     */
    Map<String, Object> getContentEffectAnalysis(String startDate, String endDate);
    
    /**
     * 获取系统性能分析
     * 
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 系统性能分析数据
     */
    Map<String, Object> getSystemPerformanceAnalysis(String startDate, String endDate);
    
    /**
     * 获取平台概览数据
     * 
     * @return 平台概览数据
     */
    Map<String, Object> getPlatformOverview();
    
    /**
     * 获取内容分析数据
     * 
     * @return 内容分析数据
     */
    Map<String, Object> getContentAnalysis();
    
    /**
     * 获取互动数据分析
     * 
     * @return 互动分析数据
     */
    Map<String, Object> getInteractionAnalysis();
    
    /**
     * 获取用户活跃度分析
     * 
     * @return 用户活跃度数据
     */
    Map<String, Object> getUserActivityAnalysis();
    
    /**
     * 获取举报类型分析
     * 
     * @return 举报类型数据
     */
    Map<String, Object> getReportTypeAnalysis();
}