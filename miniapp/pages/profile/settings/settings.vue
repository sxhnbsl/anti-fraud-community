<template>
	<view class="settings-container">
		<view class="profile-section card">
			<view class="profile-header">
				<view class="avatar-wrapper">
					<image v-if="userInfo.avatar" :src="getStaticUrl(userInfo.avatar)" class="avatar-image" mode="aspectFill"></image>
					<text v-else class="avatar-emoji">👤</text>
				</view>
				<view class="user-info">
					<text class="user-name">{{userInfo.nickname || userInfo.username || '用户'}}</text>
					<view class="user-level">
						<text class="level-icon">🛡️</text>
						<text class="level-text">Lv.{{userInfo.level || 1}}</text>
					</view>
				</view>
				<view class="edit-btn" @click="editProfile">
					<text class="edit-text">编辑</text>
				</view>
			</view>
			
			<view class="profile-info">
				<view class="info-item">
					<text class="info-label">昵称</text>
					<text class="info-value">{{userInfo.nickname || '未设置'}}</text>
				</view>
				<view class="info-divider"></view>
				<view class="info-item">
					<text class="info-label">手机号</text>
					<text class="info-value">{{userInfo.phone || '未绑定'}}</text>
				</view>
				<view class="info-divider"></view>
				<view class="info-item">
					<text class="info-label">邮箱</text>
					<text class="info-value">{{userInfo.email || '未绑定'}}</text>
				</view>
				<view class="info-divider"></view>
				<view class="info-item">
					<text class="info-label">地区</text>
					<text class="info-value">{{userInfo.region ? userInfo.region.join(' ') : '未设置'}}</text>
				</view>
			</view>
		</view>
		
		<view class="settings-section card">
			<view class="settings-title">账号设置</view>
			<view class="settings-list">
				<view class="setting-item" @click="changePassword">
					<view class="setting-left">
						<view class="setting-icon-wrapper password-icon">
							<text class="setting-icon">🔒</text>
						</view>
						<text class="setting-text">修改密码</text>
					</view>
					<text class="setting-arrow">›</text>
				</view>
				<view class="setting-item" @click="logout">
					<view class="setting-left">
						<view class="setting-icon-wrapper logout-icon">
							<text class="setting-icon">🚪</text>
						</view>
						<text class="setting-text">退出登录</text>
					</view>
					<text class="setting-arrow">›</text>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	// 导入HTTP请求工具
	import { request } from '@/utils/request.js'
	// 导入API接口配置
	import { API, getStaticUrl } from '@/utils/api.js'
	// 导入Vuex store
	import store from '@/store/index.js'

	/**
	 * 账号设置页面组件
	 * 
	 * 功能说明：
	 * 1. 展示用户详细信息（头像、昵称、等级、手机号、邮箱、地区）
	 * 2. 支持编辑个人资料
	 * 3. 支持修改密码（功能开发中）
	 * 4. 支持退出登录
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
		 * - nickname: 昵称
		 * - phone: 手机号
		 * - email: 邮箱
		 * - region: 地区
		 * - level: 用户等级
		 * 
		 * loading: 加载状态标识
		 */
		data() {
			return {
				userInfo: {},
				loading: false
			}
		},
		/**
		 * 页面加载时执行
		 * 功能：获取用户信息
		 */
		onLoad() {
			this.getUserInfo()
		},
		/**
		 * 页面显示时执行
		 * 功能：重新获取用户信息，确保数据最新
		 */
		onShow() {
			this.getUserInfo()
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
			 * 1. 从本地存储获取用户信息
			 * 2. 处理地区字段格式（字符串转数组）
			 * 3. 如果未登录，显示提示并跳转到登录页
			 * 
			 * 错误处理：
			 * - 捕获获取用户信息错误
			 * - 显示获取失败提示
			 * - finally块确保loading状态被重置
			 */
			async getUserInfo() {
				this.loading = true
				try {
					const savedUserInfo = uni.getStorageSync('userInfo')
					
					if (savedUserInfo && savedUserInfo.id) {
						// 处理地区字段格式
						if (savedUserInfo.region && typeof savedUserInfo.region === 'string') {
							savedUserInfo.region = [savedUserInfo.region]
						}
						
						this.userInfo = savedUserInfo
					} else {
						uni.showToast({
							title: '请先登录',
							icon: 'none'
						})
						setTimeout(() => {
							uni.reLaunch({
								url: '/pages/login/login'
							})
						}, 1500)
					}
				} catch (error) {
					console.error('获取用户信息失败', error)
					uni.showToast({
						title: '获取用户信息失败',
						icon: 'none'
					})
				} finally {
					this.loading = false
				}
			},
			/**
			 * 编辑个人资料
			 * 
			 * 功能：
			 * 跳转到编辑个人资料页面
			 */
			editProfile() {
				uni.navigateTo({
					url: '/pages/profile/editProfile/editProfile'
				})
			},
			/**
			 * 修改密码
			 * 
			 * 功能：
			 * 跳转到修改密码页面
			 */
			changePassword() {
				uni.navigateTo({
					url: '/pages/profile/changePassword/changePassword'
				})
			},
			/**
			 * 退出登录
			 * 
			 * 功能：
			 * 1. 显示确认对话框
			 * 2. 用户确认后调用Vuex的登出方法
			 * 3. 跳转到登录页面
			 */
			logout() {
				uni.showModal({
					title: '确认退出',
					content: '确定要退出登录吗？',
					success: (res) => {
						if (res.confirm) {
							// 调用Vuex的登出方法
							store.mutations.SET_LOGOUT(store.state)
							
							// 跳转到登录页面
							uni.reLaunch({
								url: '/pages/login/login'
							})
						}
					}
				})
			}
		}
	}
</script>

<style scoped>
.settings-container {
	padding: 20rpx;
	background-color: #f5f5f5;
	min-height: 100vh;
}

.profile-section {
	margin-bottom: 24rpx;
}

.profile-header {
	display: flex;
	align-items: center;
	margin-bottom: 28rpx;
}

.avatar-wrapper {
	width: 120rpx;
	height: 120rpx;
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	margin-right: 24rpx;
	box-shadow: 0 8rpx 20rpx rgba(102, 126, 234, 0.3);
}

.avatar-image {
	width: 120rpx;
	height: 120rpx;
	border-radius: 50%;
}

.avatar-emoji {
	font-size: 60rpx;
}

.user-info {
	flex: 1;
}

.user-name {
	display: block;
	font-size: 36rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 10rpx;
}

.user-level {
	display: flex;
	align-items: center;
	background: linear-gradient(135deg, #ffeaa7 0%, #fdcb6e 100%);
	padding: 6rpx 16rpx;
	border-radius: 20rpx;
}

.level-icon {
	font-size: 24rpx;
	margin-right: 6rpx;
}

.level-text {
	font-size: 22rpx;
	color: #d63031;
	font-weight: 600;
}

.edit-btn {
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	color: #fff;
	font-size: 26rpx;
	padding: 14rpx 28rpx;
	border-radius: 24rpx;
	font-weight: 500;
	box-shadow: 0 4rpx 12rpx rgba(102, 126, 234, 0.3);
}

.edit-btn:active {
	transform: scale(0.96);
}

.profile-info {
	padding-top: 24rpx;
	border-top: 2rpx solid #f0f0f0;
}

.info-item {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 20rpx 0;
}

.info-label {
	font-size: 28rpx;
	color: #666;
}

.info-value {
	font-size: 28rpx;
	color: #333;
	font-weight: 500;
}

.info-divider {
	height: 1px;
	background-color: #f8f8f8;
}

.settings-section {
	margin-bottom: 24rpx;
}

.settings-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 20rpx;
}

.settings-list {
	border-top: 2rpx solid #f0f0f0;
}

.setting-item {
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 28rpx 0;
	border-bottom: 2rpx solid #f8f8f8;
	transition: all 0.3s ease;
}

.setting-item:last-child {
	border-bottom: none;
}

.setting-item:active {
	background-color: #f8f8f8;
}

.setting-left {
	display: flex;
	align-items: center;
}

.setting-icon-wrapper {
	width: 64rpx;
	height: 64rpx;
	border-radius: 16rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	margin-right: 20rpx;
}

.password-icon {
	background: linear-gradient(135deg, #fd79a8 0%, #e84393 100%);
}

.logout-icon {
	background: linear-gradient(135deg, #fdcb6e 0%, #e17055 100%);
}

.setting-icon {
	font-size: 32rpx;
}

.setting-text {
	font-size: 30rpx;
	color: #333;
	font-weight: 500;
}

.setting-arrow {
	font-size: 40rpx;
	color: #ccc;
	font-weight: 300;
}
</style>
