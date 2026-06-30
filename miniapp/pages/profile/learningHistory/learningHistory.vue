<template>
	<view class="history-container">
		<view class="header">
			<view class="header-content">
				<view class="header-left" @click="goBack">
					<text class="back-icon">←</text>
				</view>
				<text class="title">浏览历史</text>
				<view class="header-right" @click="clearAllHistory">
					<text class="clear-icon">🗑️</text>
				</view>
			</view>
		</view>
		
		<view v-if="loading" class="loading-container">
			<view class="loading-spinner"></view>
			<text class="loading-text">加载中...</text>
		</view>
		
		<view v-else-if="historyList.length === 0" class="empty-state">
			<view class="empty-icon-wrapper">
				<text class="empty-icon">📖</text>
			</view>
			<text class="empty-text">暂无浏览记录</text>
			<text class="empty-hint">浏览知识和案例后会在这里显示</text>
		</view>
		
		<view v-else class="history-list">
			<view class="history-item" v-for="(item, index) in historyList" :key="index" @click="viewDetail(item)">
				<view class="history-card">
					<view class="history-icon-wrapper" :class="getIconClass(item.type)">
						<text class="history-icon">{{getIcon(item.type)}}</text>
					</view>
					<view class="history-content">
						<text class="history-title">{{item.title}}</text>
						<view class="history-info">
							<view class="info-item">
								<text class="info-icon">📅</text>
								<text class="info-text">{{formatDate(item.viewTime)}}</text>
							</view>
							<view class="info-item">
								<text class="info-icon">🔍</text>
								<text class="info-text">{{item.type === 'knowledge' ? '防诈骗知识' : '真实案例'}}</text>
							</view>
						</view>
					</view>
					<view class="history-arrow">›</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	// 导入API接口配置
	import { API } from '@/utils/api.js'
	// 导入HTTP请求工具
	import { request } from '@/utils/request.js'

	/**
	 * 浏览历史页面组件
	 * 
	 * 功能说明：
	 * 1. 展示用户的浏览历史记录
	 * 2. 支持按时间顺序查看历史
	 * 3. 支持清空所有历史记录
	 * 4. 点击历史记录可查看详情
	 * 
	 * @author 反诈骗平台开发团队
	 * @version 1.0
	 */
	export default {
		/**
		 * 组件数据
		 * 
		 * historyList: 浏览历史列表
		 * - id: 内容ID
		 * - title: 标题
		 * - type: 类型（knowledge/case）
		 * - viewTime: 浏览时间
		 * 
		 * loading: 加载状态标识
		 */
		data() {
			return {
				historyList: [],
				loading: false
			}
		},
		
		/**
		 * 页面加载时执行
		 * 功能：加载浏览历史列表
		 */
		onLoad() {
			this.loadHistory()
		},
		
		methods: {
			/**
		 * 加载浏览历史
		 * 
		 * 功能：
		 * 1. 调用后端接口获取浏览历史
		 * 2. 处理加载状态和错误提示
		 * 
		 * 错误处理：
		 * - 捕获网络请求错误
		 * - 显示加载失败提示
		 * - finally块确保loading状态被重置
		 */
		async loadHistory() {
			this.loading = true
			try {
				const userId = uni.getStorageSync('userId')
				if (!userId) {
					console.log('用户未登录，不加载浏览历史')
					this.historyList = []
					return
				}
				
				const res = await request(API.LEARNING_RECORD.USER(userId), 'GET')
				this.historyList = (res.data || []).map(item => ({
					id: item.contentId,
					title: item.title || '未命名内容',
					type: item.contentType || 'knowledge',
					viewTime: item.viewTime || item.createdAt
				}))
			} catch (error) {
				console.error('加载浏览历史失败', error)
				// 显示加载失败提示
				uni.showToast({
					title: '加载失败',
					icon: 'none'
				})
			} finally {
				// 重置loading状态
				this.loading = false
			}
		},
			
			/**
			 * 查看详情
			 * 
			 * @param {object} item - 历史记录项
			 * 
			 * 功能：
			 * 根据历史记录类型跳转到对应详情页
			 */
			viewDetail(item) {
				if (item.type === 'knowledge') {
					uni.navigateTo({
						url: `/pages/knowledge/detail/detail?id=${item.id}`
					})
				} else if (item.type === 'case') {
					uni.navigateTo({
						url: `/pages/case/detail/detail?id=${item.id}`
					})
				}
			},
			
			/**
		 * 清空所有历史
		 * 
		 * 功能：
		 * 1. 显示确认对话框
		 * 2. 清空本地历史记录
		 * 3. 调用后端接口清空历史
		 */
		async clearAllHistory() {
			uni.showModal({
				title: '清空历史',
				content: '确定要清空所有浏览历史吗？',
				confirmText: '确定',
				cancelText: '取消',
				success: async (res) => {
					if (res.confirm) {
						const userId = uni.getStorageSync('userId')
						if (!userId) {
							console.log('用户未登录，无法清空历史')
							return
						}
						
						// 清空本地记录
						this.historyList = []
						
						// 调用后端接口
						try {
							await request(API.LEARNING_RECORD.DELETE_USER(userId), 'DELETE')
							uni.showToast({
								title: '清空成功',
								icon: 'success'
							})
						} catch (error) {
							console.error('清空历史失败', error)
							uni.showToast({
								title: '清空失败',
								icon: 'none'
							})
						}
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
			
			/**
			 * 获取图标
			 * 
			 * @param {string} type - 类型
			 * @returns {string} 图标
			 */
			getIcon(type) {
				return '📖'
			},
			
			/**
			 * 获取图标样式类
			 * 
			 * @param {string} type - 类型
			 * @returns {string} 样式类
			 */
			getIconClass(type) {
				return 'history-icon'
			},
			
			/**
			 * 返回上一页
			 */
			goBack() {
				uni.navigateBack()
			}
		}
	}
</script>

<style scoped>
	.history-container {
		min-height: 100vh;
		background: linear-gradient(180deg, #e3f2fd 0%, #f5f5f5 100%);
	}

	.header {
		background: linear-gradient(135deg, #2196F3 0%, #1976D2 100%);
		padding: 50rpx 30rpx 30rpx;
		position: sticky;
		top: 0;
		z-index: 100;
		box-shadow: 0 8rpx 32rpx rgba(33, 150, 243, 0.2);
	}

	.header-content {
		display: flex;
		align-items: center;
		justify-content: space-between;
	}

	.header-left,
	.header-right {
		width: 80rpx;
		height: 80rpx;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.back-icon {
		font-size: 48rpx;
		color: #fff;
		font-weight: bold;
	}

	.clear-icon {
		font-size: 36rpx;
		color: #fff;
	}

	.title {
		font-size: 36rpx;
		font-weight: bold;
		color: #fff;
		flex: 1;
		text-align: center;
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
		border-top: 4rpx solid #2196F3;
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

	.history-list {
		display: flex;
		flex-direction: column;
		padding: 20rpx;
	}

	.history-item {
		margin-bottom: 20rpx;
	}

	.history-card {
		background-color: #fff;
		border-radius: 24rpx;
		padding: 28rpx;
		display: flex;
		align-items: center;
		box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.06);
		transition: all 0.3s ease;
	}

	.history-card:active {
		transform: scale(0.98);
		box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.08);
	}

	.history-icon-wrapper {
		width: 100rpx;
		height: 100rpx;
		border-radius: 20rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		margin-right: 20rpx;
		flex-shrink: 0;
		box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
	}

	.history-icon-wrapper.history-icon {
			background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
		}

	.history-icon {
		font-size: 48rpx;
	}

	.history-content {
		flex: 1;
		min-width: 0;
	}

	.history-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 10rpx;
		display: block;
		line-height: 1.4;
		overflow: hidden;
		text-overflow: ellipsis;
		display: -webkit-box;
		-webkit-line-clamp: 2;
		-webkit-box-orient: vertical;
	}

	.history-info {
		display: flex;
		gap: 24rpx;
	}

	.info-item {
		display: flex;
		align-items: center;
		gap: 6rpx;
	}

	.info-icon {
		font-size: 22rpx;
	}

	.info-text {
		font-size: 22rpx;
		color: #999;
	}

	.history-arrow {
		font-size: 40rpx;
		color: #ccc;
		margin-left: 16rpx;
		flex-shrink: 0;
	}
</style>