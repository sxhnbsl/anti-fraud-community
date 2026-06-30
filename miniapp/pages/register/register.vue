<template>
	<!-- 注册页面容器 -->
	<view class="register-container">
		<!-- 顶部装饰圆圈 -->
		<view class="top-decoration">
			<view class="decoration-circle circle-1"></view>
			<view class="decoration-circle circle-2"></view>
			<view class="decoration-circle circle-3"></view>
		</view>
		
		<!-- logo和标题区域 -->
		<view class="logo-section">
			<view class="logo-wrapper">
				<text class="logo-emoji">🛡️</text>
			</view>
			<text class="app-name">注册账号</text>
			<text class="slogan">加入防诈骗科普平台</text>
		</view>
		
		<!-- 注册表单区域 -->
		<view class="form-section card">
			<!-- 昵称输入框 -->
			<view class="form-item">
				<view class="input-wrapper">
					<view class="input-icon-wrapper">
						<text class="icon-text">👤</text>
					</view>
					<input 
						class="input-field" 
						type="text" 
						placeholder="请输入昵称" 
						v-model="form.nickname"
						maxlength="20"
						@input="handleNicknameInput"
					/>
				</view>
				<!-- 昵称错误提示 -->
				<text v-if="errors.nickname" class="error-message">{{errors.nickname}}</text>
			</view>
			
			<!-- 密码输入框 -->
			<view class="form-item">
				<view class="input-wrapper">
					<view class="input-icon-wrapper">
						<text class="icon-text">🔒</text>
					</view>
					<input 
						class="input-field" 
						type="password" 
						placeholder="请输入密码" 
						v-model="form.password"
						maxlength="16"
						@input="handlePasswordInput"
					/>
				</view>
				<!-- 密码错误提示 -->
				<text v-if="errors.password" class="error-message">{{errors.password}}</text>
			</view>
			
			<!-- 手机号输入框（可选） -->
			<view class="form-item">
				<view class="input-wrapper">
					<view class="input-icon-wrapper">
						<text class="icon-text">📱</text>
					</view>
					<input 
						class="input-field" 
						type="number" 
						placeholder="请输入手机号（可选）" 
						v-model="form.phone"
						maxlength="11"
						@input="handlePhoneInput"
					/>
				</view>
				<!-- 手机号错误提示 -->
				<text v-if="errors.phone" class="error-message">{{errors.phone}}</text>
			</view>
			
			<!-- 邮箱输入框（可选） -->
			<view class="form-item">
				<view class="input-wrapper">
					<view class="input-icon-wrapper">
						<text class="icon-text">📧</text>
					</view>
					<input 
						class="input-field" 
						type="text" 
						placeholder="请输入邮箱（可选）" 
						v-model="form.email"
						maxlength="50"
						@input="handleEmailInput"
					/>
				</view>
				<!-- 邮箱错误提示 -->
				<text v-if="errors.email" class="error-message">{{errors.email}}</text>
			</view>
			
			<!-- 注册按钮 -->
			<button class="register-button gradient-button" @click="handleRegister" :disabled="loading">
				{{loading ? '注册中...' : '注册'}}
			</button>
			
			<!-- 跳转到登录页面链接 -->
			<view class="login-link">
				<text>已有账号？</text>
				<text class="link-text" @click="navigateToLogin">立即登录</text>
			</view>
		</view>
		
		<!-- 底部版权信息 -->
		<view class="footer">
			<text class="copyright">© 2026 防诈骗科普平台</text>
		</view>
	</view>
</template>

<script>
	// 导入API接口配置
	import { API } from '@/utils/api.js'
	// 导入HTTP请求工具
	import { request } from '@/utils/request.js'
	// 导入状态管理store
	import store from '@/store/index.js'

	/**
	 * 用户注册页面组件
	 * 
	 * 功能说明：
	 * 1. 提供用户注册表单，包括昵称、密码、手机号、邮箱
	 * 2. 实时表单验证，包括必填项检查和格式验证
	 * 3. 密码安全检查，防止包含中文字符
	 * 4. 手机号和邮箱格式验证（可选字段）
	 * 5. 注册成功后自动跳转到登录页面
	 * 
	 * @author 反诈骗平台开发团队
	 * @version 1.0
	 */
	export default {
		/**
		 * 组件数据
		 * 
		 * form: 注册表单数据
		 * - password: 密码（必填）
		 * - nickname: 昵称（必填）
		 * - phone: 手机号（可选）
		 * - email: 邮箱（可选）
		 * 
		 * errors: 表单验证错误信息
		 * - password: 密码错误提示
		 * - nickname: 昵称错误提示
		 * - phone: 手机号错误提示
		 * - email: 邮箱错误提示
		 * 
		 * loading: 注册按钮加载状态
		 */
		data() {
			return {
				form: {
					password: '',
					nickname: '',
					phone: '',
					email: ''
				},
				errors: {
					password: '',
					nickname: '',
					phone: '',
					email: ''
				},
				loading: false
			}
		},
		methods: {
			/**
			 * 处理密码输入
			 * 
			 * @param {Object} e - 输入事件对象
			 * @param {string} e.detail.value - 输入的密码值
			 * 
			 * 功能：
			 * 1. 更新表单数据
			 * 2. 清除密码错误提示
			 * 3. 检查密码是否包含中文字符，包含则提示错误
			 */
			handlePasswordInput(e) {
				this.form.password = e.detail.value
				this.errors.password = ''
				
				// 使用正则表达式检查是否包含中文字符
				if (/[\u4e00-\u9fa5]/.test(this.form.password)) {
					this.errors.password = '密码不能包含汉字'
				}
			},
			
			/**
			 * 处理昵称输入
			 * 
			 * @param {Object} e - 输入事件对象
			 * @param {string} e.detail.value - 输入的昵称值
			 * 
			 * 功能：
			 * 1. 更新表单数据
			 * 2. 清除昵称错误提示
			 */
			handleNicknameInput(e) {
				this.form.nickname = e.detail.value
				this.errors.nickname = ''
			},
			
			/**
			 * 处理手机号输入
			 * 
			 * @param {Object} e - 输入事件对象
			 * @param {string} e.detail.value - 输入的手机号值
			 * 
			 * 功能：
			 * 1. 更新表单数据
			 * 2. 清除手机号错误提示
			 * 3. 如果输入了手机号，验证格式是否正确
			 * 4. 手机号格式：1开头，第二位3-9，共11位数字
			 */
			handlePhoneInput(e) {
				this.form.phone = e.detail.value
				this.errors.phone = ''
				
				// 如果输入了手机号，验证格式
				if (this.form.phone) {
					// 中国手机号正则表达式：1开头，第二位3-9，共11位
					const phoneRegex = /^1[3-9]\d{9}$/
					if (!phoneRegex.test(this.form.phone)) {
						this.errors.phone = '请输入正确的手机号格式'
					}
				}
			},
			
			/**
			 * 处理邮箱输入
			 * 
			 * @param {Object} e - 输入事件对象
			 * @param {string} e.detail.value - 输入的邮箱值
			 * 
			 * 功能：
			 * 1. 更新表单数据
			 * 2. 清除邮箱错误提示
			 * 3. 如果输入了邮箱，验证格式是否正确
			 * 4. 邮箱格式：xxx@xxx.xxx
			 */
			handleEmailInput(e) {
				this.form.email = e.detail.value
				this.errors.email = ''
				
				// 如果输入了邮箱，验证格式
				if (this.form.email) {
					// 邮箱正则表达式
					const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
					if (!emailRegex.test(this.form.email)) {
						this.errors.email = '请输入正确的邮箱格式'
					}
				}
			},
			
			/**
			 * 验证表单
			 * 
			 * @returns {boolean} 表单是否验证通过
			 * 
			 * 验证规则：
			 * 1. 密码：必填，长度不少于6位，不能包含中文
			 * 2. 昵称：必填
			 * 3. 手机号：如果填写，必须符合中国手机号格式
			 * 4. 邮箱：如果填写，必须符合邮箱格式
			 */
			validateForm() {
				let isValid = true
				
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
				
				// 验证昵称
				if (!this.form.nickname) {
					this.errors.nickname = '请输入昵称'
					isValid = false
				}
				
				// 验证手机号（如果填写）
				if (this.form.phone) {
					const phoneRegex = /^1[3-9]\d{9}$/
					if (!phoneRegex.test(this.form.phone)) {
						this.errors.phone = '请输入正确的手机号格式'
						isValid = false
					}
				}
				
				// 验证邮箱（如果填写）
				if (this.form.email) {
					const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
					if (!emailRegex.test(this.form.email)) {
						this.errors.email = '请输入正确的邮箱格式'
						isValid = false
					}
				}
				
				return isValid
			},
			
			/**
			 * 处理注册提交
			 * 
			 * 功能：
			 * 1. 验证表单数据
			 * 2. 调用后端注册接口
			 * 3. 注册成功后显示成功提示
			 * 4. 延迟1秒后返回登录页面
			 * 
			 * 错误处理：
			 * - 捕获网络请求错误
			 * - 在控制台输出错误信息
			 * - finally块确保loading状态被重置
			 */
			async handleRegister() {
				if (this.validateForm()) {
					this.loading = true
					try {
						// 调用注册接口
						const res = await request(API.AUTH.REGISTER, 'POST', this.form)
						
						// 显示注册成功提示
						uni.showToast({
							title: '注册成功',
							icon: 'success'
						})
						
						// 延迟1秒后返回登录页面
						setTimeout(() => {
							uni.navigateBack()
						}, 1000)
					} catch (error) {
						console.error('注册失败', error)
						uni.showToast({
							title: error.message || '注册失败，请稍后重试',
							icon: 'none',
							duration: 2000
						})
					} finally {
						// 重置loading状态
						this.loading = false
					}
				}
			},
			
			/**
			 * 跳转到登录页面
			 * 
			 * 功能：
			 * 使用uni.navigateBack返回上一页（登录页面）
			 */
			navigateToLogin() {
				uni.navigateBack()
			}
		}
	}
</script>

<style scoped>
	/* 注册页面容器 */
	.register-container {
		min-height: 100vh;
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
		display: flex;
		flex-direction: column;
		align-items: center;
		padding: 40rpx 40rpx 60rpx;
		position: relative;
		overflow: hidden;
	}

	/* 顶部装饰区域 */
	.top-decoration {
		position: absolute;
		top: 0;
		left: 0;
		right: 0;
		height: 300rpx;
		overflow: hidden;
	}

	/* 装饰圆圈 */
	.decoration-circle {
		position: absolute;
		border-radius: 50%;
		background: rgba(255, 255, 255, 0.1);
	}

	/* 装饰圆圈1 - 左上角 */
	.circle-1 {
		width: 200rpx;
		height: 200rpx;
		top: -80rpx;
		left: -60rpx;
	}

	/* 装饰圆圈2 - 右上角大圆 */
	.circle-2 {
		width: 300rpx;
		height: 300rpx;
		top: -120rpx;
		right: -100rpx;
	}

	/* 装饰圆圈3 - 右侧小圆 */
	.circle-3 {
		width: 150rpx;
		height: 150rpx;
		top: 100rpx;
		right: 40rpx;
	}

	/* logo和标题区域 */
	.logo-section {
		display: flex;
		flex-direction: column;
		align-items: center;
		margin-bottom: 60rpx;
		z-index: 1;
	}

	/* logo容器 */
	.logo-wrapper {
		width: 160rpx;
		height: 160rpx;
		background: linear-gradient(135deg, #fff 0%, #f0f0f0 100%);
		border-radius: 50%;
		display: flex;
		align-items: center;
		justify-content: center;
		box-shadow: 0 8rpx 30rpx rgba(0, 0, 0, 0.2);
		margin-bottom: 30rpx;
	}

	/* logo表情符号 */
	.logo-emoji {
		font-size: 80rpx;
	}

	/* 应用名称 */
	.app-name {
		font-size: 44rpx;
		font-weight: bold;
		color: #fff;
		margin-bottom: 12rpx;
		text-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
	}

	/* 标语文字 */
	.slogan {
		font-size: 26rpx;
		color: rgba(255, 255, 255, 0.85);
	}

	/* 表单区域 */
	.form-section {
		width: 100%;
		z-index: 1;
	}

	/* 表单项容器 */
	.form-item {
		margin-bottom: 32rpx;
	}

	/* 输入框包装器 */
	.input-wrapper {
		display: flex;
		align-items: center;
		background-color: #f8f9fa;
		border-radius: 20rpx;
		padding: 0 24rpx;
		height: 96rpx;
		transition: all 0.3s ease;
	}

	/* 输入框获得焦点时的样式 */
	.input-wrapper:focus-within {
		background-color: #fff;
		box-shadow: 0 4rpx 20rpx rgba(102, 126, 234, 0.15);
	}

	/* 输入框图标容器 */
	.input-icon-wrapper {
		width: 56rpx;
		height: 56rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		margin-right: 16rpx;
	}

	/* 图标文字 */
	.icon-text {
		font-size: 36rpx;
	}

	/* 输入框字段 */
	.input-field {
		flex: 1;
		font-size: 30rpx;
		color: #333;
		height: 100%;
	}

	/* 输入框占位符样式 */
	.input-field::placeholder {
		color: #999;
	}

	/* 错误提示信息 */
	.error-message {
		font-size: 24rpx;
		color: #ff6b6b;
		margin-top: 12rpx;
		display: block;
		padding-left: 8rpx;
	}

	/* 注册按钮 */
	.register-button {
		width: 100%;
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
		margin-top: 20rpx;
	}

	/* 注册按钮按下效果 */
	.register-button:active {
		transform: scale(0.96);
	}

	/* 登录链接区域 */
	.login-link {
		display: flex;
		justify-content: center;
		align-items: center;
		font-size: 26rpx;
		color: #666;
		margin-top: 32rpx;
	}

	/* 链接文字样式 */
	.link-text {
		color: #667eea;
		margin-left: 10rpx;
		font-weight: 600;
	}

	/* 底部区域 */
	.footer {
		margin-top: auto;
		padding-top: 40rpx;
		z-index: 1;
	}

	/* 版权信息 */
	.copyright {
		font-size: 24rpx;
		color: rgba(255, 255, 255, 0.7);
	}
</style>
