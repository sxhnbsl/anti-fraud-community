package com.antifraud.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.green20220302.Client;
import com.aliyun.green20220302.models.TextModerationPlusRequest;
import com.aliyun.green20220302.models.TextModerationPlusResponse;
import com.aliyun.green20220302.models.TextModerationPlusResponseBody;
import com.aliyun.teaopenapi.models.Config;
import com.antifraud.dto.ContentCheckResult;
import com.antifraud.service.ContentSecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * 内容安全审核服务实现类
 * 
 * 功能说明：
 * 1. 实现文本内容的安全审核功能
 * 2. 支持阿里云内容安全API调用（需配置AccessKey）
 * 3. 支持本地敏感词检测（作为备用方案）
 * 4. 根据审核结果返回不同的风险等级
 * 
 * 审核流程：
 * 1. 首先进行本地敏感词检测（快速拦截明显违规内容）
 * 2. 如果配置了阿里云API，则调用云端进行深度审核
 * 3. 根据审核结果返回PASS/REVIEW/REJECT三种状态
 * 
 * 风险等级说明：
 * - PASS: 内容安全，可以直接发布
 * - REVIEW: 内容存在风险，需要人工审核
 * - REJECT: 内容严重违规，禁止发布
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
// @Slf4j: Lombok注解，自动生成日志记录器
@Slf4j
@Service
public class ContentSecurityServiceImpl implements ContentSecurityService {

    @Value("${aliyun.accessKeyId:}")
    private String accessKeyId;

    @Value("${aliyun.accessKeySecret:}")
    private String accessKeySecret;

    @Value("${aliyun.regionId:cn-shanghai}")
    private String regionId;

    @Value("${aliyun.endpoint:green-cip.cn-shanghai.aliyuncs.com}")
    private String endpoint;

    private Client client;

    /**
     * 初始化阿里云内容安全客户端
     * 
     * 功能说明：
     * - 检查是否配置了阿里云AccessKey
     * - 如果已配置，则创建阿里云内容安全客户端
     * - 如果未配置，则使用本地模拟审核
     * 
     * 注意事项：
     * - AccessKey需要妥善保管，不要泄露
     * - 建议使用RAM子账号的AccessKey，并只授予必要的权限
     */
    @PostConstruct
    public void init() {
        if (accessKeyId == null || accessKeyId.isEmpty() || 
            accessKeySecret == null || accessKeySecret.isEmpty()) {
            log.warn("阿里云内容安全服务未配置，将使用本地模拟审核");
            return;
        }

        try {
            Config config = new Config();
            config.setAccessKeyId(accessKeyId);
            config.setAccessKeySecret(accessKeySecret);
            config.setRegionId(regionId);
            config.setEndpoint(endpoint);
            config.setConnectTimeout(10000);
            config.setReadTimeout(30000);
            client = new Client(config);
            log.info("阿里云内容安全服务初始化成功");
        } catch (Exception e) {
            log.error("阿里云内容安全服务初始化失败: {}", e.getMessage(), e);
        }
    }

    @Value("${aliyun.content-security.scenes.post:ugc_moderation_byllm}")
    private String postScene;
    
    @Value("${aliyun.content-security.scenes.comment:ugc_moderation_byllm}")
    private String commentScene;

    /**
     * 审核文本内容（默认场景）
     * 
     * @param content 待审核的文本内容
     * @return 审核结果对象
     */
    @Override
    public ContentCheckResult checkText(String content) {
        return checkText(content, "post");
    }

    /**
     * 审核文本内容（指定业务场景）
     * 
     * 功能说明：
     * - 对文本内容进行安全审核
     * - 首先进行本地敏感词检测
     * - 如果本地检测通过，返回通过结果
     * 
     * 审核策略：
     * - 空内容直接通过
     * - 先进行本地敏感词检测（快速）
     * - 本地检测通过后，可调用云端API进行深度检测
     * 
     * @param content 待审核的文本内容
     * @param scene 业务场景
     * @return 审核结果对象
     */
    @Override
    public ContentCheckResult checkText(String content, String scene) {

        if (content == null || content.trim().isEmpty()) {
            return ContentCheckResult.pass();
        }

        ContentCheckResult localCheckResult = localSensitiveWordCheck(content);
        if (localCheckResult != null) {
            log.warn("本地敏感词检测发现违规内容: {}", localCheckResult.getSuggestion());
            return localCheckResult;
        }

        if (client != null) {
            ContentCheckResult cloudResult = callCloudApi(content, scene);
            if (cloudResult != null) {
                return cloudResult;
            }
            log.warn("阿里云审核异常，降级使用本地检测结果（通过）");
        }

        return ContentCheckResult.pass();
    }

    /**
     * 调用阿里云内容安全API进行深度审核
     *
     * 功能说明：
     * - 将文本内容发送到阿里云内容安全服务进行审核
     * - 支持帖子和评论两种业务场景
     * - 调用失败时返回null，由上层方法降级处理
     *
     * @param content 待审核的文本内容
     * @param scene 业务场景（post/comment）
     * @return 审核结果对象，调用失败返回null
     */
    private ContentCheckResult callCloudApi(String content, String scene) {
        try {
            TextModerationPlusRequest request = new TextModerationPlusRequest();

            String serviceScene = "comment".equals(scene) ? commentScene : postScene;
            request.setService(serviceScene);

            java.util.Map<String, Object> serviceParams = new java.util.HashMap<>();
            serviceParams.put("content", content);
            request.setServiceParameters(JSON.toJSONString(serviceParams));

            TextModerationPlusResponse response = client.textModerationPlus(request);

            if (response.getStatusCode() == 200) {
                return parseResponse(response.getBody());
            } else {
                log.error("阿里云内容审核返回非200状态码: {}", response.getStatusCode());
                return null;
            }
        } catch (Exception e) {
            log.error("阿里云内容审核调用异常: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 解析阿里云内容审核响应
     * 
     * 功能说明：
     * - 解析阿里云返回的审核结果
     * - 根据风险等级设置审核结果
     * 
     * 风险等级映射：
     * - none/low: PASS（通过）
     * - medium: REVIEW（需要人工审核）
     * - high: REJECT（拒绝）
     * 
     * @param body 阿里云响应体
     * @return 审核结果对象
     */
    private ContentCheckResult parseResponse(TextModerationPlusResponseBody body) {
        ContentCheckResult result = new ContentCheckResult();
        
        if (body.getCode() == null || body.getCode() != 200) {
            log.error("阿里云内容审核失败: code={}, message={}", body.getCode(), body.getMessage());
            return ContentCheckResult.review("审核服务异常: " + body.getMessage());
        }

        TextModerationPlusResponseBody.TextModerationPlusResponseBodyData data = body.getData();
        if (data == null) {
            log.error("阿里云内容审核返回空数据");
            return ContentCheckResult.review("审核服务异常，请稍后重试");
        }

        String riskLevel = data.getRiskLevel();
        log.info("阿里云审核结果 - 风险等级: {}", riskLevel);
        
        switch (riskLevel) {
            case "none":
            case "low":
                result.setRiskLevel(ContentCheckResult.RiskLevel.PASS);
                result.setRiskScore(0);
                result.setPassed(true);
                result.setSuggestion("内容审核通过");
                break;
            case "medium":
                result.setRiskLevel(ContentCheckResult.RiskLevel.REVIEW);
                result.setRiskScore(50);
                result.setPassed(false);
                result.setSuggestion("内容存在风险，需要人工审核");
                break;
            case "high":
            default:
                log.warn("高风险内容: {}", riskLevel);
                result.setRiskLevel(ContentCheckResult.RiskLevel.REJECT);
                result.setRiskScore(100);
                result.setPassed(false);
                result.setSuggestion("内容违规，禁止发布");
                break;
        }

        result.setRiskLabels(new ArrayList<>());
        result.setRiskDetails("");

        return result;
    }

    /**
     * 反诈骗正面语境关键词
     * 当内容包含这些正面语境词时，"诈骗/骗子/洗钱/传销/赌博"类敏感词将被放行
     * 因为用户在讨论防诈骗话题时必然会提到这些词
     */
    private static final String[] FRAUD_POSITIVE_CONTEXT = {
        "反诈骗", "防诈骗", "防骗", "反骗", "反诈", "防诈",
        "防范诈骗", "识别诈骗", "举报诈骗", "揭露诈骗",
        "反欺诈", "防欺诈", "防范欺诈",
        "反洗钱", "防洗钱",
        "反传销", "防传销",
        "反赌博", "防赌博", "拒绝赌博",
        "诈骗手法", "诈骗案例", "诈骗套路", "诈骗类型",
        "被骗", "受骗", "上当", "中招",
        "科普", "警惕", "提醒", "预警", "防范", "预防",
        "投诉", "举报", "报警", "维权",
        "受害者", "受害人", "损失"
    };

    /**
     * 需要在正面语境下豁免的诈骗/违法相关词汇
     * 这些词在防诈骗科普平台上是正常讨论内容，不应一刀切拦截
     */
    private static final String[] FRAUD_EXEMPTION_KEYWORDS = {
        "诈骗", "骗子", "骗钱", "骗局", "骗术",
        "洗钱", "传销", "赌博", "非法集资"
    };

    /**
     * 判断内容是否包含反诈骗正面语境
     * 只要内容中出现任意一个正面语境关键词，就认为是在正常讨论防诈骗话题
     */
    private boolean isPositiveContext(String lowerContent) {
        for (String positiveWord : FRAUD_POSITIVE_CONTEXT) {
            if (lowerContent.contains(positiveWord.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断某词是否是防诈骗平台应豁免的词汇
     */
    private boolean isFraudExemptionWord(String lowerWord) {
        for (String keyword : FRAUD_EXEMPTION_KEYWORDS) {
            if (lowerWord.equals(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private ContentCheckResult localSensitiveWordCheck(String content) {
        String[] severeSensitiveWords = {
            "他妈", "傻缺", "傻逼", "傻B", "傻b", "傻×", "傻叉", "傻吊",
            "操你", "草你", "草泥马", "妈的", "妈了个", "妈个",
            "去死", "死全家", "全家死", "死光", "死绝",
            "废物", "垃圾", "狗东西", "狗屎", "狗屁",
            "滚蛋", "滚开", "滚粗", "滚远点",
            "婊子", "贱人", "贱货", "贱逼",
            "畜生", "禽兽", "人渣", "败类",
            "强奸", "轮奸", "性交", "做爱", "性器官",
            "杀人", "杀光", "屠杀", "血洗",
            "恐怖", "炸弹", "炸药", "毒药", "毒药",
            "诈骗", "骗子", "骗钱", "骗局", "骗术",
            "赌博", "赌场", "赌球", "赌马",
            "色情", "黄色", "裸聊", "裸照",
            "毒品", "大麻", "海洛因", "冰毒",
            "枪支", "枪械", "炸弹", "手雷",
            "邪教", "传销", "非法集资", "洗钱",
            "微信", "加我微信", "加微信", "微信号", "wxid",
            "vx", "Vx", "VX", "wchat", "Wchat", "WX",
            "加v", "加V", "加我v", "加我V", "v信", "V信",
            "手机号", "手机号码", "电话号", "电话号码",
            "邮箱", "邮想", "youxiang", "you xiang",
            "@qq.com", "@163.com", "@126.com", "@gmail.com", "@outlook.com",
            "联系我", "加好友", "加我好友", "私聊", "私加",
            "二维码", "扫码", "扫一扫", "添加好友",
            "qq", "QQ", "扣扣", "Qq",
            "加qq", "加QQ", "加我qq", "加我QQ",
            "qq号", "QQ号", "扣扣号",
            "tg", "TG", "电报", "telegram", "Telegram",
            "推特", "twitter", "Twitter",
            "脸书", "facebook", "Facebook",
            "instagram", "Instagram", "ins", "Ins",
            "line", "Line", "LINE",
            "whatsapp", "WhatsApp",
            "tiktok", "TikTok", "抖音",
            "youtube", "YouTube", "油管",
            "加群", "进群", "拉群", "群聊",
            "代理", "代聊", "代练", "代打",
            "代刷", "代充", "代付",
            "刷单", "刷钻", "刷信誉", "刷销量",
            "提现", "取款", "转账", "汇款",
            "贷款", "借款", "借钱", "高利贷",
            "办证", "刻章", "发票", "代开发票",
            "代孕", "捐卵", "卖卵", "精子", "卵子",
            "兼职", "日结", "高薪", "轻松赚钱",
            "投资", "理财", "基金", "股票",
            "中奖", "抽奖", "领取奖品",
            "免费领", "免费送", "0元购",
            "返利", "返现", "佣金", "提成",
            "点击链接", "点击网址", "打开网站",
            "下载", "安装", "注册", "登录",
            "输入密码", "输入验证码",
            "信用卡", "银行卡", "卡号", "密码",
            "身份证", "身份证号", "身份证号码",
            "户口本", "护照", "驾驶证",
            "社保", "医保", "公积金",
            "裸贷", "校园贷", "套路贷",
            "黑户", "白户", "征信",
            "跑分", "刷单", "刷量",
            "洗钱", "跑分", "过账",
            "博彩", "六合彩", "时时彩",
            "网赌", "线上赌博", "网络赌博",
            "sex", "porn", "porno", "fuck", "shit",
            "ass", "asshole", "bitch", "cock", "dick",
            "pussy", "cum", "masturbate", "blowjob",
            "orgasm", "sex tape", "sex video",
            "xxx", "XXX", "18+", "十八禁", "成人",
            "色情", "色欲", "性欲", "性用品",
            "壮阳", "伟哥", "延时", "增大",
            "丰胸", "隆胸", "抽脂", "整容",
            "迷药", "春药", "催情", "迷奸",
            "偷拍", "偷录", "偷窥", "露阴",
            "暴露", "裸露", "裸体", "全裸",
            "人妖", "变性", "同性恋", "拉拉",
            "基佬", "搞基", "同志", "蕾丝",
            "乱伦", "兽交", "人兽", "娈童",
            "恋童", "幼交", "儿童色情"
        };

        String lowerContent = content.toLowerCase();

        boolean hasPositiveContext = isPositiveContext(lowerContent);

        List<String> foundWords = new ArrayList<>();

        for (String word : severeSensitiveWords) {
            String lowerWord = word.toLowerCase();
            if (lowerContent.contains(lowerWord)) {
                if (hasPositiveContext && isFraudExemptionWord(lowerWord)) {
                    continue;
                }
                foundWords.add(word);
            }
        }

        if (!foundWords.isEmpty()) {
            ContentCheckResult result = new ContentCheckResult();
            result.setRiskLevel(ContentCheckResult.RiskLevel.REJECT);
            result.setRiskScore(100);
            result.setPassed(false);
            result.setSuggestion("内容包含违规词汇：" + String.join(", ", foundWords));
            result.setRiskLabels(foundWords);
            return result;
        }

        return null;
    }

    /**
     * 本地模拟审核（用于测试）
     * 
     * 功能说明：
     * - 在没有配置阿里云API时使用
     * - 进行简单的敏感词匹配
     * - 仅用于开发测试环境
     * 
     * @param content 待审核的文本内容
     * @return 审核结果对象
     */
    private ContentCheckResult localMockCheck(String content) {
        String[] sensitiveWords = {
            "诈骗", "欺诈", "骗子", "赌博", "色情", "暴力", "毒品", "枪支", "恐怖主义",
            "反动", "邪教", "传销", "非法集资", "洗钱", "黑客", "攻击", "破坏"
        };

        String lowerContent = content.toLowerCase();
        List<String> foundWords = new ArrayList<>();

        for (String word : sensitiveWords) {
            if (lowerContent.contains(word)) {
                foundWords.add(word);
            }
        }

        if (!foundWords.isEmpty()) {
            ContentCheckResult result = new ContentCheckResult();
            result.setRiskLevel(ContentCheckResult.RiskLevel.REJECT);
            result.setRiskScore(100);
            result.setPassed(false);
            result.setSuggestion("内容包含敏感词：" + String.join(", ", foundWords));
            result.setRiskLabels(foundWords);
            return result;
        }

        return ContentCheckResult.pass();
    }
}
