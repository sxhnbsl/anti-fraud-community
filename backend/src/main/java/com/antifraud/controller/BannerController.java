package com.antifraud.controller;

import com.antifraud.model.Banner;
import com.antifraud.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 轮播图控制器
 * 
 * 功能说明：
 * 1. 提供轮播图的增删改查接口
 * 2. 支持按状态查询轮播图
 * 3. 支持排序管理
 * 
 * 接口列表：
 * - GET /api/banners/active - 获取所有启用的轮播图
 * - GET /api/banners - 获取所有轮播图
 * - GET /api/banners/{id} - 根据ID获取轮播图
 * - POST /api/banners - 创建轮播图
 * - PUT /api/banners/{id} - 更新轮播图
 * - PUT /api/banners/{id}/status - 更新轮播图状态
 * - DELETE /api/banners/{id} - 删除轮播图
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@RestController
@RequestMapping("/api/banners")
public class BannerController {

    @Autowired
    private BannerService bannerService;

    /**
     * 获取所有启用的轮播图
     * 
     * 功能说明：
     * - 返回所有状态为active的轮播图
     * - 按排序顺序和创建时间排序
     * 
     * @return 轮播图列表
     */
    @GetMapping("/active")
    public Map<String, Object> getActiveBanners() {
        List<Banner> banners = bannerService.findActive();
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "获取成功");
        response.put("data", banners);
        return response;
    }

    /**
     * 获取所有轮播图（包括禁用的）
     * 
     * 功能说明：
     * - 返回所有轮播图，包括禁用的
     * - 按排序顺序和创建时间排序
     * 
     * @return 轮播图列表
     */
    @GetMapping
    public Map<String, Object> getAllBanners() {
        List<Banner> banners = bannerService.findAll();
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "获取成功");
        response.put("data", banners);
        return response;
    }

    /**
     * 根据ID获取轮播图
     * 
     * 功能说明：
     * - 根据轮播图ID查询轮播图详情
     * - 如果轮播图不存在，返回404错误
     * 
     * @param id 轮播图ID
     * @return 轮播图详情
     */
    @GetMapping("/{id}")
    public Map<String, Object> getBannerById(@PathVariable Long id) {
        Banner banner = bannerService.findById(id);
        Map<String, Object> response = new HashMap<>();
        
        if (banner == null) {
            response.put("code", 404);
            response.put("message", "轮播图不存在");
            response.put("data", null);
        } else {
            response.put("code", 200);
            response.put("message", "获取成功");
            response.put("data", banner);
        }
        
        return response;
    }

    /**
     * 创建轮播图
     * 
     * 功能说明：
     * - 创建新的轮播图
     * - 需要提供标题、图片URL等信息
     * - 返回创建的轮播图对象
     * 
     * @param banner 轮播图对象
     * @return 创建的轮播图
     */
    @PostMapping
    public Map<String, Object> createBanner(@RequestBody Banner banner) {
        Banner createdBanner = bannerService.create(banner);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "创建成功");
        response.put("data", createdBanner);
        return response;
    }

    /**
     * 更新轮播图
     * 
     * 功能说明：
     * - 根据轮播图ID更新轮播图信息
     * - 支持更新标题、图片URL、跳转链接等
     * - 如果轮播图不存在，返回404错误
     * 
     * @param id 轮播图ID
     * @param banner 轮播图对象
     * @return 更新后的轮播图
     */
    @PutMapping("/{id}")
    public Map<String, Object> updateBanner(@PathVariable Long id, @RequestBody Banner banner) {
        Banner existingBanner = bannerService.findById(id);
        Map<String, Object> response = new HashMap<>();
        
        if (existingBanner == null) {
            response.put("code", 404);
            response.put("message", "轮播图不存在");
            response.put("data", null);
        } else {
            banner.setId(id);
            bannerService.update(banner);
            response.put("code", 200);
            response.put("message", "更新成功");
            response.put("data", banner);
        }
        
        return response;
    }

    /**
     * 更新轮播图状态
     * 
     * 功能说明：
     * - 根据轮播图ID更新轮播图状态
     * - 状态可以是active或inactive
     * - 如果轮播图不存在，返回404错误
     * 
     * @param id 轮播图ID
     * @param status 状态
     * @return 操作结果
     */
    @PutMapping("/{id}/status")
    public Map<String, Object> updateBannerStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Banner existingBanner = bannerService.findById(id);
        Map<String, Object> response = new HashMap<>();
        
        if (existingBanner == null) {
            response.put("code", 404);
            response.put("message", "轮播图不存在");
            response.put("data", null);
        } else {
            String status = body.get("status");
            bannerService.updateStatus(id, status);
            response.put("code", 200);
            response.put("message", "状态更新成功");
            response.put("data", null);
        }
        
        return response;
    }

    /**
     * 删除轮播图
     * 
     * 功能说明：
     * - 根据轮播图ID删除轮播图
     * - 删除操作不可恢复
     * - 如果轮播图不存在，返回404错误
     * 
     * @param id 轮播图ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteBanner(@PathVariable Long id) {
        Banner existingBanner = bannerService.findById(id);
        Map<String, Object> response = new HashMap<>();
        
        if (existingBanner == null) {
            response.put("code", 404);
            response.put("message", "轮播图不存在");
            response.put("data", null);
        } else {
            bannerService.delete(id);
            response.put("code", 200);
            response.put("message", "删除成功");
            response.put("data", null);
        }
        
        return response;
    }
}
