package com.antifraud.service;

import com.antifraud.model.UserCollection;
import java.util.List;

/**
 * 收藏服务接口
 * 
 * 功能说明：
 * 1. 提供收藏/取消收藏功能
 * 2. 支持检查用户是否已收藏
 * 3. 支持按用户查询收藏列表
 * 4. 支持帖子、案例、知识等多种内容类型
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
public interface CollectionService {
    
    /**
     * 切换收藏状态
     * 
     * 功能说明：
     * - 如果用户已收藏，则取消收藏
     * - 如果用户未收藏，则添加收藏
     * 
     * @param userId 用户ID
     * @param targetId 目标内容ID
     * @param targetType 目标类型（post/case/knowledge）
     * @param title 标题
     * @param summary 摘要
     * @return true-已收藏，false-未收藏
     */
    boolean toggleCollection(Long userId, Long targetId, String targetType, String title, String summary);
    
    /**
     * 检查用户是否已收藏
     * 
     * @param userId 用户ID
     * @param targetId 目标内容ID
     * @param targetType 目标类型
     * @return true-已收藏，false-未收藏
     */
    boolean checkCollection(Long userId, Long targetId, String targetType);
    
    /**
     * 根据用户ID查询收藏列表
     * 
     * @param userId 用户ID
     * @return 该用户的收藏列表
     */
    List<UserCollection> findByUserId(Long userId);
    
    /**
     * 根据用户ID和类型查询收藏列表
     * 
     * @param userId 用户ID
     * @param targetType 目标类型
     * @return 指定类型的收藏列表
     */
    List<UserCollection> findByUserIdAndType(Long userId, String targetType);
    
    /**
     * 删除收藏
     * 
     * @param id 收藏ID
     */
    void delete(Long id);
}
