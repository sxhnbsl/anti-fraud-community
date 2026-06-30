package com.antifraud.service.impl;

import com.antifraud.model.Knowledge;
import com.antifraud.dto.KnowledgeDTO;
import com.antifraud.service.KnowledgeService;
import com.antifraud.repository.KnowledgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 防诈骗知识服务实现类
 * 
 * 功能说明：
 * 1. 实现知识的增删改查功能
 * 2. 支持按分类、标签查询
 * 3. 支持浏览量统计
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@Service
public class KnowledgeServiceImpl implements KnowledgeService {

    @Autowired
    private KnowledgeRepository knowledgeRepository;

    /**
     * 创建知识
     * 
     * 功能说明：
     * - 创建知识对象
     * - 设置标题、内容、分类、标签等信息
     * - 保存到数据库
     * 
     * @param knowledgeDTO 知识数据传输对象
     * @return 创建的知识对象
     */
    @Override
    public Knowledge create(KnowledgeDTO knowledgeDTO) {
        Knowledge knowledge = new Knowledge();
        knowledge.setTitle(knowledgeDTO.getTitle());
        knowledge.setContent(knowledgeDTO.getContent());
        knowledge.setCategory(knowledgeDTO.getCategory());
        knowledge.setTags(knowledgeDTO.getTags());
        knowledge.setAuthorId(knowledgeDTO.getAuthorId());
        knowledge.setCoverImage(knowledgeDTO.getCoverImage());
        knowledge.setImages(knowledgeDTO.getImages());
        knowledge.setVideoUrl(knowledgeDTO.getVideoUrl());
        knowledge.setVideoCover(knowledgeDTO.getVideoCover());

        knowledgeRepository.insert(knowledge);
        return knowledge;
    }

    /**
     * 更新知识
     * 
     * 功能说明：
     * - 更新知识对象
     * - 更新标题、内容、分类、标签等信息
     * - 保存到数据库
     * 
     * @param knowledge 知识对象
     */
    @Override
    public void update(Knowledge knowledge) {
        knowledgeRepository.update(knowledge);
    }

    /**
     * 根据ID查询知识
     * 
     * @param id 知识ID
     * @return 知识对象，不存在返回null
     */
    @Override
    public Knowledge findById(Long id) {
        return knowledgeRepository.findById(id);
    }

    /**
     * 查询所有知识
     * 
     * @return 知识列表
     */
    @Override
    public List<Knowledge> findAll() {
        return knowledgeRepository.findAll();
    }

    /**
     * 根据分类查询知识
     * 
     * @param category 分类名称
     * @return 指定分类的知识列表
     */
    @Override
    public List<Knowledge> findByCategory(String category) {
        return knowledgeRepository.findByCategory(category);
    }

    /**
     * 根据标签查询知识
     * 
     * @param tag 标签名称
     * @return 包含指定标签的知识列表
     */
    @Override
    public List<Knowledge> findByTag(String tag) {
        return knowledgeRepository.findByTag(tag);
    }

    /**
     * 增加知识浏览量
     * 
     * @param id 知识ID
     */
    @Override
    public void incrementViewCount(Long id) {
        knowledgeRepository.incrementViewCount(id);
    }

    /**
     * 删除知识
     * 
     * @param id 知识ID
     */
    @Override
    public void delete(Long id) {
        knowledgeRepository.deleteById(id);
    }

    /**
     * 搜索知识（支持标题、内容、标签）
     * 
     * 功能说明：
     * - 在标题、内容、标签中搜索关键词
     * - 返回匹配的知识列表
     * 
     * @param keyword 搜索关键词
     * @return 知识列表
     */
    @Override
    public List<Knowledge> search(String keyword) {
        return knowledgeRepository.search(keyword);
    }

    /**
     * 分页搜索知识（支持标题、内容、标签）
     * 
     * 功能说明：
     * - 在标题、内容、标签中搜索关键词
     * - 支持分页查询
     * - 返回匹配的知识列表
     * 
     * @param keyword 搜索关键词
     * @param page 页码（从1开始）
     * @param size 每页数量
     * @return 知识列表
     */
    @Override
    public List<Knowledge> searchWithPagination(String keyword, int page, int size) {
        int offset = (page - 1) * size;
        return knowledgeRepository.searchWithPagination(keyword, offset, size);
    }

    /**
     * 统计搜索结果数量
     * 
     * 功能说明：
     * - 统计匹配关键词的知识数量
     * - 用于分页计算
     * 
     * @param keyword 搜索关键词
     * @return 结果数量
     */
    @Override
    public int countSearchResults(String keyword) {
        return knowledgeRepository.countSearchResults(keyword);
    }
}
