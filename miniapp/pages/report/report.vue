<template>
	<view class="report-container">
		<view class="header">
			<view class="header-content">
				<text class="title">举报诈骗</text>
				<text class="subtitle">保护自己，也保护他人</text>
			</view>
		</view>
		
		<scroll-view scroll-y class="form-scroll">
			<view class="report-form">
				<view class="form-section">
					<view class="section-title">
						<text class="section-icon">📋</text>
						<text class="section-text">基本信息</text>
					</view>
					
					<view class="form-item">
						<text class="form-label">举报类型</text>
						<picker @change="onTypeChange" :value="typeIndex" :range="typeList">
							<view class="picker-content">
								<text class="picker-text">{{typeList[typeIndex]}}</text>
								<text class="picker-arrow">›</text>
							</view>
						</picker>
					</view>
					
					<view class="form-item">
						<text class="form-label">诈骗号码/网址</text>
						<input type="text" placeholder="请输入诈骗号码或网址" class="form-input" v-model="reportInfo.target" />
					</view>
				</view>
				
				<view class="form-section">
					<view class="section-title">
						<text class="section-icon">📝</text>
						<text class="section-text">详细描述</text>
					</view>
					
					<view class="form-item">
						<text class="form-label">诈骗经过</text>
						<textarea placeholder="请详细描述诈骗经过，包括时间、地点、方式等信息" class="form-textarea" v-model="reportInfo.description" auto-height maxlength="500" />
						<text class="char-count">{{reportInfo.description.length}}/500</text>
					</view>
				</view>
				
				<view class="form-section">
					<view class="section-title">
						<text class="section-icon">📷</text>
						<text class="section-text">证据上传</text>
						<text class="section-hint">(选填，最多5张)</text>
					</view>
					
					<view class="upload-container">
						<view class="upload-item" v-for="(item, index) in uploadedImages" :key="index">
							<view class="upload-preview">
								<text class="preview-icon">🖼️</text>
							</view>
							<view class="delete-icon" @click="deleteImage(index)">
								<text class="delete-text">×</text>
							</view>
						</view>
						<view class="upload-btn" @click="uploadImage" v-if="uploadedImages.length < 5">
							<text class="upload-icon">+</text>
							<text class="upload-text">添加图片</text>
						</view>
					</view>
				</view>
				
				<view class="form-section">
					<view class="section-title">
						<text class="section-icon">📞</text>
						<text class="section-text">联系方式</text>
						<text class="section-hint">(选填)</text>
					</view>
					
					<view class="form-item">
						<input type="text" placeholder="请输入您的联系方式（方便我们跟进）" class="form-input" v-model="reportInfo.contact" />
					</view>
				</view>
				
				<view class="button-group">
					<button class="submit-button" @click="submitReport">
						<text class="button-icon">✅</text>
						提交举报
					</button>
					<button class="history-button" @click="checkHistory">
						<text class="button-icon">📋</text>
						查看历史
					</button>
				</view>
			</view>
			
			<view class="notice-section">
				<view class="notice-header">
					<text class="notice-icon">⚠️</text>
					<text class="notice-title">举报须知</text>
				</view>
				<view class="notice-content">
					<view class="notice-item">
						<text class="notice-dot">•</text>
						<text class="notice-text">请如实填写举报信息，虚假举报将承担法律责任</text>
					</view>
					<view class="notice-item">
						<text class="notice-dot">•</text>
						<text class="notice-text">我们将严格保护您的个人信息安全</text>
					</view>
					<view class="notice-item">
						<text class="notice-dot">•</text>
						<text class="notice-text">举报信息经核实后，将提交给相关部门处理</text>
					</view>
					<view class="notice-item">
						<text class="notice-dot">•</text>
						<text class="notice-text">您可以通过举报编号查询处理进度</text>
					</view>
				</view>
			</view>
		</scroll-view>
	</view>
</template>

<script>
	import { API } from '@/utils/api.js'
	import { request } from '@/utils/request.js'

	export default {
		data() {
			return {
				typeList: ['📱 电信诈骗', '🌐 网络诈骗', '💬 短信诈骗', '📞 诈骗电话', '🎣 钓鱼网站', '📦 其他诈骗'],
				typeIndex: 0,
				uploadedImages: [],
				reportInfo: {
					target: '',
					description: '',
					contact: ''
				}
			}
		},
		onLoad() {

		},
		methods: {
			onTypeChange(e) {
				this.typeIndex = e.detail.value;
			},
			uploadImage() {
				uni.chooseImage({
					count: 5 - this.uploadedImages.length,
					success: (res) => {
						this.uploadedImages.push(...res.tempFilePaths);
					}
				});
			},
			deleteImage(index) {
				this.uploadedImages.splice(index, 1);
			},
			async submitReport() {
				if (!this.reportInfo.target || !this.reportInfo.description) {
					uni.showToast({
						title: '请填写必填信息',
						icon: 'none'
					});
					return;
				}
				
				const userInfo = uni.getStorageSync('userInfo');
				if (!userInfo || !userInfo.id) {
					uni.showToast({
						title: '请先登录',
						icon: 'none'
					});
					return;
				}
				
				uni.showLoading({
					title: '提交中...'
				});
				
				try {
					const reportData = {
						reporterId: userInfo.id,
						reportedType: this.typeList[this.typeIndex].replace(/^[^\s]+\s/, ''),
						reason: this.reportInfo.target,
						description: this.reportInfo.description
					};
					
					await request(API.REPORT.CREATE, 'POST', reportData);
					
					uni.hideLoading();
					
					uni.navigateTo({
						url: '/pages/report/result/result'
					});
				} catch (error) {
					uni.hideLoading();
					console.error('提交举报失败', error);
					uni.showToast({
						title: '提交失败，请重试',
						icon: 'none'
					});
				}
			},
			checkHistory() {
				uni.navigateTo({
					url: '/pages/report/history/history'
				});
			}
		}
	}
</script>

<style scoped>
	.report-container {
		min-height: 100vh;
		background: linear-gradient(180deg, #ffebee 0%, #f5f5f5 100%);
	}

	.header {
		background: linear-gradient(135deg, #f44336 0%, #d32f2f 100%);
		padding: 50rpx 30rpx 40rpx;
		box-shadow: 0 8rpx 32rpx rgba(244, 67, 54, 0.2);
	}

	.header-content {
		display: flex;
		flex-direction: column;
	}

	.title {
		font-size: 40rpx;
		font-weight: bold;
		color: #fff;
		margin-bottom: 8rpx;
	}

	.subtitle {
		font-size: 26rpx;
		color: rgba(255, 255, 255, 0.85);
	}

	.form-scroll {
		height: calc(100vh - 180rpx);
	}

	.report-form {
		padding: 20rpx 10rpx;
	}

	.form-section {
		background-color: #fff;
		border-radius: 24rpx;
		padding: 30rpx 16rpx;
		margin-bottom: 20rpx;
		box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.06);
	}

	.section-title {
		display: flex;
		align-items: center;
		margin-bottom: 24rpx;
	}

	.section-icon {
		font-size: 32rpx;
		margin-right: 10rpx;
	}

	.section-text {
		font-size: 30rpx;
		font-weight: bold;
		color: #333;
	}

	.section-hint {
		font-size: 22rpx;
		color: #999;
		margin-left: 8rpx;
	}

	.form-item {
		margin-bottom: 24rpx;
		position: relative;
	}

	.form-item:last-child {
		margin-bottom: 0;
	}

	.form-label {
		font-size: 28rpx;
		color: #333;
		margin-bottom: 12rpx;
		display: block;
		font-weight: 500;
	}

	.picker-content {
		display: flex;
		justify-content: space-between;
		align-items: center;
		height: 88rpx;
		background-color: #f8f8f8;
		padding: 0 24rpx;
		border-radius: 16rpx;
	}

	.picker-text {
		font-size: 28rpx;
		color: #333;
	}

	.picker-arrow {
		font-size: 36rpx;
		color: #ccc;
	}

	.form-input {
		height: 88rpx;
		background-color: #f8f8f8;
		padding: 0 24rpx;
		border-radius: 16rpx;
		font-size: 28rpx;
		color: #333;
	}

	.form-textarea {
		background-color: #f8f8f8;
		padding: 24rpx 20rpx;
		border-radius: 20rpx;
		font-size: 30rpx;
		color: #333;
		min-height: 280rpx;
		line-height: 1.8;
		transition: all 0.3s ease;
		border: 2rpx solid transparent;
		width: 100%;
		box-sizing: border-box;
	}

	.form-textarea:focus {
		background-color: #fff;
		border-color: #f44336;
		box-shadow: 0 4rpx 16rpx rgba(244, 67, 54, 0.15);
	}

	.char-count {
		position: absolute;
		right: 0;
		bottom: -30rpx;
		font-size: 22rpx;
		color: #999;
	}

	.upload-container {
		display: flex;
		flex-wrap: wrap;
		gap: 16rpx;
	}

	.upload-item {
		width: 160rpx;
		height: 160rpx;
		position: relative;
	}

	.upload-preview {
		width: 100%;
		height: 100%;
		background: linear-gradient(135deg, #e0e0e0 0%, #bdbdbd 100%);
		border-radius: 16rpx;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.preview-icon {
		font-size: 56rpx;
	}

	.delete-icon {
		position: absolute;
		top: -10rpx;
		right: -10rpx;
		width: 44rpx;
		height: 44rpx;
		background-color: #f44336;
		border-radius: 50%;
		display: flex;
		align-items: center;
		justify-content: center;
		box-shadow: 0 2rpx 8rpx rgba(244, 67, 54, 0.4);
	}

	.delete-text {
		color: #fff;
		font-size: 32rpx;
		line-height: 1;
		font-weight: bold;
	}

	.upload-btn {
		width: 160rpx;
		height: 160rpx;
		border: 2rpx dashed #ddd;
		border-radius: 16rpx;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		background-color: #fafafa;
		transition: all 0.2s ease;
	}

	.upload-btn:active {
		background-color: #f0f0f0;
	}

	.upload-icon {
		font-size: 56rpx;
		color: #999;
		margin-bottom: 8rpx;
		line-height: 1;
	}

	.upload-text {
		font-size: 22rpx;
		color: #999;
	}

	.button-group {
		display: flex;
		flex-direction: column;
		gap: 16rpx;
		margin-top: 10rpx;
	}

	.submit-button,
	.history-button {
		height: 96rpx;
		line-height: 96rpx;
		font-size: 32rpx;
		font-weight: bold;
		border-radius: 48rpx;
		border: none;
		display: flex;
		align-items: center;
		justify-content: center;
		transition: all 0.3s ease;
	}

	.submit-button:active,
	.history-button:active {
		transform: scale(0.96);
	}

	.submit-button {
		color: #fff;
		background: linear-gradient(135deg, #f44336 0%, #d32f2f 100%);
		box-shadow: 0 8rpx 24rpx rgba(244, 67, 54, 0.4);
	}

	.history-button {
		color: #f44336;
		background-color: #fff;
		border: 2rpx solid #f44336;
		box-shadow: 0 4rpx 12rpx rgba(244, 67, 54, 0.15);
	}

	.button-icon {
		font-size: 28rpx;
		margin-right: 10rpx;
	}

	.notice-section {
		background-color: #fff;
		border-radius: 24rpx;
		padding: 30rpx;
		margin: 0 20rpx 40rpx;
		box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.06);
	}

	.notice-header {
		display: flex;
		align-items: center;
		margin-bottom: 20rpx;
	}

	.notice-icon {
		font-size: 32rpx;
		margin-right: 10rpx;
	}

	.notice-title {
		font-size: 30rpx;
		font-weight: bold;
		color: #333;
	}

	.notice-content {
		display: flex;
		flex-direction: column;
		gap: 16rpx;
	}

	.notice-item {
		display: flex;
		align-items: flex-start;
	}

	.notice-dot {
		font-size: 28rpx;
		color: #f44336;
		margin-right: 12rpx;
		flex-shrink: 0;
	}

	.notice-text {
		font-size: 26rpx;
		color: #666;
		line-height: 1.6;
		flex: 1;
	}
</style>
