package com.antifraud.service.impl;

import com.antifraud.model.Like;
import com.antifraud.service.LikeService;
import com.antifraud.repository.LikeRepository;
import com.antifraud.repository.PostRepository;
import com.antifraud.repository.CaseRepository;
import com.antifraud.repository.KnowledgeRepository;
import com.antifraud.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

/**
 * 点赞服务实现类
 * 
 * 功能说明：
 * 1. 实现点赞/取消点赞功能
 * 2. 支持帖子、案例、知识等多种内容类型
 * 3. 自动更新目标内容的点赞数
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@Service
public class LikeServiceImpl implements LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CaseRepository caseRepository;

    @Autowired
    private KnowledgeRepository knowledgeRepository;

    @Autowired
    private CommentRepository commentRepository;

    /**
     * 切换点赞状态
     * 
     * 功能说明：
     * - 查询用户是否已点赞该内容
     * - 如果已点赞则删除点赞记录，减少点赞数
     * - 如果未点赞则创建点赞记录，增加点赞数
     * - 返回最新的点赞状态和点赞数
     * 
     * @param userId 用户ID
     * @param targetId 目标内容ID
     * @param targetType 目标类型
     * @return 点赞结果对象
     */
    @Override
    public LikeService.LikeResult toggleLike(Long userId, Long targetId, String targetType) {
        Like existingLike = likeRepository.findByUserIdAndTargetIdAndTargetType(
                userId, targetId, targetType
        );

        boolean isLiked;
        if (existingLike != null) {
            likeRepository.deleteByUserIdAndTargetIdAndTargetType(userId, targetId, targetType);
            isLiked = false;
            updateLikeCount(targetId, targetType, -1);
        } else {
            Like like = new Like();
            like.setUserId(userId);
            like.setTargetId(targetId);
            like.setTargetType(targetType);
            like.setCreatedAt(LocalDateTime.now());
            likeRepository.insert(like);
            isLiked = true;
            updateLikeCount(targetId, targetType, 1);
        }

        LikeService.LikeResult result = new LikeService.LikeResult();
        result.setLiked(isLiked);
        result.setLikeCount(getCurrentLikeCount(targetId, targetType));
        return result;
    }

    /**
     * 检查用户是否已点赞
     * 
     * @param userId 用户ID
     * @param targetId 目标内容ID
     * @param targetType 目标类型
     * @return true-已点赞，false-未点赞
     */
    @Override
    public boolean checkLike(Long userId, Long targetId, String targetType) {
        Like like = likeRepository.findByUserIdAndTargetIdAndTargetType(userId, targetId, targetType);
        return like != null;
    }

    /**
     * 更新目标内容的点赞数
     * 
     * 功能说明：
     * - 根据内容类型调用对应的Repository
     * - 更新点赞数（增加或减少）
     * 
     * @param targetId 目标内容ID
     * @param targetType 目标类型
     * @param delta 变化量（+1或-1）
     */
    private void updateLikeCount(Long targetId, String targetType, int delta) {
        switch (targetType) {
            case "post":
                postRepository.updateLikeCount(targetId, delta);
                break;
            case "case":
                caseRepository.updateLikeCount(targetId, delta);
                break;
            case "knowledge":
                knowledgeRepository.updateLikeCount(targetId, delta);
                break;
            case "comment":
                commentRepository.updateLikeCount(targetId, delta);
                break;
        }
    }

    /**
     * 获取目标内容当前的点赞数
     * 
     * @param targetId 目标内容ID
     * @param targetType 目标类型
     * @return 当前点赞数
     */
    private Integer getCurrentLikeCount(Long targetId, String targetType) {
        switch (targetType) {
            case "post":
                return postRepository.getLikeCount(targetId);
            case "case":
                return caseRepository.getLikeCount(targetId);
            case "knowledge":
                return knowledgeRepository.getLikeCount(targetId);
            case "comment":
                return commentRepository.getLikeCount(targetId);
            default:
                return 0;
        }
    }
}
