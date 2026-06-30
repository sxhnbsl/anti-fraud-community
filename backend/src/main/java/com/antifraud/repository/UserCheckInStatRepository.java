package com.antifraud.repository;

import com.antifraud.model.UserCheckInStat;
import org.apache.ibatis.annotations.*;

/**
 * 用户签到统计Repository
 * 
 * 功能说明：
 * 1. 提供用户签到统计的数据库操作方法
 * 2. 支持签到统计的增删改查
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@Mapper
public interface UserCheckInStatRepository {
    
    /**
     * 插入用户签到统计
     * 
     * @param stat 用户签到统计
     * @return 插入行数
     */
    @Insert("INSERT INTO user_check_in_stats (user_id, total_check_in_days, max_consecutive_days, " +
            "current_consecutive_days, total_points, last_check_in_date) " +
            "VALUES (#{userId}, #{totalCheckInDays}, #{maxConsecutiveDays}, " +
            "#{currentConsecutiveDays}, #{totalPoints}, #{lastCheckInDate})")
    int insert(UserCheckInStat stat);
    
    /**
     * 根据用户ID查询签到统计
     * 
     * @param userId 用户ID
     * @return 用户签到统计
     */
    @Select("SELECT * FROM user_check_in_stats WHERE user_id = #{userId}")
    UserCheckInStat findByUserId(@Param("userId") Long userId);
    
    /**
     * 更新用户签到统计
     * 
     * @param stat 用户签到统计
     * @return 更新行数
     */
    @Update("UPDATE user_check_in_stats SET " +
            "total_check_in_days = #{totalCheckInDays}, " +
            "max_consecutive_days = #{maxConsecutiveDays}, " +
            "current_consecutive_days = #{currentConsecutiveDays}, " +
            "total_points = #{totalPoints}, " +
            "last_check_in_date = #{lastCheckInDate} " +
            "WHERE user_id = #{userId}")
    int update(UserCheckInStat stat);
}
