package com.antifraud.service;

/**
 * 点赞服务接口
 * 
 * 功能说明：
 * 1. 提供点赞/取消点赞功能
 * 2. 支持检查用户是否已点赞
 * 3. 支持帖子、案例、知识等多种内容类型
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
public interface LikeService {
    
    /**
     * 切换点赞状态
     * 
     * 功能说明：
     * - 如果用户已点赞，则取消点赞
     * - 如果用户未点赞，则添加点赞
     * - 同时更新目标内容的点赞数
     * 
     * @param userId 用户ID
     * @param targetId 目标内容ID
     * @param targetType 目标类型（post/case/knowledge）
     * @return 点赞结果对象，包含当前点赞状态和点赞数
     */
    LikeResult toggleLike(Long userId, Long targetId, String targetType);
    
    /**
     * 检查用户是否已点赞
     * 
     * @param userId 用户ID
     * @param targetId 目标内容ID
     * @param targetType 目标类型
     * @return true-已点赞，false-未点赞
     */
    boolean checkLike(Long userId, Long targetId, String targetType);

    /**
     * 点赞结果内部类
     * 
     * 用于返回点赞操作的结果信息
     */
    class LikeResult {
        private boolean liked;
        private Integer likeCount;

        /**
         * 获取是否已点赞
         * 
         * @return true-已点赞，false-未点赞
         */
        public boolean isLiked() {
            return liked;
        }

        /**
         * 设置点赞状态
         * 
         * @param liked 点赞状态
         */
        public void setLiked(boolean liked) {
            this.liked = liked;
        }

        /**
         * 获取当前点赞数
         * 
         * @return 点赞数
         */
        public Integer getLikeCount() {
            return likeCount;
        }

        /**
         * 设置点赞数
         * 
         * @param likeCount 点赞数
         */
        public void setLikeCount(Integer likeCount) {
            this.likeCount = likeCount;
        }
    }
}
