package com.antifraud.model;

import lombok.Data;
import java.util.Date;

/**
 * 轮播图实体类
 * 
 * 功能说明：
 * 1. 存储轮播图基本信息
 * 2. 支持多种跳转类型（知识、案例、帖子、网页）
 * 3. 支持排序和状态管理
 * 
 * 数据表：banners
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@Data
public class Banner {
    /**
     * 轮播图ID（主键，自增）
     */
    private Long id;
    
    /**
     * 轮播图标题
     */
    private String title;
    
    /**
     * 图片URL
     */
    private String imageUrl;
    
    /**
     * 跳转链接
     */
    private String linkUrl;
    
    /**
     * 链接类型
     * - none: 无链接
     * - knowledge: 知识
     * - case: 案例
     * - post: 帖子
     * - webview: 网页
     */
    private String linkType;
    
    /**
     * 关联内容ID（当linkType为knowledge/case/post时使用）
     */
    private Long linkId;
    
    /**
     * 排序顺序，数字越小越靠前
     */
    private Integer sortOrder;
    
    /**
     * 状态
     * - active: 启用
     * - inactive: 禁用
     */
    private String status;
    
    /**
     * 创建时间
     */
    private Date createdAt;
    
    /**
     * 更新时间
     */
    private Date updatedAt;
}
