<template>
	<view class="edit-profile-container">
		<!-- 头像上传 -->
		<view class="avatar-section">
			<view class="avatar-wrapper" @click="chooseAvatar">
				<image v-if="form.avatar" :src="getStaticUrl(form.avatar)" class="avatar-image"></image>
				<text v-else class="avatar-placeholder">👤</text>
				<view class="avatar-edit">
					<text class="edit-icon">📷</text>
				</view>
			</view>
			<text class="avatar-tip">点击更换头像</text>
		</view>
		
		<!-- 表单 -->
		<view class="form-section">
			<view class="form-item">
				<text class="form-label">昵称</text>
				<input class="form-input" v-model="form.nickname" placeholder="请输入昵称" />
			</view>
			
			<view class="form-item">
				<text class="form-label">手机号</text>
				<input class="form-input" v-model="form.phone" placeholder="请输入手机号" type="number" />
			</view>
			
			<view class="form-item">
				<text class="form-label">邮箱</text>
				<input class="form-input" v-model="form.email" placeholder="请输入邮箱" type="email" />
			</view>
			
			<view class="form-item">
				<text class="form-label">地区</text>
				<picker class="form-picker" mode="region" :value="form.region" @change="onRegionChange">
					<view class="picker-text">
						{{form.region && form.region.length > 0 ? form.region.join(' ') : '请选择地区'}}
					</view>
				</picker>
			</view>
		</view>
		
		<!-- 保存按钮 -->
		<view class="save-btn-container">
			<button class="save-btn" @click="saveProfile" :disabled="loading">
				{{loading ? '保存中...' : '保存资料'}}
			</button>
		</view>
	</view>
</template>

<script>
	// 导入HTTP请求工具
	import { request } from '@/utils/request.js'
	// 导入API接口配置
	import { API, getStaticUrl } from '@/utils/api.js'

	/**
	 * 编辑个人资料页面组件
	 * 
	 * 功能说明：
	 * 1. 提供用户资料编辑表单（昵称、手机号、邮箱、地区）
	 * 2. 支持表单验证（昵称必填、手机号格式、邮箱格式）
	 * 3. 保存后更新本地存储的用户信息
	 * 4. 保存成功后返回上一页
	 * 
	 * @author 反诈骗平台开发团队
	 * @version 1.0
	 */
	export default {
		/**
		 * 组件数据
		 * 
		 * form: 表单数据
		 * - nickname: 昵称
		 * - phone: 手机号
		 * - email: 邮箱
		 * - region: 地区（数组格式）
		 * 
		 * loading: 保存状态标识
		 */
		data() {
		return {
			form: {
				nickname: '',
				avatar: '',
				phone: '',
				email: '',
				region: []
			},
			loading: false
		}
	},
		/**
		 * 页面加载时执行
		 * 功能：初始化表单数据
		 */
		onLoad() {
			this.initForm()
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
			 * 初始化表单
			 * 
			 * 功能：
			 * 从本地存储获取用户信息并填充到表单
			 */
			initForm() {
				// 从本地存储获取用户信息
				const savedUserInfo = uni.getStorageSync('userInfo')
				
				if (savedUserInfo && savedUserInfo.id) {
					// 初始化表单
					this.form = {
						nickname: savedUserInfo.nickname || '',
						avatar: savedUserInfo.avatar || '',
						phone: savedUserInfo.phone || '',
						email: savedUserInfo.email || '',
						region: savedUserInfo.region || []
					}
				}
			},
			/**
			 * 选择头像
			 * 
			 * 功能：
			 * 1. 调用微信选择图片API
			 * 2. 上传图片到服务器
			 * 3. 设置头像URL到表单
			 */
			chooseAvatar() {
				uni.chooseImage({
					count: 1,
					sizeType: ['compressed'],
					sourceType: ['album', 'camera'],
					success: (res) => {
						const tempFilePaths = res.tempFilePaths
						// 上传图片
						uni.uploadFile({
							url: API.UPLOAD.AVATAR,
							filePath: tempFilePaths[0],
							name: 'file',
							header: {
								'Authorization': uni.getStorageSync('token')
							},
							formData: {
								'type': 'avatar'
							},
							success: (uploadRes) => {
								const data = JSON.parse(uploadRes.data)
								if (data.success) {
									this.form.avatar = data.data.url
									uni.showToast({
										title: '头像上传成功',
										icon: 'success'
									})
								} else {
									uni.showToast({
										title: '头像上传失败',
										icon: 'none'
									})
								}
							},
							fail: (err) => {
								console.error('上传失败', err)
								uni.showToast({
									title: '网络错误，请稍后重试',
									icon: 'none'
								})
							}
						})
					},
					fail: (err) => {
						console.error('选择图片失败', err)
					}
				})
			},
			/**
			 * 地区选择变化事件
			 * 
			 * @param {Object} e - 事件对象
			 * @param {Array} e.detail.value - 选中的地区数组
			 * 
			 * 功能：
			 * 更新表单中的地区数据
			 */
			onRegionChange(e) {
				this.form.region = e.detail.value
			},
			/**
			 * 保存个人资料
			 * 
			 * 功能：
			 * 1. 表单验证（昵称必填、手机号格式、邮箱格式）
			 * 2. 调用后端接口保存资料
			 * 3. 更新本地存储的用户信息
			 * 4. 保存成功后返回上一页
			 * 
			 * 错误处理：
			 * - 表单验证失败提示
			 * - 网络请求错误提示
			 * - finally块确保loading状态被重置
			 */
			async saveProfile() {
				// 表单验证
				if (!this.form.nickname.trim()) {
					uni.showToast({
						title: '请输入昵称',
						icon: 'none'
					})
					return
				}
				
				// 手机号验证
				if (this.form.phone && !/^1[3-9]\d{9}$/.test(this.form.phone)) {
					uni.showToast({
						title: '请输入正确的手机号',
						icon: 'none'
					})
					return
				}
				
				// 邮箱验证
				if (this.form.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.form.email)) {
					uni.showToast({
						title: '请输入正确的邮箱',
						icon: 'none'
					})
					return
				}
				
				this.loading = true
				try {
					console.log('开始保存用户资料，API路径:', API.USER.UPDATE)
					console.log('提交的数据:', this.form)
					
					// 调用后端API保存资料
					const res = await request(API.USER.UPDATE, 'POST', this.form)
					
					console.log('保存资料响应:', res)
					
					if (res && res.success) {
						// 根据响应消息显示不同的提示
						if (res.message && res.message.includes('需要审核')) {
							uni.showToast({
								title: '昵称和头像已提交审核',
								icon: 'none',
								duration: 2000
							})
							
							// 如果昵称或头像需要审核，重置表单中的这些字段为原来的值
							const savedUserInfo = uni.getStorageSync('userInfo')
							if (this.form.nickname !== savedUserInfo.nickname) {
								this.form.nickname = savedUserInfo.nickname
							}
							if (this.form.avatar !== savedUserInfo.avatar) {
								this.form.avatar = savedUserInfo.avatar
							}
						} else {
							uni.showToast({
								title: '保存成功',
								icon: 'success'
							})
						}
						
						// 更新本地存储的用户信息（只更新不需要审核的字段）
						const savedUserInfo = uni.getStorageSync('userInfo')
						const updatedUserInfo = {
							...savedUserInfo,
							// 只更新邮箱和手机号，昵称和头像不更新
							email: this.form.email,
							phone: this.form.phone,
							region: this.form.region
						}
						uni.setStorageSync('userInfo', updatedUserInfo)
						
						// 返回上一页
						setTimeout(() => {
							uni.navigateBack()
						}, 1500)
					} else {
						uni.showToast({
							title: res.message || '保存失败',
							icon: 'none'
						})
					}
				} catch (error) {
					console.error('保存资料失败', error)
					console.error('错误详情:', error.errMsg, error.message, error.stack)
					uni.showToast({
						title: error.errMsg || error.message || '保存失败',
						icon: 'none'
					})
				} finally {
					this.loading = false
				}
			}
		}
	}
</script>

<style scoped>
.edit-profile-container {
	padding: 20rpx;
	background-color: #f5f5f5;
	min-height: 100vh;
}

.avatar-section {
	display: flex;
	flex-direction: column;
	align-items: center;
	margin-bottom: 30rpx;
}

.avatar-wrapper {
	position: relative;
	width: 160rpx;
	height: 160rpx;
	border-radius: 50%;
	background-color: #f0f0f0;
	display: flex;
	align-items: center;
	justify-content: center;
	margin-bottom: 10rpx;
	cursor: pointer;
	overflow: hidden;
}

.avatar-image {
	width: 100%;
	height: 100%;
	border-radius: 50%;
	object-fit: cover;
}

.avatar-placeholder {
	font-size: 60rpx;
}

.avatar-edit {
	position: absolute;
	bottom: 0;
	left: 0;
	right: 0;
	height: 40rpx;
	background-color: rgba(0, 0, 0, 0.5);
	display: flex;
	align-items: center;
	justify-content: center;
}

.edit-icon {
	font-size: 20rpx;
	color: #fff;
}

.avatar-tip {
	font-size: 24rpx;
	color: #666;
}

.form-section {
	background-color: #fff;
	border-radius: 16rpx;
	padding: 20rpx;
	margin-bottom: 40rpx;
}

.form-item {
	display: flex;
	flex-direction: column;
	margin-bottom: 30rpx;
}

.form-label {
	font-size: 32rpx;
	color: #333;
	margin-bottom: 12rpx;
}

.form-input {
	height: 80rpx;
	border: 1rpx solid #eee;
	border-radius: 12rpx;
	padding: 0 20rpx;
	font-size: 32rpx;
}

.form-input:disabled {
	background-color: #f5f5f5;
	color: #999;
}

.form-picker {
	height: 80rpx;
	border: 1rpx solid #eee;
	border-radius: 12rpx;
	padding: 0 20rpx;
	font-size: 32rpx;
}

.picker-text {
	line-height: 80rpx;
	color: #666;
}

.save-btn-container {
	display: flex;
	justify-content: center;
	padding: 0 20rpx;
}

.save-btn {
	width: 100%;
	height: 88rpx;
	background-color: #1890ff;
	color: #fff;
	border-radius: 44rpx;
	font-size: 36rpx;
	font-weight: bold;
}

.save-btn:disabled {
	background-color: #ccc;
}
</style>