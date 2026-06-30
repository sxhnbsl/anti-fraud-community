package com.antifraud.model;

import lombok.Data;
import java.util.Date;

/**
 * 测试题目实体类
 */
@Data
public class TestQuestion {
    private Long id;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctAnswer;
    private String explanation;
    private String category;
    private Integer difficulty;
    private Date createdAt;
    private Date updatedAt;
}
