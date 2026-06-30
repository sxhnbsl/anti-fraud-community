<template>
	<view class="community-container">
		<view class="header">
			<view class="header-content">
				<text class="title">防诈骗社区</text>
				<view class="search-box" @click="showSearch">
					<text class="search-icon">🔍</text>
					<text class="search-placeholder">搜索社区内容</text>
				</view>
			</view>
		</view>
		
		<scroll-view scroll-x class="category-scroll">
			<view class="category-tabs">
				<view class="category-item" :class="{ active: activeCategory === 'all' }" @click="activeCategory = 'all'">📋 全部</view>
				<view class="category-item" :class="{ active: activeCategory === '经验分享' }" @click="activeCategory = '经验分享'">💡 经验分享</view>
				<view class="category-item" :class="{ active: activeCategory === '问题求助' }" @click="activeCategory = '问题求助'">❓ 问题求助</view>
				<view class="category-item" :class="{ active: activeCategory === '诈骗预警' }" @click="activeCategory = '诈骗预警'">⚠️ 诈骗预警</view>
				<view class="category-item" :class="{ active: activeCategory === '防骗技巧' }" @click="activeCategory = '防骗技巧'">🛡️ 防骗技巧</view>
			</view>
		</scroll-view>
		
		<view class="sort-section">
			<view class="sort-options">
				<view class="sort-item" :class="{ active: activeSort === 'latest' }" @click="activeSort = 'latest'">
					<text class="sort-text">最新</text>
					<view v-if="activeSort === 'latest'" class="sort-indicator"></view>
				</view>
				<view class="sort-item" :class="{ active: activeSort === 'hot' }" @click="activeSort = 'hot'">
					<text class="sort-text">热门</text>
					<view v-if="activeSort === 'hot'" class="sort-indicator"></view>
				</view>
			</view>
		</view>
		
		<view class="fab-button" @click="createPost">
			<text class="fab-icon">✏️</text>
			<text class="fab-text">发帖</text>
		</view>
		
		<view v-if="loading" class="loading-container">
			<view class="loading-spinner"></view>
			<text class="loading-text">加载中...</text>
		</view>
		
		<view v-else class="post-list">
			<view class="post-item" v-for="(item, index) in postList" :key="item.id" @click="viewPost(item.id)">
				<view class="post-card">
					<view class="post-header">
						<view class="user-avatar-wrapper">
						<image v-if="item.authorAvatar" :src="getStaticUrl(item.authorAvatar)" class="user-avatar-image" mode="aspectFill"></image>
						<text v-else class="user-avatar-emoji">👤</text>
					</view>
						<view class="user-info">
							<text class="user-name">{{item.authorName || '匿名用户'}}</text>
							<text class="post-time">{{formatDate(item.createdAt)}}</text>
						</view>
						<view class="post-category-badge" v-if="item.category">
							<text class="category-text">{{item.category}}</text>
						</view>
					</view>
					<text class="post-title">{{item.title}}</text>
					<text class="post-content">{{item.content}}</text>
					<view class="post-footer">
						<view class="footer-item" :class="{ 'liked': item.isLiked }" @click.stop="likePost(item)">
							<text class="footer-icon">{{ item.isLiked ? '❤️' : '🤍' }}</text>
							<text class="footer-text">{{ item.likeCount || 0 }}</text>
						</view>
						<view class="footer-item" @click.stop="commentPost(item.id)">
							<text class="footer-icon">💬</text>
							<text class="footer-text">{{ item.commentCount || 0 }}</text>
						</view>
					</view>
				</view>
			</view>
		</view>
		
		<view v-if="!loading && postList.length === 0" class="empty-state">
			<view class="empty-icon-wrapper">
				<text class="empty-icon">📝</text>
			</view>
			<text class="empty-text">暂无相关内容</text>
			<text class="empty-hint">成为第一个发布内容的人吧</text>
		</view>
	</view>
</template>

<script>
	// 导入API接口配置
import { API, getStaticUrl } from '@/utils/api.js'
// 导入HTTP请求工具
import { request } from '@/utils/request.js'

	/**
	 * 社区首页组件
	 * 
	 * 功能说明：
	 * 1. 展示社区帖子列表，支持分类筛选和排序
	 * 2. 提供搜索功能，可搜索帖子标题和内容
	 * 3. 支持点赞和评论操作
	 * 4. 提供发帖入口，跳转到发帖页面
	 * 5. 支持下拉刷新加载最新帖子
	 * 6. 展示帖子卡片，包含作者信息、标题、内容、点赞数、评论数
	 * 
	 * @author 反诈骗平台开发团队
	 * @version 1.0
	 */
	export default {
		/**
		 * 组件数据
		 * 
		 * activeCategory: 当前选中的分类，默认为'all'（全部）
		 * activeSort: 当前选中的排序方式，默认为'latest'（最新）
		 * searchKeyword: 搜索关键词
		 * postList: 帖子列表数据
		 * loading: 加载状态标识
		 * likedPosts: 已点赞的帖子集合，使用Map存储
		 */
		data() {
			return {
				activeCategory: 'all',
				activeSort: 'latest',
				searchKeyword: '',
				postList: [],
				loading: false,
				likedPosts: new Map()
			}
		},
		
		/**
		 * 页面加载时执行
		 * 功能：加载帖子列表
		 */
		onLoad() {
			this.loadPosts()
		},
		
		/**
		 * 页面显示时执行
		 * 功能：重新加载帖子列表，确保数据最新
		 */
		onShow() {
			this.loadPosts()
		},
		
		/**
		 * 下拉刷新时执行
		 * 功能：重新加载帖子列表
		 */
		onPullDownRefresh() {
			this.loadPosts()
		},
		
		/**
		 * 监听分类和排序变化，自动重新加载帖子
		 */
		watch: {
			activeCategory() {
				this.loadPosts()
			},
			activeSort() {
				this.loadPosts()
			}
		},
		
		methods: {
			/**
			 * 加载帖子列表
			 * 
			 * 功能：
			 * 1. 调用后端接口获取帖子列表
			 * 2. 处理返回数据，初始化点赞数、浏览数、评论数
			 * 3. 处理加载状态和错误提示
			 * 
			 * 错误处理：
			 * - 捕获网络请求错误
			 * - 显示加载失败提示
			 * - finally块确保loading状态和下拉刷新状态被重置
			 */
			async loadPosts() {
				this.loading = true
				try {
					// 构建查询参数
					let url = API.POST.LIST
					let params = []
					
					// 添加分类参数
					if (this.activeCategory !== 'all') {
						params.push(`category=${encodeURIComponent(this.activeCategory)}`)
					}
					
					// 添加排序参数
					params.push(`sort=${this.activeSort}`)
					
					// 构建完整URL
					if (params.length > 0) {
						url += '?' + params.join('&')
					}
					
					// 调用帖子列表接口
					const res = await request(url, 'GET')
					console.log('帖子列表响应:', res)
					
					// 处理返回数据，初始化各项统计数据
					this.postList = (res.data || []).map(post => ({
						...post,
						likeCount: post.likeCount || 0,
						viewCount: post.viewCount || 0,
						commentCount: post.commentCount || 0,
						isLiked: false
					}))
				} catch (error) {
					console.error('加载帖子失败', error)
					// 显示加载失败提示
					uni.showToast({
						title: '加载失败',
						icon: 'none'
					})
				} finally {
					// 重置loading状态
					this.loading = false
					// 停止下拉刷新动画
					uni.stopPullDownRefresh()
				}
			},
			
			/**
			 * 搜索帖子
			 * 
			 * 功能：
			 * 1. 验证搜索关键词是否为空
			 * 2. 调用接口获取所有帖子
			 * 3. 根据关键词过滤帖子（匹配标题或内容）
			 * 4. 显示搜索结果数量或未找到提示
			 * 
			 * 错误处理：
			 * - 捕获网络请求错误
			 * - 显示搜索失败提示
			 */
			async searchPosts() {
				// 验证搜索关键词
				if (!this.searchKeyword) {
					uni.showToast({
						title: '请输入搜索关键词',
						icon: 'none'
					})
					return
				}
				
				this.loading = true
				try {
					// 获取所有帖子
					const res = await request(API.POST.LIST, 'GET')
					const allPosts = res.data || []
					
					// 根据关键词过滤帖子
					this.postList = allPosts.filter(post => 
						post.title && post.title.includes(this.searchKeyword) || 
						post.content && post.content.includes(this.searchKeyword)
					)
					
					// 显示搜索结果
					if (this.postList.length === 0) {
						uni.showToast({
							title: '未找到相关内容',
							icon: 'none'
						})
					} else {
						uni.showToast({
							title: `找到 ${this.postList.length} 条结果`,
							icon: 'success'
						})
					}
				} catch (error) {
					console.error('搜索失败', error)
					uni.showToast({
						title: '搜索失败，请重试',
						icon: 'none'
					})
				} finally {
					// 重置loading状态
					this.loading = false
				}
			},
			
			/**
			 * 创建新帖子
			 * 
			 * 功能：
			 * 跳转到发帖页面
			 */
			createPost() {
				uni.navigateTo({
					url: '/pages/community/createPost/createPost'
				})
			},
			
			/**
			 * 点赞或取消点赞帖子
			 * 
			 * @param {Object} post - 帖子对象
			 * 
			 * 功能：
			 * 1. 调用后端点赞接口
			 * 2. 更新帖子的点赞状态和点赞数
			 * 3. 显示操作成功提示
			 * 
			 * 错误处理：
			 * - 捕获网络请求错误
			 * - 显示操作失败提示
			 */
			async likePost(post) {
				try {
					// 调用点赞接口
					const res = await request(API.POST.LIKE(post.id), 'POST')
					
					console.log('点赞响应:', res)
					
					// 处理返回结果
					if (res && res.liked !== undefined) {
						const result = res
						
						// 更新点赞状态和点赞数
						post.isLiked = result.liked
						post.likeCount = result.likeCount
						
						// 显示操作成功提示
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
			 * 评论帖子
			 * 
			 * @param {number} postId - 帖子ID
			 * 
			 * 功能：
			 * 跳转到评论页面，并传递帖子ID
			 */
			commentPost(postId) {
				uni.navigateTo({
					url: `/pages/community/comment/comment?id=${postId}`
				})
			},
			
			/**
			 * 查看帖子详情
			 * 
			 * @param {number} postId - 帖子ID
			 * 
			 * 功能：
			 * 跳转到帖子详情页面，并传递帖子ID
			 */
			viewPost(postId) {
				console.log('点击帖子，帖子ID:', postId)
				uni.navigateTo({
					url: `/pages/community/detail/detail?id=${postId}`
				})
			},
			
			/**
			 * 分享帖子
			 * 
			 * @param {number} index - 帖子索引
			 * 
			 * 功能：
			 * 开启微信分享功能
			 */
			sharePost(index) {
				uni.showShareMenu({
					withShareTicket: true
				})
				uni.showToast({
					title: '分享功能已开启',
					icon: 'success'
				})
			},
			
			/**
			 * 显示搜索框
			 * 
			 * 功能：
			 * 1. 显示模态对话框，允许用户输入搜索关键词
			 * 2. 用户确认后执行搜索
			 */
			showSearch() {
				uni.showModal({
					title: '搜索社区',
					editable: true,
					placeholderText: '搜索帖子标题或内容',
					success: (res) => {
						if (res.confirm && res.content) {
							this.searchKeyword = res.content
							this.searchPosts()
						}
					}
				})
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
				if (minutes < 60) return `${minutes}分钟前`
				// 小于24小时显示"X小时前"
				if (hours < 24) return `${hours}小时前`
				// 小于7天显示"X天前"
				if (days < 7) return `${days}天前`
				
				// 超过7天显示具体日期
				const year = date.getFullYear()
				const month = String(date.getMonth() + 1).padStart(2, '0')
				const day = String(date.getDate()).padStart(2, '0')
				return `${year}-${month}-${day}`
			},
			
			getStaticUrl(url) {
				return getStaticUrl(url)
			}
		}
	}
</script>

<style scoped>
	.community-container {
		min-height: 100vh;
		background: linear-gradient(180deg, #e3f2fd 0%, #f5f5f5 100%);
		position: relative;
	}

	.loading-container {
		display: flex;
		flex-direction: column;
		justify-content: center;
		align-items: center;
		padding: 150rpx 0;
	}

	.loading-spinner {
		width: 60rpx;
		height: 60rpx;
		border: 4rpx solid #f3f3f3;
		border-top: 4rpx solid #667eea;
		border-radius: 50%;
		animation: spin 1s linear infinite;
		margin-bottom: 20rpx;
	}

	@keyframes spin {
		0% { transform: rotate(0deg); }
		100% { transform: rotate(360deg); }
	}

	.loading-text {
		font-size: 28rpx;
		color: #999;
	}

	.header {
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
		padding: 50rpx 30rpx 30rpx;
		position: sticky;
		top: 0;
		z-index: 100;
		box-shadow: 0 8rpx 32rpx rgba(102, 126, 234, 0.2);
	}

	.header-content {
		display: flex;
		flex-direction: column;
		gap: 20rpx;
	}

	.title {
		font-size: 40rpx;
		font-weight: bold;
		color: #fff;
	}

	.search-box {
		height: 70rpx;
		background: rgba(255, 255, 255, 0.95);
		border-radius: 35rpx;
		padding: 0 24rpx;
		display: flex;
		align-items: center;
		box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.1);
	}

	.search-icon {
		font-size: 32rpx;
		margin-right: 12rpx;
	}

	.search-placeholder {
		font-size: 28rpx;
		color: #999;
	}

	.category-scroll {
		background-color: #fff;
		box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.06);
	}

	.category-tabs {
		display: flex;
		align-items: center;
		padding: 20rpx;
		white-space: nowrap;
	}

	.category-item {
		height: 60rpx;
		line-height: 60rpx;
		padding: 0 28rpx;
		margin-right: 16rpx;
		border-radius: 30rpx;
		font-size: 26rpx;
		color: #666;
		background-color: #f5f5f5;
		transition: all 0.3s ease;
		flex-shrink: 0;
	}

	.category-item.active {
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
		color: #fff;
		box-shadow: 0 4rpx 12rpx rgba(102, 126, 234, 0.3);
	}

	.sort-section {
		background-color: #fff;
		margin-top: 16rpx;
		box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
	}

	.sort-options {
		display: flex;
		align-items: center;
		padding: 0 30rpx;
	}

	.sort-item {
		height: 80rpx;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		padding: 0 30rpx;
		position: relative;
	}

	.sort-text {
		font-size: 28rpx;
		color: #666;
		font-weight: 500;
	}

	.sort-item.active .sort-text {
		color: #667eea;
		font-weight: bold;
	}

	.sort-indicator {
		position: absolute;
		bottom: 0;
		width: 40rpx;
		height: 6rpx;
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
		border-radius: 3rpx;
	}

	.fab-button {
		height: 100rpx;
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
		border-radius: 50rpx;
		position: fixed;
		right: 30rpx;
		bottom: 150rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		padding: 0 30rpx;
		box-shadow: 0 8rpx 24rpx rgba(102, 126, 234, 0.4);
		z-index: 999;
		transition: all 0.3s ease;
	}

	.fab-button:active {
		transform: scale(0.95);
		box-shadow: 0 4rpx 12rpx rgba(102, 126, 234, 0.3);
	}

	.fab-icon {
		font-size: 32rpx;
		margin-right: 8rpx;
	}

	.fab-text {
		font-size: 28rpx;
		color: #fff;
		font-weight: bold;
	}

	.post-list {
		display: flex;
		flex-direction: column;
		padding: 20rpx;
	}

	.post-item {
		margin-bottom: 20rpx;
	}

	.post-card {
		background-color: #fff;
		border-radius: 24rpx;
		padding: 28rpx;
		box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.06);
		transition: all 0.3s ease;
	}

	.post-card:active {
		transform: scale(0.98);
		box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.08);
	}

	.post-header {
		display: flex;
		align-items: center;
		margin-bottom: 20rpx;
	}

	.user-avatar-wrapper {
			width: 80rpx;
			height: 80rpx;
			background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
			border-radius: 50%;
			display: flex;
			align-items: center;
			justify-content: center;
			margin-right: 16rpx;
			box-shadow: 0 4rpx 12rpx rgba(102, 126, 234, 0.3);
		}

		.user-avatar-image {
			width: 80rpx;
			height: 80rpx;
			border-radius: 50%;
		}

		.user-avatar-emoji {
			font-size: 40rpx;
		}

	.user-info {
		display: flex;
		flex-direction: column;
		flex: 1;
	}

	.user-name {
		font-size: 30rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 6rpx;
	}

	.post-time {
		font-size: 24rpx;
		color: #999;
	}

	.post-category-badge {
		height: 44rpx;
		line-height: 44rpx;
		padding: 0 20rpx;
		background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
		border-radius: 22rpx;
		font-size: 22rpx;
		font-weight: 500;
		box-shadow: 0 2rpx 8rpx rgba(240, 147, 251, 0.3);
	}

	.category-text {
		color: #fff;
	}

	.post-title {
		font-size: 34rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 12rpx;
		display: block;
		line-height: 1.5;
	}

	.post-content {
		font-size: 28rpx;
		color: #666;
		line-height: 48rpx;
		margin-bottom: 20rpx;
		display: block;
		display: -webkit-box;
		-webkit-line-clamp: 3;
		-webkit-box-orient: vertical;
		overflow: hidden;
		text-overflow: ellipsis;
	}

	.post-footer {
		display: flex;
		justify-content: space-around;
		padding-top: 20rpx;
		border-top: 1rpx solid #f0f0f0;
	}

	.footer-item {
		display: flex;
		align-items: center;
		justify-content: center;
		padding: 10rpx 20rpx;
		border-radius: 20rpx;
		transition: all 0.2s ease;
	}

	.footer-item:active {
		background-color: #f5f5f5;
	}
	
	.footer-item.liked {
		background-color: #fff0f0;
	}
	
	.footer-item.liked .footer-text {
		color: #f5576c;
	}

	.footer-icon {
		font-size: 34rpx;
		margin-right: 8rpx;
	}

	.footer-text {
		font-size: 26rpx;
		color: #666;
		font-weight: 500;
	}

	.empty-state {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		padding: 180rpx 40rpx;
		background-color: #fff;
		margin: 20rpx;
		border-radius: 24rpx;
		box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.06);
	}

	.empty-icon-wrapper {
		width: 160rpx;
		height: 160rpx;
		background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
		border-radius: 80rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		margin-bottom: 30rpx;
	}

	.empty-icon {
		font-size: 80rpx;
	}

	.empty-text {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 16rpx;
	}

	.empty-hint {
		font-size: 26rpx;
		color: #999;
		text-align: center;
		line-height: 1.6;
	}
</style>
