<template>
	<view class="my-posts-container">
		<view class="header">
			<text class="title">我的帖子</text>
		</view>
		
		<view v-if="loading" class="loading-state">
			<text class="loading-text">加载中...</text>
		</view>
		
		<view v-else-if="myPosts.length > 0" class="post-list">
			<view class="post-item card" v-for="(item, index) in myPosts" :key="item.id" @click="viewPostDetail(item.id)">
				<view class="post-header">
					<text class="post-title">{{item.title}}</text>
					<view class="post-actions">
						<view class="action-btn edit-btn" @click.stop="editPost(item.id)">
							<text class="action-text">编辑</text>
						</view>
						<view class="action-btn delete-btn" @click.stop="deletePost(item.id, index)">
							<text class="action-text">删除</text>
						</view>
					</view>
				</view>
				<text class="post-content">{{item.content}}</text>
				
				<view class="post-meta">
					<text class="post-time">{{formatDate(item.createdAt)}}</text>
					<text class="post-category" v-if="item.category">{{item.category}}</text>
				</view>
				
				<view class="post-stats">
					<view class="stat-item">
						<text class="stat-icon">👍</text>
						<text class="stat-text">{{item.likeCount || 0}}</text>
					</view>
					<view class="stat-item">
						<text class="stat-icon">💬</text>
						<text class="stat-text">{{item.commentCount || 0}}</text>
					</view>
				</view>
			</view>
		</view>
		
		<view v-if="!loading && myPosts.length === 0" class="empty-state card">
			<text class="empty-icon">📝</text>
			<text class="empty-text">还没有发布任何帖子</text>
			<button class="create-btn" @click="createPost">
				<text class="create-text">发布帖子</text>
			</button>
		</view>
	</view>
</template>

<script>
	// 导入HTTP请求工具
	import { request } from '@/utils/request.js'
	// 导入API接口配置
	import { API } from '@/utils/api.js'

	/**
	 * 我的帖子页面组件
	 * 
	 * 功能说明：
	 * 1. 展示当前用户发布的所有帖子
	 * 2. 支持查看帖子详情
	 * 3. 支持编辑和删除帖子
	 * 4. 显示帖子统计信息（点赞数、评论数）
	 * 5. 空状态提示，引导用户发布帖子
	 * 
	 * @author 反诈骗平台开发团队
	 * @version 1.0
	 */
	export default {
		/**
		 * 组件数据
		 * 
		 * myPosts: 我的帖子列表数据
		 * - id: 帖子ID
		 * - title: 帖子标题
		 * - content: 帖子内容
		 * - category: 帖子分类
		 * - createdAt: 创建时间
		 * - likeCount: 点赞数
		 * - commentCount: 评论数
		 * - viewCount: 浏览数
		 * 
		 * loading: 加载状态标识
		 */
		data() {
			return {
				myPosts: [],
				loading: false
			}
		},
		/**
		 * 页面加载时执行
		 * 功能：加载我的帖子列表
		 */
		onLoad() {
			this.loadMyPosts()
		},
		/**
		 * 页面显示时执行
		 * 功能：重新加载我的帖子列表，确保数据最新
		 */
		onShow() {
			this.loadMyPosts()
		},
		methods: {
			/**
			 * 加载我的帖子列表
			 * 
			 * 功能：
			 * 1. 调用后端接口获取我的帖子列表
			 * 2. 初始化帖子统计数据（点赞数、浏览数、评论数）
			 * 3. 处理加载状态和错误提示
			 * 
			 * 错误处理：
			 * - 捕获网络请求错误
			 * - 显示加载失败提示
			 * - finally块确保loading状态被重置
			 */
			async loadMyPosts() {
				this.loading = true
				try {
					const res = await request(API.POST.MY, 'GET')
					this.myPosts = res.data.map(post => ({
						...post,
						likeCount: post.likeCount || 0,
						viewCount: post.viewCount || 0,
						commentCount: post.commentCount || 0
					}))
				} catch (error) {
					console.error('加载我的帖子失败', error)
					uni.showToast({
						title: '加载失败',
						icon: 'none'
					})
				} finally {
					this.loading = false
				}
			},
			/**
			 * 查看帖子详情
			 * 
			 * @param {number} postId - 帖子ID
			 * 
			 * 功能：
			 * 跳转到帖子详情页面
			 */
			viewPostDetail(postId) {
				uni.navigateTo({
					url: '/pages/community/detail/detail?id=' + postId
				})
			},
			/**
			 * 编辑帖子
			 * 
			 * @param {number} postId - 帖子ID
			 * 
			 * 功能：
			 * 跳转到编辑帖子页面
			 */
			editPost(postId) {
				uni.navigateTo({
					url: '/pages/community/createPost/createPost?id=' + postId
				})
			},
			/**
			 * 删除帖子
			 * 
			 * @param {number} postId - 帖子ID
			 * @param {number} index - 帖子在列表中的索引
			 * 
			 * 功能：
			 * 1. 显示确认对话框
			 * 2. 用户确认后调用后端接口删除帖子
			 * 3. 从本地列表中移除该帖子
			 * 
			 * 错误处理：
			 * - 捕获网络请求错误
			 * - 显示删除失败提示
			 */
			deletePost(postId, index) {
				uni.showModal({
					title: '确认删除',
					content: '确定要删除这篇帖子吗？',
					success: async (res) => {
						if (res.confirm) {
							try {
								await request(API.POST.DELETE(postId), 'DELETE')
								uni.showToast({
									title: '删除成功',
									icon: 'success'
								})
								this.myPosts.splice(index, 1)
							} catch (error) {
								console.error('删除帖子失败', error)
								uni.showToast({
									title: '删除失败',
									icon: 'none'
								})
							}
						}
					}
				})
			},
			/**
			 * 创建帖子
			 * 
			 * 功能：
			 * 跳转到创建帖子页面
			 */
			createPost() {
				uni.navigateTo({
					url: '/pages/community/createPost/createPost'
				})
			},
			/**
			 * 格式化日期
			 * 
			 * @param {string} dateStr - 日期字符串
			 * @returns {string} 格式化后的日期字符串
			 * 
			 * 功能：
			 * 将日期格式化为中文格式（YYYY年MM月DD日 HH:mm）
			 */
			formatDate(dateStr) {
				if (!dateStr) return ''
				const date = new Date(dateStr)
				return date.toLocaleString('zh-CN', {
					year: 'numeric',
					month: '2-digit',
					day: '2-digit',
					hour: '2-digit',
					minute: '2-digit'
				})
			}
		}
	}
</script>

<style scoped>
.my-posts-container {
	padding: 0;
	background-color: #f5f5f5;
	min-height: 100vh;
}

.header {
	height: 88rpx;
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 0 20rpx;
	box-shadow: 0 4rpx 16rpx rgba(102, 126, 234, 0.3);
}

.title {
	font-size: 32rpx;
	font-weight: bold;
	color: #fff;
}

.loading-state {
	display: flex;
	justify-content: center;
	align-items: center;
	height: 300rpx;
}

.loading-text {
	font-size: 28rpx;
	color: #999;
}

.post-list {
	padding: 20rpx;
}

.post-item {
	margin-bottom: 20rpx;
}

.post-header {
	display: flex;
	justify-content: space-between;
	align-items: flex-start;
	margin-bottom: 12rpx;
}

.post-title {
	flex: 1;
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
	line-height: 1.5;
	margin-right: 16rpx;
}

.post-actions {
	display: flex;
	gap: 12rpx;
}

.action-btn {
	padding: 8rpx 16rpx;
	border-radius: 8rpx;
	font-size: 24rpx;
}

.edit-btn {
	background-color: #e6f7ff;
	color: #1890ff;
}

.delete-btn {
	background-color: #fff1f0;
	color: #ff4d4f;
}

.action-text {
	font-size: 24rpx;
}

.post-content {
	display: block;
	font-size: 28rpx;
	color: #666;
	line-height: 1.7;
	margin-bottom: 12rpx;
}

.post-meta {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 12rpx;
}

.post-time {
	font-size: 24rpx;
	color: #999;
}

.post-category {
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	color: #fff;
	font-size: 22rpx;
	padding: 6rpx 14rpx;
	border-radius: 16rpx;
	font-weight: 500;
}

.post-stats {
	display: flex;
	gap: 32rpx;
	padding-top: 12rpx;
	border-top: 1rpx solid #f0f0f0;
}

.stat-item {
	display: flex;
	align-items: center;
	color: #666;
}

.stat-icon {
	font-size: 26rpx;
	margin-right: 6rpx;
}

.stat-text {
	font-size: 24rpx;
}

.empty-state {
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 100rpx 40rpx;
	margin: 20rpx;
}

.empty-icon {
	font-size: 100rpx;
	margin-bottom: 20rpx;
}

.empty-text {
	font-size: 30rpx;
	color: #999;
	margin-bottom: 40rpx;
}

.create-btn {
	width: 200rpx;
	height: 80rpx;
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	color: #fff;
	border-radius: 40rpx;
	font-size: 28rpx;
	font-weight: 500;
	border: none;
	display: flex;
	align-items: center;
	justify-content: center;
	box-shadow: 0 4rpx 12rpx rgba(102, 126, 234, 0.3);
}

.create-text {
	font-size: 28rpx;
	font-weight: 500;
}
</style>
