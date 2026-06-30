package com.antifraud.repository;

import com.antifraud.model.CheckInRecord;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

/**
 * 签到记录Repository
 * 
 * 功能说明：
 * 1. 提供签到记录的数据库操作方法
 * 2. 支持签到记录的增删改查
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@Mapper
public interface CheckInRecordRepository {
    
    /**
     * 插入签到记录
     * 
     * @param record 签到记录
     * @return 插入行数
     */
    @Insert("INSERT INTO check_in_records (user_id, check_in_date, check_in_time, consecutive_days, points) " +
            "VALUES (#{userId}, #{checkInDate}, #{checkInTime}, #{consecutiveDays}, #{points})")
    int insert(CheckInRecord record);
    
    /**
     * 根据用户ID和日期查询签到记录
     * 
     * @param userId 用户ID
     * @param checkInDate 签到日期
     * @return 签到记录
     */
    @Select("SELECT * FROM check_in_records WHERE user_id = #{userId} AND check_in_date = #{checkInDate}")
    CheckInRecord findByUserIdAndDate(@Param("userId") Long userId, @Param("checkInDate") Date checkInDate);
    
    /**
     * 查询用户最近的签到记录
     * 
     * @param userId 用户ID
     * @param limit 限制条数
     * @return 签到记录列表
     */
    @Select("SELECT * FROM check_in_records WHERE user_id = #{userId} " +
            "ORDER BY check_in_date DESC LIMIT #{limit}")
    List<CheckInRecord> findRecentByUserId(@Param("userId") Long userId, @Param("limit") int limit);
}
