<template>
	<view class="ai-chat-container">
		<!-- 顶部导航 -->
		<view class="chat-header">
			<view class="header-left" @click="goBack">
				<view class="back-button">
					<text class="back-icon">←</text>
				</view>
			</view>
			<view class="header-center">
				<view class="ai-avatar">
					<text class="avatar-icon">🤖</text>
				</view>
				<view class="header-info">
					<text class="header-title">AI防诈骗助手</text>
					<text class="header-status">在线</text>
				</view>
			</view>
			<view class="header-right" @click="clearHistory">
				<view class="action-button">
					<text class="clear-icon">🗑️</text>
				</view>
			</view>
		</view>
		
		<!-- 聊天内容区域 -->
		<scroll-view class="chat-content" scroll-y>
			<!-- 系统欢迎消息 -->
			<view class="message-item system" v-if="messages.length === 0">
				<view class="message-bubble system">
					<text class="message-text">您好！我是防诈骗AI助手，专为反诈骗平台设计。

我可以帮您：
• 识别各类诈骗手法
• 分析可疑信息的真实性
• 提供防诈骗知识和建议
• 解答防诈骗相关问题
• 指导如何应对诈骗情况

无论是电信诈骗、网络诈骗、兼职刷单诈骗，还是投资理财诈骗，我都能为您提供专业的防诈指导。请问有什么可以帮助您的？</text>
				</view>
			</view>
			
			<!-- 聊天消息 -->
			<view v-for="(msg, index) in messages" :key="index" :class="['message-item', msg.role]">
				<view v-if="msg.role === 'assistant'" class="message-avatar">
					<view class="avatar assistant">
						<text class="avatar-icon">🤖</text>
					</view>
				</view>
				<view v-if="msg.role === 'user'" class="message-bubble user">
					<text class="message-text">{{msg.content}}</text>
				</view>
				<view v-else class="message-bubble assistant">
					<text class="message-text">{{msg.content}}</text>
				</view>
				<view v-if="msg.role === 'user'" class="message-avatar">
					<view class="avatar user">
						<image v-if="userInfo && userInfo.avatar" :src="getStaticUrl(userInfo.avatar)" class="user-avatar-img" mode="aspectFill"></image>
						<text v-else class="avatar-icon">👤</text>
					</view>
				</view>
			</view>
			
			<!-- 加载状态 -->
			<view class="message-item assistant" v-if="loading">
				<view class="message-avatar">
					<view class="avatar assistant">
						<text class="avatar-icon">🤖</text>
					</view>
				</view>
				<view class="message-bubble assistant loading">
					<text class="loading-dots">
						<span class="dot"></span>
						<span class="dot"></span>
						<span class="dot"></span>
					</text>
				</view>
			</view>
		</scroll-view>
		
		<!-- 输入区域 -->
		<view class="chat-input-area">
			<textarea 
				v-model="inputText" 
				class="chat-input" 
				placeholder="输入您的问题..." 
				@focus="onInputFocus"
				@blur="onInputBlur"
				auto-height
				max-height="200rpx"
				@confirm="sendMessage"
			></textarea>
			<button class="send-button" @click="sendMessage" :disabled="!inputText.trim() || loading" :style="{ opacity: (!inputText.trim() || loading) ? 0.6 : 1 }">
				<text class="send-icon">➤</text>
			</button>
		</view>
		

	</view>
</template>

<script>
	// 导入API接口配置
	import { API, getStaticUrl } from '@/utils/api.js'
	// 导入HTTP请求工具
	import { request } from '@/utils/request.js'
	// 导入状态管理store
	import store from '@/store/index.js'

	/**
	 * AI防诈骗问答页面
	 * 
	 * 功能说明：
	 * 1. 提供AI防诈骗问答功能
	 * 2. 支持多轮对话
	 * 3. 展示聊天历史
	 * 4. 支持清空聊天历史
	 * 
	 * @author 反诈骗平台开发团队
	 * @version 1.0
	 */
	export default {
		/**
		 * 组件数据
		 * 
		 * messages: 聊天消息列表
		 * - role: 角色（user/assistant）
		 * - content: 消息内容
		 * 
		 * inputText: 输入框内容
		 * loading: 加载状态
		 * sessionId: 会话ID
		 * focused: 输入框是否聚焦
		 */
		data() {
			return {
				messages: [],
				inputText: '',
				loading: false,
				sessionId: '',
				focused: false
			}
		},
		
		/**
		 * 计算属性
		 * 
		 * userInfo: 当前登录用户信息
		 */
		computed: {
			userInfo() {
				return store.state.userInfo || uni.getStorageSync('userInfo')
			}
		},
		
		/**
		 * 页面加载时执行
		 * 功能：初始化会话
		 */
		onLoad() {
			this.initSession()
		},
		
		methods: {
			/**
			 * 初始化会话
			 * 
			 * 功能：
			 * 1. 调用后端接口创建新会话
			 * 2. 获取会话ID
			 */
			async initSession() {
				try {
					const res = await request(API.AI.SESSION, 'POST')
					this.sessionId = res.data || ''
				} catch (error) {
					console.error('初始化会话失败', error)
					uni.showToast({
						title: '初始化失败',
						icon: 'none'
					})
				}
			},
			
			/**
			 * 发送消息
			 * 
			 * 功能：
			 * 1. 验证输入内容
			 * 2. 发送用户消息
			 * 3. 调用AI接口获取回答
			 * 4. 显示AI回答
			 */
			async sendMessage() {
				const text = this.inputText.trim()
				if (!text || this.loading) return
				
				// 添加用户消息
				this.messages.push({
					role: 'user',
					content: text
				})
				this.inputText = ''
				
				// 滚动到底部
				this.scrollToBottom()
				
				// 显示加载状态
				this.loading = true
				
				try {
					console.log('开始发送消息:', text)
					console.log('会话ID:', this.sessionId)
					console.log('API地址:', API.AI.CHAT)
					
					// 调用AI接口
					const res = await request(API.AI.CHAT, 'POST', {
						question: text,
						sessionId: this.sessionId
					})
					
					console.log('API响应:', res)
					
					// 添加AI回答
					this.messages.push({
						role: 'assistant',
						content: res.data || '抱歉，我暂时无法回答这个问题'
					})
					
					// 滚动到底部
					this.scrollToBottom()
				} catch (error) {
					console.error('发送消息失败', error)
					uni.showToast({
						title: '发送失败，请重试',
						icon: 'none'
					})
				} finally {
					// 隐藏加载状态
					this.loading = false
				}
			},
			
			/**
			 * 滚动到底部
			 * 
			 * 功能：
			 * 滚动聊天内容到底部
			 */
			scrollToBottom() {
				setTimeout(() => {
					const query = uni.createSelectorQuery()
					query.select('.chat-content').boundingClientRect()
					query.select('.chat-content').scrollOffset()
					query.exec((res) => {
						if (res && res[0] && res[1]) {
							const scrollHeight = res[1].scrollHeight
							const query2 = uni.createSelectorQuery()
							query2.select('.chat-content').node()
							query2.exec((res2) => {
								if (res2 && res2[0] && res2[0].node) {
									res2[0].node.scrollTop = scrollHeight
								}
							})
						}
					})
				}, 100)
			},
			
			/**
			 * 清空历史
			 * 
			 * 功能：
			 * 1. 显示确认对话框
			 * 2. 清空聊天记录
			 * 3. 调用后端接口清空历史
			 */
			async clearHistory() {
				uni.showModal({
					title: '清空聊天',
					content: '确定要清空聊天记录吗？',
					confirmText: '确定',
					cancelText: '取消',
					success: async (res) => {
						if (res.confirm) {
							// 清空本地记录
							this.messages = []
							
							// 调用后端接口
							try {
								await request(`${API.AI.HISTORY}?sessionId=${this.sessionId}`, 'DELETE')
							} catch (error) {
								console.error('清空历史失败', error)
							}
						}
					}
				})
			},
			
			/**
			 * 返回上一页
			 */
			goBack() {
				const pages = getCurrentPages()
				if (pages.length > 1) {
					uni.navigateBack()
				} else {
					uni.switchTab({
						url: '/pages/index/index'
					})
				}
			},
			
			/**
			 * 输入框聚焦
			 */
			onInputFocus() {
				this.focused = true
			},
			
			/**
			 * 输入框失焦
			 */
			onInputBlur() {
			this.focused = false
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
	.ai-chat-container {
		min-height: 100vh;
		background: linear-gradient(180deg, #f8f9fa 0%, #e3f2fd 100%);
		display: flex;
		flex-direction: column;
		padding-top: env(safe-area-inset-top);
		box-sizing: border-box;
	}

	/* 安全区域适配 */
	@supports (padding: max(0px)) {
		.ai-chat-container {
			padding-top: max(env(safe-area-inset-top), 20rpx);
		}
	}

	/* 顶部导航 */
	.chat-header {
		height: 120rpx;
		background: linear-gradient(135deg, #2196F3 0%, #1565C0 100%);
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 0 30rpx;
		box-shadow: 0 4rpx 20rpx rgba(33, 150, 243, 0.3);
		position: sticky;
		top: 0;
		z-index: 100;
		margin-top: -20rpx;
	}

	/* 聊天内容 */
	.chat-content {
		flex: 1;
		padding: 30rpx 20rpx;
		padding-bottom: 140rpx; /* 增加底部内边距，防止输入区域遮挡 */
		overflow-y: auto;
		background-image: 
			radial-gradient(circle at 10% 20%, rgba(33, 150, 243, 0.05) 0%, transparent 20%),
			radial-gradient(circle at 90% 80%, rgba(102, 126, 234, 0.05) 0%, transparent 20%);
	}

	.header-left,
	.header-right {
		width: 80rpx;
		height: 80rpx;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.back-button,
	.action-button {
		width: 60rpx;
		height: 60rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		border-radius: 30rpx;
		background: rgba(255, 255, 255, 0.2);
		transition: all 0.3s ease;
	}

	.back-button:active,
	.action-button:active {
		background: rgba(255, 255, 255, 0.3);
		transform: scale(0.95);
	}

	.back-icon {
		font-size: 40rpx;
		color: #fff;
		font-weight: bold;
	}

	.clear-icon {
		font-size: 32rpx;
		color: #fff;
	}

	.header-center {
		flex: 1;
		display: flex;
		align-items: center;
		justify-content: center;
		gap: 16rpx;
	}

	.ai-avatar {
		width: 60rpx;
		height: 60rpx;
		border-radius: 30rpx;
		background: rgba(255, 255, 255, 0.9);
		display: flex;
		align-items: center;
		justify-content: center;
		box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.2);
	}

	.avatar-icon {
		font-size: 40rpx;
	}

	.header-info {
		display: flex;
		flex-direction: column;
		align-items: flex-start;
	}

	.header-title {
		font-size: 36rpx;
		font-weight: bold;
		color: #fff;
		margin-bottom: 4rpx;
	}

	.header-status {
		font-size: 24rpx;
		color: rgba(255, 255, 255, 0.8);
	}



	/* 消息项 */
	.message-item {
		margin-bottom: 30rpx;
		display: flex;
		align-items: flex-end;
		gap: 12rpx;
	}

	.message-item.user {
		flex-direction: row-reverse;
	}

	.message-item.assistant {
		flex-direction: row;
	}

	.message-item.system {
		justify-content: center;
		flex-direction: row;
	}

	/* 消息头像 */
	.message-avatar {
		display: flex;
		align-items: flex-end;
	}

	.avatar {
		width: 64rpx;
		height: 64rpx;
		border-radius: 32rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.15);
	}

	.avatar.assistant {
		background: linear-gradient(135deg, #E3F2FD 0%, #BBDEFB 100%);
	}

	.avatar.user {
		background: linear-gradient(135deg, #F3E5F5 0%, #E1BEE7 100%);
	}

	/* 用户头像图片 */
	.user-avatar-img {
		width: 100%;
		height: 100%;
		border-radius: 32rpx;
	}

	/* 消息气泡 */
	.message-bubble {
		max-width: 70%;
		padding: 28rpx 36rpx;
		border-radius: 28rpx;
		word-wrap: break-word;
		line-height: 48rpx;
		position: relative;
	}

	.message-bubble.user {
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
		color: #fff;
		border-bottom-right-radius: 10rpx;
		box-shadow: 0 4rpx 12rpx rgba(102, 126, 234, 0.3);
	}

	.message-bubble.assistant {
		background: #fff;
		color: #333;
		border-bottom-left-radius: 10rpx;
		box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
	}

	.message-bubble.system {
		background: rgba(33, 150, 243, 0.1);
		color: #1976D2;
		border-radius: 24rpx;
		max-width: 80%;
		box-shadow: 0 2rpx 8rpx rgba(33, 150, 243, 0.15);
	}

	.message-text {
		font-size: 34rpx;
		font-weight: 400;
	}

	/* 加载状态 */
	.message-bubble.loading {
		display: flex;
		align-items: center;
		justify-content: center;
		min-height: 90rpx;
		min-width: 140rpx;
	}

	.loading-dots {
		display: flex;
		align-items: center;
		gap: 12rpx;
	}

	.loading-dots .dot {
		width: 16rpx;
		height: 16rpx;
		background-color: #2196F3;
		border-radius: 50%;
		animation: pulse 1.4s infinite ease-in-out both;
	}

	.loading-dots .dot:nth-child(1) {
		animation-delay: -0.32s;
	}

	.loading-dots .dot:nth-child(2) {
		animation-delay: -0.16s;
	}

	@keyframes pulse {
		0%, 80%, 100% {
			top: 0;
			height: 16rpx;
		}
		40% {
			top: -12rpx;
			height: 28rpx;
		}
	}

	/* 输入区域 */
	.chat-input-area {
		background: #fff;
		display: flex;
		align-items: flex-end;
		padding: 24rpx;
		box-shadow: 0 -4rpx 20rpx rgba(0, 0, 0, 0.1);
		position: sticky;
		bottom: 0;
		min-height: 140rpx;
		z-index: 5;
	}

	.chat-input {
		flex: 1;
		background: #f5f5f5;
		border-radius: 44rpx;
		padding: 24rpx 32rpx;
		font-size: 32rpx;
		color: #333;
		resize: none;
		margin-right: 16rpx;
		border: 2rpx solid transparent;
		transition: all 0.3s ease;
		max-height: 220rpx;
		min-height: 92rpx;
	}

	.chat-input:focus {
		border-color: #2196F3;
		background: #fff;
		box-shadow: 0 0 0 4rpx rgba(33, 150, 243, 0.1);
	}

	.send-button {
		width: 92rpx;
		height: 92rpx;
		background: linear-gradient(135deg, #2196F3 0%, #1565C0 100%);
		border-radius: 50%;
		display: flex;
		align-items: center;
		justify-content: center;
		box-shadow: 0 4rpx 16rpx rgba(33, 150, 243, 0.4);
		transition: all 0.3s ease;
		flex-shrink: 0;
		cursor: pointer;
		z-index: 10;
		border: none;
		padding: 0;
		margin: 0;
		appearance: none;
		-webkit-appearance: none;
	}

	.send-button:active {
		transform: scale(0.95);
		box-shadow: 0 2rpx 10rpx rgba(33, 150, 243, 0.5);
	}

	.send-button.disabled {
		background: #e0e0e0;
		box-shadow: none;
		cursor: not-allowed;
	}

	.send-icon {
		font-size: 36rpx;
		color: #fff;
		font-weight: bold;
		transform: rotate(45deg);
	}

	/* 滚动条样式 */
	.chat-content::-webkit-scrollbar {
		width: 6rpx;
	}

	.chat-content::-webkit-scrollbar-track {
		background: transparent;
	}

	.chat-content::-webkit-scrollbar-thumb {
		background: rgba(33, 150, 243, 0.3);
		border-radius: 3rpx;
	}

	.chat-content::-webkit-scrollbar-thumb:hover {
		background: rgba(33, 150, 243, 0.5);
	}


</style>