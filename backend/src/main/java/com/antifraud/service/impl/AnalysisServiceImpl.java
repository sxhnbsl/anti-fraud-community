package com.antifraud.service.impl;

import com.antifraud.service.AnalysisService;
import com.antifraud.service.UserService;
import com.antifraud.service.PostService;
import com.antifraud.service.CommentService;
import com.antifraud.service.ReportService;
import com.antifraud.model.Report;
import com.antifraud.repository.ReportRepository;
import com.antifraud.repository.PostRepository;
import com.antifraud.repository.CommentRepository;
import com.antifraud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据分析服务实现类
 * 
 * 功能说明：
 * 1. 实现各种数据分析功能
 * 2. 从真实数据库中获取数据
 * 3. 为管理端提供数据可视化所需的数据
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@Service
public class AnalysisServiceImpl implements AnalysisService {

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private ReportService reportService;
    
    @Autowired
    private ReportRepository reportRepository;
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private com.antifraud.repository.KnowledgeRepository knowledgeRepository;
    
    @Autowired
    private com.antifraud.repository.CaseRepository caseRepository;

    /**
     * 获取诈骗类型分析数据
     * 
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 诈骗类型分析数据
     */
    @Override
    public Map<String, Object> getFraudTypeAnalysis(String startDate, String endDate) {
        Map<String, Object> result = new HashMap<>();
        
        // 从数据库获取举报数据
        List<Report> reports = reportRepository.findAll();
        
        // 统计不同类型的举报
        Map<String, Integer> typeCountMap = new HashMap<>();
        for (Report report : reports) {
            String reportedType = report.getReportedType();
            typeCountMap.put(reportedType, typeCountMap.getOrDefault(reportedType, 0) + 1);
        }
        
        // 转换为前端需要的格式
        List<String> categories = new ArrayList<>(typeCountMap.keySet());
        List<Integer> values = new ArrayList<>();
        for (String category : categories) {
            values.add(typeCountMap.get(category));
        }
        
        // 如果没有数据，使用默认数据
        if (categories.isEmpty()) {
            categories.add("POST");
            categories.add("COMMENT");
            categories.add("USER");
            
            values.add(0);
            values.add(0);
            values.add(0);
        }
        
        result.put("categories", categories);
        result.put("values", values);
        
        return result;
    }

    /**
     * 获取举报数据分析
     * 
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 举报分析数据
     */
    @Override
    public Map<String, Object> getReportAnalysis(String startDate, String endDate) {
        Map<String, Object> result = new HashMap<>();
        
        // 从数据库获取举报数据
        List<Report> reports = reportRepository.findAll();
        
        // 时间趋势数据
        Map<String, Object> timeTrend = new HashMap<>();
        List<String> dates = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();
        
        // 模拟时间数据，实际项目中应该按日期分组
        dates.add("1月");
        dates.add("2月");
        dates.add("3月");
        dates.add("4月");
        dates.add("5月");
        dates.add("6月");
        
        // 统计每个月的举报数
        for (int i = 0; i < dates.size(); i++) {
            counts.add(reports.size() / dates.size());
        }
        
        timeTrend.put("dates", dates);
        timeTrend.put("counts", counts);
        
        // 类型分布数据
        Map<String, Object> typeDistribution = new HashMap<>();
        List<String> types = new ArrayList<>();
        List<Integer> typeCounts = new ArrayList<>();
        
        // 统计不同类型的举报
        Map<String, Integer> typeCountMap = new HashMap<>();
        for (Report report : reports) {
            String reportedType = report.getReportedType();
            typeCountMap.put(reportedType, typeCountMap.getOrDefault(reportedType, 0) + 1);
        }
        
        for (Map.Entry<String, Integer> entry : typeCountMap.entrySet()) {
            types.add(entry.getKey());
            typeCounts.add(entry.getValue());
        }
        
        // 如果没有数据，使用默认数据
        if (types.isEmpty()) {
            types.add("POST");
            types.add("COMMENT");
            types.add("USER");
            
            typeCounts.add(0);
            typeCounts.add(0);
            typeCounts.add(0);
        }
        
        typeDistribution.put("types", types);
        typeDistribution.put("counts", typeCounts);
        
        // 状态分布数据
        Map<String, Object> statusDistribution = new HashMap<>();
        List<String> statuses = new ArrayList<>();
        List<Integer> statusCounts = new ArrayList<>();
        
        // 统计不同状态的举报
        Map<String, Integer> statusCountMap = new HashMap<>();
        for (Report report : reports) {
            String status = report.getStatus();
            statusCountMap.put(status, statusCountMap.getOrDefault(status, 0) + 1);
        }
        
        for (Map.Entry<String, Integer> entry : statusCountMap.entrySet()) {
            statuses.add(entry.getKey());
            statusCounts.add(entry.getValue());
        }
        
        // 如果没有数据，使用默认数据
        if (statuses.isEmpty()) {
            statuses.add("PENDING");
            statuses.add("PROCESSING");
            statuses.add("RESOLVED");
            statuses.add("REJECTED");
            
            statusCounts.add(0);
            statusCounts.add(0);
            statusCounts.add(0);
            statusCounts.add(0);
        }
        
        statusDistribution.put("statuses", statuses);
        statusDistribution.put("counts", statusCounts);
        
        result.put("timeTrend", timeTrend);
        result.put("typeDistribution", typeDistribution);
        result.put("statusDistribution", statusDistribution);
        
        return result;
    }

    /**
     * 获取用户行为分析
     * 
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 用户行为分析数据
     */
    @Override
    public Map<String, Object> getUserBehaviorAnalysis(String startDate, String endDate) {
        Map<String, Object> result = new HashMap<>();
        
        // 从数据库获取用户数据
        int totalUsers = userService.findAll().size();
        
        // 活跃用户数据
        Map<String, Object> activeUsers = new HashMap<>();
        List<String> dates = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();
        
        dates.add("周一");
        dates.add("周二");
        dates.add("周三");
        dates.add("周四");
        dates.add("周五");
        dates.add("周六");
        dates.add("周日");
        
        // 模拟活跃用户数据
        for (int i = 0; i < dates.size(); i++) {
            counts.add(totalUsers / 2 + (int)(Math.random() * totalUsers / 2));
        }
        
        activeUsers.put("dates", dates);
        activeUsers.put("counts", counts);
        
        // 用户访问路径数据
        Map<String, Object> userPaths = new HashMap<>();
        List<String> paths = new ArrayList<>();
        List<Integer> pathCounts = new ArrayList<>();
        
        paths.add("首页 → 知识库");
        paths.add("首页 → 案例展示");
        paths.add("首页 → 社区交流");
        paths.add("首页 → 诈骗举报");
        paths.add("知识库 → 社区交流");
        
        // 模拟路径数据
        for (int i = 0; i < paths.size(); i++) {
            pathCounts.add(totalUsers / 5 + (int)(Math.random() * totalUsers / 10));
        }
        
        userPaths.put("paths", paths);
        userPaths.put("counts", pathCounts);
        
        // 内容互动数据
        Map<String, Object> contentInteraction = new HashMap<>();
        List<String> contentTypes = new ArrayList<>();
        List<Integer> interactionCounts = new ArrayList<>();
        
        contentTypes.add("帖子点赞");
        contentTypes.add("帖子评论");
        contentTypes.add("知识库收藏");
        contentTypes.add("案例分享");
        
        // 模拟互动数据
        for (int i = 0; i < contentTypes.size(); i++) {
            interactionCounts.add(totalUsers / 4 + (int)(Math.random() * totalUsers / 10));
        }
        
        contentInteraction.put("types", contentTypes);
        contentInteraction.put("counts", interactionCounts);
        
        result.put("activeUsers", activeUsers);
        result.put("userPaths", userPaths);
        result.put("contentInteraction", contentInteraction);
        
        return result;
    }

    /**
     * 获取内容效果分析
     * 
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 内容效果分析数据
     */
    @Override
    public Map<String, Object> getContentEffectAnalysis(String startDate, String endDate) {
        Map<String, Object> result = new HashMap<>();
        
        // 从数据库获取内容数据
        int totalPosts = postService.findAll().size();
        int totalComments = commentService.findAll().size();
        
        // 知识库分析数据
        Map<String, Object> knowledgeBase = new HashMap<>();
        List<String> articles = new ArrayList<>();
        List<Integer> views = new ArrayList<>();
        
        articles.add("如何识别电信诈骗");
        articles.add("网络购物防骗指南");
        articles.add("投资理财诈骗防范");
        articles.add("兼职刷单诈骗预警");
        articles.add("冒充客服诈骗识别");
        
        // 模拟知识库数据
        for (int i = 0; i < articles.size(); i++) {
            views.add(totalPosts * 10 + (int)(Math.random() * 100));
        }
        
        knowledgeBase.put("articles", articles);
        knowledgeBase.put("views", views);
        
        // 案例分析数据
        Map<String, Object> cases = new HashMap<>();
        List<String> caseTitles = new ArrayList<>();
        List<Integer> caseViews = new ArrayList<>();
        
        caseTitles.add("网购诈骗真实案例");
        caseTitles.add("电信诈骗受害者经历");
        caseTitles.add("投资理财诈骗陷阱");
        caseTitles.add("兼职刷单诈骗揭秘");
        
        // 模拟案例数据
        for (int i = 0; i < caseTitles.size(); i++) {
            caseViews.add(totalPosts * 8 + (int)(Math.random() * 80));
        }
        
        cases.put("titles", caseTitles);
        cases.put("views", caseViews);
        
        // 社区分析数据
        Map<String, Object> community = new HashMap<>();
        List<String> postTitles = new ArrayList<>();
        List<Integer> interactions = new ArrayList<>();
        
        postTitles.add("遇到诈骗怎么办？");
        postTitles.add("分享一个诈骗经历");
        postTitles.add("防诈骗小技巧");
        postTitles.add("如何帮助老年人防骗");
        
        // 模拟社区数据
        for (int i = 0; i < postTitles.size(); i++) {
            interactions.add(totalComments / 4 + (int)(Math.random() * 20));
        }
        
        community.put("titles", postTitles);
        community.put("interactions", interactions);
        
        result.put("knowledgeBase", knowledgeBase);
        result.put("cases", cases);
        result.put("community", community);
        
        return result;
    }

    /**
     * 获取系统性能分析
     * 
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 系统性能分析数据
     */
    @Override
    public Map<String, Object> getSystemPerformanceAnalysis(String startDate, String endDate) {
        Map<String, Object> result = new HashMap<>();
        
        // 响应时间数据
        Map<String, Object> responseTime = new HashMap<>();
        List<String> times = new ArrayList<>();
        List<Double> values = new ArrayList<>();
        
        times.add("8:00");
        times.add("10:00");
        times.add("12:00");
        times.add("14:00");
        times.add("16:00");
        times.add("18:00");
        times.add("20:00");
        
        // 模拟响应时间数据
        for (int i = 0; i < times.size(); i++) {
            values.add(0.1 + Math.random() * 0.15);
        }
        
        responseTime.put("times", times);
        responseTime.put("values", values);
        
        // 错误率数据
        Map<String, Object> errorRate = new HashMap<>();
        List<String> errorTimes = new ArrayList<>();
        List<Double> errorValues = new ArrayList<>();
        
        errorTimes.add("1月");
        errorTimes.add("2月");
        errorTimes.add("3月");
        errorTimes.add("4月");
        errorTimes.add("5月");
        errorTimes.add("6月");
        
        // 模拟错误率数据
        for (int i = 0; i < errorTimes.size(); i++) {
            errorValues.add(0.1 + Math.random() * 0.7);
        }
        
        errorRate.put("times", errorTimes);
        errorRate.put("values", errorValues);
        
        // 系统负载数据
        Map<String, Object> systemLoad = new HashMap<>();
        List<String> loadTimes = new ArrayList<>();
        List<Double> loadValues = new ArrayList<>();
        
        loadTimes.add("周一");
        loadTimes.add("周二");
        loadTimes.add("周三");
        loadTimes.add("周四");
        loadTimes.add("周五");
        loadTimes.add("周六");
        loadTimes.add("周日");
        
        // 模拟系统负载数据
        for (int i = 0; i < loadTimes.size(); i++) {
            loadValues.add(15.0 + Math.random() * 15.0);
        }
        
        systemLoad.put("times", loadTimes);
        systemLoad.put("values", loadValues);
        
        result.put("responseTime", responseTime);
        result.put("errorRate", errorRate);
        result.put("systemLoad", systemLoad);
        
        return result;
    }

    /**
     * 获取平台概览数据
     * 
     * @return 平台概览数据
     */
    @Override
    public Map<String, Object> getPlatformOverview() {
        Map<String, Object> result = new HashMap<>();
        
        // 从数据库获取真实数据
        int totalUsers = userService.findAll().size();
        int totalPosts = postService.findAll().size();
        int totalComments = commentService.findAll().size();
        int totalReports = reportRepository.findAll().size();
        
        // 计算活跃用户数（这里简单计算为总用户数的一半）
        int activeUsers = totalUsers / 2;
        
        // 计算增长率（模拟数据）
        double growthRate = 15.5;
        
        result.put("totalUsers", totalUsers);
        result.put("activeUsers", activeUsers);
        result.put("totalPosts", totalPosts);
        result.put("totalComments", totalComments);
        result.put("totalReports", totalReports);
        result.put("growthRate", growthRate);
        
        return result;
    }
    
    /**
     * 获取内容分析数据
     * 
     * @return 内容分析数据
     */
    public Map<String, Object> getContentAnalysis() {
        Map<String, Object> result = new HashMap<>();
        
        // 帖子分类分布
        List<com.antifraud.model.Post> posts = postRepository.findAll();
        Map<String, Integer> postCategoryCount = new HashMap<>();
        for (com.antifraud.model.Post post : posts) {
            String category = post.getCategory();
            if (category != null && !category.isEmpty()) {
                postCategoryCount.put(category, postCategoryCount.getOrDefault(category, 0) + 1);
            }
        }
        
        Map<String, Object> postCategory = new HashMap<>();
        postCategory.put("categories", new ArrayList<>(postCategoryCount.keySet()));
        postCategory.put("counts", new ArrayList<>(postCategoryCount.values()));
        
        // 知识库分类分布
        List<com.antifraud.model.Knowledge> knowledgeList = knowledgeRepository.findAll();
        Map<String, Integer> knowledgeCategoryCount = new HashMap<>();
        for (com.antifraud.model.Knowledge knowledge : knowledgeList) {
            String category = knowledge.getCategory();
            if (category != null && !category.isEmpty()) {
                knowledgeCategoryCount.put(category, knowledgeCategoryCount.getOrDefault(category, 0) + 1);
            }
        }
        
        Map<String, Object> knowledgeCategory = new HashMap<>();
        knowledgeCategory.put("categories", new ArrayList<>(knowledgeCategoryCount.keySet()));
        knowledgeCategory.put("counts", new ArrayList<>(knowledgeCategoryCount.values()));
        
        // 案例分类分布
        List<com.antifraud.model.Case> cases = caseRepository.findAll();
        Map<String, Integer> caseCategoryCount = new HashMap<>();
        for (com.antifraud.model.Case caseItem : cases) {
            String category = caseItem.getCategory();
            if (category != null && !category.isEmpty()) {
                caseCategoryCount.put(category, caseCategoryCount.getOrDefault(category, 0) + 1);
            }
        }
        
        Map<String, Object> caseCategory = new HashMap<>();
        caseCategory.put("categories", new ArrayList<>(caseCategoryCount.keySet()));
        caseCategory.put("counts", new ArrayList<>(caseCategoryCount.values()));
        
        result.put("postCategory", postCategory);
        result.put("knowledgeCategory", knowledgeCategory);
        result.put("caseCategory", caseCategory);
        
        return result;
    }
    
    /**
     * 获取互动数据分析
     * 
     * @return 互动分析数据
     */
    public Map<String, Object> getInteractionAnalysis() {
        Map<String, Object> result = new HashMap<>();
        
        // 帖子浏览量排行（前5名）
        List<com.antifraud.model.Post> posts = postRepository.findAll();
        posts.sort((a, b) -> (b.getViewCount() != null ? b.getViewCount() : 0) - (a.getViewCount() != null ? a.getViewCount() : 0));
        
        List<String> postTitles = new ArrayList<>();
        List<Integer> postViews = new ArrayList<>();
        for (int i = 0; i < Math.min(5, posts.size()); i++) {
            postTitles.add(posts.get(i).getTitle());
            postViews.add(posts.get(i).getViewCount() != null ? posts.get(i).getViewCount() : 0);
        }
        
        Map<String, Object> postViewRank = new HashMap<>();
        postViewRank.put("titles", postTitles);
        postViewRank.put("views", postViews);
        
        // 知识库点赞排行（前5名）
        List<com.antifraud.model.Knowledge> knowledgeList = knowledgeRepository.findAll();
        knowledgeList.sort((a, b) -> (b.getLikeCount() != null ? b.getLikeCount() : 0) - (a.getLikeCount() != null ? a.getLikeCount() : 0));
        
        List<String> knowledgeTitles = new ArrayList<>();
        List<Integer> knowledgeLikes = new ArrayList<>();
        for (int i = 0; i < Math.min(5, knowledgeList.size()); i++) {
            knowledgeTitles.add(knowledgeList.get(i).getTitle());
            knowledgeLikes.add(knowledgeList.get(i).getLikeCount() != null ? knowledgeList.get(i).getLikeCount() : 0);
        }
        
        Map<String, Object> knowledgeLikeRank = new HashMap<>();
        knowledgeLikeRank.put("titles", knowledgeTitles);
        knowledgeLikeRank.put("likes", knowledgeLikes);
        
        // 案例浏览量排行（前5名）
        List<com.antifraud.model.Case> cases = caseRepository.findAll();
        cases.sort((a, b) -> (b.getViewCount() != null ? b.getViewCount() : 0) - (a.getViewCount() != null ? a.getViewCount() : 0));
        
        List<String> caseTitles = new ArrayList<>();
        List<Integer> caseViews = new ArrayList<>();
        for (int i = 0; i < Math.min(5, cases.size()); i++) {
            caseTitles.add(cases.get(i).getTitle());
            caseViews.add(cases.get(i).getViewCount() != null ? cases.get(i).getViewCount() : 0);
        }
        
        Map<String, Object> caseViewRank = new HashMap<>();
        caseViewRank.put("titles", caseTitles);
        caseViewRank.put("views", caseViews);
        
        result.put("postViewRank", postViewRank);
        result.put("knowledgeLikeRank", knowledgeLikeRank);
        result.put("caseViewRank", caseViewRank);
        
        return result;
    }
    
    /**
     * 获取用户活跃度分析
     * 
     * 功能说明：
     * - 统计用户发帖、评论、点赞等行为
     * - 分析用户活跃度分布
     * 
     * @return 用户活跃度数据
     */
    @Override
    public Map<String, Object> getUserActivityAnalysis() {
        Map<String, Object> result = new HashMap<>();
        
        // 获取所有用户
        List<com.antifraud.model.User> users = userRepository.findAll();
        
        // 统计活跃用户（发帖数>0或评论数>0）
        int activeUserCount = 0;
        int inactiveUserCount = 0;
        
        for (com.antifraud.model.User user : users) {
            Long userId = user.getId();
            int postCount = (int) postRepository.findAll().stream()
                .filter(p -> p.getAuthorId() != null && p.getAuthorId().equals(userId))
                .count();
            int commentCount = (int) commentRepository.findAll().stream()
                .filter(c -> c.getAuthorId() != null && c.getAuthorId().equals(userId))
                .count();
            
            if (postCount > 0 || commentCount > 0) {
                activeUserCount++;
            } else {
                inactiveUserCount++;
            }
        }
        
        Map<String, Object> activityData = new HashMap<>();
        activityData.put("active", activeUserCount);
        activityData.put("inactive", inactiveUserCount);
        activityData.put("total", users.size());
        
        result.put("userActivity", activityData);
        
        // 统计用户活跃度等级
        int highlyActive = 0;
        int moderatelyActive = 0;
        int lowlyActive = 0;
        
        for (com.antifraud.model.User user : users) {
            Long userId = user.getId();
            int postCount = (int) postRepository.findAll().stream()
                .filter(p -> p.getAuthorId() != null && p.getAuthorId().equals(userId))
                .count();
            int commentCount = (int) commentRepository.findAll().stream()
                .filter(c -> c.getAuthorId() != null && c.getAuthorId().equals(userId))
                .count();
            
            int totalActivity = postCount + commentCount;
            if (totalActivity >= 10) {
                highlyActive++;
            } else if (totalActivity >= 3) {
                moderatelyActive++;
            } else if (totalActivity > 0) {
                lowlyActive++;
            }
        }
        
        Map<String, Object> activityLevel = new HashMap<>();
        activityLevel.put("highlyActive", highlyActive);
        activityLevel.put("moderatelyActive", moderatelyActive);
        activityLevel.put("lowlyActive", lowlyActive);
        activityLevel.put("inactive", inactiveUserCount);
        
        result.put("activityLevel", activityLevel);
        
        return result;
    }
    
    /**
     * 获取举报类型分析
     * 
     * 功能说明：
     * - 统计不同类型的举报数量
     * - 分析举报类型分布
     * 
     * @return 举报类型数据
     */
    @Override
    public Map<String, Object> getReportTypeAnalysis() {
        Map<String, Object> result = new HashMap<>();
        
        // 获取所有举报
        List<Report> reports = reportRepository.findAll();
        
        // 统计不同类型的举报
        Map<String, Integer> typeCountMap = new HashMap<>();
        for (Report report : reports) {
            String reportedType = report.getReportedType();
            if (reportedType != null && !reportedType.isEmpty()) {
                typeCountMap.put(reportedType, typeCountMap.getOrDefault(reportedType, 0) + 1);
            }
        }
        
        // 转换为前端需要的格式
        List<String> types = new ArrayList<>(typeCountMap.keySet());
        List<Integer> counts = new ArrayList<>();
        for (String type : types) {
            counts.add(typeCountMap.get(type));
        }
        
        Map<String, Object> reportTypeData = new HashMap<>();
        reportTypeData.put("types", types);
        reportTypeData.put("counts", counts);
        
        result.put("reportType", reportTypeData);
        
        // 统计举报状态
        Map<String, Integer> statusCountMap = new HashMap<>();
        for (Report report : reports) {
            String status = report.getStatus();
            if (status != null && !status.isEmpty()) {
                statusCountMap.put(status, statusCountMap.getOrDefault(status, 0) + 1);
            }
        }
        
        List<String> statuses = new ArrayList<>(statusCountMap.keySet());
        List<Integer> statusCounts = new ArrayList<>();
        for (String status : statuses) {
            statusCounts.add(statusCountMap.get(status));
        }
        
        Map<String, Object> reportStatusData = new HashMap<>();
        reportStatusData.put("statuses", statuses);
        reportStatusData.put("counts", statusCounts);
        
        result.put("reportStatus", reportStatusData);
        
        return result;
    }
}