package com.antifraud.service.impl;

import com.antifraud.model.CheckInRecord;
import com.antifraud.model.UserCheckInStat;
import com.antifraud.repository.CheckInRecordRepository;
import com.antifraud.repository.UserCheckInStatRepository;
import com.antifraud.service.CheckInService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 签到服务实现类
 * 
 * 功能说明：
 * 1. 实现签到相关的业务逻辑
 * 2. 处理用户签到、签到统计等功能
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@Service
public class CheckInServiceImpl implements CheckInService {
    
    @Autowired
    private CheckInRecordRepository checkInRecordRepository;
    
    @Autowired
    private UserCheckInStatRepository userCheckInStatRepository;
    
    @Override
    public Map<String, Object> checkIn(Long userId) {
        Map<String, Object> result = new HashMap<>();
        
        // 检查今日是否已签到
        Date today = getToday();
        CheckInRecord existingRecord = checkInRecordRepository.findByUserIdAndDate(userId, today);
        if (existingRecord != null) {
            result.put("success", false);
            result.put("message", "今日已签到");
            return result;
        }
        
        // 获取用户签到统计
        UserCheckInStat stat = userCheckInStatRepository.findByUserId(userId);
        if (stat == null) {
            stat = new UserCheckInStat();
            stat.setUserId(userId);
            stat.setTotalCheckInDays(0);
            stat.setMaxConsecutiveDays(0);
            stat.setCurrentConsecutiveDays(0);
            stat.setTotalPoints(0);
        }
        
        // 计算连续签到天数
        int consecutiveDays = 1;
        if (stat.getLastCheckInDate() != null) {
            Date lastDate = stat.getLastCheckInDate();
            if (isYesterday(lastDate)) {
                consecutiveDays = stat.getCurrentConsecutiveDays() + 1;
            }
        }
        
        // 计算获得的积分（连续签到天数越多，获得的积分越多）
        int points = calculatePoints(consecutiveDays);
        
        // 创建签到记录
        CheckInRecord record = new CheckInRecord();
        record.setUserId(userId);
        record.setCheckInDate(today);
        record.setCheckInTime(new Date());
        record.setConsecutiveDays(consecutiveDays);
        record.setPoints(points);
        checkInRecordRepository.insert(record);
        
        // 更新用户签到统计
        stat.setTotalCheckInDays(stat.getTotalCheckInDays() + 1);
        stat.setCurrentConsecutiveDays(consecutiveDays);
        if (consecutiveDays > stat.getMaxConsecutiveDays()) {
            stat.setMaxConsecutiveDays(consecutiveDays);
        }
        stat.setTotalPoints(stat.getTotalPoints() + points);
        stat.setLastCheckInDate(today);
        
        if (userCheckInStatRepository.findByUserId(userId) == null) {
            userCheckInStatRepository.insert(stat);
        } else {
            userCheckInStatRepository.update(stat);
        }
        
        // 返回签到结果
        result.put("success", true);
        result.put("message", "签到成功");
        result.put("data", Map.of(
            "consecutiveDays", consecutiveDays,
            "points", points,
            "totalPoints", stat.getTotalPoints(),
            "totalCheckInDays", stat.getTotalCheckInDays(),
            "maxConsecutiveDays", stat.getMaxConsecutiveDays()
        ));
        
        return result;
    }
    
    @Override
    public UserCheckInStat getUserCheckInStat(Long userId) {
        return userCheckInStatRepository.findByUserId(userId);
    }
    
    @Override
    public boolean isCheckedInToday(Long userId) {
        Date today = getToday();
        CheckInRecord record = checkInRecordRepository.findByUserIdAndDate(userId, today);
        return record != null;
    }
    
    @Override
    public java.util.List<CheckInRecord> getCheckInHistory(Long userId) {
        return checkInRecordRepository.findRecentByUserId(userId, 30);
    }
    
    /**
     * 获取今天的日期（只包含年月日，不包含时分秒）
     */
    private Date getToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
    
    /**
     * 检查指定日期是否是昨天
     */
    private boolean isYesterday(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        
        Calendar yesterdayCalendar = Calendar.getInstance();
        yesterdayCalendar.add(Calendar.DAY_OF_MONTH, -1);
        yesterdayCalendar.set(Calendar.HOUR_OF_DAY, 0);
        yesterdayCalendar.set(Calendar.MINUTE, 0);
        yesterdayCalendar.set(Calendar.SECOND, 0);
        yesterdayCalendar.set(Calendar.MILLISECOND, 0);
        
        return date.equals(yesterdayCalendar.getTime());
    }
    
    /**
     * 计算签到获得的积分
     * 连续签到天数越多，获得的积分越多
     */
    private int calculatePoints(int consecutiveDays) {
        if (consecutiveDays >= 7) {
            return 5; // 连续签到7天及以上，获得5积分
        } else if (consecutiveDays >= 3) {
            return 3; // 连续签到3-6天，获得3积分
        } else {
            return 1; // 连续签到1-2天，获得1积分
        }
    }
}
