package com.antifraud.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Like {
    private Long id;
    private Long userId;
    private Long targetId;
    private String targetType;
    private LocalDateTime createdAt;
}