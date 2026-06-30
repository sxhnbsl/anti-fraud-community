package com.antifraud.repository;

import com.antifraud.model.TestQuestion;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 测试题目Repository
 */
@Mapper
public interface TestQuestionRepository {

    @Insert("INSERT INTO test_question(question_text, option_a, option_b, option_c, option_d, " +
            "correct_answer, explanation, category, difficulty, created_at, updated_at) " +
            "VALUES(#{questionText}, #{optionA}, #{optionB}, #{optionC}, #{optionD}, " +
            "#{correctAnswer}, #{explanation}, #{category}, #{difficulty}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TestQuestion question);

    @Select("SELECT * FROM test_question ORDER BY RAND() LIMIT #{count}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "questionText", column = "question_text"),
            @Result(property = "optionA", column = "option_a"),
            @Result(property = "optionB", column = "option_b"),
            @Result(property = "optionC", column = "option_c"),
            @Result(property = "optionD", column = "option_d"),
            @Result(property = "correctAnswer", column = "correct_answer"),
            @Result(property = "explanation", column = "explanation"),
            @Result(property = "category", column = "category"),
            @Result(property = "difficulty", column = "difficulty"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    List<TestQuestion> findRandom(@Param("count") int count);

    @Select("SELECT * FROM test_question WHERE id = #{id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "questionText", column = "question_text"),
            @Result(property = "optionA", column = "option_a"),
            @Result(property = "optionB", column = "option_b"),
            @Result(property = "optionC", column = "option_c"),
            @Result(property = "optionD", column = "option_d"),
            @Result(property = "correctAnswer", column = "correct_answer"),
            @Result(property = "explanation", column = "explanation"),
            @Result(property = "category", column = "category"),
            @Result(property = "difficulty", column = "difficulty"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    TestQuestion findById(Long id);

    @Select("SELECT * FROM test_question ORDER BY created_at DESC")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "questionText", column = "question_text"),
            @Result(property = "optionA", column = "option_a"),
            @Result(property = "optionB", column = "option_b"),
            @Result(property = "optionC", column = "option_c"),
            @Result(property = "optionD", column = "option_d"),
            @Result(property = "correctAnswer", column = "correct_answer"),
            @Result(property = "explanation", column = "explanation"),
            @Result(property = "category", column = "category"),
            @Result(property = "difficulty", column = "difficulty"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    List<TestQuestion> findAll();

    @Select("SELECT COUNT(*) FROM test_question")
    int count();
}
