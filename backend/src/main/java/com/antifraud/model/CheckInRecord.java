package com.antifraud.model;

import lombok.Data;

import java.util.Date;

/**
 * 签到记录模型
 * 
 * 功能说明：
 * 1. 记录用户每日签到情况
 * 2. 包含签到日期、连续签到天数、获得积分等信息
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@Data
public class CheckInRecord {
    /**
     * 签到记录ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 签到日期
     */
    private Date checkInDate;
    
    /**
     * 签到时间
     */
    private Date checkInTime;
    
    /**
     * 连续签到天数
     */
    private Integer consecutiveDays;
    
    /**
     * 本次签到获得的积分
     */
    private Integer points;
    
    /**
     * 创建时间
     */
    private Date createdAt;
    
    /**
     * 更新时间
     */
    private Date updatedAt;
}
