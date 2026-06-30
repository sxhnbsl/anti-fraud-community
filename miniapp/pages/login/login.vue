<template>
	<view class="login-container">
		<!-- 顶部装饰 -->
		<view class="top-decoration">
			<view class="decoration-circle circle-1"></view>
			<view class="decoration-circle circle-2"></view>
			<view class="decoration-circle circle-3"></view>
		</view>
		
		<!-- logo区域 -->
		<view class="logo-section">
			<view class="logo-wrapper">
				<text class="logo-icon">🛡️</text>
			</view>
			<text class="app-name">防诈骗科普平台</text>
			<text class="slogan">守护财产安全，远离诈骗陷阱</text>
		</view>
		
		<!-- 登录表单 -->
		<view class="form-section">
			<view class="form-title">
				<text class="title">欢迎回来</text>
				<text class="subtitle">请登录您的账号</text>
			</view>
			
			<view class="form-item">
				<view class="input-wrapper">
					<view class="input-icon">
						<text class="icon-text">👤</text>
					</view>
					<input 
					class="input-field" 
					type="text" 
					placeholder="请输入昵称/手机号/邮箱" 
					v-model="form.username"
					maxlength="20"
					@input="handleUsernameInput"
				/>
				</view>
				<text v-if="errors.username" class="error-message">{{errors.username}}</text>
			</view>
			
			<view class="form-item">
				<view class="input-wrapper">
					<view class="input-icon">
						<text class="icon-text">🔒</text>
					</view>
					<!-- #ifdef H5 -->
					<input 
						class="input-field" 
						type="password" 
						placeholder="请输入密码" 
						v-model="form.password"
						maxlength="16"
						@input="handlePasswordInput"
					/>
					<!-- #endif -->
					<!-- #ifndef H5 -->
					<input 
						class="input-field" 
						type="text" 
						password="true"
						placeholder="请输入密码" 
						v-model="form.password"
						maxlength="16"
						@input="handlePasswordInput"
					/>
					<!-- #endif -->
				</view>
				<text v-if="errors.password" class="error-message">{{errors.password}}</text>
			</view>
			
			<button class="login-button" @click="handleLogin" :disabled="loading">
				{{loading ? '登录中...' : '安全登录'}}
			</button>
			
			<view class="register-link">
				<text>还没有账号？</text>
				<text class="link" @click="navigateToRegister">立即注册</text>
			</view>
		</view>
		
		<!-- 底部信息 -->
		<view class="footer">
			<text class="copyright">© 2026 防诈骗科普平台</text>
		</view>
	</view>
</template>

<script>
	// 导入API配置文件，用于获取后端接口地址
	import { API } from '@/utils/api.js'
	// 导入请求工具，用于发送HTTP请求
	import { request } from '@/utils/request.js'
	// 导入状态管理store，用于管理全局状态
	import store from '@/store/index.js'

	export default {
		// 组件数据
		data() {
			return {
				// 登录表单数据
				form: {
					username: '', // 账号
					password: '' // 密码
				},
				// 表单验证错误信息
				errors: {
					username: '', // 账号错误信息
					password: '' // 密码错误信息
				},
				// 登录加载状态
				loading: false
			}
		},
		// 页面加载时执行
		onLoad() {
			// 检查用户是否已登录
			this.checkLoginStatus()
		},
		// 组件方法
		methods: {
			// 检查登录状态
			// 如果用户已登录，直接跳转到首页
			checkLoginStatus() {
				// 从本地存储获取登录状态
				const isLoggedIn = uni.getStorageSync('isLoggedIn')
				// 如果已登录，跳转到首页
				if (isLoggedIn) {
					uni.switchTab({
						url: '/pages/index/index'
					})
				}
			},
			// 处理账号输入事件
			// @param {Object} e - 输入事件对象
			handleUsernameInput(e) {
				// 更新账号
				this.form.username = e.detail.value
				// 清除账号错误提示
				this.errors.username = ''
			},
			// 处理密码输入事件
			// @param {Object} e - 输入事件对象
			handlePasswordInput(e) {
				// 更新密码
				this.form.password = e.detail.value
				// 清除密码错误提示
				this.errors.password = ''
				
				// 检查密码是否包含汉字
				if (/[\u4e00-\u9fa5]/.test(this.form.password)) {
					this.errors.password = '密码不能包含汉字'
				}
			},
			// 验证表单数据
			// @returns {boolean} 验证是否通过
			validateForm() {
				let isValid = true
				
				// 验证账号
				if (!this.form.username) {
					this.errors.username = '请输入账号'
					isValid = false
				}
				
				// 验证密码
				if (!this.form.password) {
					this.errors.password = '请输入密码'
					isValid = false
				} else if (this.form.password.length < 6) {
					this.errors.password = '密码长度不能少于6个字符'
					isValid = false
				} else if (/[\u4e00-\u9fa5]/.test(this.form.password)) {
					this.errors.password = '密码不能包含汉字'
					isValid = false
				}
				
				return isValid
			},
			// 处理登录按钮点击事件
			async handleLogin() {
				// 先验证表单
				if (this.validateForm()) {
					// 设置加载状态
					this.loading = true
					try {
						// 打印日志，方便调试
						console.log('开始登录，表单数据：', this.form)
						// 发送登录请求到后端
						const res = await request(API.AUTH.LOGIN, 'POST', this.form)
						console.log('登录响应：', res)
						
						// 检查响应数据格式
						if (!res || !res.token) {
							throw new Error('登录响应格式错误')
						}
						
						// 显示登录成功提示
						uni.showToast({
							title: '登录成功',
							icon: 'success'
						})
						
						// 构建完整的用户信息对象
						const userInfo = res.user || {}
						const completeUserInfo = {
							id: userInfo.id, // 用户ID
							username: userInfo.username || this.form.username, // 用户名
							nickname: userInfo.nickname || userInfo.username || this.form.username, // 昵称
							phone: userInfo.phone || '', // 手机号
							email: userInfo.email || '', // 邮箱
							region: userInfo.region || [], // 地区
							level: userInfo.level || 1, // 用户等级
							knowledgeCount: userInfo.knowledgeCount || 0, // 学习的知识数量
							testCount: userInfo.testCount || 0, // 参与测试次数
							score: userInfo.score || 0, // 积分
							createdAt: userInfo.createdAt || '' // 注册时间
						}
						
						// 保存登录状态和用户信息到store
						store.mutations.SET_LOGIN(store.state, {
							token: res.token,
							userInfo: completeUserInfo
						})
						
						// 跳转到首页
						uni.switchTab({
							url: '/pages/index/index'
						})
					} catch (error) {
						// 登录失败，显示错误提示
						console.error('登录失败', error)
						let errorMsg = error.message || '登录失败'
						// 处理特定错误信息
						if (errorMsg.includes('账号待审核')) {
							errorMsg = '账号待审核，请等待管理员审核'
						} else if (errorMsg.includes('账号已被拒绝')) {
							errorMsg = '账号已被拒绝，请联系管理员'
						} else if (errorMsg.includes('账号已被停用')) {
							errorMsg = '账号已被停用，请联系管理员'
						} else if (errorMsg.includes('用户名或密码错误')) {
								errorMsg = '账号或密码错误'
							}
						uni.showToast({
							title: errorMsg,
							icon: 'none'
						})
					} finally {
						// 无论成功失败，都关闭加载状态
						this.loading = false
					}
				}
			},
			// 跳转到注册页面
			navigateToRegister() {
				uni.navigateTo({
					url: '/pages/register/register'
				})
			}
		}
	}
</script>

<style scoped>
	.login-container {
		min-height: 100vh;
		background: linear-gradient(180deg, #e3f2fd 0%, #f5f5f5 50%, #ffffff 100%);
		display: flex;
		flex-direction: column;
		align-items: center;
		padding: 40rpx 40rpx 60rpx;
		position: relative;
		overflow: hidden;
	}

	.top-decoration {
		position: absolute;
		top: 0;
		left: 0;
		right: 0;
		height: 300rpx;
		pointer-events: none;
	}

	.decoration-circle {
		position: absolute;
		border-radius: 50%;
		opacity: 0.3;
	}

	.circle-1 {
		width: 200rpx;
		height: 200rpx;
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
		top: -100rpx;
		left: -50rpx;
	}

	.circle-2 {
		width: 150rpx;
		height: 150rpx;
		background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
		top: -30rpx;
		right: 100rpx;
	}

	.circle-3 {
		width: 100rpx;
		height: 100rpx;
		background: linear-gradient(135deg, #81C784 0%, #C8E6C9 100%);
		top: 80rpx;
		right: -30rpx;
	}

	.logo-section {
		display: flex;
		flex-direction: column;
		align-items: center;
		margin-bottom: 60rpx;
		margin-top: 60rpx;
		position: relative;
		z-index: 1;
	}

	.logo-wrapper {
		width: 160rpx;
		height: 160rpx;
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
		border-radius: 40rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		margin-bottom: 24rpx;
		box-shadow: 0 12rpx 32rpx rgba(102, 126, 234, 0.3);
	}

	.logo-icon {
		font-size: 80rpx;
	}

	.app-name {
		font-size: 38rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 12rpx;
	}

	.slogan {
		font-size: 24rpx;
		color: #666;
	}

	.form-section {
		width: 100%;
		background-color: #fff;
		border-radius: 24rpx;
		padding: 50rpx 40rpx;
		box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.08);
		margin-bottom: 40rpx;
		position: relative;
		z-index: 1;
	}

	.form-title {
		margin-bottom: 40rpx;
		text-align: center;
	}

	.form-title .title {
		display: block;
		font-size: 36rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 8rpx;
	}

	.form-title .subtitle {
		font-size: 26rpx;
		color: #999;
	}

	.form-item {
		margin-bottom: 32rpx;
	}

	.input-wrapper {
		display: flex;
		align-items: center;
		background-color: #f8f9fa;
		border-radius: 16rpx;
		padding: 24rpx 20rpx;
		transition: all 0.3s ease;
		border: 2rpx solid transparent;
	}

	.input-wrapper:focus-within {
		background-color: #fff;
		border-color: #667eea;
		box-shadow: 0 0 0 4rpx rgba(102, 126, 234, 0.1);
	}

	.input-icon {
		height: 44rpx;
		width: 44rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		margin-right: 16rpx;
	}

	.icon-text {
		font-size: 36rpx;
	}

	.input-field {
		flex: 1;
		font-size: 28rpx;
		color: #333;
		padding: 0;
	}

	.input-field::placeholder {
		color: #aaa;
	}

	.error-message {
		font-size: 22rpx;
		color: #ff6b6b;
		margin-top: 12rpx;
		display: block;
		padding-left: 8rpx;
	}

	.login-button {
		width: 100%;
		height: 90rpx;
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
		color: #fff;
		font-size: 32rpx;
		font-weight: bold;
		border-radius: 45rpx;
		margin-bottom: 30rpx;
		box-shadow: 0 8rpx 24rpx rgba(102, 126, 234, 0.3);
		transition: all 0.3s ease;
	}

	.login-button:active {
		transform: scale(0.96);
		box-shadow: 0 4rpx 12rpx rgba(102, 126, 234, 0.2);
	}

	.login-button[disabled] {
		opacity: 0.7;
	}

	.register-link {
		display: flex;
		justify-content: center;
		align-items: center;
		font-size: 26rpx;
		color: #666;
	}

	.register-link .link {
		color: #667eea;
		margin-left: 10rpx;
		font-weight: 600;
		font-size: 28rpx;
	}

	.footer {
		margin-top: auto;
		padding-top: 40rpx;
		position: relative;
		z-index: 1;
	}

	.copyright {
		font-size: 22rpx;
		color: #aaa;
	}
</style>
