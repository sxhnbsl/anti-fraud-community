package com.antifraud.repository;

import com.antifraud.model.Banner;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 轮播图数据访问层接口
 * 
 * 功能说明：
 * 1. 提供轮播图数据的增删改查操作
 * 2. 支持按状态查询轮播图
 * 3. 支持按排序顺序查询
 * 
 * 数据表：banners
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@Mapper
public interface BannerRepository {

    /**
     * 插入新轮播图
     * 
     * @param banner 轮播图对象
     * @return 影响的行数
     */
    @Insert("INSERT INTO banners(title, image_url, link_url, link_type, link_id, sort_order, status, created_at, updated_at) " +
            "VALUES(#{title}, #{imageUrl}, #{linkUrl}, #{linkType}, #{linkId}, #{sortOrder}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Banner banner);

    /**
     * 查询所有启用的轮播图（按排序顺序）
     * 
     * @return 轮播图列表
     */
    @Select("SELECT * FROM banners WHERE status = 'active' ORDER BY sort_order ASC, created_at DESC")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "title", column = "title"),
            @Result(property = "imageUrl", column = "image_url"),
            @Result(property = "linkUrl", column = "link_url"),
            @Result(property = "linkType", column = "link_type"),
            @Result(property = "linkId", column = "link_id"),
            @Result(property = "sortOrder", column = "sort_order"),
            @Result(property = "status", column = "status"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    List<Banner> findActive();

    /**
     * 查询所有轮播图（包括禁用的）
     * 
     * @return 轮播图列表
     */
    @Select("SELECT * FROM banners ORDER BY sort_order ASC, created_at DESC")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "title", column = "title"),
            @Result(property = "imageUrl", column = "image_url"),
            @Result(property = "linkUrl", column = "link_url"),
            @Result(property = "linkType", column = "link_type"),
            @Result(property = "linkId", column = "link_id"),
            @Result(property = "sortOrder", column = "sort_order"),
            @Result(property = "status", column = "status"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    List<Banner> findAll();

    /**
     * 根据ID查询轮播图
     * 
     * @param id 轮播图ID
     * @return 轮播图对象，如果不存在返回null
     */
    @Select("SELECT * FROM banners WHERE id = #{id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "title", column = "title"),
            @Result(property = "imageUrl", column = "image_url"),
            @Result(property = "linkUrl", column = "link_url"),
            @Result(property = "linkType", column = "link_type"),
            @Result(property = "linkId", column = "link_id"),
            @Result(property = "sortOrder", column = "sort_order"),
            @Result(property = "status", column = "status"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    Banner findById(Long id);

    /**
     * 更新轮播图
     * 
     * @param banner 轮播图对象
     * @return 影响的行数
     */
    @Update("UPDATE banners SET title = #{title}, image_url = #{imageUrl}, link_url = #{linkUrl}, " +
            "link_type = #{linkType}, link_id = #{linkId}, sort_order = #{sortOrder}, " +
            "status = #{status}, updated_at = NOW() WHERE id = #{id}")
    int update(Banner banner);

    /**
     * 更新轮播图状态
     * 
     * @param id 轮播图ID
     * @param status 状态
     * @return 影响的行数
     */
    @Update("UPDATE banners SET status = #{status}, updated_at = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    /**
     * 根据ID删除轮播图
     * 
     * @param id 轮播图ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM banners WHERE id = #{id}")
    int deleteById(Long id);
}
