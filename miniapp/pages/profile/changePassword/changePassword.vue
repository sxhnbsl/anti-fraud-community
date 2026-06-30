<template>
	<view class="change-password-container">
		<!-- 表单 -->
		<view class="form-section">
			<view class="form-item">
				<text class="form-label">旧密码</text>
				<input class="form-input" v-model="form.oldPassword" placeholder="请输入旧密码" type="password" />
			</view>
			
			<view class="form-item">
				<text class="form-label">新密码</text>
				<input class="form-input" v-model="form.newPassword" placeholder="请输入新密码" type="password" />
			</view>
			
			<view class="form-item">
				<text class="form-label">确认新密码</text>
				<input class="form-input" v-model="form.confirmPassword" placeholder="请再次输入新密码" type="password" />
			</view>
		</view>
		
		<!-- 保存按钮 -->
		<view class="save-btn-container">
			<button class="save-btn" @click="changePassword" :disabled="loading">
				{{loading ? '修改中...' : '修改密码'}}
			</button>
		</view>
	</view>
</template>

<script>
	// 导入HTTP请求工具
	import { request } from '@/utils/request.js'
	// 导入API接口配置
	import { API } from '@/utils/api.js'

	/**
	 * 修改密码页面组件
	 * 
	 * 功能说明：
	 * 1. 提供修改密码表单（旧密码、新密码、确认新密码）
	 * 2. 支持表单验证（密码长度、两次密码是否一致）
	 * 3. 调用后端API修改密码
	 * 4. 修改成功后返回上一页
	 * 
	 * @author 反诈骗平台开发团队
	 * @version 1.0
	 */
	export default {
		/**
		 * 组件数据
		 * 
		 * form: 表单数据
		 * - oldPassword: 旧密码
		 * - newPassword: 新密码
		 * - confirmPassword: 确认新密码
		 * 
		 * loading: 保存状态标识
		 */
		data() {
			return {
				form: {
					oldPassword: '',
					newPassword: '',
					confirmPassword: ''
				},
				loading: false
			}
		},
		methods: {
			/**
			 * 修改密码
			 * 
			 * 功能：
			 * 1. 表单验证
			 * 2. 调用API修改密码
			 * 3. 处理响应
			 */
			changePassword() {
				// 表单验证
				if (!this.form.oldPassword) {
					uni.showToast({
						title: '请输入旧密码',
						icon: 'none'
					})
					return
				}
				
				if (!this.form.newPassword) {
					uni.showToast({
						title: '请输入新密码',
						icon: 'none'
					})
					return
				}
				
				if (this.form.newPassword.length < 6) {
					uni.showToast({
						title: '新密码至少6位',
						icon: 'none'
					})
					return
				}
				
				if (this.form.newPassword !== this.form.confirmPassword) {
					uni.showToast({
						title: '两次输入的密码不一致',
						icon: 'none'
					})
					return
				}
				
				// 开始修改密码
				this.loading = true
				request(API.USER.CHANGE_PASSWORD, 'POST', {
					oldPassword: this.form.oldPassword,
					newPassword: this.form.newPassword
				}).then(res => {
					if (res.success) {
						uni.showToast({
							title: '密码修改成功',
							icon: 'success'
						})
						// 延迟返回上一页
						setTimeout(() => {
							uni.navigateBack()
						}, 1500)
					} else {
						uni.showToast({
							title: res.message || '修改失败',
							icon: 'none'
						})
					}
				}).catch(err => {
					console.error('修改密码失败', err)
					uni.showToast({
						title: '网络错误，请稍后重试',
						icon: 'none'
					})
				}).finally(() => {
					this.loading = false
				})
			}
		}
	}
</script>

<style scoped>
.change-password-container {
	padding: 20rpx;
	background-color: #f5f5f5;
	min-height: 100vh;
}

.form-section {
	background-color: #fff;
	border-radius: 12rpx;
	padding: 30rpx;
	margin-bottom: 30rpx;
	box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.form-item {
	margin-bottom: 30rpx;
}

.form-item:last-child {
	margin-bottom: 0;
}

.form-label {
	display: block;
	font-size: 28rpx;
	color: #333;
	margin-bottom: 10rpx;
	font-weight: 500;
}

.form-input {
	width: 100%;
	height: 80rpx;
	border: 2rpx solid #e5e5e5;
	border-radius: 8rpx;
	padding: 0 20rpx;
	font-size: 28rpx;
	color: #333;
	background-color: #f9f9f9;
}

.form-input:focus {
	border-color: #007aff;
	background-color: #fff;
}

.save-btn-container {
	padding: 0 20rpx;
}

.save-btn {
	width: 100%;
	height: 90rpx;
	background-color: #007aff;
	color: #fff;
	font-size: 32rpx;
	font-weight: 500;
	border-radius: 12rpx;
	border: none;
}

.save-btn:disabled {
	background-color: #ccc;
}
</style>