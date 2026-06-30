<template>
	<view class="collections-container">
		<view v-if="loading" class="loading-state">
			<text class="loading-text">加载中...</text>
		</view>
		
		<view v-else-if="collections.length > 0" class="collections-list">
			<view class="collection-item card" v-for="(item, index) in collections" :key="item.id" @click="viewDetail(item)">
				<view class="collection-header">
					<view class="collection-type">
						<text class="type-icon">{{item.type === 'knowledge' ? '📖' : '📂'}}</text>
						<text class="type-text">{{item.type === 'knowledge' ? '知识库' : '案例'}}</text>
					</view>
					<view class="delete-btn" @click.stop="deleteCollection(item.id, index)">
						<text class="delete-icon">🗑️</text>
					</view>
				</view>
				<text class="collection-title">{{item.title}}</text>
				<text class="collection-summary">{{item.summary || item.content}}</text>
				<view class="collection-info">
					<text class="collect-time">📅 {{formatDate(item.createdAt)}}</text>
				</view>
			</view>
		</view>
		
		<view v-if="!loading && collections.length === 0" class="empty-state card">
			<text class="empty-icon">⭐</text>
			<text class="empty-text">还没有收藏任何内容</text>
			<text class="empty-hint">快去知识库和案例页面收藏吧</text>
		</view>
	</view>
</template>

<script>
	// 导入HTTP请求工具
	import { request } from '@/utils/request.js'
	// 导入API接口配置
	import { API } from '@/utils/api.js'

	/**
	 * 我的收藏页面组件
	 * 
	 * 功能说明：
	 * 1. 展示用户收藏的所有内容（知识库和案例）
	 * 2. 支持查看收藏内容详情
	 * 3. 支持取消收藏操作
	 * 4. 显示收藏时间
	 * 5. 空状态提示
	 * 
	 * @author 反诈骗平台开发团队
	 * @version 1.0
	 */
	export default {
		/**
		 * 组件数据
		 * 
		 * collections: 收藏列表数据
		 * - id: 收藏ID
		 * - type: 收藏类型（knowledge-知识库、case-案例）
		 * - targetId: 目标内容ID
		 * - title: 标题
		 * - summary: 摘要
		 * - content: 内容
		 * - createdAt: 收藏时间
		 * 
		 * loading: 加载状态标识
		 */
		data() {
			return {
				collections: [],
				loading: false
			}
		},
		/**
		 * 页面加载时执行
		 * 功能：加载收藏列表
		 */
		onLoad() {
			this.loadCollections()
		},
		/**
		 * 页面显示时执行
		 * 功能：重新加载收藏列表，确保数据最新
		 */
		onShow() {
			this.loadCollections()
		},
		methods: {
			/**
			 * 加载收藏列表
			 * 
			 * 功能：
			 * 1. 调用后端接口获取收藏列表
			 * 2. 处理加载状态和错误提示
			 * 
			 * 错误处理：
			 * - 捕获网络请求错误
			 * - 显示加载失败提示
			 * - finally块确保loading状态被重置
			 */
			async loadCollections() {
				this.loading = true
				try {
					const res = await request(API.COLLECTION.LIST, 'GET')
					this.collections = res || []
				} catch (error) {
					console.error('加载收藏失败', error)
					uni.showToast({
						title: '加载失败',
						icon: 'none'
					})
				} finally {
					this.loading = false
				}
			},
			/**
			 * 查看收藏内容详情
			 * 
			 * @param {Object} item - 收藏项
			 * 
			 * 功能：
			 * 根据收藏类型跳转到对应的详情页面
			 */
			viewDetail(item) {
				if (item.type === 'knowledge') {
					uni.navigateTo({
						url: '/pages/knowledge/detail/detail?id=' + item.targetId
					})
				} else if (item.type === 'case') {
					uni.navigateTo({
						url: '/pages/case/detail/detail?id=' + item.targetId
					})
				}
			},
			/**
			 * 取消收藏
			 * 
			 * @param {number} id - 收藏ID
			 * @param {number} index - 收藏项在列表中的索引
			 * 
			 * 功能：
			 * 1. 显示确认对话框
			 * 2. 用户确认后调用后端接口删除收藏
			 * 3. 从本地列表中移除该收藏项
			 * 
			 * 错误处理：
			 * - 捕获网络请求错误
			 * - 显示操作失败提示
			 */
			deleteCollection(id, index) {
				uni.showModal({
					title: '确认取消收藏',
					content: '确定要取消收藏这篇内容吗？',
					success: async (res) => {
						if (res.confirm) {
							try {
								await request(API.COLLECTION.DELETE(id), 'DELETE')
								uni.showToast({
									title: '已取消收藏',
									icon: 'success'
								})
								this.collections.splice(index, 1)
							} catch (error) {
								console.error('取消收藏失败', error)
								uni.showToast({
									title: '操作失败',
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
			 * 将日期格式化为YYYY-MM-DD格式
			 */
			formatDate(dateStr) {
				if (!dateStr) return ''
				const date = new Date(dateStr)
				const year = date.getFullYear()
				const month = String(date.getMonth() + 1).padStart(2, '0')
				const day = String(date.getDate()).padStart(2, '0')
				return `${year}-${month}-${day}`
			}
		}
	}
</script>

<style scoped>
.collections-container {
	padding: 20rpx 20rpx 0;
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

.collections-list {
	padding: 20rpx;
}

.collection-item {
	margin-bottom: 20rpx;
}

.collection-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 12rpx;
}

.collection-type {
	display: flex;
	align-items: center;
	gap: 8rpx;
}

.type-icon {
	font-size: 28rpx;
}

.type-text {
	font-size: 24rpx;
	color: #667eea;
	font-weight: 500;
}

.delete-btn {
	padding: 8rpx;
	border-radius: 8rpx;
	transition: all 0.2s ease;
}

.delete-btn:active {
	background-color: #f5f5f5;
}

.delete-icon {
	font-size: 32rpx;
}

.collection-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 12rpx;
	display: block;
	line-height: 1.4;
}

.collection-summary {
	font-size: 26rpx;
	color: #666;
	line-height: 1.6;
	margin-bottom: 12rpx;
	display: block;
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
	overflow: hidden;
	text-overflow: ellipsis;
}

.collection-info {
	display: flex;
	align-items: center;
}

.collect-time {
	font-size: 24rpx;
	color: #999;
}

.empty-state {
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 120rpx 40rpx;
	margin: 20rpx;
}

.empty-icon {
	font-size: 100rpx;
	margin-bottom: 20rpx;
}

.empty-text {
	font-size: 30rpx;
	color: #666;
	margin-bottom: 12rpx;
	font-weight: 500;
}

.empty-hint {
	font-size: 26rpx;
	color: #999;
	text-align: center;
	line-height: 1.6;
}
</style>
