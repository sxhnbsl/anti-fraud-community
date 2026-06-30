package com.antifraud.model;

import lombok.Data;
import java.util.Date;

/**
 * 测试记录实体类
 */
@Data
public class TestRecord {
    private Long id;
    private Long userId;
    private Integer score;
    private Integer totalQuestions;
    private Integer correctCount;
    private Integer wrongCount;
    private Integer timeUsed;
    private Date testDate;
    private String nickname;
    private String username;
    private String avatar;
}
