package com.antifraud.service;

import com.antifraud.dto.ContentCheckResult;

/**
 * 内容安全审核服务接口
 * 
 * 功能说明：
 * 1. 提供文本内容的安全审核功能
 * 2. 支持检测敏感词、违规内容
 * 3. 支持不同业务场景的审核策略
 * 4. 返回详细的审核结果和风险等级
 * 
 * 使用场景：
 * - 帖子发布前的内容审核
 * - 评论发布前的内容审核
 * - 用户提交内容的实时检测
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
public interface ContentSecurityService {
    
    /**
     * 审核文本内容（默认场景）
     * 
     * 功能说明：
     * - 对文本内容进行安全审核
     * - 检测是否包含敏感词、违规内容
     * - 返回审核结果，包含是否通过、风险等级等信息
     * 
     * @param content 待审核的文本内容
     * @return ContentCheckResult 审核结果对象，包含以下信息：
     *         - passed: 是否通过审核
     *         - riskLevel: 风险等级（PASS/REVIEW/REJECT）
     *         - riskScore: 风险分数（0-100）
     *         - suggestion: 审核建议或违规原因
     *         - riskLabels: 检测到的风险标签列表
     */
    ContentCheckResult checkText(String content);
    
    /**
     * 审核文本内容（指定业务场景）
     * 
     * 功能说明：
     * - 对文本内容进行安全审核
     * - 根据不同业务场景应用不同的审核策略
     * - 支持的场景包括：post（帖子）、comment（评论）等
     * 
     * 业务场景说明：
     * - post: 帖子内容审核，相对宽松，允许讨论诈骗防范
     * - comment: 评论内容审核，相对严格，防止恶意评论
     * 
     * @param content 待审核的文本内容
     * @param scene 业务场景，如post、comment等
     * @return ContentCheckResult 审核结果对象
     */
    ContentCheckResult checkText(String content, String scene);
}
