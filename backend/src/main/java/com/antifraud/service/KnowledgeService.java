package com.antifraud.service;

import com.antifraud.model.Knowledge;
import com.antifraud.dto.KnowledgeDTO;
import java.util.List;

/**
 * 防诈骗知识服务接口
 * 
 * 功能说明：
 * 1. 提供知识的增删改查功能
 * 2. 支持按分类、标签查询
 * 3. 支持浏览量统计
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
public interface KnowledgeService {
    
    /**
     * 创建知识
     * 
     * @param knowledgeDTO 知识数据传输对象
     * @return 创建的知识对象
     */
    Knowledge create(KnowledgeDTO knowledgeDTO);
    
    /**
     * 更新知识
     * 
     * @param knowledge 知识对象
     */
    void update(Knowledge knowledge);
    
    /**
     * 根据ID查询知识
     * 
     * @param id 知识ID
     * @return 知识对象，不存在返回null
     */
    Knowledge findById(Long id);
    
    /**
     * 查询所有知识
     * 
     * @return 知识列表
     */
    List<Knowledge> findAll();
    
    /**
     * 根据分类查询知识
     * 
     * @param category 分类名称
     * @return 指定分类的知识列表
     */
    List<Knowledge> findByCategory(String category);
    
    /**
     * 根据标签查询知识
     * 
     * @param tag 标签名称
     * @return 包含指定标签的知识列表
     */
    List<Knowledge> findByTag(String tag);
    
    /**
     * 增加知识浏览量
     * 
     * @param id 知识ID
     */
    void incrementViewCount(Long id);
    
    /**
     * 删除知识
     * 
     * @param id 知识ID
     */
    void delete(Long id);

    /**
     * 搜索知识（支持标题、内容、标签）
     * 
     * @param keyword 搜索关键词
     * @return 知识列表
     */
    List<Knowledge> search(String keyword);

    /**
     * 分页搜索知识（支持标题、内容、标签）
     * 
     * @param keyword 搜索关键词
     * @param page 页码（从1开始）
     * @param size 每页数量
     * @return 知识列表
     */
    List<Knowledge> searchWithPagination(String keyword, int page, int size);

    /**
     * 统计搜索结果数量
     * 
     * @param keyword 搜索关键词
     * @return 结果数量
     */
    int countSearchResults(String keyword);
}
