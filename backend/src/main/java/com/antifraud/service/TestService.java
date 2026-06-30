package com.antifraud.service;

import com.antifraud.model.TestQuestion;
import com.antifraud.model.TestRecord;
import java.util.List;
import java.util.Map;

/**
 * 测试服务接口
 */
public interface TestService {
    /**
     * 获取随机测试题目
     */
    List<TestQuestion> getRandomQuestions(int count);

    /**
     * 提交测试
     * @param userId 用户ID
     * @param answers 答案Map（题目ID -> 用户答案）
     * @param timeUsed 用时（秒）
     * @return 测试记录
     */
    TestRecord submitTest(Long userId, Map<Long, String> answers, int timeUsed);

    /**
     * 获取用户测试记录
     */
    List<TestRecord> getUserTestRecords(Long userId);

    /**
     * 获取排行榜
     */
    List<TestRecord> getRankingList(int limit);

    /**
     * 获取测试详情
     */
    TestRecord getTestDetail(Long recordId);
}
