package com.antifraud.service.impl;

import com.antifraud.model.TestQuestion;
import com.antifraud.model.TestRecord;
import com.antifraud.model.UserAnswer;
import com.antifraud.repository.TestQuestionRepository;
import com.antifraud.repository.TestRecordRepository;
import com.antifraud.repository.UserAnswerRepository;
import com.antifraud.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 测试服务实现类
 */
@Service
public class TestServiceImpl implements TestService {

    @Autowired
    private TestQuestionRepository testQuestionRepository;

    @Autowired
    private TestRecordRepository testRecordRepository;

    @Autowired
    private UserAnswerRepository userAnswerRepository;

    @Override
    public List<TestQuestion> getRandomQuestions(int count) {
        return testQuestionRepository.findRandom(count);
    }

    @Override
    @Transactional
    public TestRecord submitTest(Long userId, Map<Long, String> answers, int timeUsed) {
        // 创建测试记录
        TestRecord record = new TestRecord();
        record.setUserId(userId);
        record.setTotalQuestions(answers.size());
        record.setTimeUsed(timeUsed);

        // 计算分数
        int correctCount = 0;
        int wrongCount = 0;
        int score = 0;

        List<UserAnswer> userAnswers = new ArrayList<>();

        for (Map.Entry<Long, String> entry : answers.entrySet()) {
            Long questionId = entry.getKey();
            String userAnswer = entry.getValue();

            // 获取题目
            TestQuestion question = testQuestionRepository.findById(questionId);
            if (question != null) {
                boolean isCorrect = question.getCorrectAnswer().equalsIgnoreCase(userAnswer);

                if (isCorrect) {
                    correctCount++;
                    score += 10; // 每题10分
                } else {
                    wrongCount++;
                }

                // 保存答题详情
                UserAnswer answer = new UserAnswer();
                answer.setQuestionId(questionId);
                answer.setUserAnswer(userAnswer);
                answer.setIsCorrect(isCorrect);
                userAnswers.add(answer);
            }
        }

        record.setCorrectCount(correctCount);
        record.setWrongCount(wrongCount);
        record.setScore(score);

        // 保存测试记录
        testRecordRepository.insert(record);

        // 批量保存答题详情
        if (!userAnswers.isEmpty()) {
            for (UserAnswer answer : userAnswers) {
                answer.setRecordId(record.getId());
            }
            userAnswerRepository.batchInsert(userAnswers);
        }

        return record;
    }

    @Override
    public List<TestRecord> getUserTestRecords(Long userId) {
        return testRecordRepository.findByUserId(userId);
    }

    @Override
    public List<TestRecord> getRankingList(int limit) {
        return testRecordRepository.findTopByScoreDistinct(limit);
    }

    @Override
    public TestRecord getTestDetail(Long recordId) {
        return testRecordRepository.findById(recordId);
    }
}
