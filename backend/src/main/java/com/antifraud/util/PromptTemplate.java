package com.antifraud.util;

/**
 * AI Prompt模板管理类
 * 
 * 功能说明：
 * 1. 定义防诈骗问答的系统Prompt
 * 2. 提供不同场景的Prompt模板
 * 3. 支持动态生成Prompt
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
public class PromptTemplate {
    
    /**
     * 系统角色定义
     */
    public static final String SYSTEM_PROMPT = 
        "你是一个专业的反诈骗AI助手，致力于帮助用户识别和防范各类诈骗。\n" +
        "\n" +
        "你的职责：\n" +
        "1. 分析用户描述的情况，判断是否存在诈骗风险\n" +
        "2. 提供具体的防范建议和应对措施\n" +
        "3. 普及反诈骗知识和常见诈骗手段\n" +
        "4. 引导用户保护个人信息和财产安全\n" +
        "\n" +
        "回答要求：\n" +
        "- 专业、准确、易懂\n" +
        "- 避免过于技术化的语言\n" +
        "- 对于高风险情况，给出明确的警示\n" +
        "- 提供可操作的建议\n" +
        "- 如果情况紧急，建议用户立即报警\n" +
        "\n" +
        "常见诈骗类型：\n" +
        "- 电信诈骗：冒充客服、冒充公检法、冒充领导\n" +
        "- 网络诈骗：刷单返利、虚假投资、网络贷款\n" +
        "- 社交诈骗：杀猪盘、交友诈骗、冒充熟人\n" +
        "- 金融诈骗：高收益投资、非法集资、庞氏骗局\n" +
        "\n" +
        "风险等级说明：\n" +
        "- 高风险：立即停止操作，建议报警\n" +
        "- 中风险：提高警惕，核实对方身份\n" +
        "- 低风险：保持警惕，注意保护个人信息";
    
    /**
     * 识别诈骗场景Prompt
     */
    public static final String IDENTIFY_SCAM_PROMPT = 
        "请分析以下情况，判断是否存在诈骗风险：\n" +
        "\n" +
        "用户描述：{user_input}\n" +
        "\n" +
        "请按以下格式回答：\n" +
        "1. 风险等级：高/中/低\n" +
        "2. 诈骗类型：如电信诈骗、网络诈骗等\n" +
        "3. 风险分析：详细说明为什么存在风险\n" +
        "4. 应对建议：具体的防范措施\n" +
        "5. 紧急程度：是否需要立即采取行动";
    
    /**
     * 防范建议场景Prompt
     */
    public static final String PREVENTION_ADVICE_PROMPT = 
        "用户询问如何防范诈骗，请提供详细的防范建议：\n" +
        "\n" +
        "用户问题：{user_input}\n" +
        "\n" +
        "请提供以下方面的建议：\n" +
        "1. 识别要点：如何识别此类诈骗\n" +
        "2. 防范措施：具体的防范方法\n" +
        "3. 注意事项：需要特别警惕的地方\n" +
        "4. 应急处理：如果已经受骗怎么办";
    
    /**
     * 知识普及场景Prompt
     */
    public static final String KNOWLEDGE_PROMPT = 
        "用户想了解反诈骗相关知识，请提供详细的解答：\n" +
        "\n" +
        "用户问题：{user_input}\n" +
        "\n" +
        "请提供：\n" +
        "1. 概念解释：清晰解释相关概念\n" +
        "2. 常见手段：列举常见的诈骗手法\n" +
        "3. 真实案例：提供1-2个真实案例\n" +
        "4. 防范要点：总结防范要点";
    
    /**
     * 紧急求助场景Prompt
     */
    public static final String EMERGENCY_HELP_PROMPT = 
        "用户可能正在遭遇诈骗，需要紧急帮助：\n" +
        "\n" +
        "用户描述：{user_input}\n" +
        "\n" +
        "请立即提供：\n" +
        "1. 风险判断：是否为紧急情况\n" +
        "2. 紧急措施：用户应该立即采取的行动\n" +
        "3. 报警指引：如何报警和求助\n" +
        "4. 资金保护：如何保护资金安全\n" +
        "5. 后续处理：后续应该做什么";
    
    /**
     * 生成识别诈骗的Prompt
     * 
     * @param userInput 用户输入
     * @return 完整的Prompt
     */
    public static String generateIdentifyScamPrompt(String userInput) {
        return IDENTIFY_SCAM_PROMPT.replace("{user_input}", userInput);
    }
    
    /**
     * 生成防范建议的Prompt
     * 
     * @param userInput 用户输入
     * @return 完整的Prompt
     */
    public static String generatePreventionAdvicePrompt(String userInput) {
        return PREVENTION_ADVICE_PROMPT.replace("{user_input}", userInput);
    }
    
    /**
     * 生成知识普及的Prompt
     * 
     * @param userInput 用户输入
     * @return 完整的Prompt
     */
    public static String generateKnowledgePrompt(String userInput) {
        return KNOWLEDGE_PROMPT.replace("{user_input}", userInput);
    }
    
    /**
     * 生成紧急求助的Prompt
     * 
     * @param userInput 用户输入
     * @return 完整的Prompt
     */
    public static String generateEmergencyHelpPrompt(String userInput) {
        return EMERGENCY_HELP_PROMPT.replace("{user_input}", userInput);
    }
}
