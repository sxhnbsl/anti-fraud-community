<template>
	<view class="banner-edit-container">
		<view class="header">
			<view class="header-content">
				<view class="header-left" @click="goBack">
					<text class="back-icon">←</text>
				</view>
				<text class="title">{{isEdit ? '编辑轮播图' : '添加轮播图'}}</text>
				<view class="header-right" @click="saveBanner">
					<text class="save-icon">💾</text>
				</view>
			</view>
		</view>
		
		<view class="form-container">
			<!-- 轮播图图片 -->
			<view class="form-item">
				<text class="form-label">轮播图图片</text>
				<view class="image-uploader">
					<view class="upload-area" @click="chooseImage">
						<image v-if="banner.imageUrl" :src="getStaticUrl(banner.imageUrl)" mode="aspectFill" class="uploaded-image"></image>
						<view v-else class="upload-placeholder">
							<text class="upload-icon">🖼️</text>
							<text class="upload-text">点击上传轮播图</text>
						</view>
					</view>
					<text class="form-hint">建议尺寸：1080x420px，支持JPG、PNG格式</text>
			</view>
			
			<!-- 轮播图标题 -->
			<view class="form-item">
				<text class="form-label">轮播图标题</text>
				<input 
					v-model="banner.title" 
					class="form-input" 
					placeholder="请输入轮播图标题"
					maxlength="100"
				/>
			</view>
			
			<!-- 链接类型 -->
			<view class="form-item">
				<text class="form-label">链接类型</text>
				<view class="form-select">
					<picker 
						:range="linkTypeOptions" 
						:range-key="'label'"
						v-model="linkTypeIndex"
						@change="handleLinkTypeChange"
					>
						<view class="picker-display">
							<text>{{linkTypeOptions[linkTypeIndex].label}}</text>
							<text class="picker-arrow">▼</text>
						</view>
					</picker>
				</view>
			</view>
			
			<!-- 链接地址 -->
			<view class="form-item" v-if="banner.linkType === 'webview'">
				<text class="form-label">链接地址</text>
				<input 
					v-model="banner.linkUrl" 
					class="form-input" 
					placeholder="请输入网页链接地址"
					maxlength="255"
				/>
				<text class="form-hint">例如：https://www.example.com</text>
			</view>
			
			<!-- 关联内容ID -->
			<view class="form-item" v-if="['knowledge', 'case', 'post'].includes(banner.linkType)">
				<text class="form-label">关联内容ID</text>
				<input 
					v-model="banner.linkId" 
					class="form-input" 
					placeholder="请输入关联内容的ID"
					type="number"
				/>
				<text class="form-hint">对应知识、案例或帖子的ID</text>
			</view>
			
			<!-- 排序顺序 -->
			<view class="form-item">
				<text class="form-label">排序顺序</text>
				<input 
					v-model="banner.sortOrder" 
					class="form-input" 
					placeholder="请输入排序顺序，数字越小越靠前"
					type="number"
				/>
			</view>
			
			<!-- 状态 -->
			<view class="form-item">
				<text class="form-label">状态</text>
				<view class="form-switch">
					<switch 
						v-model="banner.status" 
						@change="handleStatusChange"
						active-color="#2196F3"
						inactive-color="#E0E0E0"
					/>
					<text class="switch-label">{{banner.status === 'active' ? '启用' : '禁用'}}</text>
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
	 * 轮播图编辑页面组件
	 * 
	 * 功能说明：
	 * 1. 支持添加新轮播图
	 * 2. 支持编辑现有轮播图
	 * 3. 支持上传轮播图图片
	 * 4. 支持设置链接类型和链接地址
	 * 5. 支持设置排序顺序和状态
	 * 
	 * @author 反诈骗平台开发团队
	 * @version 1.0
	 */
	export default {
		/**
		 * 组件数据
		 * 
		 * banner: 轮播图对象
		 * linkTypeOptions: 链接类型选项
		 * linkTypeIndex: 当前选中的链接类型索引
		 * isEdit: 是否为编辑模式
		 * loading: 加载状态标识
		 */
		data() {
			return {
				banner: {
					title: '',
					imageUrl: '',
					linkUrl: '',
					linkType: 'none',
					linkId: null,
					sortOrder: 100,
					status: 'active'
				},
				linkTypeOptions: [
					{ value: 'none', label: '无链接' },
					{ value: 'knowledge', label: '知识' },
					{ value: 'case', label: '案例' },
					{ value: 'post', label: '帖子' },
					{ value: 'webview', label: '网页' }
				],
				linkTypeIndex: 0,
				isEdit: false,
				loading: false
			}
		},
		
		/**
		 * 页面加载时执行
		 * 功能：
		 * 1. 检查是否为编辑模式
		 * 2. 如果是编辑模式，加载轮播图详情
		 */
		onLoad(options) {
			if (options.id) {
				this.isEdit = true
				this.loadBannerDetail(options.id)
			} else {
				this.isEdit = false
			}
		},
		
		methods: {
			/**
			 * 加载轮播图详情
			 * 
			 * @param {number} id - 轮播图ID
			 * 
			 * 功能：
			 * 1. 调用后端接口获取轮播图详情
			 * 2. 处理加载状态和错误提示
			 */
			async loadBannerDetail(id) {
				this.loading = true
				try {
					const res = await request(API.BANNER.DETAIL(id), 'GET')
					if (res.data) {
						this.banner = res.data
						// 设置链接类型索引
						this.linkTypeIndex = this.linkTypeOptions.findIndex(option => option.value === this.banner.linkType)
					}
				} catch (error) {
					console.error('加载轮播图详情失败', error)
					// 显示加载失败提示
					uni.showToast({
						title: '加载失败',
						icon: 'none'
					})
				} finally {
					// 重置loading状态
					this.loading = false
				}
			},
			
			/**
			 * 选择图片
			 * 
			 * 功能：
			 * 1. 调起系统相册选择图片
			 * 2. 上传图片到服务器
			 * 3. 设置轮播图图片URL
			 */
			async chooseImage() {
				uni.chooseImage({
					count: 1,
					sizeType: ['compressed'],
					sourceType: ['album', 'camera'],
					success: async (res) => {
						const tempFilePaths = res.tempFilePaths
						this.loading = true
						try {
							// 上传图片
							const uploadTask = uni.uploadFile({
								url: API.UPLOAD.AVATAR,
								filePath: tempFilePaths[0],
								name: 'file',
								header: {
									'Content-Type': 'multipart/form-data'
								},
								success: (uploadRes) => {
									const uploadResult = JSON.parse(uploadRes.data)
									if (uploadResult.code === 200 && uploadResult.data) {
										this.banner.imageUrl = uploadResult.data
										uni.showToast({
											title: '上传成功',
											icon: 'success'
										})
									} else {
										uni.showToast({
											title: '上传失败',
											icon: 'none'
										})
									}
								},
								fail: (error) => {
									console.error('上传图片失败', error)
									uni.showToast({
										title: '上传失败',
										icon: 'none'
									})
								}
							})
						} catch (error) {
							console.error('选择图片失败', error)
							uni.showToast({
								title: '选择图片失败',
								icon: 'none'
							})
						} finally {
							this.loading = false
						}
					}
				})
			},
			
			/**
			 * 处理链接类型变更
			 * 
			 * @param {object} e - 选择事件对象
			 * 
			 * 功能：
			 * 1. 更新链接类型
			 * 2. 重置相关字段
			 */
			handleLinkTypeChange(e) {
				const index = e.detail.value
				this.linkTypeIndex = index
				this.banner.linkType = this.linkTypeOptions[index].value
				// 重置相关字段
				if (this.banner.linkType === 'none') {
					this.banner.linkUrl = ''
					this.banner.linkId = null
				} else if (this.banner.linkType === 'webview') {
					this.banner.linkId = null
				} else {
					this.banner.linkUrl = ''
				}
			},
			
			/**
			 * 处理状态变更
			 * 
			 * @param {object} e - 开关事件对象
			 * 
			 * 功能：
			 * 更新轮播图状态
			 */
			handleStatusChange(e) {
				this.banner.status = e.detail.value ? 'active' : 'inactive'
			},
			
			/**
			 * 保存轮播图
			 * 
			 * 功能：
			 * 1. 验证表单数据
			 * 2. 调用后端接口保存轮播图
			 * 3. 保存成功后返回上一页
			 */
			async saveBanner() {
				// 验证表单数据
				if (!this.banner.title) {
					uni.showToast({
						title: '请输入轮播图标题',
						icon: 'none'
					})
					return
				}
				
				if (!this.banner.imageUrl) {
					uni.showToast({
						title: '请上传轮播图图片',
						icon: 'none'
					})
					return
				}
				
				if (this.banner.linkType === 'webview' && !this.banner.linkUrl) {
					uni.showToast({
						title: '请输入链接地址',
						icon: 'none'
					})
					return
				}
				
				if (['knowledge', 'case', 'post'].includes(this.banner.linkType) && !this.banner.linkId) {
					uni.showToast({
						title: '请输入关联内容ID',
						icon: 'none'
					})
					return
				}
				
				this.loading = true
				try {
					let res
					if (this.isEdit) {
						// 编辑模式
						res = await request(API.BANNER.UPDATE(this.banner.id), 'PUT', this.banner)
					} else {
						// 添加模式
						res = await request(API.BANNER.CREATE, 'POST', this.banner)
					}
					
					if (res.code === 200) {
						uni.showToast({
							title: this.isEdit ? '更新成功' : '添加成功',
							icon: 'success'
						})
						// 返回上一页
						setTimeout(() => {
							uni.navigateBack()
						}, 1500)
					} else {
						uni.showToast({
							title: res.message || (this.isEdit ? '更新失败' : '添加失败'),
							icon: 'none'
						})
					}
				} catch (error) {
					console.error('保存轮播图失败', error)
					uni.showToast({
						title: this.isEdit ? '更新失败' : '添加失败',
						icon: 'none'
					})
				} finally {
					this.loading = false
				}
			},
			
			/**
			 * 返回上一页
			 */
			goBack() {
				uni.navigateBack()
			}
		}
	}
</script>

<style scoped>
	.banner-edit-container {
		min-height: 100vh;
		background: linear-gradient(180deg, #e3f2fd 0%, #f5f5f5 100%);
	}

	.header {
		background: linear-gradient(135deg, #2196F3 0%, #1976D2 100%);
		padding: 50rpx 30rpx 30rpx;
		position: sticky;
		top: 0;
		z-index: 100;
		box-shadow: 0 8rpx 32rpx rgba(33, 150, 243, 0.2);
	}

	.header-content {
		display: flex;
		align-items: center;
		justify-content: space-between;
	}

	.header-left,
	.header-right {
		width: 80rpx;
		height: 80rpx;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.back-icon {
		font-size: 48rpx;
		color: #fff;
		font-weight: bold;
	}

	.save-icon {
		font-size: 40rpx;
		color: #fff;
	}

	.title {
		font-size: 36rpx;
		font-weight: bold;
		color: #fff;
		flex: 1;
		text-align: center;
	}

	.form-container {
		padding: 20rpx;
	}

	.form-item {
		background-color: #fff;
		border-radius: 24rpx;
		padding: 30rpx;
		margin-bottom: 20rpx;
		box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.06);
	}

	.form-label {
		display: block;
		font-size: 30rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 20rpx;
	}

	.image-uploader {
		position: relative;
	}

	.upload-area {
		width: 100%;
		height: 280rpx;
		border-radius: 16rpx;
		background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
		display: flex;
		align-items: center;
		justify-content: center;
		cursor: pointer;
		overflow: hidden;
		box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
	}

	.uploaded-image {
		width: 100%;
		height: 100%;
	}

	.upload-placeholder {
		display: flex;
		flex-direction: column;
		align-items: center;
		gap: 16rpx;
	}

	.upload-icon {
		font-size: 80rpx;
	}

	.upload-text {
		font-size: 28rpx;
		color: #666;
	}

	.form-input {
		width: 100%;
		height: 80rpx;
		border: 2rpx solid #e0e0e0;
		border-radius: 12rpx;
		padding: 0 20rpx;
		font-size: 28rpx;
		color: #333;
		background-color: #f9f9f9;
	}

	.form-input:focus {
		border-color: #2196F3;
		background-color: #fff;
	}

	.form-hint {
		display: block;
		font-size: 24rpx;
		color: #999;
		margin-top: 12rpx;
	}

	.form-select {
		width: 100%;
		height: 80rpx;
		border: 2rpx solid #e0e0e0;
		border-radius: 12rpx;
		padding: 0 20rpx;
		background-color: #f9f9f9;
	}

	.picker-display {
		display: flex;
		align-items: center;
		justify-content: space-between;
		height: 100%;
	}

	.picker-display text {
		font-size: 28rpx;
		color: #333;
	}

	.picker-arrow {
		font-size: 24rpx;
		color: #999;
	}

	.form-switch {
		display: flex;
		align-items: center;
		gap: 20rpx;
	}

	.switch-label {
		font-size: 28rpx;
		color: #333;
	}
</style>