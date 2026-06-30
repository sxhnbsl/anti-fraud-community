package com.antifraud.repository;

import com.antifraud.model.UserAnswer;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 用户答题详情Repository
 */
@Mapper
public interface UserAnswerRepository {

    @Insert("INSERT INTO user_answer(record_id, question_id, user_answer, is_correct) " +
            "VALUES(#{recordId}, #{questionId}, #{userAnswer}, #{isCorrect})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserAnswer userAnswer);

    @Insert("<script>" +
            "INSERT INTO user_answer(record_id, question_id, user_answer, is_correct) VALUES " +
            "<foreach collection='list' item='item' separator=','>" +
            "(#{item.recordId}, #{item.questionId}, #{item.userAnswer}, #{item.isCorrect})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("list") List<UserAnswer> userAnswers);

    @Select("SELECT * FROM user_answer WHERE record_id = #{recordId}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "recordId", column = "record_id"),
            @Result(property = "questionId", column = "question_id"),
            @Result(property = "userAnswer", column = "user_answer"),
            @Result(property = "isCorrect", column = "is_correct")
    })
    List<UserAnswer> findByRecordId(Long recordId);

    @Select("SELECT * FROM user_answer WHERE question_id = #{questionId}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "recordId", column = "record_id"),
            @Result(property = "questionId", column = "question_id"),
            @Result(property = "userAnswer", column = "user_answer"),
            @Result(property = "isCorrect", column = "is_correct")
    })
    List<UserAnswer> findByQuestionId(Long questionId);
}
