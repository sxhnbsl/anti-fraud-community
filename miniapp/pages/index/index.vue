<template>
	<view class="home-container">
		<!-- 欢迎卡片 -->
		<view class="welcome-card">
			<view class="welcome-content">
				<view class="welcome-text">
					<text class="welcome-title">您好！</text>
					<text class="welcome-desc">欢迎来到防诈骗科普平台</text>
				</view>
				<view class="welcome-badge">
					<text class="badge-icon">🛡️</text>
				</view>
			</view>
		</view>
		
		<!-- 轮播图 -->
		<swiper class="banner" indicator-dots indicator-active-color="#667eea" autoplay interval="4000" circular v-if="banners.length > 0">
			<swiper-item v-for="banner in banners" :key="banner.id" @click="handleBannerClick(banner)">
				<view class="banner-item">
					<image class="banner-image" :src="banner.imageUrl" mode="aspectFill"></image>
					<view class="banner-content">
						<text class="banner-title">{{banner.title}}</text>
					</view>
				</view>
			</swiper-item>
		</swiper>
		
		<!-- 功能入口 -->
		<view class="function-section">
			<view class="function-grid">
				<view class="function-item" @click="navigateTo('test')">
					<view class="icon test">
						<text class="icon-text">✏️</text>
					</view>
					<text class="function-name">防诈骗测试</text>
				</view>
				<view class="function-item" @click="navigateTo('report')">
					<view class="icon report">
						<text class="icon-text">🚨</text>
					</view>
					<text class="function-name">一键举报</text>
				</view>
				<view class="function-item" @click="navigateTo('ai')">
					<view class="icon ai">
						<text class="icon-text">🤖</text>
					</view>
					<text class="function-name">AI防诈骗助手</text>
				</view>
				<view class="function-item" @click="navigateTo('checkin')">
					<view class="icon check-in">
						<text class="icon-text">📅</text>
					</view>
					<text class="function-name">每日签到</text>
				</view>
			</view>
		</view>
		
		<!-- 快速预警 -->
		<view class="warning-section">
			<view class="warning-card">
				<view class="warning-header">
					<text class="warning-icon">🚨</text>
					<text class="warning-title">紧急预警</text>
				</view>
				<view class="warning-content">
					<text class="warning-subtitle">近期高发：冒充客服诈骗</text>
					<text class="warning-desc">据统计，本周冒充客服诈骗案件上升30%，请务必核实对方身份！</text>
				</view>
			</view>
		</view>
		
		<!-- 最新资讯 -->
		<view class="news-section">
			<view class="section-header">
				<view class="section-title">
					<view class="title-dot"></view>
					<text class="title">最新防诈骗资讯</text>
				</view>
				<text class="more" @click="navigateTo('knowledge')">查看更多</text>
			</view>
			<view class="news-list">
				<view class="news-item" v-for="news in newsList" :key="news.id" @click="viewNewsDetail(news.id)">
					<view class="news-cover">
						<image v-if="news.coverImage" :src="news.coverImage" class="news-image" mode="aspectFill"></image>
						<text v-else class="news-emoji">📰</text>
					</view>
					<view class="news-content">
						<text class="news-title">{{news.title}}</text>
						<view class="news-info">
							<text class="news-time">{{news.time}}</text>
							<view class="news-tag">{{news.type}}</view>
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
	 * 首页组件
	 * 
	 * 功能说明：
	 * 1. 展示欢迎卡片，显示用户问候语
	 * 2. 展示防诈骗知识轮播图，自动播放
	 * 3. 提供功能入口：知识库、案例、测试、举报
	 * 4. 展示紧急预警信息，提醒用户注意最新诈骗手法
	 * 5. 展示最新防诈骗资讯列表
	 * 6. 支持下拉刷新加载最新资讯
	 * 
	 * @author 反诈骗平台开发团队
	 * @version 1.0
	 */
	export default {
		/**
		 * 组件数据
		 * 
		 * banners: 轮播图列表数据
		 * - id: 轮播图ID
		 * - title: 轮播图标题
		 * - imageUrl: 图片URL
		 * - linkUrl: 跳转链接
		 * - linkType: 链接类型
		 * - linkId: 关联内容ID
		 * 
		 * newsList: 资讯列表数据
		 * - id: 资讯ID
		 * - title: 资讯标题
		 * - time: 发布时间
		 * - type: 资讯类型
		 * 
		 * loading: 加载状态标识
		 */
		data() {
			return {
				banners: [
					{
						id: 1,
						title: '警惕电信诈骗',
						imageUrl: '/static/banner/banner1.png',
						linkType: 'knowledge',
						linkId: 1
					},
					{
						id: 2,
						title: '刷单返利都是骗局',
						imageUrl: '/static/banner/banner2.png',
						linkType: 'knowledge',
						linkId: 2
					},
					{
						id: 3,
						title: '保护个人信息',
						imageUrl: '/static/banner/banner3.png',
						linkType: 'knowledge',
						linkId: 3
					},
					{
						id: 4,
						title: '遇到诈骗及时报警',
						imageUrl: '/static/banner/banner4.png',
						linkType: 'report',
						linkId: null
					},
					{
						id: 5,
						title: '学习防诈骗知识',
						imageUrl: '/static/banner/banner5.png',
						linkType: 'knowledge',
						linkId: 4
					}
				],
				newsList: [],
				loading: false
			}
		},
		
		/**
		 * 页面加载时执行
		 * 功能：加载最新资讯列表和轮播图
		 */
		onLoad() {
			this.loadNews()
			this.loadBanners()
		},
		
		/**
		 * 下拉刷新时执行
		 * 功能：重新加载最新资讯列表和轮播图
		 */
		onPullDownRefresh() {
			this.loadNews()
			this.loadBanners()
		},
		
		methods: {
			/**
		 * 处理轮播图点击事件
		 * 
		 * @param {object} banner - 轮播图对象
		 * 
		 * 功能：
		 * 根据轮播图的linkType和linkId跳转到对应页面
		 * - none: 无操作
		 * - knowledge: 跳转到知识详情页
		 * - case: 跳转到案例详情页
		 * - post: 跳转到帖子详情页
		 * - webview: 跳转到网页
		 */
		handleBannerClick(banner) {
			if (!banner.linkType || banner.linkType === 'none') {
				return
			}

			switch (banner.linkType) {
				case 'knowledge':
					if (banner.linkId) {
						uni.navigateTo({
							url: `/pages/knowledge/detail/detail?id=${banner.linkId}`
						})
					}
					break
				case 'case':
					if (banner.linkId) {
						uni.navigateTo({
							url: `/pages/case/detail/detail?id=${banner.linkId}`
						})
					}
					break
				case 'post':
					if (banner.linkId) {
						uni.navigateTo({
							url: `/pages/post/detail/detail?id=${banner.linkId}`
						})
					}
					break
				case 'webview':
					if (banner.linkUrl) {
						uni.navigateTo({
							url: `/pages/webview/webview?url=${encodeURIComponent(banner.linkUrl)}`
						})
					}
					break
			}
		},
		
		/**
		 * 加载轮播图
		 * 
		 * 功能：
		 * 1. 调用后端接口获取启用的轮播图
		 * 2. 处理加载状态和错误提示
		 * 
		 * 错误处理：
		 * - 捕获网络请求错误
		 * - 显示加载失败提示
		 */
		async loadBanners() {
			try {
				const res = await request(API.BANNER.ACTIVE, 'GET')
				this.banners = (res.data || []).map(banner => ({
					...banner,
					imageUrl: getStaticUrl(banner.imageUrl)
				}))
			} catch (error) {
				console.error('加载轮播图失败', error)
				// 加载失败时使用默认轮播图
				this.banners = [
					{
						id: 1,
						title: '警惕电信诈骗',
						imageUrl: '/static/banner/banner1.png',
						linkType: 'knowledge',
						linkId: 1
					},
					{
						id: 2,
						title: '刷单返利都是骗局',
						imageUrl: '/static/banner/banner2.png',
						linkType: 'knowledge',
						linkId: 2
					},
					{
						id: 3,
						title: '保护个人信息',
						imageUrl: '/static/banner/banner3.png',
						linkType: 'knowledge',
						linkId: 3
					},
					{
						id: 4,
						title: '遇到诈骗及时报警',
						imageUrl: '/static/banner/banner4.png',
						linkType: 'report',
						linkId: null
					},
					{
						id: 5,
						title: '学习防诈骗知识',
						imageUrl: '/static/banner/banner5.png',
						linkType: 'knowledge',
						linkId: 4
					}
				]
			}
		},

			/**
			 * 加载资讯列表
			 * 
			 * 功能：
			 * 1. 调用后端接口获取知识列表
			 * 2. 只取前4条作为首页展示
			 * 3. 格式化发布时间
			 * 4. 处理加载状态和错误提示
			 * 
			 * 错误处理：
			 * - 捕获网络请求错误
			 * - 显示加载失败提示
			 * - finally块确保loading状态和下拉刷新状态被重置
			 */
			async loadNews() {
				this.loading = true
				try {
					// 调用知识列表接口
					const res = await request(API.KNOWLEDGE.LIST, 'GET')
					
					// 处理返回数据，只取前3条
				this.newsList = (res.data || []).slice(0, 3).map(item => ({
					id: item.id,
					title: item.title,
					time: this.formatDate(item.createdAt),
					type: item.category || '防诈骗知识',
					coverImage: getStaticUrl(item.coverImage)
				}))
				} catch (error) {
					console.error('加载资讯失败', error)
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
			 * 格式化日期
			 * 
			 * @param {string} dateStr - 日期字符串
			 * @returns {string} 格式化后的日期，格式：YYYY-MM-DD
			 * 
			 * 功能：
			 * 将日期字符串转换为 YYYY-MM-DD 格式
			 */
			formatDate(dateStr) {
				if (!dateStr) return ''
				const date = new Date(dateStr)
				const year = date.getFullYear()
				const month = String(date.getMonth() + 1).padStart(2, '0')
				const day = String(date.getDate()).padStart(2, '0')
				return `${year}-${month}-${day}`
			},
				
				/**
			 * 跳转到指定页面
			 * 
			 * @param {string} page - 页面名称
			 * 
			 * 功能：
			 * 根据页面名称跳转到对应的功能页面
			 * 支持的页面：knowledge（知识库）、case（案例）、test（测试）、report（举报）、ai（AI助手）、checkIn（每日签到）
			 */
			navigateTo(page) {
				// 对于tabBar页面，使用switchTab
				if (page === 'knowledge' || page === 'case' || page === 'community' || page === 'profile') {
					uni.switchTab({
						url: `/pages/${page}/${page}`
					});
				} else if (page === 'ai') {
					uni.navigateTo({
						url: `/pages/ai/chat/chat`
					});
				} else {
					uni.navigateTo({
						url: `/pages/${page}/${page}`
					});
				}
			},
			
			/**
			 * 查看资讯详情
			 * 
			 * @param {number} id - 资讯ID
			 * 
			 * 功能：
			 * 跳转到知识详情页面，并传递资讯ID
			 */
			viewNewsDetail(id) {
				// 跳转到知识详情页
				uni.navigateTo({
					url: `/pages/knowledge/detail/detail?id=${id}`
				});
			},
			
			/**
			 * 退出登录
			 * 
			 * 功能：
			 * 1. 显示确认对话框
			 * 2. 用户确认后清除登录状态
			 * 3. 跳转到登录页面
			 */
			logout() {
				// 显示确认对话框
				uni.showModal({
					title: '退出登录',
					content: '确定要退出登录吗？',
					confirmText: '确定',
					cancelText: '取消',
					success: (res) => {
						if (res.confirm) {
							// 清除登录状态
							uni.removeStorageSync('isLoggedIn');
							uni.removeStorageSync('username');
							// 跳转到登录页面
							uni.reLaunch({
								url: '/pages/login/login'
							});
						}
					}
				});
			}
		}
	}
</script>

<style scoped>
	.home-container {
		min-height: 100vh;
		background: linear-gradient(180deg, #e3f2fd 0%, #f5f5f5 100%);
		padding-bottom: 30rpx;
		padding-top: env(safe-area-inset-top);
		box-sizing: border-box;
	}

	/* 安全区域适配 */
	@supports (padding: max(0px)) {
		.home-container {
			padding-top: max(env(safe-area-inset-top), 20rpx);
		}
	}

	/* 欢迎卡片 */
	.welcome-card {
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
		margin: 0 20rpx 20rpx 20rpx;
		border-radius: 20rpx;
		padding: 30rpx;
		box-shadow: 0 8rpx 24rpx rgba(102, 126, 234, 0.3);
	}

	.welcome-content {
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.welcome-text {
		display: flex;
		flex-direction: column;
	}

	.welcome-title {
		font-size: 40rpx;
		font-weight: bold;
		color: #fff;
		margin-bottom: 8rpx;
	}

	.welcome-desc {
		font-size: 26rpx;
		color: rgba(255, 255, 255, 0.9);
	}

	.welcome-badge {
		background-color: rgba(255, 255, 255, 0.2);
		border-radius: 50%;
		width: 100rpx;
		height: 100rpx;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.badge-icon {
		font-size: 50rpx;
	}

	/* 轮播图 */
	.banner {
		height: 280rpx;
		width: calc(100% - 40rpx);
		margin: 0 20rpx 20rpx 20rpx;
		border-radius: 20rpx;
		overflow: hidden;
		box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.08);
	}

	.banner-item {
		height: 100%;
		width: 100%;
		position: relative;
		display: flex;
		align-items: flex-end;
	}

	.banner-image {
		width: 100%;
		height: 100%;
		position: absolute;
		top: 0;
		left: 0;
	}

	.banner-content {
		position: relative;
		z-index: 1;
		width: 100%;
		background: linear-gradient(to top, rgba(0, 0, 0, 0.7), transparent);
		padding: 30rpx 20rpx 20rpx;
	}

	.banner-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #fff;
		text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.3);
	}

	/* 功能入口 */
	.function-section {
		margin: 0 20rpx 20rpx 20rpx;
	}

	.function-grid {
		display: flex;
		flex-wrap: wrap;
		padding: 30rpx 20rpx;
		background-color: #fff;
		border-radius: 20rpx;
		box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
	}

	.function-item {
			width: 25%;
			display: flex;
			flex-direction: column;
			align-items: center;
			padding: 20rpx 0;
			transition: all 0.3s ease;
		}

	.function-item:active {
		transform: scale(0.92);
	}

	.icon {
		height: 100rpx;
		width: 100rpx;
		border-radius: 24rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		margin-bottom: 15rpx;
		box-shadow: 0 6rpx 16rpx rgba(0, 0, 0, 0.1);
	}

	.icon.knowledge {
		background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
	}

	.icon.case {
		background: linear-gradient(135deg, #d299c2 0%, #fef9d7 100%);
	}

	.icon.test {
		background: linear-gradient(135deg, #89f7fe 0%, #66a6ff 100%);
	}

	.icon.report {
		background: linear-gradient(135deg, #fddb92 0%, #d1fdff 100%);
	}

	.icon.ai {
			background: linear-gradient(135deg, #a1c4fd 0%, #c2e9fb 100%);
		}

		.icon.check-in {
			background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
		}

	.icon-text {
		font-size: 48rpx;
	}

	.function-name {
		font-size: 24rpx;
		color: #333;
		font-weight: 500;
		text-align: center;
	}

	/* 预警区域 */
	.warning-section {
		margin: 0 20rpx 20rpx 20rpx;
	}

	.warning-card {
		background: linear-gradient(135deg, #fff5f5 0%, #ffe0e0 100%);
		padding: 25rpx;
		border-radius: 20rpx;
		border: 2rpx solid #ffb3b3;
		box-shadow: 0 4rpx 16rpx rgba(255, 107, 107, 0.15);
	}

	.warning-header {
		display: flex;
		align-items: center;
		margin-bottom: 15rpx;
	}

	.warning-icon {
		font-size: 36rpx;
		margin-right: 12rpx;
	}

	.warning-title {
		font-size: 30rpx;
		font-weight: bold;
		color: #d32f2f;
	}

	.warning-content {
		flex: 1;
	}

	.warning-subtitle {
		display: block;
		font-size: 26rpx;
		font-weight: 600;
		color: #333;
		margin-bottom: 10rpx;
	}

	.warning-desc {
		font-size: 24rpx;
		color: #666;
		line-height: 38rpx;
	}

	/* 资讯区域 */
	.news-section {
		background-color: #fff;
		margin: 0 20rpx;
		border-radius: 20rpx;
		padding: 25rpx;
		box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
	}

	.section-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 25rpx;
	}

	.section-title {
		display: flex;
		align-items: center;
	}

	.title-dot {
		width: 12rpx;
		height: 12rpx;
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
		border-radius: 50%;
		margin-right: 12rpx;
	}

	.section-title .title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
	}

	.more {
		font-size: 24rpx;
		color: #667eea;
		font-weight: 500;
	}

	.news-list {
		display: flex;
		flex-direction: column;
	}

	.news-item {
		display: flex;
		padding: 20rpx 0;
		border-bottom: 1rpx solid #f0f0f0;
		transition: all 0.2s ease;
	}

	.news-item:last-child {
		border-bottom: none;
	}

	.news-item:active {
		background-color: #f9f9f9;
		border-radius: 12rpx;
		padding-left: 10rpx;
		padding-right: 10rpx;
	}

	.news-cover {
		height: 140rpx;
		width: 180rpx;
		border-radius: 16rpx;
		margin-right: 20rpx;
		background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
		display: flex;
		align-items: center;
		justify-content: center;
		overflow: hidden;
	}
	
	.news-image {
		width: 100%;
		height: 100%;
		border-radius: 16rpx;
	}

	.news-emoji {
		font-size: 50rpx;
	}

	.news-content {
		flex: 1;
		display: flex;
		flex-direction: column;
		justify-content: space-between;
	}

	.news-title {
		font-size: 28rpx;
		color: #333;
		line-height: 42rpx;
		overflow: hidden;
		text-overflow: ellipsis;
		display: -webkit-box;
		-webkit-line-clamp: 2;
		-webkit-box-orient: vertical;
		font-weight: 500;
	}

	.news-info {
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.news-time {
		font-size: 22rpx;
		color: #999;
	}

	.news-tag {
		font-size: 20rpx;
		color: #667eea;
		background-color: #e3f2fd;
		padding: 6rpx 14rpx;
		border-radius: 12rpx;
		font-weight: 500;
	}
</style>
