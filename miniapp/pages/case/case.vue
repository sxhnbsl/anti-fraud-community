<template>
	<view class="case-container">
		<view class="header">
			<view class="header-content">
				<text class="title">真实案例</text>
				<view class="search-box" @click="showSearch">
					<text class="search-icon">🔍</text>
					<text class="search-placeholder">搜索案例</text>
				</view>
			</view>
		</view>
		
		<view class="filter-bar">
			<view class="filter-item" :class="{ active: filterType === 'all' }" @click="setFilter('all')">
				📋 全部案例
			</view>
			<view class="filter-item" :class="{ active: filterType === 'recent' }" @click="setFilter('recent')">
				🆕 最新案例
			</view>
			<view class="filter-item" :class="{ active: filterType === 'common' }" @click="setFilter('common')">
				🔥 常见案例
			</view>
		</view>
		
		<view v-if="loading" class="loading-container">
			<view class="loading-spinner"></view>
			<text class="loading-text">加载中...</text>
		</view>
		
		<view v-else-if="caseList.length === 0" class="empty-state">
			<view class="empty-icon-wrapper">
				<text class="empty-icon">📂</text>
			</view>
			<text class="empty-text">暂无案例数据</text>
			<text class="empty-hint">案例库正在更新中...</text>
		</view>
		
		<view v-else class="case-list">
			<view class="case-item" v-for="(item, index) in caseList" :key="item.id" @click="viewCaseDetail(item.id)">
				<view class="case-card">
					<view class="case-cover" v-if="item.coverImage">
						<image :src="getStaticUrl(item.coverImage)" class="cover-image" mode="aspectFill"></image>
					</view>
					<view class="case-cover no-cover" v-else>
						<text class="case-icon">⚠️</text>
					</view>
					<view class="case-content">
						<view class="case-header">
							<view class="case-type-wrapper" v-if="item.category">
								<text class="case-type">{{item.category}}</text>
							</view>
							<text class="case-time">{{formatDate(item.createdAt)}}</text>
						</view>
						<text class="case-title">{{item.title}}</text>
						<text class="case-summary">{{getSummary(item)}}</text>
						<view class="case-footer">
							<view class="case-tags" v-if="item.tags">
								<text class="case-tag"># {{item.tags}}</text>
							</view>
							<view class="view-detail">
								<text class="view-text">查看详情</text>
								<text class="view-arrow">›</text>
							</view>
						</view>
					</view>
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
	 * 真实案例页面组件
	 * 
	 * 功能说明：
	 * 1. 展示真实诈骗案例列表，支持筛选（全部、最新、常见）
	 * 2. 提供搜索功能，可搜索案例标题和内容
	 * 3. 支持下拉刷新加载最新案例
	 * 4. 展示案例卡片，包含类型、标题、摘要、标签
	 * 5. 点击案例卡片跳转到详情页面
	 * 
	 * @author 反诈骗平台开发团队
	 * @version 1.0
	 */
	export default {
		/**
		 * 组件数据
		 * 
		 * filterType: 当前筛选类型（all-全部、recent-最新、common-常见）
		 * searchKeyword: 搜索关键词
		 * caseList: 案例列表数据
		 * loading: 加载状态标识
		 */
		data() {
			return {
				filterType: 'all',
				searchKeyword: '',
				caseList: [],
				loading: false
			}
		},
		
		/**
		 * 页面加载时执行
		 * 功能：加载案例列表
		 */
		onLoad() {
			this.loadCases()
		},
		
		/**
		 * 下拉刷新时执行
		 * 功能：重新加载案例列表
		 */
		onPullDownRefresh() {
			this.loadCases()
		},
		
		methods: {
			/**
			 * 加载案例列表
			 * 
			 * 功能：
			 * 1. 根据筛选类型构建请求URL
			 * 2. 调用后端接口获取案例列表
			 * 3. 如果有搜索关键词，使用搜索接口
			 * 4. 处理加载状态和错误提示
			 * 
			 * 错误处理：
			 * - 捕获网络请求错误
			 * - 显示加载失败提示
			 * - finally块确保loading状态和下拉刷新状态被重置
			 */
			async loadCases() {
				this.loading = true
				try {
					// 如果有搜索关键词，使用搜索接口
					if (this.searchKeyword) {
						const url = API.CASE.SEARCH
						const res = await request(url, 'GET', { keyword: this.searchKeyword })
						this.caseList = res.data || []
					} else if (this.filterType === 'common') {
						// 如果选择了"常见案例"，使用分类接口
						const url = API.CASE.CATEGORY('常见')
						const res = await request(url, 'GET')
						this.caseList = res.data || []
					} else {
						// 默认使用列表接口
						const url = API.CASE.LIST
						const res = await request(url, 'GET')
						this.caseList = res.data || []
					}
					
					// 调试：查看案例数据结构
					console.log('案例数据:', this.caseList)
				} catch (error) {
					console.error('加载案例失败', error)
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
			 * 显示搜索框
			 * 
			 * 功能：
			 * 1. 显示模态对话框，允许用户输入搜索关键词
			 * 2. 用户确认后执行搜索
			 */
			showSearch() {
				uni.showModal({
					title: '搜索案例',
					editable: true,
					placeholderText: '搜索案例标题或内容',
					success: (res) => {
						if (res.confirm && res.content) {
							this.searchKeyword = res.content
							this.filterType = 'all'
							this.handleSearch()
						}
					}
				})
			},
			
			/**
			 * 处理搜索
			 * 
			 * 功能：
			 * 1. 验证搜索关键词是否为空
			 * 2. 加载搜索结果
			 * 3. 显示搜索结果数量或未找到提示
			 */
			async handleSearch() {
				// 验证搜索关键词
				if (!this.searchKeyword.trim()) {
					uni.showToast({
						title: '请输入搜索关键词',
						icon: 'none'
					})
					return
				}
				
				// 加载搜索结果
				await this.loadCases()
				
				// 显示搜索结果
				if (this.caseList.length === 0) {
					uni.showToast({
						title: '未找到相关内容',
						icon: 'none'
					})
				} else {
					uni.showToast({
						title: `找到 ${this.caseList.length} 条结果`,
						icon: 'success'
					})
				}
			},
			
			/**
			 * 设置筛选类型
			 * 
			 * @param {string} type - 筛选类型（all、recent、common）
			 * 
			 * 功能：
			 * 1. 更新筛选类型
			 * 2. 清空搜索关键词
			 * 3. 重新加载案例列表
			 */
			setFilter(type) {
				this.filterType = type
				this.searchKeyword = ''
				this.loadCases()
			},
			
			/**
			 * 查看案例详情
			 * 
			 * @param {number} id - 案例ID
			 * 
			 * 功能：
			 * 跳转到案例详情页面，并传递案例ID
			 */
			viewCaseDetail(id) {
				uni.navigateTo({
					url: `/pages/case/detail/detail?id=${id}`
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
			 * 获取静态资源的完整URL
			 * 
			 * @param {string} url - 资源URL
			 * @returns {string} 完整的资源URL
			 */
			getStaticUrl(url) {
				if (!url) {
					return ''
				}
				// 如果是绝对路径，直接返回
				if (url.startsWith('http://') || url.startsWith('https://')) {
					return url
				}
				// 如果是相对路径，添加基础URL
				const baseUrl = 'http://localhost:8080'
				return url.startsWith('/') ? `${baseUrl}${url}` : `${baseUrl}/${url}`
			},
			getSummary(item) {
				// 优先使用摘要字段
				if (item.summary && item.summary.trim()) {
					return item.summary
				}
				
				// 如果没有摘要，从内容中提取
				if (item.content) {
					// 移除HTML标签（如果有的话）
					let content = item.content.replace(/<[^>]*>/g, '')
					// 移除特殊字符
					content = content.replace(/【[^】]+】/g, '')
					// 截取前120个字符
					if (content.length > 120) {
						return content.substring(0, 120) + '...'
					}
					return content
				}
				
				return '暂无摘要'
			}
		}
	}
</script>

<style scoped>
	.case-container {
		min-height: 100vh;
		background: linear-gradient(180deg, #f3e5f5 0%, #f5f5f5 100%);
		position: relative;
	}

	.header {
		background: linear-gradient(135deg, #9C27B0 0%, #7B1FA2 100%);
		padding: 50rpx 30rpx 30rpx;
		position: sticky;
		top: 0;
		z-index: 100;
		box-shadow: 0 8rpx 32rpx rgba(156, 39, 176, 0.2);
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

	.filter-bar {
		display: flex;
		justify-content: space-around;
		align-items: center;
		background-color: #fff;
		padding: 16rpx 20rpx;
		box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.06);
	}

	.filter-item {
		height: 60rpx;
		line-height: 60rpx;
		padding: 0 24rpx;
		border-radius: 30rpx;
		font-size: 24rpx;
		color: #666;
		background-color: #f5f5f5;
		transition: all 0.3s ease;
	}

	.filter-item.active {
		background: linear-gradient(135deg, #9C27B0 0%, #7B1FA2 100%);
		color: #fff;
		box-shadow: 0 4rpx 12rpx rgba(156, 39, 176, 0.3);
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
		border-top: 4rpx solid #9C27B0;
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
		background: linear-gradient(135deg, #f3e5f5 0%, #e1bee7 100%);
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

	.case-list {
		display: flex;
		flex-direction: column;
		padding: 20rpx;
	}

	.case-item {
		margin-bottom: 20rpx;
	}

	.case-card {
		background-color: #fff;
		border-radius: 20rpx;
		padding: 20rpx;
		display: flex;
		align-items: flex-start;
		box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.08);
		transition: all 0.3s ease;
		margin-bottom: 20rpx;
	}

	.case-card:active {
		transform: scale(0.98);
		box-shadow: 0 1rpx 8rpx rgba(0, 0, 0, 0.1);
	}

	.case-cover {
		width: 120rpx;
		height: 160rpx;
		border-radius: 12rpx;
		overflow: hidden;
		margin-right: 16rpx;
		flex-shrink: 0;
		box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
	}

	.cover-image {
		width: 100%;
		height: 100%;
		object-fit: cover;
	}

	.case-cover.no-cover {
		background: linear-gradient(135deg, #9C27B0 0%, #7B1FA2 100%);
		display: flex;
		align-items: center;
		justify-content: center;
		box-shadow: 0 2rpx 8rpx rgba(156, 39, 176, 0.3);
	}

	.case-icon {
		font-size: 36rpx;
		color: #fff;
	}

	.case-content {
		flex: 1;
		min-width: 0;
	}

	.case-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 8rpx;
	}

	.case-type-wrapper {
		background: linear-gradient(135deg, #9C27B0 0%, #7B1FA2 100%);
		padding: 4rpx 12rpx;
		border-radius: 10rpx;
	}

	.case-type {
		font-size: 20rpx;
		color: #fff;
		font-weight: 500;
	}

	.case-time {
		font-size: 20rpx;
		color: #999;
	}

	.case-title {
		font-size: 28rpx;
		font-weight: 600;
		color: #333;
		margin-bottom: 8rpx;
		display: block;
		line-height: 1.4;
	}

	.case-summary {
		font-size: 24rpx;
		color: #666;
		line-height: 36rpx;
		overflow: hidden;
		text-overflow: ellipsis;
		display: -webkit-box;
		-webkit-line-clamp: 2;
		-webkit-box-orient: vertical;
		margin-bottom: 12rpx;
		display: block;
	}

	.case-footer {
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.case-tags {
		flex: 1;
	}

	.case-tag {
		font-size: 20rpx;
		color: #999;
	}

	.view-detail {
		display: flex;
		align-items: center;
		gap: 4rpx;
	}

	.view-text {
		font-size: 22rpx;
		color: #9C27B0;
		font-weight: 500;
	}

	.view-arrow {
		font-size: 24rpx;
		color: #9C27B0;
		font-weight: bold;
	}
</style>
