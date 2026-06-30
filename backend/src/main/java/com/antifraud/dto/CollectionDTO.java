package com.antifraud.dto;

import lombok.Data;

@Data
public class CollectionDTO {
    private Long userId;
    private Long targetId;
    private String type;
    private String title;
    private String summary;
}