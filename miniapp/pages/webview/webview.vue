<template>
	<view class="webview-container">
		<!-- webview内容区域：显示标题、URL和操作按钮 -->
		<view class="webview-content">
			<!-- 页面标题 -->
			<text class="webview-title">外部链接</text>
			<!-- 显示外部链接的URL -->
			<view class="webview-url">{{url}}</view>
			<!-- 打开外部链接的按钮 -->
			<button class="open-btn" @click="openExternalUrl">
				在浏览器中打开
			</button>
		</view>
	</view>
</template>

<script>
/**
 * 外部链接展示页面组件
 * 
 * 功能说明：
 * 1. 接收并显示外部链接URL
 * 2. 提供在浏览器中打开链接的功能
 * 3. 支持复制链接到剪贴板
 * 4. 适配测试环境，提供友好的提示信息
 * 
 * 使用场景：
 * - 在知识详情、案例详情等页面中，需要跳转到外部网页时使用
 * - 由于小程序环境限制，某些外部链接无法直接打开，需要复制到浏览器
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
export default {
	/**
	 * 组件数据
	 * 
	 * url: 外部链接的URL地址
	 *   通过页面参数传递，需要进行URL解码
	 */
	data() {
		return {
			url: ''
		}
	},
	/**
	 * 页面加载时执行
	 * @param {object} options - 页面参数对象
	 *   options.url: 编码后的URL字符串
	 * 从页面参数中获取URL并进行解码
	 */
	onLoad(options) {
		if (options.url) {
			this.url = decodeURIComponent(options.url);
		}
	},
	/**
	 * 组件方法
	 */
	methods: {
		/**
		 * 在浏览器中打开外部链接
		 * 
		 * 功能说明：
		 * 1. 由于测试环境使用touristappid，无法直接使用web-view组件
		 * 2. 提供模态框提示用户复制链接
		 * 3. 用户确认后，将链接复制到剪贴板
		 * 4. 显示复制成功的提示
		 * 
		 * 注意事项：
		 * - 在正式环境中，可以使用web-view组件直接加载外部链接
		 * - 测试环境中，需要用户手动复制链接到浏览器打开
		 */
		openExternalUrl() {
			// 在微信小程序中，使用navigateTo打开外部链接
			// 由于使用的是touristappid，可能无法正常使用web-view
			// 这里提供一个提示，告诉用户复制链接到浏览器打开
			uni.showModal({
				title: '提示',
				content: '由于当前是测试环境，无法直接打开外部链接，请复制以下链接到浏览器打开：\n' + this.url,
				confirmText: '复制链接',
				cancelText: '取消',
				success: (res) => {
					if (res.confirm) {
						// 复制链接到剪贴板
						uni.setClipboardData({
							data: this.url,
							success: () => {
								uni.showToast({
									title: '链接已复制到剪贴板',
									icon: 'success'
								});
							}
						});
					}
				}
			});
		}
	}
}
</script>

<style>
	.webview-container {
		width: 100%;
		height: 100vh;
		background-color: #f5f5f5;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.webview-content {
		width: 90%;
		background-color: #fff;
		border-radius: 20rpx;
		padding: 40rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
	}

	.webview-title {
		font-size: 36rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 30rpx;
		display: block;
		text-align: center;
	}

	.webview-url {
		font-size: 24rpx;
		color: #666;
		margin-bottom: 40rpx;
		word-break: break-all;
		line-height: 40rpx;
	}

	.open-btn {
		width: 100%;
		height: 80rpx;
		background-color: #4CAF50;
		color: #fff;
		border: none;
		border-radius: 40rpx;
		font-size: 30rpx;
	}
</style>
