<template>
	<view class="case-detail-container">
		<!-- 案例详情头部 -->
		<view class="detail-header card">
			<view class="case-type">
				<text>{{caseItem.type}}</text>
			</view>
			<text class="detail-title">{{caseItem.title}}</text>
			<view class="detail-info">
				<text class="publish-time">{{caseItem.time}}</text>
				<text class="case-tag"># {{caseItem.tag}}</text>
			</view>
		</view>
		
		<!-- 视频播放区域 -->
		<view class="video-section card" v-if="caseItem.videoUrl">
			<view class="video-title">
				<text>🎬 案例视频</text>
			</view>
			<video 
				:src="caseItem.videoUrl" 
				:poster="caseItem.videoCover"
				class="video-player"
				controls
				show-center-play-btn
				enable-progress-gesture
				show-play-btn
			></video>
		</view>
		
		<!-- 图片展示区域 -->
		<view class="images-section card" v-if="imageList.length > 0">
			<view class="images-title">
				<text>🖼️ 案例图片</text>
			</view>
			<view class="images-grid">
				<view 
					class="image-item" 
					v-for="(img, index) in imageList" 
					:key="index"
					@click="previewImage(index)"
				>
					<image :src="img" mode="aspectFill" class="image-preview"></image>
				</view>
			</view>
		</view>
		
		<!-- 案例详情内容 -->
		<view class="detail-content card">
			<rich-text :nodes="caseItem.content"></rich-text>
		</view>
		
		<!-- 诈骗手法分析 -->
		<view class="analysis-section card">
			<text class="analysis-title">诈骗手法分析</text>
			<rich-text :nodes="caseItem.fraudAnalysis"></rich-text>
		</view>
		
		<!-- 防范建议 -->
		<view class="suggestion-section card">
			<text class="suggestion-title">防范建议</text>
			<rich-text :nodes="caseItem.preventionAdvice"></rich-text>
		</view>
		
		<!-- 查看真实案例 -->
		<view class="real-case-section card" v-if="caseItem.url">
			<button class="view-real-case-btn" @click="openRealCaseUrl">
				📖 查看真实案例详情
			</button>
		</view>
		
		<!-- 底部操作栏 -->
		<view class="bottom-bar">
			<view class="action-item" :class="{ 'liked': caseItem.isLiked }" @click="like">
				<text class="action-icon">{{ caseItem.isLiked ? '❤️' : '🤍' }}</text>
				<text class="action-text">{{caseItem.likeCount || 0}}</text>
			</view>
			<view class="action-item" @click="toggleCollect">
				<text class="action-icon">{{isCollected ? '⭐' : '☆'}}</text>
				<text class="action-text">{{isCollected ? '已收藏' : '收藏'}}</text>
			</view>
			<view class="action-item" @click="share">
				<text class="action-icon">📤</text>
				<text class="action-text">分享</text>
			</view>
		</view>
	</view>
</template>

<script>
	// // 导入API接口配置
	import { API, getStaticUrl } from '@/utils/api.js'
	import { request } from '@/utils/request.js'

	/**
	 * 真实案例详情页面组件
	 * 
	 * 功能说明：
	 * 1. 展示案例详细信息（类型、标题、标签、发布时间）
	 * 2. 展示案例详细内容，支持富文本格式
	 * 3. 展示诈骗手法分析和防范建议
	 * 4. 支持查看真实案例详情（跳转到外部链接）
	 * 5. 支持点赞和收藏操作
	 * 6. 支持分享功能
	 * 
	 * @author 反诈骗平台开发团队
	 * @version 1.0
	 */
	export default {
		/**
		 * 组件数据
		 * 
		 * caseId: 案例ID
		 * caseItem: 案例详细信息
		 * - type: 案例类型
		 * - time: 发布时间
		 * - title: 案例标题
		 * - tag: 案例标签
		 * - content: 案例内容（富文本）
		 * - analysis: 诈骗手法分析（富文本）
		 * - suggestions: 防范建议（富文本）
		 * - url: 真实案例详情链接
		 * - likeCount: 点赞数
		 * - isLiked: 是否已点赞
		 * 
		 * isCollected: 是否已收藏
		 * loading: 加载状态标识
		 */
		data() {
			return {
				caseId: null,
				caseItem: {
				type: '',
				time: '',
				title: '',
				tag: '',
				content: '',
				fraudAnalysis: '',
				preventionAdvice: '',
				url: '',
				likeCount: 0,
				isLiked: false,
				images: '',
				videoUrl: '',
				videoCover: ''
			},
				imageList: [],
				isCollected: false,
				loading: false
			}
		},
		/**
		 * 页面加载时执行
		 * 
		 * @param {Object} options - 页面参数
		 * @param {string} options.id - 案例ID
		 * 
		 * 功能：
		 * 加载案例详情、检查收藏状态、记录浏览
		 */
		onLoad(options) {
			if (options.id) {
				this.caseId = options.id
				this.loadCaseDetail()
				this.checkCollection()
			}
		},
		methods: {
			parseImages(imagesStr) {
				if (!imagesStr) return []
				try {
					const images = JSON.parse(imagesStr)
					return Array.isArray(images) ? images : []
				} catch (e) {
					return [imagesStr]
				}
			},
			previewImage(index) {
				uni.previewImage({
					current: index,
					urls: this.imageList
				})
			},
			/**
			 * 记录浏览
			 * 
			 * 功能：
			 * 调用后端接口记录用户浏览案例的行为
			 * 
			 * 错误处理：
			 * - 捕获网络请求错误
			 * - 在控制台输出错误信息
			 */
			async recordView() {
				try {
					const userId = uni.getStorageSync('userId')
					if (!userId) {
						console.log('用户未登录，不记录浏览')
						return
					}
					
					await request(API.LEARNING_RECORD.CREATE, 'POST', {
						userId: userId,
						contentType: 'case',
						contentId: this.caseId,
						title: this.caseItem.title || '未命名案例',
						viewDuration: 0
					})
				} catch (error) {
					console.error('记录浏览失败', error)
				}
			},

			/**
			 * 加载案例详情
			 * 
			 * 功能：
			 * 1. 调用后端接口获取案例详情
			 * 2. 填充案例数据
			 * 3. 处理点赞状态
			 * 
			 * 错误处理：
			 * - 捕获网络请求错误
			 * - 显示加载失败提示
			 * - finally块确保loading状态被重置
			 */
			// 处理文本格式，将换行符转换为<br>，空格转换为&nbsp;
			formatRichText(text) {
				if (!text) return ''
				return text
					.replace(/\n/g, '<br>')
					.replace(/\s/g, '&nbsp;')
					.replace(/\t/g, '&nbsp;&nbsp;&nbsp;&nbsp;')
			},

			async loadCaseDetail() {
			this.loading = true
			try {
				const res = await request(API.CASE.DETAIL(this.caseId), 'GET')
				this.caseItem = {
					...res.data,
					isLiked: res.isLiked || false,
					videoUrl: getStaticUrl(res.data.videoUrl),
					videoCover: getStaticUrl(res.data.videoCover),
					content: this.formatRichText(res.data.content),
					fraudAnalysis: this.formatRichText(res.data.fraudAnalysis),
					preventionAdvice: this.formatRichText(res.data.preventionAdvice)
				}
				this.imageList = this.parseImages(this.caseItem.images).map(url => getStaticUrl(url))
				
				// 案例详情加载完成后，记录浏览历史
				this.recordView()
			} catch (error) {
				console.error('加载案例详情失败', error)
				uni.showToast({
					title: '加载失败',
					icon: 'none'
				})
			} finally {
				this.loading = false
			}
		},
			/**
			 * 检查收藏状态
			 * 
			 * 功能：
			 * 调用后端接口检查当前用户是否已收藏该案例
			 * 
			 * 错误处理：
			 * - 捕获网络请求错误
			 * - 在控制台输出错误信息
			 */
			async checkCollection() {
				try {
					const res = await request(API.COLLECTION.CHECK(this.caseId, 'case'), 'GET')
					this.isCollected = res || false
				} catch (error) {
					console.error('检查收藏状态失败', error)
				}
			},
			/**
			 * 切换收藏状态
			 * 
			 * 功能：
			 * 1. 调用后端接口切换收藏状态
			 * 2. 更新本地收藏状态
			 * 3. 显示操作成功提示
			 * 
			 * 错误处理：
			 * - 捕获网络请求错误
			 * - 显示操作失败提示
			 */
			async toggleCollect() {
				try {
					const res = await request(API.COLLECTION.TOGGLE, 'POST', {
						targetId: this.caseId,
						type: 'case',
						title: this.caseItem.title,
						summary: this.caseItem.tag || this.caseItem.type
					})
					this.isCollected = !this.isCollected
					uni.showToast({
						title: this.isCollected ? '收藏成功' : '已取消收藏',
						icon: 'success'
					})
				} catch (error) {
					console.error('收藏操作失败', error)
					uni.showToast({
						title: '操作失败',
						icon: 'none'
					})
				}
			},
			/**
			 * 点赞案例
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
			async like() {
				try {
					const res = await request(API.CASE.LIKE(this.caseId), 'POST')
					
					console.log('案例点赞响应:', res)
					
					if (res && res.liked !== undefined) {
						const result = res
						
						this.caseItem.isLiked = result.liked
						this.caseItem.likeCount = result.likeCount
						
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
			 * 分享案例
			 * 
			 * 功能：
			 * 调用uni-app分享接口，显示分享菜单
			 */
			async share() {
				uni.showShareMenu({
					success: function (res) {
						console.log('分享成功', res)
					},
					fail: function (err) {
						console.log('分享失败', err)
					}
				})
			},
			/**
			 * 打开真实案例详情链接
			 * 
			 * 功能：
			 * 使用plus.runtime.openURL打开外部链接
			 * 需要在支持plus API的环境中运行
			 */
			openRealCaseUrl() {
				if (this.caseItem.url) {
					plus.runtime.openURL(this.caseItem.url)
				}
			}
		}
	}
</script>

<style scoped>
.case-detail-container {
	min-height: 100vh;
	background: linear-gradient(180deg, #e3f2fd 0%, #f5f5f5 100%);
	padding-bottom: 120rpx;
	padding-top: env(safe-area-inset-top);
	box-sizing: border-box;
}

/* 安全区域适配 */
@supports (padding: max(0px)) {
	.case-detail-container {
		padding-top: max(env(safe-area-inset-top), 20rpx);
	}
}

.detail-header {
	margin: 20rpx;
	padding: 30rpx;
}

.case-type {
	display: inline-block;
	padding: 8rpx 16rpx;
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	color: #fff;
	border-radius: 20rpx;
	font-size: 24rpx;
	margin-bottom: 16rpx;
}

.detail-title {
	font-size: 36rpx;
	font-weight: bold;
	color: #333;
	line-height: 1.4;
	margin-bottom: 16rpx;
}

.detail-info {
	display: flex;
	align-items: center;
	gap: 20rpx;
}

.publish-time {
	font-size: 24rpx;
	color: #999;
}

.case-tag {
	font-size: 24rpx;
	color: #667eea;
}

.video-section {
	margin: 20rpx;
	padding: 30rpx;
}

.video-title {
	font-size: 30rpx;
	font-weight: bold;
	color: #667eea;
	margin-bottom: 20rpx;
	display: flex;
	align-items: center;
	gap: 10rpx;
}

.video-player {
	width: 100%;
	border-radius: 12rpx;
	background: #000;
}

.images-section {
	margin: 20rpx;
	padding: 30rpx;
}

.images-title {
	font-size: 30rpx;
	font-weight: bold;
	color: #667eea;
	margin-bottom: 20rpx;
	display: flex;
	align-items: center;
	gap: 10rpx;
}

.images-grid {
	display: flex;
	flex-wrap: wrap;
	gap: 16rpx;
}

.image-item {
	width: calc((100% - 32rpx) / 3);
	height: 200rpx;
	border-radius: 12rpx;
	overflow: hidden;
	background: #f5f5f5;
}

.image-preview {
	width: 100%;
	height: 100%;
}

.detail-content {
	margin: 20rpx;
	padding: 30rpx;
}

.detail-content >>> img {
	max-width: 100%;
	height: auto;
	border-radius: 12rpx;
	margin: 20rpx 0;
}

.detail-content >>> p,
.analysis-section >>> p,
.suggestion-section >>> p {
	font-size: 28rpx;
	line-height: 1.8;
	color: #333;
	font-weight: bold;
	margin-bottom: 16rpx;
}

.analysis-section,
.suggestion-section {
	margin: 20rpx;
	padding: 30rpx;
}

.analysis-title,
.suggestion-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #667eea;
	margin-bottom: 20rpx;
}

.real-case-section {
	margin: 20rpx;
	padding: 30rpx;
}

.view-real-case-btn {
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	color: #fff;
	border: none;
	border-radius: 12rpx;
	padding: 24rpx 48rpx;
	font-size: 28rpx;
	font-weight: bold;
}

.bottom-bar {
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	background: #fff;
	display: flex;
	justify-content: space-around;
	align-items: center;
	padding: 20rpx 0;
	box-shadow: 0 -4rpx 20rpx rgba(0, 0, 0, 0.1);
	border-top: 1rpx solid #f0f0f0;
}

.action-item {
	display: flex;
	flex-direction: column;
	align-items: center;
	gap: 8rpx;
}

.action-icon {
	font-size: 40rpx;
}

.action-text {
	font-size: 24rpx;
	color: #666;
}

.action-item.liked {
	background-color: #fff0f0;
	border-radius: 20rpx;
	padding: 8rpx 16rpx;
}

.action-item.liked .action-text {
	color: #f5576c;
}
</style>
