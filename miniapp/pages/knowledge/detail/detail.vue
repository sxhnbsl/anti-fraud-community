<template>
	<view class="knowledge-detail-container">
		<view class="detail-header card">
			<text class="detail-title">{{knowledgeItem.title}}</text>
			<view class="detail-info">
				<text class="publish-time">📅 {{formatDate(knowledgeItem.createdAt)}}</text>
			</view>
		</view>
		
		<!-- 视频播放区域 -->
		<view class="video-section card" v-if="knowledgeItem.videoUrl">
			<view class="video-title">
				<text>🎬 视频讲解</text>
			</view>
			<video 
				:src="knowledgeItem.videoUrl" 
				:poster="knowledgeItem.videoCover"
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
				<text>🖼️ 相关图片</text>
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
		
		<view class="detail-content card">
			<view class="content-wrapper">
				<rich-text :nodes="formatContent(knowledgeItem.content)"></rich-text>
			</view>
		</view>
		
		<view class="bottom-bar">
			<view class="action-item" :class="{ 'liked': knowledgeItem.isLiked }" @click="like">
				<text class="action-icon">{{ knowledgeItem.isLiked ? '❤️' : '🤍' }}</text>
				<text class="action-text">{{knowledgeItem.likeCount || 0}}</text>
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
	import { API, getStaticUrl } from '@/utils/api.js'
	import { request } from '@/utils/request.js'

	export default {
		data() {
			return {
				knowledgeId: null,
				knowledgeItem: {
					title: '',
					summary: '',
					content: '',
					createdAt: '',
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
		onLoad(options) {
			if (options.id) {
				this.knowledgeId = options.id
				this.loadKnowledgeDetail()
				this.checkCollection()
			}
		},
		methods: {
			formatDate(dateStr) {
				if (!dateStr) return ''
				const date = new Date(dateStr)
				const year = date.getFullYear()
				const month = String(date.getMonth() + 1).padStart(2, '0')
				const day = String(date.getDate()).padStart(2, '0')
				return `${year}-${month}-${day}`
			},
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
			formatContent(content) {
				if (!content) return '<p>暂无详细内容</p>'
				
				// 处理文本格式，添加适当的HTML标签
				let formattedContent = content
				
				// 处理换行符
				formattedContent = formattedContent.replace(/\n/g, '<br>')
				
				// 处理标题格式
				formattedContent = formattedContent.replace(/【([^】]+)】/g, '<h3 class="section-title">$1</h3>')
				
				// 处理列表项
				formattedContent = formattedContent.replace(/(\d+\.)\s/g, '<span class="list-item">$1 </span>')
				formattedContent = formattedContent.replace(/(-)\s/g, '<span class="list-item">$1 </span>')
				
				// 处理段落
				formattedContent = '<div class="content-paragraph">' + formattedContent + '</div>'
				
				return formattedContent
			},
			async loadKnowledgeDetail() {
			this.loading = true
			try {
				const res = await request(API.KNOWLEDGE.DETAIL(this.knowledgeId), 'GET')
				this.knowledgeItem = {
					...res.data,
					isLiked: res.isLiked || false,
					videoUrl: getStaticUrl(res.data.videoUrl),
					videoCover: getStaticUrl(res.data.videoCover)
				}
				this.imageList = this.parseImages(this.knowledgeItem.images).map(url => getStaticUrl(url))
				
				// 记录浏览历史
				this.recordLearningHistory()
			} catch (error) {
				console.error('加载知识详情失败', error)
				uni.showToast({
					title: '加载失败',
					icon: 'none'
				})
			} finally {
				this.loading = false
			}
		},
		
		async recordLearningHistory() {
			try {
				const userId = uni.getStorageSync('userId')
				if (!userId) {
					console.log('用户未登录，不记录浏览历史')
					return
				}
				
				await request(API.LEARNING_RECORD.CREATE, 'POST', {
					userId: userId,
					contentType: 'knowledge',
					contentId: this.knowledgeId,
					title: this.knowledgeItem.title
				})
				console.log('浏览历史记录成功')
			} catch (error) {
				console.error('记录浏览历史失败', error)
			}
		},
			async checkCollection() {
				try {
					const res = await request(API.COLLECTION.CHECK(this.knowledgeId, 'knowledge'), 'GET')
					this.isCollected = res || false
				} catch (error) {
					console.error('检查收藏状态失败', error)
				}
			},
			async toggleCollect() {
				try {
					const res = await request(API.COLLECTION.TOGGLE, 'POST', {
						targetId: this.knowledgeId,
						type: 'knowledge',
						title: this.knowledgeItem.title,
						summary: this.knowledgeItem.summary || this.knowledgeItem.content
					})
					this.isCollected = !this.isCollected
					uni.showToast({
						title: this.isCollected ? '收藏成功' : '取消收藏',
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
			async like() {
				try {
					const res = await request(API.KNOWLEDGE.LIKE(this.knowledgeId), 'POST')
					
					console.log('知识点赞响应:', res)
					
					if (res && res.liked !== undefined) {
						const result = res
						
						this.knowledgeItem.isLiked = result.liked
						this.knowledgeItem.likeCount = result.likeCount
						
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
			async share() {
				uni.showShareMenu({
					success: function (res) {
						console.log('分享成功', res)
					},
					fail: function (err) {
						console.log('分享失败', err)
					}
				})
			}
		}
	}
</script>

<style scoped>
.knowledge-detail-container {
	min-height: 100vh;
	background: linear-gradient(180deg, #e3f2fd 0%, #f5f5f5 100%);
	padding-bottom: 120rpx;
	padding-top: env(safe-area-inset-top);
	box-sizing: border-box;
}

/* 安全区域适配 */
@supports (padding: max(0px)) {
	.knowledge-detail-container {
		padding-top: max(env(safe-area-inset-top), 20rpx);
	}
}

.detail-header {
	margin: 20rpx;
	padding: 30rpx;
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
}

.publish-time {
	font-size: 24rpx;
	color: #999;
}

.video-section {
	margin: 20rpx;
	padding: 30rpx;
}

.video-title {
	font-size: 30rpx;
	font-weight: bold;
	color: #409EFF;
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
	color: #409EFF;
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

.content-wrapper {
			line-height: 1.8;
			color: #666;
			font-size: 28rpx;
		}

		.content-wrapper >>> .content-paragraph {
			line-height: 1.8;
			color: #666;
			font-size: 28rpx;
		}

		.content-wrapper >>> .section-title {
			font-size: 32rpx;
			font-weight: bold;
			color: #333;
			margin: 24rpx 0 16rpx 0;
			padding-bottom: 8rpx;
			border-bottom: 2rpx solid #e3f2fd;
		}

		.content-wrapper >>> .list-item {
			font-weight: 500;
			color: #444;
		}

		.content-wrapper >>> br {
			margin-bottom: 8rpx;
		}

		.content-wrapper >>> img {
			max-width: 100%;
			height: auto;
			border-radius: 12rpx;
			margin: 20rpx 0;
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
