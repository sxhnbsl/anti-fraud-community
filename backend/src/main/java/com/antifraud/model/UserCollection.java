package com.antifraud.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserCollection {
    private Long id;
    private Long userId;
    private Long targetId;
    private String type; // knowledge, case
    private String title;
    private String summary;
    private LocalDateTime createdAt;
}