package com.antifraud.service.impl;

import com.antifraud.model.Case;
import com.antifraud.dto.CaseDTO;
import com.antifraud.service.CaseService;
import com.antifraud.repository.CaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 诈骗案例服务实现类
 * 
 * 功能说明：
 * 1. 实现案例的增删改查功能
 * 2. 支持按分类查询
 * 3. 支持浏览量统计
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@Service
public class CaseServiceImpl implements CaseService {

    @Autowired
    private CaseRepository caseRepository;

    /**
     * 创建案例
     * 
     * 功能说明：
     * - 创建案例对象
     * - 设置标题、内容、分类、诈骗类型等信息
     * - 设置受害者信息和警示提示
     * - 保存到数据库
     * 
     * @param caseDTO 案例数据传输对象
     * @return 创建的案例对象
     */
    @Override
    public Case create(CaseDTO caseDTO) {
        Case caseItem = new Case();
        caseItem.setTitle(caseDTO.getTitle());
        caseItem.setContent(caseDTO.getContent());
        caseItem.setCategory(caseDTO.getCategory());
        caseItem.setFraudAnalysis(caseDTO.getFraudAnalysis());
        caseItem.setPreventionAdvice(caseDTO.getPreventionAdvice());
        caseItem.setCoverImage(caseDTO.getCoverImage());
        caseItem.setImages(caseDTO.getImages());
        caseItem.setVideoUrl(caseDTO.getVideoUrl());
        caseItem.setVideoCover(caseDTO.getVideoCover());
        caseItem.setAuthorId(caseDTO.getAuthorId());

        caseRepository.insert(caseItem);
        return caseItem;
    }

    /**
     * 更新案例
     * 
     * 功能说明：
     * - 更新案例对象
     * - 更新标题、内容、分类、诈骗类型等信息
     * - 保存到数据库
     * 
     * @param c 案例对象
     */
    @Override
    public void update(Case c) {
        caseRepository.update(c);
    }

    /**
     * 根据ID查询案例
     * 
     * @param id 案例ID
     * @return 案例对象，不存在返回null
     */
    @Override
    public Case findById(Long id) {
        return caseRepository.findById(id);
    }

    /**
     * 查询所有案例
     * 
     * @return 案例列表
     */
    @Override
    public List<Case> findAll() {
        return caseRepository.findAll();
    }

    /**
     * 根据分类查询案例
     * 
     * @param category 分类名称
     * @return 指定分类的案例列表
     */
    @Override
    public List<Case> findByCategory(String category) {
        return caseRepository.findByCategory(category);
    }

    /**
     * 增加案例浏览量
     * 
     * @param id 案例ID
     */
    @Override
    public void incrementViewCount(Long id) {
        caseRepository.incrementViewCount(id);
    }

    /**
     * 删除案例
     * 
     * @param id 案例ID
     */
    @Override
    public void delete(Long id) {
        caseRepository.deleteById(id);
    }

    /**
     * 搜索案例（支持标题、内容、诈骗类型）
     * 
     * 功能说明：
     * - 在标题、内容、诈骗类型中搜索关键词
     * - 返回匹配的案例列表
     * 
     * @param keyword 搜索关键词
     * @return 案例列表
     */
    @Override
    public List<Case> search(String keyword) {
        return caseRepository.search(keyword);
    }

    /**
     * 分页搜索案例（支持标题、内容、诈骗类型）
     * 
     * 功能说明：
     * - 在标题、内容、诈骗类型中搜索关键词
     * - 支持分页查询
     * - 返回匹配的案例列表
     * 
     * @param keyword 搜索关键词
     * @param page 页码（从1开始）
     * @param size 每页数量
     * @return 案例列表
     */
    @Override
    public List<Case> searchWithPagination(String keyword, int page, int size) {
        int offset = (page - 1) * size;
        return caseRepository.searchWithPagination(keyword, offset, size);
    }

    /**
     * 统计搜索结果数量
     * 
     * 功能说明：
     * - 统计匹配关键词的案例数量
     * - 用于分页计算
     * 
     * @param keyword 搜索关键词
     * @return 结果数量
     */
    @Override
    public int countSearchResults(String keyword) {
        return caseRepository.countSearchResults(keyword);
    }
}
