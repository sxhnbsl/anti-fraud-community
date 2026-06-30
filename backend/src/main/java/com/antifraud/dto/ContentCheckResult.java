package com.antifraud.dto;

import lombok.Data;
import java.util.List;

/**
 * 内容审核结果DTO
 */
@Data
public class ContentCheckResult {
    
    /**
     * 风险等级
     * PASS: 通过，无风险
     * REVIEW: 需要人工审核
     * REJECT: 拒绝，高风险
     */
    private RiskLevel riskLevel;
    
    /**
     * 风险分数 (0-100)
     */
    private Integer riskScore;
    
    /**
     * 审核建议
     */
    private String suggestion;
    
    /**
     * 命中的风险标签列表
     */
    private List<String> riskLabels;
    
    /**
     * 详细的风险信息
     */
    private String riskDetails;
    
    /**
     * 是否通过审核
     */
    private boolean passed;
    
    /**
     * 风险等级枚举
     */
    public enum RiskLevel {
        PASS("通过", "内容正常，可以发布"),
        REVIEW("待审核", "内容存在风险，需要人工审核"),
        REJECT("拒绝", "内容违规，禁止发布");
        
        private final String label;
        private final String description;
        
        RiskLevel(String label, String description) {
            this.label = label;
            this.description = description;
        }
        
        public String getLabel() {
            return label;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * 创建通过结果
     */
    public static ContentCheckResult pass() {
        ContentCheckResult result = new ContentCheckResult();
        result.setRiskLevel(RiskLevel.PASS);
        result.setRiskScore(0);
        result.setSuggestion("内容审核通过");
        result.setPassed(true);
        return result;
    }
    
    /**
     * 创建拒绝结果
     */
    public static ContentCheckResult reject(String reason) {
        ContentCheckResult result = new ContentCheckResult();
        result.setRiskLevel(RiskLevel.REJECT);
        result.setRiskScore(100);
        result.setSuggestion(reason);
        result.setPassed(false);
        return result;
    }
    
    /**
     * 创建需要审核结果
     */
    public static ContentCheckResult review(String reason) {
        ContentCheckResult result = new ContentCheckResult();
        result.setRiskLevel(RiskLevel.REVIEW);
        result.setRiskScore(50);
        result.setSuggestion(reason);
        result.setPassed(false);
        return result;
    }
}
