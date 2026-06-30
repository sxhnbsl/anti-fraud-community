package com.antifraud.service.impl;

import com.antifraud.model.LearningRecord;
import com.antifraud.service.LearningRecordService;
import com.antifraud.repository.LearningRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 浏览记录服务实现类
 * 
 * 功能说明：
 * 1. 实现浏览记录的增删改查功能
 * 2. 支持按用户ID查询浏览记录
 * 3. 支持按内容类型查询浏览记录
 * 4. 支持统计浏览次数
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@Service
public class LearningRecordServiceImpl implements LearningRecordService {

    @Autowired
    private LearningRecordRepository learningRecordRepository;

    /**
     * 创建浏览记录
     * 
     * 功能说明：
     * - 创建浏览记录对象
     * - 设置用户ID、内容类型、内容ID等信息
     * - 保存到数据库
     * 
     * @param record 浏览记录对象
     * @return 创建的浏览记录对象
     */
    @Override
    public LearningRecord create(LearningRecord record) {
        learningRecordRepository.insert(record);
        return record;
    }

    /**
     * 查询用户的所有浏览记录
     * 
     * @param userId 用户ID
     * @return 浏览记录列表
     */
    @Override
    public List<LearningRecord> findByUserId(Long userId) {
        return learningRecordRepository.findByUserId(userId);
    }

    /**
     * 分页查询用户的浏览记录
     * 
     * @param userId 用户ID
     * @param page 页码（从1开始）
     * @param size 每页数量
     * @return 浏览记录列表
     */
    @Override
    public List<LearningRecord> findByUserIdWithPagination(Long userId, int page, int size) {
        int offset = (page - 1) * size;
        return learningRecordRepository.findByUserIdWithPagination(userId, offset, size);
    }

    /**
     * 按内容类型查询用户的浏览记录
     * 
     * @param userId 用户ID
     * @param contentType 内容类型
     * @return 浏览记录列表
     */
    @Override
    public List<LearningRecord> findByUserIdAndContentType(Long userId, String contentType) {
        return learningRecordRepository.findByUserIdAndContentType(userId, contentType);
    }

    /**
     * 查询用户最近浏览的记录
     * 
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 浏览记录列表
     */
    @Override
    public List<LearningRecord> findRecentByUserId(Long userId, int limit) {
        return learningRecordRepository.findRecentByUserId(userId, limit);
    }

    /**
     * 统计用户的浏览次数
     * 
     * @param userId 用户ID
     * @return 浏览次数
     */
    @Override
    public int countByUserId(Long userId) {
        return learningRecordRepository.countByUserId(userId);
    }

    /**
     * 统计用户对指定内容的浏览次数
     * 
     * @param userId 用户ID
     * @param contentType 内容类型
     * @param contentId 内容ID
     * @return 浏览次数
     */
    @Override
    public int countByUserAndContent(Long userId, String contentType, Long contentId) {
        return learningRecordRepository.countByUserAndContent(userId, contentType, contentId);
    }

    /**
     * 删除浏览记录
     * 
     * @param id 浏览记录ID
     */
    @Override
    public void delete(Long id) {
        learningRecordRepository.deleteById(id);
    }

    /**
     * 删除用户的所有浏览记录
     * 
     * @param userId 用户ID
     */
    @Override
    public void deleteByUserId(Long userId) {
        learningRecordRepository.deleteByUserId(userId);
    }
}
