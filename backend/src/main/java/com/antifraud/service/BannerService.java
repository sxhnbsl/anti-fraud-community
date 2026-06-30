package com.antifraud.service;

import com.antifraud.model.Banner;
import java.util.List;

/**
 * 轮播图服务接口
 * 
 * 功能说明：
 * 1. 提供轮播图的增删改查功能
 * 2. 支持按状态查询轮播图
 * 3. 支持排序管理
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
public interface BannerService {
    
    /**
     * 创建轮播图
     * 
     * @param banner 轮播图对象
     * @return 创建的轮播图对象
     */
    Banner create(Banner banner);
    
    /**
     * 查询所有启用的轮播图
     * 
     * @return 启用的轮播图列表
     */
    List<Banner> findActive();
    
    /**
     * 查询所有轮播图（包括禁用的）
     * 
     * @return 所有轮播图列表
     */
    List<Banner> findAll();
    
    /**
     * 根据ID查询轮播图
     * 
     * @param id 轮播图ID
     * @return 轮播图对象，不存在返回null
     */
    Banner findById(Long id);
    
    /**
     * 更新轮播图
     * 
     * @param banner 轮播图对象
     */
    void update(Banner banner);
    
    /**
     * 更新轮播图状态
     * 
     * @param id 轮播图ID
     * @param status 状态
     */
    void updateStatus(Long id, String status);
    
    /**
     * 删除轮播图
     * 
     * @param id 轮播图ID
     */
    void delete(Long id);
}
