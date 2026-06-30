package com.antifraud.repository;

import com.antifraud.model.UserCollection;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import java.util.List;

@Mapper
public interface CollectionRepository {

    @Insert("INSERT INTO collections(user_id, target_id, type, title, summary, created_at) " +
            "VALUES(#{userId}, #{targetId}, #{type}, #{title}, #{summary}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserCollection collection);

    @Select("SELECT * FROM collections WHERE user_id = #{userId} AND target_id = #{targetId} AND type = #{type}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "targetId", column = "target_id"),
            @Result(property = "type", column = "type"),
            @Result(property = "title", column = "title"),
            @Result(property = "summary", column = "summary"),
            @Result(property = "createdAt", column = "created_at")
    })
    UserCollection findByUserIdAndTargetIdAndType(Long userId, Long targetId, String type);

    @Select("SELECT * FROM collections WHERE user_id = #{userId} ORDER BY created_at DESC")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "targetId", column = "target_id"),
            @Result(property = "type", column = "type"),
            @Result(property = "title", column = "title"),
            @Result(property = "summary", column = "summary"),
            @Result(property = "createdAt", column = "created_at")
    })
    List<UserCollection> findByUserId(Long userId);

    @Select("SELECT * FROM collections WHERE user_id = #{userId} AND type = #{type} ORDER BY created_at DESC")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "targetId", column = "target_id"),
            @Result(property = "type", column = "type"),
            @Result(property = "title", column = "title"),
            @Result(property = "summary", column = "summary"),
            @Result(property = "createdAt", column = "created_at")
    })
    List<UserCollection> findByUserIdAndType(Long userId, String type);

    @Delete("DELETE FROM collections WHERE id = #{id}")
    int deleteById(Long id);

    @Delete("DELETE FROM collections WHERE user_id = #{userId} AND target_id = #{targetId} AND type = #{type}")
    int deleteByUserIdAndTargetIdAndType(Long userId, Long targetId, String type);
}