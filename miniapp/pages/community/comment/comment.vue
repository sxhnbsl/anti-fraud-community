<template>
	<view class="comment-container">
		<view class="header">
			<text class="back" @click="goBack">←</text>
			<text class="title">评论</text>
		</view>
		
		<view class="content">
			<view class="post-info card">
				<text class="post-title">{{post.title}}</text>
				<text class="post-content">{{post.content}}</text>
			</view>
			
			<view class="comment-list">
				<view class="comment-item card" v-for="(comment, index) in comments" :key="comment.id">
					<view class="avatar-wrapper">
						<text class="avatar-emoji">👤</text>
					</view>
					<view class="comment-content">
						<view class="comment-header">
							<text class="user-name">{{comment.authorName || '匿名用户'}}</text>
							<text class="comment-time">{{formatDate(comment.createdAt)}}</text>
						</view>
						<text class="comment-text">{{comment.content}}</text>
						<view class="comment-footer">
							<view class="footer-item" :class="{ 'liked': comment.isLiked }" @click="likeComment(comment.id, index)">
								<text class="footer-icon">{{ comment.isLiked ? '❤️' : '🤍' }}</text>
								<text class="footer-text">{{comment.likeCount || 0}}</text>
							</view>
							<view class="footer-item" @click="replyComment(comment.authorName)">
								<text class="footer-icon">💬</text>
								<text class="footer-text">回复</text>
							</view>
							<view v-if="isCurrentUserComment(comment.authorId)" class="footer-item" @click="deleteComment(comment.id)">
								<text class="footer-icon">🗑️</text>
								<text class="footer-text">删除</text>
							</view>
						</view>
					</view>
				</view>
			</view>
			
			<view v-if="loading" class="loading-state">
				<text class="loading-text">加载中...</text>
			</view>
			
			<view v-if="!loading && comments.length === 0" class="empty-state card">
				<text class="empty-icon">💬</text>
				<text class="empty-text">暂无评论</text>
				<text class="empty-hint">成为第一个评论的人吧</text>
			</view>
		</view>
		
		<view class="comment-input">
			<input type="text" class="input" placeholder="写下你的评论..." v-model="newComment" />
			<button class="send-btn" @click="sendComment" :disabled="!newComment.trim()">发送</button>
		</view>
	</view>
</template>

<script>
	// 导入API接口配置
	import { API } from '@/utils/api.js'
	// 导入HTTP请求工具
	import { request } from '@/utils/request.js'

	/**
	 * 评论页面组件
	 * 
	 * 功能说明：
	 * 1. 展示帖子简要信息（标题和内容）
	 * 2. 展示评论列表，包含作者、内容、时间、点赞数
	 * 3. 支持发布评论，发布前进行内容安全审核
	 * 4. 支持点赞评论
	 * 5. 支持回复评论（@用户名）
	 * 6. 审核通过后才能发布评论
	 * 
	 * @author 反诈骗平台开发团队
	 * @version 1.0
	 */
	export default {
		/**
		 * 组件数据
		 * 
		 * postId: 帖子ID
		 * post: 帖子信息
		 * - title: 帖子标题
		 * - content: 帖子内容
		 * 
		 * comments: 评论列表
		 * newComment: 新评论内容
		 * replyTo: 回复的用户名
		 * loading: 加载状态标识
		 */
		data() {
			return {
				postId: null,
				post: {
					title: '',
					content: ''
				},
				comments: [],
				newComment: '',
				replyTo: '',
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
		 * 加载帖子详情和评论列表
		 */
		onLoad(options) {
			if (options.id) {
				this.postId = options.id
				this.loadPostDetail()
				this.loadComments()
			}
		},
		
		methods: {
			/**
			 * 加载帖子详情
			 * 
			 * 功能：
			 * 调用后端接口获取帖子详情，用于在评论页展示帖子信息
			 * 
			 * 错误处理：
			 * - 捕获网络请求错误
			 * - 在控制台输出错误信息
			 */
			async loadPostDetail() {
				try {
					const res = await request(API.POST.DETAIL(this.postId), 'GET')
					this.post = res.data
				} catch (error) {
					console.error('加载帖子失败', error)
				}
			},
			
			/**
			 * 加载评论列表
			 * 
			 * 功能：
			 * 调用后端接口获取帖子的所有评论
			 * 
			 * 错误处理：
			 * - 捕获网络请求错误
			 * - 在控制台输出错误信息
			 * - finally块确保loading状态被重置
			 */
			async loadComments() {
				this.loading = true
				try {
					const res = await request(API.COMMENT.LIST(this.postId), 'GET')
					this.comments = res.data || []
				} catch (error) {
					console.error('加载评论失败', error)
				} finally {
					this.loading = false
				}
			},
			
			/**
			 * 返回上一页
			 * 
			 * 功能：
			 * 使用uni.navigateBack返回上一页
			 */
			goBack() {
				uni.navigateBack()
			},
			
			/**
			 * 回复评论
			 * 
			 * @param {string} userName - 被回复的用户名
			 * 
			 * 功能：
			 * 在评论输入框中添加@用户名，方便用户回复
			 */
			replyComment(userName) {
				this.replyTo = userName
				this.newComment = '@' + userName + ' '
			},
			
			/**
			 * 点赞或取消点赞评论
			 * 
			 * @param {number} commentId - 评论ID
			 * @param {number} index - 评论在列表中的索引
			 * 
			 * 功能：
			 * 1. 调用后端点赞接口
			 * 2. 根据返回的状态更新点赞状态和点赞数
			 * 3. 显示操作成功提示
			 * 
			 * 错误处理：
			 * - 捕获网络请求错误
			 * - 显示操作失败提示
			 */
			async likeComment(commentId, index) {
				try {
					// 调用点赞接口
					const res = await request(API.COMMENT.LIKE(commentId), 'POST')
					console.log('评论点赞响应:', res)
					
					if (res && res.liked !== undefined) {
						// 更新点赞状态和点赞数
						this.comments[index].isLiked = res.liked
						this.comments[index].likeCount = res.likeCount
						
						// 显示成功提示
						uni.showToast({
							title: res.message || (res.liked ? '点赞成功' : '取消点赞'),
							icon: 'success'
						})
					}
				} catch (error) {
					console.error('点赞失败', error)
					uni.showToast({
						title: '点赞失败',
						icon: 'none'
					})
				}
			},
			
			/**
			 * 检查评论内容安全性
			 * 
			 * @param {string} content - 待检查的评论内容
			 * @returns {Object|null} 审核结果对象，失败返回null
			 * 
			 * 功能：
			 * 调用后端内容安全审核接口，检查评论是否包含敏感信息
			 */
			async checkCommentContent(content) {
				try {
					const res = await request(API.COMMENT.CHECK_CONTENT, 'POST', { content })
					return res
				} catch (error) {
					console.error('评论内容审核失败', error)
					return null
				}
			},
			
			/**
			 * 发送评论
			 * 
			 * 功能：
			 * 1. 验证评论内容是否为空
			 * 2. 验证用户是否登录
			 * 3. 进行内容安全审核
			 * 4. 审核通过后调用发布接口
			 * 5. 成功后清空输入框并重新加载评论列表
			 * 
			 * 错误处理：
			 * - 评论内容为空提示
			 * - 未登录提示并跳转到登录页
			 * - 审核服务异常提示
			 * - 审核未通过提示
			 * - 发布失败提示
			 */
			async sendComment() {
				// 验证评论内容
				if (!this.newComment.trim()) {
					uni.showToast({
						title: '请输入评论内容',
						icon: 'none'
					})
					return
				}
				
				// 验证登录状态
				const isLoggedIn = uni.getStorageSync('isLoggedIn')
				const userInfo = uni.getStorageSync('userInfo')
				const token = uni.getStorageSync('token')
				
				if (!isLoggedIn || !token || !userInfo || !userInfo.id) {
					uni.showToast({
						title: '请先登录',
						icon: 'none'
					})
					// 延迟跳转到登录页
					setTimeout(() => {
						uni.navigateTo({
							url: '/pages/login/login'
						})
					}, 1500)
					return
				}
				
				// 显示审核加载提示
				uni.showLoading({
					title: '内容审核中...',
					mask: true
				})
				
				// 进行内容审核
				const checkResult = await this.checkCommentContent(this.newComment)
				
				// 检查审核服务是否正常
				if (!checkResult) {
					uni.hideLoading()
					uni.showToast({
						title: '审核服务异常，请稍后重试',
						icon: 'none'
					})
					return
				}
				
				// 检查审核是否通过
				if (!checkResult.data || !checkResult.data.passed) {
					uni.hideLoading()
					const suggestion = checkResult.data?.suggestion || '内容审核未通过'
					const riskLabels = checkResult.data?.riskLabels || []
					
					// 构建错误提示信息
					let message = suggestion
					if (riskLabels.length > 0) {
						message += '\n风险标签: ' + riskLabels.join(', ')
					}
					
					// 显示审核未通过提示
					uni.showModal({
						title: '内容审核未通过',
						content: message,
						showCancel: false,
						confirmText: '我知道了'
					})
					return
				}
				
				// 审核通过，隐藏加载提示
				uni.hideLoading()
				
				try {
					// 准备评论数据
					const commentData = {
						postId: this.postId,
						authorId: userInfo.id,
						authorName: userInfo.nickname || userInfo.username,
						content: this.newComment
					}
					
					// 调用发布评论接口
					await request(API.COMMENT.CREATE, 'POST', commentData)
					
					// 显示成功提示
					uni.showToast({
						title: '评论成功',
						icon: 'success'
					})
					
					// 清空输入框
					this.newComment = ''
					
					// 重新加载评论列表
					await this.loadComments()
				} catch (error) {
					console.error('评论失败', error)
					uni.showToast({
						title: '评论失败',
						icon: 'none'
					})
				}
			},
			
			/**
			 * 格式化日期
			 * 
			 * @param {string} dateStr - 日期字符串
			 * @returns {string} 格式化后的日期字符串
			 * 
			 * 功能：
			 * 将日期转换为相对时间格式（如：刚刚、5分钟前、2小时前、3天前）
			 * 超过7天显示具体日期（YYYY-MM-DD）
			 */
			formatDate(dateStr) {
				if (!dateStr) return ''
				const date = new Date(dateStr)
				const now = new Date()
				const diff = now - date
				const minutes = Math.floor(diff / 60000)
				const hours = Math.floor(diff / 3600000)
				const days = Math.floor(diff / 86400000)
				
				// 小于1分钟显示"刚刚"
				if (minutes < 1) return '刚刚'
				// 小于1小时显示"X分钟前"
				if (minutes < 60) return minutes + '分钟前'
				// 小于24小时显示"X小时前"
				if (hours < 24) return hours + '小时前'
				// 小于7天显示"X天前"
				if (days < 7) return days + '天前'
				
				// 超过7天显示具体日期
				const year = date.getFullYear()
				const month = String(date.getMonth() + 1).padStart(2, '0')
				const day = String(date.getDate()).padStart(2, '0')
				return year + '-' + month + '-' + day
			},
			
			/**
			 * 判断是否是当前用户的评论
			 * 
			 * @param {number} authorId - 评论作者ID
			 * @returns {boolean} 是否是当前用户的评论
			 */
			isCurrentUserComment(authorId) {
				const userInfo = uni.getStorageSync('userInfo')
				return userInfo && userInfo.id === authorId
			},
			
			/**
			 * 删除评论
			 * 
			 * @param {number} commentId - 评论ID
			 * 
			 * 功能：
			 * 1. 显示删除确认对话框
			 * 2. 确认后调用后端删除接口
			 * 3. 删除成功后重新加载评论列表
			 * 4. 显示删除成功提示
			 */
			async deleteComment(commentId) {
				const _this = this
				uni.showModal({
					title: '确认删除',
					content: '确定要删除这条评论吗？',
					confirmText: '删除',
					cancelText: '取消',
					success: async function(res) {
						if (res.confirm) {
							try {
								// 调用删除评论接口
								await request(API.COMMENT.DELETE(commentId), 'DELETE')
								
								// 重新加载评论列表
								await _this.loadComments()
								
								// 显示成功提示
								uni.showToast({
									title: '删除成功',
									icon: 'success'
								})
							} catch (error) {
								console.error('删除评论失败', error)
								uni.showToast({
									title: '删除失败',
									icon: 'none'
								})
							}
						}
					}
				})
			}
		}
	}
</script>

<style scoped>
	.comment-container {
		min-height: 100vh;
		background-color: #f5f5f5;
		padding-bottom: 120rpx;
	}

	.header {
		height: 88rpx;
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
		display: flex;
		align-items: center;
		padding: 0 20rpx;
		box-shadow: 0 4rpx 16rpx rgba(102, 126, 234, 0.3);
		position: sticky;
		top: 0;
		z-index: 100;
	}

	.back {
		font-size: 40rpx;
		color: #fff;
		margin-right: 30rpx;
	}

	.title {
		font-size: 32rpx;
		font-weight: bold;
		color: #fff;
	}

	.content {
		padding: 20rpx;
	}

	.post-info {
		margin-bottom: 20rpx;
	}

	.post-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 12rpx;
		display: block;
	}

	.post-content {
		font-size: 28rpx;
		color: #666;
		line-height: 1.7;
		display: block;
	}

	.comment-list {
		display: flex;
		flex-direction: column;
	}

	.comment-item {
		display: flex;
		margin-bottom: 20rpx;
	}

	.avatar-wrapper {
		width: 72rpx;
		height: 72rpx;
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
		border-radius: 50%;
		display: flex;
		align-items: center;
		justify-content: center;
		margin-right: 16rpx;
		flex-shrink: 0;
	}

	.avatar-emoji {
		font-size: 36rpx;
	}

	.comment-content {
		flex: 1;
	}

	.comment-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 12rpx;
	}

	.user-name {
		font-size: 28rpx;
		font-weight: 600;
		color: #333;
	}

	.comment-time {
		font-size: 22rpx;
		color: #999;
	}

	.comment-text {
		font-size: 28rpx;
		color: #555;
		line-height: 1.7;
		margin-bottom: 12rpx;
		display: block;
	}

	.comment-footer {
		display: flex;
		align-items: center;
	}

	.footer-item {
		display: flex;
		align-items: center;
		margin-right: 40rpx;
		padding: 8rpx 16rpx;
		border-radius: 20rpx;
		transition: all 0.3s ease;
	}

	.footer-item:active {
		background-color: #f5f5f5;
	}

	.footer-item.liked {
		background-color: #fff0f0;
	}

	.footer-item.liked .footer-icon {
		animation: heartBeat 0.3s ease;
	}

	@keyframes heartBeat {
		0% {
			transform: scale(1);
		}
		50% {
			transform: scale(1.2);
		}
		100% {
			transform: scale(1);
		}
	}

	.footer-icon {
		font-size: 28rpx;
		margin-right: 6rpx;
	}

	.footer-text {
		font-size: 24rpx;
		color: #666;
		font-weight: 500;
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

	.empty-state {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		padding: 100rpx 40rpx;
	}

	.empty-icon {
		font-size: 88rpx;
		margin-bottom: 24rpx;
	}

	.empty-text {
		font-size: 32rpx;
		font-weight: 600;
		color: #333;
		margin-bottom: 12rpx;
	}

	.empty-hint {
		font-size: 26rpx;
		color: #999;
	}

	.comment-input {
		position: fixed;
		bottom: 0;
		left: 0;
		right: 0;
		height: 100rpx;
		background-color: #fff;
		display: flex;
		align-items: center;
		padding: 0 20rpx;
		box-shadow: 0 -4rpx 16rpx rgba(0,0,0,0.08);
	}

	.input {
		flex: 1;
		height: 64rpx;
		background-color: #f5f5f5;
		border-radius: 32rpx;
		padding: 0 24rpx;
		font-size: 28rpx;
		margin-right: 16rpx;
	}

	.send-btn {
		height: 64rpx;
		width: 112rpx;
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
		color: #fff;
		border-radius: 32rpx;
		font-size: 26rpx;
		font-weight: 500;
		border: none;
		display: flex;
		align-items: center;
		justify-content: center;
		box-shadow: 0 4rpx 12rpx rgba(102, 126, 234, 0.3);
	}

	.send-btn:disabled {
		background: #ccc;
		box-shadow: none;
	}
</style>
