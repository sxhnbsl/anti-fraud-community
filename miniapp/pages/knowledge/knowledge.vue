<template>
	<view class="knowledge-container">
		<view class="header">
			<view class="header-content">
				<text class="title">防诈骗知识库</text>
				<view class="search-container">
					<view class="search-box" :class="{ 'active': searchActive }">
						<text class="search-icon">🔍</text>
						<input 
							v-model="searchKeyword" 
							class="search-input" 
							placeholder="搜索防诈骗知识" 
							placeholder-style="color: #999;" 
							@focus="searchActive = true"
							@blur="searchActive = false"
							@confirm="handleSearch"
						/>
						<text v-if="searchKeyword" class="clear-icon" @click="clearSearch">✕</text>
						<button class="search-button" @click="handleSearch">搜索</button>
					</view>
				</view>
			</view>
		</view>
		
		<scroll-view scroll-x class="category-scroll">
			<view class="category-tabs">
				<view class="category-item" v-for="(category, index) in categories" :key="index" :class="{ active: currentCategory === index }" @click="selectCategory(index)">
					{{category}}
				</view>
			</view>
		</scroll-view>
		
		<view v-if="loading" class="loading-container">
			<view class="loading-spinner"></view>
			<text class="loading-text">加载中...</text>
		</view>
		
		<view v-else-if="knowledgeList.length === 0" class="empty-state">
			<view class="empty-icon-wrapper">
				<text class="empty-icon">📚</text>
			</view>
			<text class="empty-text">暂无知识数据</text>
			<text class="empty-hint">知识库正在更新中...</text>
		</view>
		
		<view v-else class="knowledge-list">
			<view class="knowledge-item" v-for="(item, index) in knowledgeList" :key="item.id" @click="viewKnowledgeDetail(item.id)">
				<view class="knowledge-card">
					<view class="knowledge-cover" v-if="item.coverImage">
						<image :src="getStaticUrl(item.coverImage)" class="cover-image" mode="aspectFill"></image>
					</view>
					<view class="knowledge-cover no-cover" v-else>
						<text class="knowledge-icon">📖</text>
					</view>
					<view class="knowledge-content">
					<text class="knowledge-title">{{item.title}}</text>
					<text class="knowledge-summary">{{getSummary(item)}}</text>
					<view class="knowledge-info">
						<view class="info-item">
							<text class="info-icon">📅</text>
							<text class="info-text">{{formatDate(item.createdAt)}}</text>
						</view>
					</view>
				</view>
					<view class="knowledge-arrow">›</view>
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
	// 导入静态资源URL处理工具
	import { getStaticUrl } from '@/utils/staticUrl.js'

	/**
	 * 防诈骗知识库页面组件
	 * 
	 * 功能说明：
	 * 1. 展示防诈骗知识列表，支持分类筛选
	 * 2. 提供搜索功能，可搜索知识标题和内容
	 * 3. 支持下拉刷新加载最新知识
	 * 4. 展示知识卡片，包含标题、摘要、发布时间
	 * 5. 点击知识卡片跳转到详情页面
	 * 
	 * @author 反诈骗平台开发团队
	 * @version 1.0
	 */
	export default {
		/**
		 * 组件数据
		 * 
		 * categories: 知识分类列表
		 * currentCategory: 当前选中的分类索引
		 * searchKeyword: 搜索关键词
		 * knowledgeList: 知识列表数据
		 * loading: 加载状态标识
		 */
		data() {
			return {
				categories: ['📋 全部', '📱 电信诈骗', '🌐 网络诈骗', '💬 短信诈骗', '📞 诈骗电话', '🎣 钓鱼网站', '💰 投资理财诈骗', '💼 兼职刷单诈骗', '💳 贷款诈骗', '👴 老年人诈骗'],
				currentCategory: 0,
				searchKeyword: '',
				searchActive: false,
				knowledgeList: [],
				loading: false
			}
		},
		
		/**
		 * 页面加载时执行
		 * 功能：加载知识列表
		 */
		onLoad() {
			this.loadKnowledge()
		},
		
		/**
		 * 下拉刷新时执行
		 * 功能：重新加载知识列表
		 */
		onPullDownRefresh() {
			this.loadKnowledge()
		},
		
		methods: {
			/**
			 * 加载知识列表
			 * 
			 * 功能：
			 * 1. 根据当前分类或搜索关键词构建请求URL
			 * 2. 调用后端接口获取知识列表
			 * 3. 处理加载状态和错误提示
			 * 
			 * 错误处理：
			 * - 捕获网络请求错误
			 * - 显示加载失败提示
			 * - finally块确保loading状态和下拉刷新状态被重置
			 */
			async loadKnowledge() {
				this.loading = true
				try {
					// 如果有搜索关键词，使用搜索接口
					if (this.searchKeyword) {
						const url = API.KNOWLEDGE.SEARCH
						const res = await request(url, 'GET', { keyword: this.searchKeyword })
						this.knowledgeList = res.data || []
					} else if (this.currentCategory > 0) {
						// 如果选择了分类，使用分类接口
						// 提取分类名称，移除表情符号（匹配1-2个Unicode表情字符）
						const category = this.categories[this.currentCategory].replace(/^[\uD83C-\uDBFF\uDC00-\uDFFF\u2600-\u27BF\s]+/, '').trim()
						const url = API.KNOWLEDGE.CATEGORY(category)
						const res = await request(url, 'GET')
						this.knowledgeList = res.data || []
					} else {
						// 默认使用列表接口
						const url = API.KNOWLEDGE.LIST
						const res = await request(url, 'GET')
						this.knowledgeList = res.data || []
					}
				} catch (error) {
					console.error('加载知识失败', error)
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
			 * 选择分类
			 * 
			 * @param {number} index - 分类索引
			 * 
			 * 功能：
			 * 1. 更新当前选中的分类
			 * 2. 清空搜索关键词
			 * 3. 重新加载知识列表
			 */
			selectCategory(index) {
				this.currentCategory = index
				this.searchKeyword = ''
				this.loadKnowledge()
			},
			
			/**
			 * 清除搜索
			 * 
			 * 功能：
			 * 1. 清空搜索关键词
			 * 2. 重置分类为"全部"
			 * 3. 重新加载知识列表
			 */
			clearSearch() {
				this.searchKeyword = ''
				this.currentCategory = 0
				this.loadKnowledge()
			},
			
			/**
			 * 处理搜索
			 * 
			 * 功能：
			 * 1. 验证搜索关键词是否为空
			 * 2. 重置分类为"全部"
			 * 3. 加载搜索结果
			 * 4. 显示搜索结果数量或未找到提示
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
				
				// 重置分类为"全部"
				this.currentCategory = 0
				
				// 加载搜索结果
				await this.loadKnowledge()
				
				// 显示搜索结果
				if (this.knowledgeList.length === 0) {
					uni.showToast({
						title: '未找到相关内容',
						icon: 'none'
					})
				} else {
					uni.showToast({
						title: `找到 ${this.knowledgeList.length} 条结果`,
						icon: 'success'
					})
				}
			},
			
			/**
			 * 查看知识详情
			 * 
			 * @param {number} id - 知识ID
			 * 
			 * 功能：
			 * 跳转到知识详情页面，并传递知识ID
			 */
			viewKnowledgeDetail(id) {
				uni.navigateTo({
					url: `/pages/knowledge/detail/detail?id=${id}`
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
					// 截取前100个字符
					if (content.length > 100) {
						return content.substring(0, 100) + '...'
					}
					return content
				}
				
				return '暂无摘要'
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
			}
		}
	}
</script>

<style scoped>
	.knowledge-container {
		min-height: 100vh;
		background: linear-gradient(180deg, #e3f2fd 0%, #f5f5f5 100%);
		position: relative;
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
		flex-direction: column;
		gap: 20rpx;
	}

	.title {
		font-size: 40rpx;
		font-weight: bold;
		color: #fff;
	}

	.search-container {
		width: 100%;
	}

	.search-box {
		height: 70rpx;
		background: rgba(255, 255, 255, 0.95);
		border-radius: 35rpx;
		padding: 0 24rpx;
		display: flex;
		align-items: center;
		box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.1);
		transition: all 0.3s ease;
	}

	.search-box.active {
		box-shadow: 0 6rpx 24rpx rgba(0, 0, 0, 0.15);
		background: rgba(255, 255, 255, 0.98);
	}

	.search-icon {
		font-size: 32rpx;
		margin-right: 12rpx;
		color: #666;
	}

	.search-input {
		flex: 1;
		height: 100%;
		font-size: 28rpx;
		color: #333;
		padding: 0 10rpx;
	}

	.clear-icon {
		font-size: 24rpx;
		color: #999;
		margin: 0 10rpx;
		padding: 6rpx;
		border-radius: 50%;
		background: #f0f0f0;
		transition: all 0.2s ease;
	}

	.clear-icon:active {
		background: #e0e0e0;
		transform: scale(0.9);
	}

	.search-button {
		height: 50rpx;
		padding: 0 24rpx;
		background: linear-gradient(135deg, #2196F3 0%, #1976D2 100%);
		color: #fff;
		font-size: 24rpx;
		font-weight: 500;
		border-radius: 25rpx;
		border: none;
		margin-left: 10rpx;
		transition: all 0.3s ease;
	}

	.search-button:active {
		transform: scale(0.95);
		box-shadow: 0 2rpx 8rpx rgba(33, 150, 243, 0.4);
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
		padding: 0 24rpx;
		margin-right: 16rpx;
		border-radius: 30rpx;
		font-size: 24rpx;
		color: #666;
		background-color: #f5f5f5;
		transition: all 0.3s ease;
		flex-shrink: 0;
	}

	.category-item.active {
		background: linear-gradient(135deg, #2196F3 0%, #1976D2 100%);
		color: #fff;
		box-shadow: 0 4rpx 12rpx rgba(33, 150, 243, 0.3);
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

	.knowledge-list {
		display: flex;
		flex-direction: column;
		padding: 20rpx;
	}

	.knowledge-item {
		margin-bottom: 20rpx;
	}

	.knowledge-card {
		background-color: #fff;
		border-radius: 20rpx;
		padding: 20rpx;
		display: flex;
		align-items: flex-start;
		box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.08);
		transition: all 0.3s ease;
	}

	.knowledge-card:active {
		transform: scale(0.98);
		box-shadow: 0 1rpx 8rpx rgba(0, 0, 0, 0.1);
	}

	.knowledge-cover {
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

	.knowledge-cover.no-cover {
		background: linear-gradient(135deg, #2196F3 0%, #1976D2 100%);
		display: flex;
		align-items: center;
		justify-content: center;
		box-shadow: 0 2rpx 8rpx rgba(33, 150, 243, 0.3);
	}

	.knowledge-icon {
		font-size: 40rpx;
		color: #fff;
	}

	.knowledge-content {
		flex: 1;
		min-width: 0;
	}

	.knowledge-title {
		font-size: 28rpx;
		font-weight: 600;
		color: #333;
		margin-bottom: 8rpx;
		display: block;
		line-height: 1.4;
	}

	.knowledge-summary {
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

	.knowledge-info {
		display: flex;
		gap: 20rpx;
		align-items: center;
	}

	.info-item {
		display: flex;
		align-items: center;
		gap: 4rpx;
	}

	.info-icon {
		font-size: 20rpx;
		color: #999;
	}

	.info-text {
		font-size: 20rpx;
		color: #999;
	}

	.knowledge-arrow {
		font-size: 40rpx;
		color: #ccc;
		margin-left: 16rpx;
		flex-shrink: 0;
	}
</style>
