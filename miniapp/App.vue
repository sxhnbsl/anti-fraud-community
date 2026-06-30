<script>
	export default {
		onLaunch: function() {
			console.log('App Launch')
		},
		onShow: function() {
			console.log('App Show')
		},
		onHide: function() {
			console.log('App Hide')
		}
	}
</script>

<style>
	/*每个页面公共css */
	page, view, text, button, input, textarea, image, swiper, swiper-item, navigator {
		margin: 0;
		padding: 0;
		box-sizing: border-box;
	}
	
	body, page {
		font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
		font-size: 28rpx;
		color: #333;
		background-color: #f5f5f5;
	}
	
	/* 按钮样式 */
	button {
		border: none;
		background-color: transparent;
		outline: none;
		font-size: 28rpx;
	}
	
	button::after {
		border: none;
	}
	
	/* 输入框样式 */
	input, textarea {
		outline: none;
		border: none;
		font-size: 28rpx;
		color: #333;
	}
	
	/* 链接样式 */
	.link {
		color: #667eea;
		text-decoration: none;
	}
	
	/* 容器样式 */
	.container {
		width: 100%;
		padding: 20rpx;
	}
	
	/* 标题样式 */
	.title {
		font-size: 36rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 20rpx;
	}
	
	/* 副标题样式 */
	.subtitle {
		font-size: 32rpx;
		font-weight: bold;
		color: #666;
		margin-bottom: 15rpx;
	}
	
	/* 段落样式 */
	.paragraph {
		font-size: 28rpx;
		color: #666;
		line-height: 45rpx;
		margin-bottom: 15rpx;
	}
	
	/* 分隔线样式 */
	.divider {
		height: 2rpx;
		background-color: #f0f0f0;
		margin: 20rpx 0;
	}
	
	/* 加载动画 */
	.loading {
		display: flex;
		justify-content: center;
		align-items: center;
		padding: 40rpx 0;
	}
	
	/* 空数据提示 */
	.empty {
		display: flex;
		flex-direction: column;
		justify-content: center;
		align-items: center;
		padding: 80rpx 0;
		color: #999;
	}
	
	.empty text {
		font-size: 28rpx;
		margin-top: 20rpx;
	}
	
	/* 卡片通用样式 */
	.card {
		background-color: #fff;
		border-radius: 24rpx;
		padding: 28rpx;
		box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.06);
		transition: all 0.3s ease;
	}
	
	.card:active {
		transform: scale(0.98);
		box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.08);
	}
	
	/* 渐变按钮通用样式 */
	.gradient-button {
		height: 96rpx;
		line-height: 96rpx;
		font-size: 32rpx;
		font-weight: bold;
		color: #fff;
		border-radius: 48rpx;
		border: none;
		display: flex;
		align-items: center;
		justify-content: center;
		transition: all 0.3s ease;
		box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.15);
	}
	
	.gradient-button:active {
		transform: scale(0.96);
		box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
	}
	
	/* 输入框通用样式 */
	.input-box {
		height: 88rpx;
		background-color: #f8f8f8;
		padding: 0 24rpx;
		border-radius: 16rpx;
		font-size: 28rpx;
		color: #333;
	}
	
	/* 文本域通用样式 */
	.textarea-box {
		background-color: #f8f8f8;
		padding: 20rpx 24rpx;
		border-radius: 16rpx;
		font-size: 28rpx;
		color: #333;
		min-height: 200rpx;
		line-height: 1.6;
	}
</style>
