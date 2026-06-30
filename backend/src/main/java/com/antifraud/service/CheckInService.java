package com.antifraud.service;

import com.antifraud.model.CheckInRecord;
import com.antifraud.model.UserCheckInStat;

import java.util.Map;

/**
 * 签到服务接口
 * 
 * 功能说明：
 * 1. 提供签到相关的业务逻辑
 * 2. 处理用户签到、签到统计等功能
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
public interface CheckInService {
    /**
     * 用户签到
     * 
     * @param userId 用户ID
     * @return 签到结果，包含连续签到天数、获得积分等信息
     */
    Map<String, Object> checkIn(Long userId);
    
    /**
     * 获取用户签到统计信息
     * 
     * @param userId 用户ID
     * @return 用户签到统计信息
     */
    UserCheckInStat getUserCheckInStat(Long userId);
    
    /**
     * 检查用户今日是否已签到
     * 
     * @param userId 用户ID
     * @return 是否已签到
     */
    boolean isCheckedInToday(Long userId);
    
    /**
     * 获取用户签到历史记录
     * 
     * @param userId 用户ID
     * @return 签到历史记录列表
     */
    java.util.List<CheckInRecord> getCheckInHistory(Long userId);
}
