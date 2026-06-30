package com.antifraud.model;

import lombok.Data;

import java.util.Date;

/**
 * 用户签到统计模型
 * 
 * 功能说明：
 * 1. 统计用户的签到总天数和连续签到天数
 * 2. 记录用户的签到积分和最大连续签到记录
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@Data
public class UserCheckInStat {
    /**
     * 统计ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 总签到天数
     */
    private Integer totalCheckInDays;
    
    /**
     * 最大连续签到天数
     */
    private Integer maxConsecutiveDays;
    
    /**
     * 当前连续签到天数
     */
    private Integer currentConsecutiveDays;
    
    /**
     * 签到总积分
     */
    private Integer totalPoints;
    
    /**
     * 最后签到日期
     */
    private Date lastCheckInDate;
    
    /**
     * 创建时间
     */
    private Date createdAt;
    
    /**
     * 更新时间
     */
    private Date updatedAt;
}
