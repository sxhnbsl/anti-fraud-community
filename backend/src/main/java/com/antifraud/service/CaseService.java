package com.antifraud.service;

import com.antifraud.model.Case;
import com.antifraud.dto.CaseDTO;
import java.util.List;

/**
 * 诈骗案例服务接口
 * 
 * 功能说明：
 * 1. 提供案例的增删改查功能
 * 2. 支持按分类查询
 * 3. 支持浏览量统计
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
public interface CaseService {
    
    /**
     * 创建案例
     * 
     * @param caseDTO 案例数据传输对象
     * @return 创建的案例对象
     */
    Case create(CaseDTO caseDTO);
    
    /**
     * 更新案例
     * 
     * @param c 案例对象
     */
    void update(Case c);
    
    /**
     * 根据ID查询案例
     * 
     * @param id 案例ID
     * @return 案例对象，不存在返回null
     */
    Case findById(Long id);
    
    /**
     * 查询所有案例
     * 
     * @return 案例列表
     */
    List<Case> findAll();
    
    /**
     * 根据分类查询案例
     * 
     * @param category 分类名称
     * @return 指定分类的案例列表
     */
    List<Case> findByCategory(String category);
    
    /**
     * 增加案例浏览量
     * 
     * @param id 案例ID
     */
    void incrementViewCount(Long id);
    
    /**
     * 删除案例
     * 
     * @param id 案例ID
     */
    void delete(Long id);

    /**
     * 搜索案例（支持标题、内容、诈骗类型）
     * 
     * @param keyword 搜索关键词
     * @return 案例列表
     */
    List<Case> search(String keyword);

    /**
     * 分页搜索案例（支持标题、内容、诈骗类型）
     * 
     * @param keyword 搜索关键词
     * @param page 页码（从1开始）
     * @param size 每页数量
     * @return 案例列表
     */
    List<Case> searchWithPagination(String keyword, int page, int size);

    /**
     * 统计搜索结果数量
     * 
     * @param keyword 搜索关键词
     * @return 结果数量
     */
    int countSearchResults(String keyword);
}
