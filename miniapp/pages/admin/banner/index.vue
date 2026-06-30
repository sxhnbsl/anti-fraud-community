<template>
	<view class="banner-admin-container">
		<view class="header">
			<view class="header-content">
				<view class="header-left" @click="goBack">
					<text class="back-icon">←</text>
				</view>
				<text class="title">轮播图管理</text>
				<view class="header-right" @click="addBanner">
					<text class="add-icon">+</text>
				</view>
			</view>
		</view>
		
		<view v-if="loading" class="loading-container">
			<view class="loading-spinner"></view>
			<text class="loading-text">加载中...</text>
		</view>
		
		<view v-else-if="banners.length === 0" class="empty-state">
			<view class="empty-icon-wrapper">
				<text class="empty-icon">🖼️</text>
			</view>
			<text class="empty-text">暂无轮播图</text>
			<text class="empty-hint">点击右上角添加轮播图</text>
		</view>
		
		<view v-else class="banner-list">
			<view class="banner-item" v-for="banner in banners" :key="banner.id">
				<view class="banner-card">
					<view class="banner-image-wrapper">
						<image :src="getStaticUrl(banner.imageUrl)" mode="aspectFill" class="banner-image"></image>
					</view>
					<view class="banner-content">
						<text class="banner-title">{{banner.title}}</text>
						<view class="banner-info">
							<view class="info-item">
								<text class="info-icon">🔗</text>
								<text class="info-text">{{getLinkTypeText(banner.linkType)}}</text>
							</view>
							<view class="info-item">
								<text class="info-icon">📋</text>
								<text class="info-text">{{banner.status === 'active' ? '启用' : '禁用'}}</text>
							</view>
							<view class="info-item">
								<text class="info-icon">🔢</text>
								<text class="info-text">排序: {{banner.sortOrder}}</text>
							</view>
						</view>
					</view>
					<view class="banner-actions">
						<view class="action-button edit" @click="editBanner(banner.id)">
							<text class="action-icon">✏️</text>
						</view>
						<view class="action-button delete" @click="deleteBanner(banner.id)">
							<text class="action-icon">🗑️</text>
						</view>
						<view class="action-button status" @click="toggleStatus(banner)">
							<text class="action-icon">{{banner.status === 'active' ? '🔴' : '🟢'}}</text>
						</view>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	// 导入API接口配置
	import { API, getStaticUrl } from '@/utils/api.js'
	// 导入HTTP请求工具
	import { request } from '@/utils/request.js'

	/**
	 * 轮播图管理页面组件
	 * 
	 * 功能说明：
	 * 1. 展示所有轮播图列表
	 * 2. 支持添加、编辑、删除轮播图
	 * 3. 支持启用/禁用轮播图
	 * 4. 支持排序管理
	 * 
	 * @author 反诈骗平台开发团队
	 * @version 1.0
	 */
	export default {
		/**
		 * 组件数据
		 * 
		 * banners: 轮播图列表
		 * loading: 加载状态标识
		 */
		data() {
			return {
				banners: [],
				loading: false
			}
		},
		
		/**
		 * 页面加载时执行
		 * 功能：加载轮播图列表
		 */
		onLoad() {
			this.loadBanners()
		},
		
		/**
		 * 页面显示时执行
		 * 功能：重新加载轮播图列表
		 */
		onShow() {
			this.loadBanners()
		},
		
		methods: {
			/**
			 * 加载轮播图列表
			 * 
			 * 功能：
			 * 1. 调用后端接口获取所有轮播图
			 * 2. 处理加载状态和错误提示
			 */
			async loadBanners() {
				this.loading = true
				try {
					const res = await request(API.BANNER.LIST, 'GET')
					this.banners = res.data || []
				} catch (error) {
					console.error('加载轮播图失败', error)
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
			 * 添加轮播图
			 * 
			 * 功能：
			 * 跳转到轮播图编辑页面，用于添加新轮播图
			 */
			addBanner() {
				uni.navigateTo({
					url: '/pages/admin/banner/edit'
				})
			},
			
			/**
			 * 编辑轮播图
			 * 
			 * @param {number} id - 轮播图ID
			 * 
			 * 功能：
			 * 跳转到轮播图编辑页面，用于编辑现有轮播图
			 */
			editBanner(id) {
				uni.navigateTo({
					url: `/pages/admin/banner/edit?id=${id}`
				})
			},
			
			/**
			 * 删除轮播图
			 * 
			 * @param {number} id - 轮播图ID
			 * 
			 * 功能：
			 * 1. 显示确认对话框
			 * 2. 用户确认后删除轮播图
			 * 3. 重新加载轮播图列表
			 */
			deleteBanner(id) {
				uni.showModal({
					title: '删除轮播图',
					content: '确定要删除这个轮播图吗？',
					confirmText: '确定',
					cancelText: '取消',
					success: async (res) => {
						if (res.confirm) {
							try {
								await request(API.BANNER.DELETE(id), 'DELETE')
								uni.showToast({
									title: '删除成功',
									icon: 'success'
								})
								// 重新加载轮播图列表
								this.loadBanners()
							} catch (error) {
								console.error('删除轮播图失败', error)
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
			 * 切换轮播图状态
			 * 
			 * @param {object} banner - 轮播图对象
			 * 
			 * 功能：
			 * 1. 切换轮播图的启用/禁用状态
			 * 2. 调用后端接口更新状态
			 * 3. 重新加载轮播图列表
			 */
			toggleStatus(banner) {
				const newStatus = banner.status === 'active' ? 'inactive' : 'active'
				uni.showModal({
					title: '修改状态',
					content: `确定要${newStatus === 'active' ? '启用' : '禁用'}这个轮播图吗？`,
					confirmText: '确定',
					cancelText: '取消',
					success: async (res) => {
						if (res.confirm) {
							try {
								await request(API.BANNER.UPDATE_STATUS(banner.id), 'PUT', {
									status: newStatus
								})
								uni.showToast({
									title: '状态更新成功',
									icon: 'success'
								})
								// 重新加载轮播图列表
								this.loadBanners()
							} catch (error) {
								console.error('更新状态失败', error)
								uni.showToast({
									title: '更新失败',
									icon: 'none'
								})
							}
						}
					}
				})
			},
			
			/**
			 * 获取链接类型文本
			 * 
			 * @param {string} linkType - 链接类型
			 * @returns {string} 链接类型文本
			 */
			getLinkTypeText(linkType) {
				const linkTypeMap = {
					none: '无链接',
					knowledge: '知识',
					case: '案例',
					post: '帖子',
					webview: '网页'
				}
				return linkTypeMap[linkType] || linkType
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
	.banner-admin-container {
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

	.add-icon {
		font-size: 48rpx;
		color: #fff;
		font-weight: bold;
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

	.banner-list {
		display: flex;
		flex-direction: column;
		padding: 20rpx;
	}

	.banner-item {
		margin-bottom: 20rpx;
	}

	.banner-card {
		background-color: #fff;
		border-radius: 24rpx;
		padding: 28rpx;
		display: flex;
		align-items: flex-start;
		box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.06);
		transition: all 0.3s ease;
	}

	.banner-card:active {
		transform: scale(0.98);
		box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.08);
	}

	.banner-image-wrapper {
		width: 180rpx;
		height: 120rpx;
		border-radius: 16rpx;
		overflow: hidden;
		margin-right: 20rpx;
		flex-shrink: 0;
		box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
	}

	.banner-image {
		width: 100%;
		height: 100%;
	}

	.banner-content {
		flex: 1;
		min-width: 0;
	}

	.banner-title {
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

	.banner-info {
		display: flex;
		flex-wrap: wrap;
		gap: 16rpx;
		margin-bottom: 10rpx;
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

	.banner-actions {
		display: flex;
		flex-direction: column;
		gap: 12rpx;
		margin-left: 16rpx;
	}

	.action-button {
		width: 60rpx;
		height: 60rpx;
		border-radius: 12rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		transition: all 0.3s ease;
	}

	.action-button:active {
		transform: scale(0.92);
	}

	.action-button.edit {
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	}

	.action-button.delete {
		background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
	}

	.action-button.status {
		background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
	}

	.action-icon {
		font-size: 32rpx;
		color: #fff;
	}
</style>