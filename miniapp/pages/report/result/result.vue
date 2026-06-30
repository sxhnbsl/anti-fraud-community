<template>
	<view class="result-container">
		<view class="success-section">
			<text class="success-icon">✅</text>
			<text class="success-title">举报提交成功</text>
			<text class="success-desc">感谢您的举报，我们将尽快处理</text>
		</view>
		
		<view class="info-section">
			<view class="info-item">
				<text class="info-label">举报编号</text>
				<text class="info-value">{{reportId}}</text>
			</view>
			<view class="info-item">
				<text class="info-label">举报类型</text>
				<text class="info-value">{{reportType}}</text>
			</view>
			<view class="info-item">
				<text class="info-label">提交时间</text>
				<text class="info-value">{{submitTime}}</text>
			</view>
		</view>
		
		<view class="process-section">
			<text class="process-title">处理进度</text>
			<view class="process-steps">
				<view class="steps-container active-line">
					<view class="step-item active">
						<view class="step-circle">1</view>
						<text class="step-text">举报提交</text>
					</view>
					<view class="step-item">
						<view class="step-circle">2</view>
						<text class="step-text">信息核实</text>
					</view>
					<view class="step-item">
						<view class="step-circle">3</view>
						<text class="step-text">处理反馈</text>
					</view>
				</view>
			</view>
		</view>
		
		<view class="tips-section">
			<text class="tips-title">温馨提示</text>
			<text class="tips-content">• 您可以通过举报编号查询处理进度</text>
			<text class="tips-content">• 一般处理时间为1-3个工作日</text>
			<text class="tips-content">• 我们将严格保护您的个人信息</text>
			<text class="tips-content">• 如有疑问，请联系客服</text>
		</view>
		
		<view class="button-group">
			<button class="button primary" @click="backToHome">返回首页</button>
			<button class="button secondary" @click="checkHistory">查看举报历史</button>
		</view>
	</view>
</template>

<script>
	/**
	 * 举报结果页面组件
	 * 
	 * 功能说明：
	 * 1. 展示举报提交成功的提示信息
	 * 2. 显示举报编号、类型、提交时间
	 * 3. 展示处理进度（举报提交、信息核实、处理反馈）
	 * 4. 提供温馨提示（查询进度、处理时间、隐私保护、联系方式）
	 * 5. 提供返回首页和查看举报历史的按钮
	 * 
	 * @author 反诈骗平台开发团队
	 * @version 1.0
	 */
	export default {
		/**
		 * 组件数据
		 * 
		 * reportId: 举报编号（自动生成，格式：RP+时间戳）
		 * reportType: 举报类型
		 * submitTime: 提交时间
		 */
		data() {
			return {
				reportId: 'RP' + Date.now(),
				reportType: '电信诈骗',
				submitTime: new Date().toLocaleString('zh-CN')
			}
		},
		methods: {
			/**
			 * 返回首页
			 * 
			 * 功能：
			 * 使用uni.reLaunch跳转到首页，清空页面栈
			 */
			backToHome() {
				uni.reLaunch({
					url: '/pages/index/index'
				});
			},
			/**
			 * 查看举报历史
			 * 
			 * 功能：
			 * 跳转到举报历史页面
			 */
			checkHistory() {
				uni.navigateTo({
					url: '/pages/report/history/history'
				});
			}
		}
	}
</script>

<style scoped>
	.result-container {
		min-height: 100vh;
		background-color: #f5f5f5;
		padding: 40rpx 20rpx;
	}

	/* 成功提示 */
	.success-section {
		display: flex;
		flex-direction: column;
		align-items: center;
		background-color: #fff;
		border-radius: 20rpx;
		padding: 60rpx 20rpx;
		margin-bottom: 30rpx;
		box-shadow: 0 2rpx 10rpx rgba(0,0,0,0.05);
	}

	.success-icon {
		font-size: 120rpx;
		margin-bottom: 30rpx;
	}

	.success-title {
		font-size: 36rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 15rpx;
	}

	.success-desc {
		font-size: 28rpx;
		color: #666;
	}

	/* 信息卡片 */
	.info-section {
		background-color: #fff;
		border-radius: 20rpx;
		padding: 30rpx 20rpx;
		margin-bottom: 30rpx;
		box-shadow: 0 2rpx 10rpx rgba(0,0,0,0.05);
	}

	.info-item {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 20rpx 0;
		border-bottom: 1rpx solid #f0f0f0;
	}

	.info-item:last-child {
		border-bottom: none;
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

	/* 处理进度 */
	.process-section {
		background-color: #fff;
		border-radius: 20rpx;
		padding: 30rpx 20rpx;
		margin-bottom: 30rpx;
		box-shadow: 0 2rpx 10rpx rgba(0,0,0,0.05);
	}

	.process-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 30rpx;
		display: block;
	}

	.process-steps {
		display: flex;
		justify-content: space-between;
		position: relative;
	}

	/* 连接线容器 */
	.steps-container {
		position: relative;
		display: flex;
		width: 100%;
	}

	/* 背景线条 */
	.steps-container::before {
		content: '';
		height: 4rpx;
		background-color: #e0e0e0;
		position: absolute;
		top: 30rpx;
		left: 0;
		right: 0;
		z-index: 1;
	}

	/* 激活状态的线条 */
	.steps-container.active-line::after {
		content: '';
		height: 4rpx;
		background-color: #4CAF50;
		position: absolute;
		top: 30rpx;
		left: 0;
		right: 50%;
		z-index: 2;
	}

	.step-item {
		display: flex;
		flex-direction: column;
		align-items: center;
		flex: 1;
		position: relative;
	}

	.step-circle {
		height: 60rpx;
		width: 60rpx;
		border-radius: 50%;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 32rpx;
		font-weight: bold;
		margin-bottom: 15rpx;
		background-color: #e0e0e0;
		color: #999;
		position: relative;
		z-index: 3;
	}

	.step-text {
		font-size: 24rpx;
		color: #666;
		text-align: center;
		position: relative;
		z-index: 3;
	}

	.step-item.active .step-circle {
		background-color: #4CAF50;
		color: #fff;
	}

	.step-item.active .step-text {
		color: #4CAF50;
		font-weight: 500;
	}

	/* 温馨提示 */
	.tips-section {
		background-color: #fff;
		border-radius: 20rpx;
		padding: 30rpx 20rpx;
		margin-bottom: 40rpx;
		box-shadow: 0 2rpx 10rpx rgba(0,0,0,0.05);
	}

	.tips-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 20rpx;
		display: block;
	}

	.tips-content {
		font-size: 26rpx;
		color: #666;
		line-height: 45rpx;
		display: block;
	}

	/* 按钮组 */
	.button-group {
		display: flex;
		flex-direction: column;
		gap: 20rpx;
	}

	.button {
		height: 80rpx;
		line-height: 80rpx;
		font-size: 32rpx;
		border-radius: 40rpx;
		border: none;
	}

	.button.primary {
		background-color: #4CAF50;
		color: #fff;
	}

	.button.secondary {
		background-color: #fff;
		color: #4CAF50;
		border: 2rpx solid #4CAF50;
	}
</style>