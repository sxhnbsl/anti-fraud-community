package com.antifraud.repository;

import com.antifraud.model.Like;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;

@Mapper
public interface LikeRepository {

    @Insert("INSERT INTO likes(user_id, target_id, target_type, created_at) " +
            "VALUES(#{userId}, #{targetId}, #{targetType}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Like like);

    @Select("SELECT * FROM likes WHERE user_id = #{userId} AND target_id = #{targetId} AND target_type = #{targetType}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "targetId", column = "target_id"),
            @Result(property = "targetType", column = "target_type"),
            @Result(property = "createdAt", column = "created_at")
    })
    Like findByUserIdAndTargetIdAndTargetType(Long userId, Long targetId, String targetType);

    @Delete("DELETE FROM likes WHERE user_id = #{userId} AND target_id = #{targetId} AND target_type = #{targetType}")
    int deleteByUserIdAndTargetIdAndTargetType(Long userId, Long targetId, String targetType);

    @Delete("DELETE FROM likes WHERE target_id = #{targetId} AND target_type = #{targetType}")
    int deleteByTargetIdAndTargetType(@org.apache.ibatis.annotations.Param("targetId") Long targetId, @org.apache.ibatis.annotations.Param("targetType") String targetType);
}