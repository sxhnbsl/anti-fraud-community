package com.antifraud.service.impl;

import com.antifraud.model.Banner;
import com.antifraud.service.BannerService;
import com.antifraud.repository.BannerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 轮播图服务实现类
 * 
 * 功能说明：
 * 1. 实现轮播图的增删改查功能
 * 2. 支持按状态查询轮播图
 * 3. 支持排序管理
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@Service
public class BannerServiceImpl implements BannerService {

    @Autowired
    private BannerRepository bannerRepository;

    /**
     * 创建轮播图
     * 
     * 功能说明：
     * - 创建轮播图对象
     * - 设置标题、图片URL、跳转链接等信息
     * - 保存到数据库
     * 
     * @param banner 轮播图对象
     * @return 创建的轮播图对象
     */
    @Override
    public Banner create(Banner banner) {
        bannerRepository.insert(banner);
        return banner;
    }

    /**
     * 查询所有启用的轮播图
     * 
     * @return 启用的轮播图列表
     */
    @Override
    public List<Banner> findActive() {
        return bannerRepository.findActive();
    }

    /**
     * 查询所有轮播图（包括禁用的）
     * 
     * @return 所有轮播图列表
     */
    @Override
    public List<Banner> findAll() {
        return bannerRepository.findAll();
    }

    /**
     * 根据ID查询轮播图
     * 
     * @param id 轮播图ID
     * @return 轮播图对象，不存在返回null
     */
    @Override
    public Banner findById(Long id) {
        return bannerRepository.findById(id);
    }

    /**
     * 更新轮播图
     * 
     * @param banner 轮播图对象
     */
    @Override
    public void update(Banner banner) {
        bannerRepository.update(banner);
    }

    /**
     * 更新轮播图状态
     * 
     * @param id 轮播图ID
     * @param status 状态
     */
    @Override
    public void updateStatus(Long id, String status) {
        bannerRepository.updateStatus(id, status);
    }

    /**
     * 删除轮播图
     * 
     * @param id 轮播图ID
     */
    @Override
    public void delete(Long id) {
        bannerRepository.deleteById(id);
    }
}
