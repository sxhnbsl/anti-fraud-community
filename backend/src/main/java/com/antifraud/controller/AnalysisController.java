package com.antifraud.controller;

import com.antifraud.service.AnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

/**
 * 数据分析控制器
 * 
 * 功能说明：
 * 1. 提供各种数据分析接口
 * 2. 支持诈骗类型分析、举报数据分析、用户行为分析等
 * 3. 为管理端提供数据可视化所需的数据
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@RestController
@RequestMapping("/api/analysis")
public class AnalysisController {

    @Autowired
    private AnalysisService analysisService;

    /**
     * 获取诈骗类型分析数据
     * 
     * 功能说明：
     * - 统计不同类型诈骗的分布情况
     * - 支持按时间范围查询
     * - 返回饼图所需的数据格式
     * 
     * @param startDate 开始日期（可选，格式：yyyy-MM-dd）
     * @param endDate 结束日期（可选，格式：yyyy-MM-dd）
     * @return ResponseEntity 包含诈骗类型分析数据的响应对象
     *         - data: 诈骗类型分布数据
     *             - categories: 诈骗类型列表
     *             - values: 各类型数量
     */
    @GetMapping("/fraud-types")
    public ResponseEntity<?> getFraudTypeAnalysis(
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate) {
        Map<String, Object> data = analysisService.getFraudTypeAnalysis(startDate, endDate);
        return ResponseEntity.ok(Map.of("data", data));
    }

    /**
     * 获取举报数据分析
     * 
     * 功能说明：
     * - 统计举报数量的时间趋势
     * - 分析举报类型分布
     * - 分析举报处理状态分布
     * 
     * @param startDate 开始日期（可选，格式：yyyy-MM-dd）
     * @param endDate 结束日期（可选，格式：yyyy-MM-dd）
     * @return ResponseEntity 包含举报分析数据的响应对象
     *         - data: 举报分析数据
     *             - timeTrend: 时间趋势数据
     *             - typeDistribution: 类型分布数据
     *             - statusDistribution: 状态分布数据
     */
    @GetMapping("/reports")
    public ResponseEntity<?> getReportAnalysis(
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate) {
        Map<String, Object> data = analysisService.getReportAnalysis(startDate, endDate);
        return ResponseEntity.ok(Map.of("data", data));
    }

    /**
     * 获取用户行为分析
     * 
     * 功能说明：
     * - 统计平台活跃用户数
     * - 分析用户访问路径
     * - 分析内容互动情况
     * 
     * @param startDate 开始日期（可选，格式：yyyy-MM-dd）
     * @param endDate 结束日期（可选，格式：yyyy-MM-dd）
     * @return ResponseEntity 包含用户行为分析数据的响应对象
     *         - data: 用户行为分析数据
     *             - activeUsers: 活跃用户数据
     *             - userPaths: 用户访问路径数据
     *             - contentInteraction: 内容互动数据
     */
    @GetMapping("/user-behavior")
    public ResponseEntity<?> getUserBehaviorAnalysis(
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate) {
        Map<String, Object> data = analysisService.getUserBehaviorAnalysis(startDate, endDate);
        return ResponseEntity.ok(Map.of("data", data));
    }

    /**
     * 获取内容效果分析
     * 
     * 功能说明：
     * - 统计知识库文章阅读量
     * - 分析案例展示访问量
     * - 分析社区帖子互动情况
     * 
     * @param startDate 开始日期（可选，格式：yyyy-MM-dd）
     * @param endDate 结束日期（可选，格式：yyyy-MM-dd）
     * @return ResponseEntity 包含内容效果分析数据的响应对象
     *         - data: 内容效果分析数据
     *             - knowledgeBase: 知识库分析数据
     *             - cases: 案例分析数据
     *             - community: 社区分析数据
     */
    @GetMapping("/content-effect")
    public ResponseEntity<?> getContentEffectAnalysis(
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate) {
        Map<String, Object> data = analysisService.getContentEffectAnalysis(startDate, endDate);
        return ResponseEntity.ok(Map.of("data", data));
    }

    /**
     * 获取系统性能分析
     * 
     * 功能说明：
     * - 统计系统响应时间
     * - 分析错误率
     * - 监控系统负载情况
     * 
     * @param startDate 开始日期（可选，格式：yyyy-MM-dd）
     * @param endDate 结束日期（可选，格式：yyyy-MM-dd）
     * @return ResponseEntity 包含系统性能分析数据的响应对象
     *         - data: 系统性能分析数据
     *             - responseTime: 响应时间数据
     *             - errorRate: 错误率数据
     *             - systemLoad: 系统负载数据
     */
    @GetMapping("/system-performance")
    public ResponseEntity<?> getSystemPerformanceAnalysis(
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate) {
        Map<String, Object> data = analysisService.getSystemPerformanceAnalysis(startDate, endDate);
        return ResponseEntity.ok(Map.of("data", data));
    }

    /**
     * 获取平台概览数据
     * 
     * 功能说明：
     * - 提供平台关键指标的概览
     * - 包括用户数、帖子数、评论数、举报数等
     * - 支持与上期数据的对比
     * 
     * @return ResponseEntity 包含平台概览数据的响应对象
     *         - data: 平台概览数据
     *             - totalUsers: 总用户数
     *             - activeUsers: 活跃用户数
     *             - totalPosts: 总帖子数
     *             - totalComments: 总评论数
     *             - totalReports: 总举报数
     *             - growthRate: 增长率
     */
    @GetMapping("/overview")
    public ResponseEntity<?> getPlatformOverview() {
        Map<String, Object> data = analysisService.getPlatformOverview();
        return ResponseEntity.ok(Map.of("data", data));
    }
    
    /**
     * 获取内容分析数据
     * 
     * 功能说明：
     * - 统计帖子、知识库、案例的分类分布
     * - 分析各类内容的数量占比
     * 
     * @return ResponseEntity 包含内容分析数据的响应对象
     *         - data: 内容分析数据
     *             - postCategory: 帖子分类分布
     *             - knowledgeCategory: 知识库分类分布
     *             - caseCategory: 案例分类分布
     */
    @GetMapping("/content")
    public ResponseEntity<?> getContentAnalysis() {
        Map<String, Object> data = analysisService.getContentAnalysis();
        return ResponseEntity.ok(Map.of("data", data));
    }
    
    /**
     * 获取互动数据分析
     * 
     * 功能说明：
     * - 统计帖子、知识库、案例的浏览量和点赞数
     * - 分析热门内容排行
     * 
     * @return ResponseEntity 包含互动分析数据的响应对象
     *         - data: 互动分析数据
     *             - postViewRank: 帖子浏览量排行
     *             - knowledgeLikeRank: 知识库点赞排行
     *             - caseViewRank: 案例浏览量排行
     */
    @GetMapping("/interaction")
    public ResponseEntity<?> getInteractionAnalysis() {
        Map<String, Object> data = analysisService.getInteractionAnalysis();
        return ResponseEntity.ok(Map.of("data", data));
    }
    
    /**
     * 获取用户活跃度分析
     * 
     * 功能说明：
     * - 统计活跃用户和未活跃用户
     * - 分析用户活跃度等级分布
     * 
     * @return ResponseEntity 包含用户活跃度分析数据的响应对象
     *         - data: 用户活跃度分析数据
     *             - userActivity: 用户活跃度统计
     *             - activityLevel: 活跃度等级分布
     */
    @GetMapping("/user-activity")
    public ResponseEntity<?> getUserActivityAnalysis() {
        Map<String, Object> data = analysisService.getUserActivityAnalysis();
        return ResponseEntity.ok(Map.of("data", data));
    }
    
    /**
     * 获取举报类型分析
     * 
     * 功能说明：
     * - 统计不同类型的举报数量
     * - 分析举报状态分布
     * 
     * @return ResponseEntity 包含举报类型分析数据的响应对象
     *         - data: 举报类型分析数据
     *             - reportType: 举报类型分布
     *             - reportStatus: 举报状态分布
     */
    @GetMapping("/report-type")
    public ResponseEntity<?> getReportTypeAnalysis() {
        Map<String, Object> data = analysisService.getReportTypeAnalysis();
        return ResponseEntity.ok(Map.of("data", data));
    }
}