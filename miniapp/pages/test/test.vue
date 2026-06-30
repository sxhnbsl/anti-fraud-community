<template>
	<view class="test-container">
		<view class="header">
			<text class="title">防诈骗知识测试</text>
		</view>
		
		<view class="test-intro">
			<view class="intro-icon-wrapper">
				<text class="intro-icon">📝</text>
			</view>
			<text class="intro-title">测试您的防诈骗知识</text>
			<text class="intro-desc">通过测试提升您的防范意识，保护个人财产安全</text>
			
			<view class="test-stats">
				<view class="stat-item">
					<view class="stat-icon-wrapper questions-icon">
						<text class="stat-icon">📋</text>
					</view>
					<text class="stat-number">20</text>
					<text class="stat-label">测试题目</text>
				</view>
				<view class="stat-item">
					<view class="stat-icon-wrapper time-icon">
						<text class="stat-icon">⏱️</text>
					</view>
					<text class="stat-number">30</text>
					<text class="stat-label">分钟限时</text>
				</view>
				<view class="stat-item">
					<view class="stat-icon-wrapper score-icon">
						<text class="stat-icon">🏆</text>
					</view>
					<text class="stat-number">80</text>
					<text class="stat-label">合格分数</text>
				</view>
			</view>
		</view>
		
		<button class="start-button" @click="startTest">
			<text class="start-icon">▶️</text>
			开始测试
		</button>
		
		<view class="ranking-section">
			<view class="ranking-header">
				<text class="ranking-title">🏅 测试排行榜</text>
				<text class="ranking-subtitle">本周高分榜</text>
			</view>
			<view class="ranking-list">
				<view class="ranking-item" v-for="(item, index) in rankingList" :key="index">
					<view class="ranking-rank" :class="{ 'top-three': index < 3 }">
						<text v-if="index === 0" class="rank-emoji">🥇</text>
						<text v-else-if="index === 1" class="rank-emoji">🥈</text>
						<text v-else-if="index === 2" class="rank-emoji">🥉</text>
						<text v-else class="rank-text">#{{index + 1}}</text>
					</view>
					<view class="user-avatar-wrapper">
						<image v-if="item.avatar" :src="getStaticUrl(item.avatar)" class="user-avatar" mode="aspectFill"></image>
						<text v-else class="user-avatar-emoji">👤</text>
					</view>
					<text class="ranking-name">{{item.name}}</text>
					<view class="score-wrapper">
						<text class="ranking-score">{{item.score}}</text>
						<text class="score-unit">分</text>
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
	 * 防诈骗知识测试页面组件
	 * 
	 * 功能说明：
	 * 1. 展示测试介绍信息（题目数量、限时、合格分数）
	 * 2. 提供开始测试按钮，跳转到答题页面
	 * 3. 展示测试排行榜，显示本周高分用户
	 * 
	 * @author 反诈骗平台开发团队
	 * @version 1.0
	 */
	export default {
		/**
		 * 组件数据
		 * 
		 * rankingList: 排行榜列表数据
		 */
		data() {
			return {
				rankingList: []
			}
		},
		
		/**
		 * 页面加载时执行
		 * 功能：加载排行榜数据
		 */
		onLoad() {
			this.loadRanking()
		},
		
		methods: {
			/**
			 * 加载排行榜数据
			 * 
			 * 功能：
			 * 1. 调用后端接口获取排行榜数据
			 * 2. 处理返回数据，格式化为排行榜列表
			 * 3. 错误处理：捕获网络请求错误，清空排行榜列表
			 */
			async loadRanking() {
				try {
					// 调用排行榜接口，获取前10名
					const res = await request(API.TEST.RANKING + '?limit=10', 'GET')
					
					// 处理返回数据
					if (res.success && res.data) {
						this.rankingList = res.data.map((record, index) => ({
							id: record.id,
							name: record.nickname || record.username || `用户${record.userId}`,
							score: record.score,
							userId: record.userId,
							avatar: record.avatar
						}))
					}
				} catch (error) {
					console.error('加载排行榜失败:', error)
					// 清空排行榜列表
					this.rankingList = []
				}
			},
			
			/**
			 * 开始测试
			 * 
			 * 功能：
			 * 跳转到答题页面
			 */
			startTest() {
				uni.navigateTo({
					url: '/pages/test/questions/questions'
				})
			},
			
			/**
			 * 获取静态资源URL
			 * 
			 * @param {string} url - 相对路径URL
			 * @returns {string} 完整的静态资源URL
			 */
			getStaticUrl(url) {
				if (!url) return ''
				if (url.startsWith('http://') || url.startsWith('https://')) {
					return url
				}
				if (url.startsWith('/')) {
					return 'http://localhost:8080' + url
				}
				return 'http://localhost:8080/' + url
			}
		}
	}
</script>

<style scoped>
	.test-container {
		min-height: 100vh;
		background: linear-gradient(180deg, #fff3e0 0%, #f5f5f5 100%);
		padding: 0 20rpx 40rpx;
	}

	.header {
		padding: 40rpx 0 20rpx;
		text-align: center;
	}

	.title {
		font-size: 44rpx;
		font-weight: bold;
		color: #FF9800;
	}

	.test-intro {
		background-color: #fff;
		border-radius: 32rpx;
		padding: 40rpx 30rpx;
		text-align: center;
		margin-bottom: 30rpx;
		box-shadow: 0 8rpx 32rpx rgba(255, 152, 0, 0.12);
	}

	.intro-icon-wrapper {
		width: 120rpx;
		height: 120rpx;
		background: linear-gradient(135deg, #FF9800 0%, #F57C00 100%);
		border-radius: 60rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		margin: 0 auto 24rpx;
		box-shadow: 0 8rpx 24rpx rgba(255, 152, 0, 0.3);
	}

	.intro-icon {
		font-size: 60rpx;
	}

	.intro-title {
		font-size: 36rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 16rpx;
		display: block;
	}

	.intro-desc {
		font-size: 26rpx;
		color: #666;
		margin-bottom: 36rpx;
		display: block;
		line-height: 1.6;
	}

	.test-stats {
		display: flex;
		justify-content: space-around;
	}

	.stat-item {
		display: flex;
		flex-direction: column;
		align-items: center;
	}

	.stat-icon-wrapper {
		width: 80rpx;
		height: 80rpx;
		border-radius: 20rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		margin-bottom: 12rpx;
	}

	.questions-icon {
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	}

	.time-icon {
		background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
	}

	.score-icon {
		background: linear-gradient(135deg, #4CAF50 0%, #45a049 100%);
	}

	.stat-icon {
		font-size: 40rpx;
	}

	.stat-number {
		font-size: 36rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 6rpx;
	}

	.stat-label {
		font-size: 22rpx;
		color: #999;
	}

	.start-button {
		height: 100rpx;
		line-height: 100rpx;
		font-size: 34rpx;
		font-weight: bold;
		color: #fff;
		background: linear-gradient(135deg, #FF9800 0%, #F57C00 100%);
		border-radius: 50rpx;
		margin-bottom: 30rpx;
		border: none;
		box-shadow: 0 8rpx 24rpx rgba(255, 152, 0, 0.4);
		display: flex;
		align-items: center;
		justify-content: center;
		transition: all 0.3s ease;
	}

	.start-button:active {
		transform: scale(0.96);
		box-shadow: 0 4rpx 12rpx rgba(255, 152, 0, 0.3);
	}

	.start-icon {
		font-size: 32rpx;
		margin-right: 12rpx;
	}

	.ranking-section {
		background-color: #fff;
		border-radius: 32rpx;
		padding: 30rpx;
		box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.06);
	}

	.ranking-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 24rpx;
		padding-bottom: 20rpx;
		border-bottom: 1rpx solid #f0f0f0;
	}

	.ranking-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
	}

	.ranking-subtitle {
		font-size: 24rpx;
		color: #999;
	}

	.ranking-list {
		display: flex;
		flex-direction: column;
	}

	.ranking-item {
		display: flex;
		align-items: center;
		padding: 24rpx 0;
		border-bottom: 1rpx solid #f8f8f8;
		transition: all 0.2s ease;
	}

	.ranking-item:last-child {
		border-bottom: none;
	}

	.ranking-item:active {
		background-color: #fafafa;
	}

	.ranking-rank {
		width: 80rpx;
		text-align: center;
		margin-right: 16rpx;
	}

	.rank-emoji {
		font-size: 40rpx;
	}

	.rank-text {
		font-size: 28rpx;
		font-weight: bold;
		color: #999;
	}

	.user-avatar-wrapper {
		width: 70rpx;
		height: 70rpx;
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
		border-radius: 35rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		margin-right: 16rpx;
		box-shadow: 0 4rpx 12rpx rgba(102, 126, 234, 0.25);
	}

	.user-avatar-emoji {
			font-size: 36rpx;
		}

		.user-avatar {
			width: 100%;
			height: 100%;
			border-radius: 35rpx;
		}

	.ranking-name {
		flex: 1;
		font-size: 28rpx;
		color: #333;
		font-weight: 500;
	}

	.score-wrapper {
		display: flex;
		align-items: baseline;
	}

	.ranking-score {
		font-size: 32rpx;
		font-weight: bold;
		background: linear-gradient(135deg, #FF9800 0%, #F57C00 100%);
		-webkit-background-clip: text;
		-webkit-text-fill-color: transparent;
		background-clip: text;
	}

	.score-unit {
		font-size: 22rpx;
		color: #FF9800;
		margin-left: 2rpx;
	}
</style>
