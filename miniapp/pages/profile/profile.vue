<template>
	<view class="profile-container">
		<view class="user-info">
			<view class="avatar-section">
				<view class="avatar-wrapper">
					<image v-if="userInfo.avatar" :src="getStaticUrl(userInfo.avatar)" class="avatar-image" mode="aspectFill"></image>
					<text v-else class="avatar-emoji">👤</text>
				</view>
				<view class="user-details">
					<view class="name-row">
						<text class="username">{{userInfo.nickname || userInfo.username}}</text>
						<view class="audit-badge" v-if="hasPendingChanges">
							<text class="audit-text">审核中</text>
						</view>
					</view>
					<view class="user-level-badge">
						<text class="level-icon">🛡️</text>
						<text class="level-text">防诈骗达人 Lv.{{userInfo.level}}</text>
					</view>
				</view>
			</view>
			<view class="user-stats">
				<view class="stat-item">
					<text class="stat-number">{{userInfo.knowledgeCount}}</text>
					<text class="stat-label">已学知识</text>
				</view>
				<view class="stat-divider"></view>
				<view class="stat-item">
					<text class="stat-number">{{userInfo.testCount}}</text>
					<text class="stat-label">测试次数</text>
				</view>
				<view class="stat-divider"></view>
				<view class="stat-item">
					<text class="stat-number">{{userInfo.score}}</text>
					<text class="stat-label">防骗指数</text>
				</view>
			</view>
		</view>
		
		<view class="menu-section">
			<view class="menu-list">
				<view class="menu-item" @click="navigateTo('myPosts')">
					<view class="menu-left">
						<view class="menu-icon-wrapper posts-icon">
							<text class="menu-icon">📝</text>
						</view>
						<text class="menu-text">我的帖子</text>
					</view>
					<text class="menu-arrow">›</text>
				</view>
				<view class="menu-item" @click="navigateTo('collections')">
					<view class="menu-left">
						<view class="menu-icon-wrapper collections-icon">
							<text class="menu-icon">⭐</text>
						</view>
						<text class="menu-text">我的收藏</text>
					</view>
					<text class="menu-arrow">›</text>
				</view>
				<view class="menu-item" @click="navigateTo('learningHistory')">
					<view class="menu-left">
						<view class="menu-icon-wrapper history-icon">
							<text class="menu-icon">📖</text>
						</view>
						<text class="menu-text">浏览历史</text>
					</view>
					<text class="menu-arrow">›</text>
				</view>
				<view class="menu-item" @click="navigateTo('settings')">
					<view class="menu-left">
						<view class="menu-icon-wrapper settings-icon">
							<text class="menu-icon">⚙️</text>
						</view>
						<text class="menu-text">账号设置</text>
					</view>
					<text class="menu-arrow">›</text>
				</view>
			</view>
		</view>
		
		<button class="logout-button" @click="logout">退出登录</button>
	</view>
</template>

<script>
	// 导入API接口配置
	import { API, getStaticUrl } from '@/utils/api.js'
	// 导入HTTP请求工具
	import { request } from '@/utils/request.js'

	/**
	 * 个人中心页面组件
	 * 
	 * 功能说明：
	 * 1. 展示用户信息（头像、昵称、等级）
	 * 2. 展示用户统计数据（已学知识、测试次数、防骗指数）
	 * 3. 提供功能菜单：我的帖子、我的收藏、账号设置
	 * 4. 支持退出登录功能
	 * 5. 自动检测登录状态，未登录则跳转到登录页
	 * 
	 * @author 反诈骗平台开发团队
	 * @version 1.0
	 */
	export default {
		/**
		 * 组件数据
		 * 
		 * userInfo: 用户信息
		 * - id: 用户ID
		 * - username: 用户名
		 * - nickname: 昵称
		 * - phone: 手机号
		 * - email: 邮箱
		 * - region: 地区
		 * - level: 用户等级
		 * - knowledgeCount: 已学知识数量
		 * - testCount: 测试次数
		 * - score: 防骗指数
		 * - createdAt: 注册时间
		 * 
		 * loading: 加载状态标识
		 */
		data() {
			return {
				userInfo: {
					id: null,
					username: '用户',
					nickname: '',
					avatar: '',
					phone: '',
					email: '',
					region: [],
					level:1,
					knowledgeCount: 0,
					testCount: 0,
					score: 0,
					createdAt: ''
				},
				loading: false,
				hasPendingChanges: false,
				pendingChangeTypes: []
			}
		},
		
		/**
		 * 页面加载时执行
		 * 功能：获取用户信息
		 */
		onLoad() {
			this.getUserInfo();
			this.checkProfileChangeStatus();
		},
		
		/**
		 * 页面显示时执行
		 * 功能：重新获取用户信息，确保数据最新
		 */
		onShow() {
			this.getUserInfo();
			this.checkProfileChangeStatus();
		},
		
		methods: {
			/**
			 * 获取静态资源完整URL
			 * 
			 * @param {string} url - 相对路径
			 * @returns {string} 完整的静态资源URL
			 */
			getStaticUrl(url) {
				return getStaticUrl(url)
			},
			/**
			 * 获取用户信息
			 * 
			 * 功能：
			 * 1. 检查登录状态，未登录则跳转到登录页
			 * 2. 从后端接口获取最新的用户信息
			 * 3. 更新本地存储的用户信息
			 * 4. 处理地区字段格式（字符串转数组）
			 * 
			 * 错误处理：
			 * - 未登录提示并跳转
			 * - 获取失败提示并跳转到登录页
			 * - finally块确保loading状态被重置
			 */
			async getUserInfo() {
				this.loading = true;
				try {
					// 检查登录状态
					const isLoggedIn = uni.getStorageSync('isLoggedIn');
					const token = uni.getStorageSync('token');
					
					console.log('个人中心登录状态:', { isLoggedIn, token });
					
					// 未登录，跳转到登录页
					if (!isLoggedIn || !token) {
						uni.showToast({
							title: '请先登录',
							icon: 'none'
						});
						setTimeout(() => {
							uni.reLaunch({
								url: '/pages/login/login'
							});
						}, 1500);
						return;
					}
					
					// 从后端获取最新的用户信息
					try {
						const res = await request(API.USER.INFO, 'GET');
						if (res && res.data) {
							// 处理地区字段格式
							let userData = res.data;
							if (userData.region && typeof userData.region === 'string') {
								userData.region = [userData.region];
							}
							
							// 更新用户信息
							this.userInfo = {
								...this.userInfo,
								...userData,
								username: userData.username || userData.nickname || '用户',
								nickname: userData.nickname || '',
								avatar: userData.avatar || '',
								phone: userData.phone || '',
								email: userData.email || '',
								region: userData.region || [],
								level: userData.level || 1,
								knowledgeCount: userData.knowledgeCount || 0,
								testCount: userData.testCount || 0,
								score: userData.score || 0
							};
							
							// 更新本地存储
							uni.setStorageSync('userInfo', userData);
						}
					} catch (error) {
						console.error('从后端获取用户信息失败', error);
						uni.showToast({
							title: '获取用户信息失败',
							icon: 'none'
						});
						// 如果获取失败，尝试使用本地存储的用户信息
						const savedUserInfo = uni.getStorageSync('userInfo');
						if (savedUserInfo && savedUserInfo.id) {
							// 处理地区字段格式
							if (savedUserInfo.region && typeof savedUserInfo.region === 'string') {
								savedUserInfo.region = [savedUserInfo.region];
							}
							
							// 更新用户信息
							this.userInfo = {
								...this.userInfo,
								...savedUserInfo,
								username: savedUserInfo.username || savedUserInfo.nickname || '用户',
								nickname: savedUserInfo.nickname || '',
								avatar: savedUserInfo.avatar || '',
								phone: savedUserInfo.phone || '',
								email: savedUserInfo.email || '',
								region: savedUserInfo.region || [],
								level: savedUserInfo.level || 1,
								knowledgeCount: savedUserInfo.knowledgeCount || 0,
								testCount: savedUserInfo.testCount || 0,
								score: savedUserInfo.score || 0
							};
						} else {
							// 本地也没有用户信息，跳转到登录页
							setTimeout(() => {
								uni.reLaunch({
									url: '/pages/login/login'
								});
							}, 1500);
						}
					}
				} catch (error) {
					console.error('获取用户信息失败', error);
					uni.showToast({
						title: '获取用户信息失败',
						icon: 'none'
					});
				} finally {
					// 重置loading状态
					this.loading = false;
				}
			},
			
			async checkProfileChangeStatus() {
				try {
					const res = await request(API.USER.PROFILE_CHANGE_STATUS, 'GET');
					if (res && res.hasPending) {
						this.hasPendingChanges = true;
						this.pendingChangeTypes = (res.pendingChanges || []).map(c => c.changeType === 'nickname' ? '昵称' : '头像');
					} else {
						this.hasPendingChanges = false;
						this.pendingChangeTypes = [];
					}
				} catch (error) {
					console.error('获取审核状态失败', error);
				}
			},
			
			/**
			 * 跳转到指定页面
			 * 
			 * @param {string} page - 页面名称
			 * 
			 * 功能：
			 * 跳转到个人中心下的子页面（myPosts、collections、settings）
			 */
			navigateTo(page) {
				uni.navigateTo({
					url: `/pages/profile/${page}/${page}`
				});
			},
			
			/**
			 * 退出登录
			 * 
			 * 功能：
			 * 1. 显示确认对话框
			 * 2. 用户确认后清除所有登录状态
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
							// 清除所有登录状态
							uni.removeStorageSync('isLoggedIn');
							uni.removeStorageSync('username');
							uni.removeStorageSync('userInfo');
							uni.removeStorageSync('token');
							
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
	.profile-container {
		min-height: 100vh;
		background: linear-gradient(180deg, #e3f2fd 0%, #f5f5f5 100%);
	}

	.user-info {
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
		padding: 50rpx 30rpx 40rpx;
		color: #fff;
		border-radius: 0 0 40rpx 40rpx;
		box-shadow: 0 8rpx 32rpx rgba(102, 126, 234, 0.25);
	}

	.avatar-section {
		display: flex;
		align-items: center;
		margin-bottom: 35rpx;
	}

	.avatar-wrapper {
		width: 120rpx;
		height: 120rpx;
		background: rgba(255, 255, 255, 0.25);
		border-radius: 50%;
		display: flex;
		align-items: center;
		justify-content: center;
		margin-right: 24rpx;
		border: 3rpx solid rgba(255, 255, 255, 0.4);
		box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.15);
	}

	.avatar-image {
		width: 120rpx;
		height: 120rpx;
		border-radius: 50%;
	}

	.avatar-emoji {
		font-size: 60rpx;
	}

	.user-details {
		flex: 1;
	}

	.username {
		font-size: 38rpx;
		font-weight: bold;
		display: block;
	}

	.name-row {
		display: flex;
		align-items: center;
		margin-bottom: 12rpx;
	}

	.audit-badge {
		margin-left: 12rpx;
		background-color: #fff3e0;
		border: 1rpx solid #ff9800;
		border-radius: 6rpx;
		padding: 4rpx 12rpx;
	}

	.audit-text {
		font-size: 20rpx;
		color: #ff9800;
	}

	.user-level-badge {
		display: inline-flex;
		align-items: center;
		background: rgba(255, 255, 255, 0.2);
		padding: 8rpx 20rpx;
		border-radius: 20rpx;
		backdrop-filter: blur(10rpx);
	}

	.level-icon {
		font-size: 28rpx;
		margin-right: 8rpx;
	}

	.level-text {
		font-size: 24rpx;
		opacity: 0.95;
	}

	.user-stats {
		display: flex;
		justify-content: space-around;
		align-items: center;
		background: rgba(255, 255, 255, 0.15);
		border-radius: 20rpx;
		padding: 25rpx 20rpx;
		backdrop-filter: blur(10rpx);
	}

	.stat-item {
		display: flex;
		flex-direction: column;
		align-items: center;
		flex: 1;
	}

	.stat-divider {
		width: 2rpx;
		height: 60rpx;
		background: rgba(255, 255, 255, 0.3);
	}

	.stat-number {
		font-size: 36rpx;
		font-weight: bold;
		margin-bottom: 6rpx;
	}

	.stat-label {
		font-size: 22rpx;
		opacity: 0.9;
	}

	.menu-section {
		margin: 30rpx 20rpx;
		background-color: #fff;
		border-radius: 24rpx;
		padding: 10rpx 0;
		box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.06);
	}

	.menu-list {
		display: flex;
		flex-direction: column;
	}

	.menu-item {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 30rpx 24rpx;
		transition: all 0.2s ease;
	}

	.menu-item:active {
		background-color: #f8f9fa;
	}

	.menu-item + .menu-item {
		border-top: 1rpx solid #f0f0f0;
	}

	.menu-left {
		display: flex;
		align-items: center;
		flex: 1;
	}

	.menu-icon-wrapper {
		width: 72rpx;
		height: 72rpx;
		border-radius: 18rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		margin-right: 20rpx;
	}

	.posts-icon {
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	}

	.collections-icon {
		background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
	}

	.history-icon {
			background: linear-gradient(135deg, #a1c4fd 0%, #c2e9fb 100%);
		}

		.settings-icon {
			background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
		}

	.menu-icon {
		font-size: 36rpx;
	}

	.menu-text {
		font-size: 30rpx;
		color: #333;
		font-weight: 500;
	}

	.menu-arrow {
		font-size: 36rpx;
		color: #ccc;
		font-weight: 300;
	}

	.logout-button {
		margin: 30rpx 20rpx;
		height: 90rpx;
		background: linear-gradient(135deg, #ff6b6b 0%, #ee5a5a 100%);
		color: #fff;
		font-size: 32rpx;
		font-weight: bold;
		border-radius: 45rpx;
		border: none;
		box-shadow: 0 8rpx 24rpx rgba(255, 107, 107, 0.3);
		transition: all 0.3s ease;
	}

	.logout-button:active {
		transform: scale(0.96);
		box-shadow: 0 4rpx 12rpx rgba(255, 107, 107, 0.2);
	}
</style>
