<template>
	<view class="detail-container">
		<view v-if="loading" class="loading-state">
			<text class="loading-text">加载中...</text>
		</view>
		
		<view v-else class="post-detail card">
			<view class="post-header">
				<view class="avatar-wrapper">
					<text class="avatar-emoji">👤</text>
				</view>
				<view class="user-info">
					<text class="user-name">{{postItem.authorName || '匿名用户'}}</text>
					<text class="post-time">{{formatDate(postItem.createdAt)}}</text>
				</view>
				<view class="post-category" v-if="postItem.category">
					{{postItem.category}}
				</view>
			</view>
			
			<text class="post-title">{{postItem.title}}</text>
			<text class="post-content">{{postItem.content}}</text>
			
			<view class="bottom-bar">
				<view class="action-item" :class="{ 'liked': postItem.isLiked }" @click="likePost">
					<text class="action-icon">{{ postItem.isLiked ? '❤️' : '🤍' }}</text>
					<text class="action-text">{{postItem.likeCount || 0}}</text>
				</view>
				<view class="action-item" @click="commentPost">
					<text class="action-icon">💬</text>
					<text class="action-text">{{postItem.commentCount || 0}}</text>
				</view>
				<view class="action-item" @click="sharePost">
					<text class="action-icon">📤</text>
					<text class="action-text">分享</text>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	// 导入HTTP请求工具
	import { request } from '@/utils/request.js'
	// 导入API接口配置
	import { API } from '@/utils/api.js'

	/**
	 * 社区帖子详情页面组件
	 * 
	 * 功能说明：
	 * 1. 展示帖子详细信息（作者、标题、内容、分类、发布时间）
	 * 2. 支持点赞帖子
	 * 3. 支持查看评论列表
	 * 4. 支持分享帖子
	 * 5. 页面显示时自动刷新数据
	 * 
	 * @author 反诈骗平台开发团队
	 * @version 1.0
	 */
	export default {
		/**
		 * 组件数据
		 * 
		 * postId: 帖子ID
		 * postItem: 帖子详细信息
		 * - authorName: 作者名称
		 * - createdAt: 创建时间
		 * - title: 帖子标题
		 * - content: 帖子内容
		 * - category: 帖子分类
		 * - likeCount: 点赞数
		 * - commentCount: 评论数
		 * - isLiked: 是否已点赞
		 * 
		 * loading: 加载状态标识
		 */
		data() {
			return {
				postId: '',
				postItem: {
					isLiked: false
				},
				loading: false
			}
		},
		/**
		 * 页面加载时执行
		 * 
		 * @param {Object} options - 页面参数
		 * @param {string} options.id - 帖子ID
		 * 
		 * 功能：
		 * 1. 验证帖子ID是否存在
		 * 2. 加载帖子详情
		 * 3. 如果帖子ID不存在，显示错误提示并返回
		 */
		onLoad(options) {
			console.log('detail页面加载，接收到的参数:', options)
			console.log('帖子ID:', options.id)
			if (!options.id) {
				uni.showToast({
					title: '帖子ID不存在',
					icon: 'none'
				})
				setTimeout(() => {
					uni.navigateBack()
				}, 1500)
				return
			}
			this.postId = options.id
			this.loadPostDetail()
		},
		/**
		 * 页面显示时执行
		 * 功能：
		 * 重新加载帖子详情，确保数据最新
		 */
		onShow() {
			if (this.postId) {
				this.loadPostDetail()
			}
		},
		methods: {
			/**
			 * 加载帖子详情
			 * 
			 * 功能：
			 * 1. 调用后端接口获取帖子详情
			 * 2. 填充帖子数据
			 * 3. 处理点赞状态
			 * 
			 * 错误处理：
			 * - 捕获网络请求错误
			 * - 显示加载失败提示
			 * - finally块确保loading状态被重置
			 */
			async loadPostDetail() {
				this.loading = true
				try {
					console.log('开始加载帖子详情，ID:', this.postId)
					console.log('API路径:', API.POST.DETAIL(this.postId))
					const res = await request(API.POST.DETAIL(this.postId), 'GET')
					console.log('帖子详情响应:', res)
					if (res && res.data) {
						this.postItem = {
							...res.data,
							isLiked: res.isLiked || false
						}
						console.log('帖子详情数据:', this.postItem)
					} else {
						throw new Error('无效的响应数据')
					}
				} catch (error) {
					console.error('加载帖子详情失败', error)
					uni.showToast({
						title: error.errMsg || error.message || '加载失败',
						icon: 'none'
					})
				} finally {
					this.loading = false
				}
			},
			/**
			 * 点赞帖子
			 * 
			 * 功能：
			 * 1. 调用后端点赞接口
			 * 2. 更新点赞状态和点赞数
			 * 3. 显示操作成功提示
			 * 
			 * 错误处理：
			 * - 捕获网络请求错误
			 * - 显示操作失败提示
			 */
			async likePost() {
				try {
					const res = await request(API.POST.LIKE(this.postId), 'POST')
					console.log('帖子点赞响应:', res)
					
					if (res && res.liked !== undefined) {
						const result = res
						
						this.postItem.isLiked = result.liked
						this.postItem.likeCount = result.likeCount
						
						uni.showToast({
							title: result.message || (result.liked ? '点赞成功' : '取消点赞'),
							icon: 'success'
						})
					}
				} catch (error) {
					console.error('点赞失败', error)
					uni.showToast({
						title: '操作失败',
						icon: 'none'
					})
				}
			},
			/**
			 * 查看评论
			 * 
			 * 功能：
			 * 跳转到评论页面，并传递帖子ID
			 */
			commentPost() {
				uni.navigateTo({
					url: `/pages/community/comment/comment?id=${this.postId}`
				})
			},
			/**
			 * 分享帖子
			 * 
			 * 功能：
			 * 调用uni-app分享接口，显示分享菜单
			 */
			sharePost() {
				uni.showShareMenu({
					withShareTicket: true
				})
				uni.showToast({
					title: '分享功能已开启',
					icon: 'success'
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
.detail-container {
	padding: 20rpx;
	background-color: #f5f5f5;
	min-height: 100vh;
}

.loading-state {
	display: flex;
	justify-content: center;
	align-items: center;
	height: 400rpx;
}

.loading-text {
	font-size: 28rpx;
	color: #999;
}

.post-detail {
	margin-bottom: 20rpx;
}

.post-header {
	display: flex;
	align-items: center;
	margin-bottom: 24rpx;
}

.avatar-wrapper {
	width: 88rpx;
	height: 88rpx;
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	margin-right: 20rpx;
	box-shadow: 0 4rpx 12rpx rgba(102, 126, 234, 0.3);
}

.avatar-emoji {
	font-size: 44rpx;
}

.user-info {
	flex: 1;
}

.user-name {
	display: block;
	font-size: 30rpx;
	font-weight: 600;
	color: #333;
	margin-bottom: 6rpx;
}

.post-time {
	display: block;
	font-size: 24rpx;
	color: #999;
}

.post-category {
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	color: #fff;
	font-size: 22rpx;
	padding: 8rpx 16rpx;
	border-radius: 20rpx;
	font-weight: 500;
}

.post-title {
	display: block;
	font-size: 36rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 20rpx;
	line-height: 1.5;
}

.post-content {
	display: block;
	font-size: 30rpx;
	color: #555;
	line-height: 1.8;
	margin-bottom: 24rpx;
}

.bottom-bar {
	display: flex;
	justify-content: space-around;
	align-items: center;
	padding: 20rpx 0;
	border-top: 2rpx solid #f0f0f0;
	margin-top: 8rpx;
}

.action-item {
	display: flex;
	flex-direction: column;
	align-items: center;
	color: #666;
	padding: 12rpx 24rpx;
	border-radius: 20rpx;
	transition: all 0.3s ease;
}

.action-item:active {
	background-color: #f5f5f5;
}

.action-item.liked {
	background-color: #fff0f0;
}

.action-item.liked .action-text {
	color: #f5576c;
}

.action-icon {
	font-size: 36rpx;
	margin-bottom: 6rpx;
}

.action-text {
	font-size: 24rpx;
	font-weight: 500;
}
</style>
