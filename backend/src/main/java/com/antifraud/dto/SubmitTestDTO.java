package com.antifraud.dto;

import lombok.Data;
import java.util.Map;

/**
 * 提交测试DTO
 */
@Data
public class SubmitTestDTO {
    private Map<Long, String> answers;  // 题目ID -> 用户答案
    private Integer timeUsed;            // 用时（秒）
}
