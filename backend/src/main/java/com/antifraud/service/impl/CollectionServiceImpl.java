package com.antifraud.service.impl;

import com.antifraud.model.UserCollection;
import com.antifraud.service.CollectionService;
import com.antifraud.repository.CollectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 收藏服务实现类
 * 
 * 功能说明：
 * 1. 实现收藏/取消收藏功能
 * 2. 支持检查用户是否已收藏
 * 3. 支持按用户查询收藏列表
 * 4. 支持帖子、案例、知识等多种内容类型
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@Service
public class CollectionServiceImpl implements CollectionService {

    @Autowired
    private CollectionRepository collectionRepository;

    /**
     * 切换收藏状态
     * 
     * 功能说明：
     * - 查询用户是否已收藏该内容
     * - 如果已收藏则删除收藏记录
     * - 如果未收藏则创建收藏记录
     * - 返回最新的收藏状态
     * 
     * @param userId 用户ID
     * @param targetId 目标内容ID
     * @param targetType 目标类型
     * @return true-已收藏，false-未收藏
     */
    @Override
    public boolean toggleCollection(Long userId, Long targetId, String targetType, String title, String summary) {
        UserCollection existingCollection = collectionRepository.findByUserIdAndTargetIdAndType(
                userId, targetId, targetType
        );

        if (existingCollection != null) {
            collectionRepository.deleteByUserIdAndTargetIdAndType(userId, targetId, targetType);
            return false;
        } else {
            UserCollection collection = new UserCollection();
            collection.setUserId(userId);
            collection.setTargetId(targetId);
            collection.setType(targetType);
            collection.setTitle(title);
            collection.setSummary(summary);
            collection.setCreatedAt(LocalDateTime.now());
            collectionRepository.insert(collection);
            return true;
        }
    }

    /**
     * 检查用户是否已收藏
     * 
     * @param userId 用户ID
     * @param targetId 目标内容ID
     * @param targetType 目标类型
     * @return true-已收藏，false-未收藏
     */
    @Override
    public boolean checkCollection(Long userId, Long targetId, String targetType) {
        UserCollection collection = collectionRepository.findByUserIdAndTargetIdAndType(
                userId, targetId, targetType
        );
        return collection != null;
    }

    /**
     * 根据用户ID查询收藏列表
     * 
     * @param userId 用户ID
     * @return 该用户的收藏列表
     */
    @Override
    public List<UserCollection> findByUserId(Long userId) {
        return collectionRepository.findByUserId(userId);
    }

    /**
     * 根据用户ID和类型查询收藏列表
     * 
     * @param userId 用户ID
     * @param targetType 目标类型
     * @return 指定类型的收藏列表
     */
    @Override
    public List<UserCollection> findByUserIdAndType(Long userId, String targetType) {
        return collectionRepository.findByUserIdAndType(userId, targetType);
    }

    /**
     * 删除收藏
     * 
     * @param id 收藏ID
     */
    @Override
    public void delete(Long id) {
        collectionRepository.deleteById(id);
    }
}
