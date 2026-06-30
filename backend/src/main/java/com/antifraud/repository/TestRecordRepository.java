package com.antifraud.repository;

import com.antifraud.model.TestRecord;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 测试记录Repository
 */
@Mapper
public interface TestRecordRepository {

    @Insert("INSERT INTO test_record(user_id, score, total_questions, correct_count, wrong_count, " +
            "time_used, test_date) VALUES(#{userId}, #{score}, #{totalQuestions}, #{correctCount}, " +
            "#{wrongCount}, #{timeUsed}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TestRecord record);

    @Select("SELECT * FROM test_record WHERE id = #{id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "score", column = "score"),
            @Result(property = "totalQuestions", column = "total_questions"),
            @Result(property = "correctCount", column = "correct_count"),
            @Result(property = "wrongCount", column = "wrong_count"),
            @Result(property = "timeUsed", column = "time_used"),
            @Result(property = "testDate", column = "test_date")
    })
    TestRecord findById(Long id);

    @Select("SELECT * FROM test_record WHERE user_id = #{userId} ORDER BY test_date DESC")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "score", column = "score"),
            @Result(property = "totalQuestions", column = "total_questions"),
            @Result(property = "correctCount", column = "correct_count"),
            @Result(property = "wrongCount", column = "wrong_count"),
            @Result(property = "timeUsed", column = "time_used"),
            @Result(property = "testDate", column = "test_date")
    })
    List<TestRecord> findByUserId(Long userId);

    @Select("SELECT * FROM test_record ORDER BY score DESC, time_used ASC LIMIT #{limit}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "score", column = "score"),
            @Result(property = "totalQuestions", column = "total_questions"),
            @Result(property = "correctCount", column = "correct_count"),
            @Result(property = "wrongCount", column = "wrong_count"),
            @Result(property = "timeUsed", column = "time_used"),
            @Result(property = "testDate", column = "test_date")
    })
    List<TestRecord> findTopByScore(@Param("limit") int limit);

    @Select("SELECT tr.*, u.nickname, u.nickname as username, u.avatar FROM test_record tr " +
            "INNER JOIN (SELECT user_id, MAX(score) as max_score FROM test_record GROUP BY user_id) max_tr " +
            "ON tr.user_id = max_tr.user_id AND tr.score = max_tr.max_score " +
            "INNER JOIN users u ON tr.user_id = u.id " +
            "ORDER BY tr.score DESC, tr.time_used ASC LIMIT #{limit}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "score", column = "score"),
            @Result(property = "totalQuestions", column = "total_questions"),
            @Result(property = "correctCount", column = "correct_count"),
            @Result(property = "wrongCount", column = "wrong_count"),
            @Result(property = "timeUsed", column = "time_used"),
            @Result(property = "testDate", column = "test_date"),
            @Result(property = "nickname", column = "nickname"),
            @Result(property = "username", column = "username"),
            @Result(property = "avatar", column = "avatar")
    })
    List<TestRecord> findTopByScoreDistinct(@Param("limit") int limit);

    @Select("SELECT * FROM test_record ORDER BY test_date DESC")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "score", column = "score"),
            @Result(property = "totalQuestions", column = "total_questions"),
            @Result(property = "correctCount", column = "correct_count"),
            @Result(property = "wrongCount", column = "wrong_count"),
            @Result(property = "timeUsed", column = "time_used"),
            @Result(property = "testDate", column = "test_date")
    })
    List<TestRecord> findAll();
}
