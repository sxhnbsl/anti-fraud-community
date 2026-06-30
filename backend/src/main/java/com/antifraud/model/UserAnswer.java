package com.antifraud.model;

import lombok.Data;
import java.util.Date;

/**
 * 用户答题详情实体类
 */
@Data
public class UserAnswer {
    private Long id;
    private Long recordId;
    private Long questionId;
    private String userAnswer;
    private Boolean isCorrect;
}
