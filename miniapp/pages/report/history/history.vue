<template>
	<view class="history-container">
		<view class="header">
			<text class="back" @click="goBack">←</text>
			<text class="title">举报历史</text>
		</view>
		
		<view class="search-section">
			<view class="search-box" @click="showSearch">
				<text class="search-icon">🔍</text>
				<text class="search-placeholder">搜索举报编号或内容</text>
			</view>
		</view>
		
		<view class="filter-section">
			<view class="filter-item" :class="{ active: activeFilter === 'all' }" @click="activeFilter = 'all'">
				全部
			</view>
			<view class="filter-item" :class="{ active: activeFilter === 'pending' }" @click="activeFilter = 'pending'">
				处理中
			</view>
			<view class="filter-item" :class="{ active: activeFilter === 'processed' }" @click="activeFilter = 'processed'">
				已处理
			</view>
		</view>
		
		<view class="history-list">
			<view class="history-item" v-for="(item, index) in filteredHistory" :key="item.id" @click="viewDetail(item)">
				<view class="item-header">
					<text class="report-id">举报编号：{{item.id}}</text>
					<view class="status-tag" :class="getStatusClass(item.status)">
				{{getStatusText(item.status)}}
				</view>
				</view>
				<view class="item-content">
					<text class="report-type">类型：{{item.reportedType}}</text>
					<text class="report-target">目标：{{item.reason}}</text>
					<text class="report-time">提交时间：{{formatDate(item.createdAt)}}</text>
				</view>
				<view class="item-handle-result" v-if="item.status === 'RESOLVED' && item.handleResult">
					<text class="handle-label">处理结果：</text>
					<text class="handle-text">{{item.handleResult}}</text>
				</view>
				<view class="item-footer">
					<text class="detail-btn">查看详情</text>
				</view>
			</view>
		</view>
		
		<view v-if="filteredHistory.length === 0" class="empty-state">
			<text class="empty-icon">📋</text>
			<text class="empty-text">暂无举报记录</text>
			<text class="empty-hint">您还没有提交过举报</text>
		</view>
	</view>
</template>

<script>
	// 导入API接口配置
	import { API } from '@/utils/api.js'
	// 导入HTTP请求工具
	import { request } from '@/utils/request.js'

	/**
	 * 举报历史页面组件
	 * 
	 * 功能说明：
	 * 1. 展示用户的举报历史记录
	 * 2. 支持按状态筛选（全部、处理中、已处理）
	 * 3. 支持搜索功能（搜索举报编号或内容）
	 * 4. 支持查看举报详情
	 * 5. 支持下拉刷新
	 * 6. 空状态提示
	 * 
	 * @author 反诈骗平台开发团队
	 * @version 1.0
	 */
	export default {
		/**
		 * 组件数据
		 * 
		 * activeFilter: 当前筛选状态（all-全部、pending-处理中、processed-已处理）
		 * searchKeyword: 搜索关键词
		 * historyList: 举报历史列表数据
		 * - id: 举报ID
		 * - reportedType: 举报类型
		 * - reason: 举报目标（诈骗号码/网址）
		 * - description: 举报描述
		 * - status: 举报状态（PENDING-处理中、PROCESSED-已处理）
		 * - createdAt: 提交时间
		 * 
		 * loading: 加载状态标识
		 */
		data() {
			return {
				activeFilter: 'all',
				searchKeyword: '',
				historyList: [],
				loading: false
			}
		},
		/**
		 * 计算属性
		 * 
		 * 功能：
		 * 1. 根据筛选状态过滤举报列表
		 * 2. 根据搜索关键词进一步过滤
		 * 3. 返回过滤后的举报列表
		 */
		computed: {
			filteredHistory() {
				// 先按状态筛选
				let filtered = this.historyList;
				if (this.activeFilter !== 'all') {
					if (this.activeFilter === 'pending') {
						// 处理中包含 PENDING 和 PROCESSING 状态
						filtered = this.historyList.filter(item => 
							item.status === 'PENDING' || item.status === 'PROCESSING'
						);
					} else if (this.activeFilter === 'processed') {
						// 已处理对应 RESOLVED 状态
						filtered = this.historyList.filter(item => 
							item.status === 'RESOLVED'
						);
					}
				}
				
				// 再按关键词搜索
				if (this.searchKeyword) {
					filtered = filtered.filter(item => 
						String(item.id).includes(this.searchKeyword) || 
						(item.reason && item.reason.includes(this.searchKeyword)) ||
						(item.description && item.description.includes(this.searchKeyword))
					);
				}
				
				return filtered;
			}
		},
		/**
		 * 页面加载时执行
		 * 功能：加载举报历史
		 */
		onLoad() {
			this.loadHistory();
		},
		/**
		 * 下拉刷新时执行
		 * 功能：重新加载举报历史
		 */
		onPullDownRefresh() {
			this.loadHistory();
		},
		methods: {
			/**
			 * 加载举报历史
			 * 
			 * 功能：
			 * 1. 调用后端接口获取举报历史列表
			 * 2. 处理加载状态和错误提示
			 * 
			 * 错误处理：
			 * - 捕获网络请求错误
			 * - 显示加载失败提示
			 * - finally块确保loading状态和下拉刷新状态被重置
			 */
			async loadHistory() {
				this.loading = true;
				try {
					const res = await request(API.REPORT.LIST, 'GET');
					this.historyList = res.data || [];
				} catch (error) {
					console.error('加载举报历史失败', error);
					uni.showToast({
						title: '加载失败',
						icon: 'none'
					});
				} finally {
					this.loading = false;
					uni.stopPullDownRefresh();
				}
			},
			/**
			 * 返回上一页
			 * 
			 * 功能：
			 * 使用uni.navigateBack返回上一页
			 */
			goBack() {
				uni.navigateBack();
			},
			/**
			 * 显示搜索框
			 * 
			 * 功能：
			 * 1. 显示模态对话框，允许用户输入搜索关键词
			 * 2. 用户确认后更新搜索关键词
			 */
			showSearch() {
				uni.showModal({
					title: '搜索举报',
					content: '请输入举报编号或内容',
					editable: true,
					placeholderText: '搜索举报记录',
					success: (res) => {
						if (res.confirm) {
							this.searchKeyword = res.content;
						}
					}
				});
			},
			/**
			 * 获取状态样式类名
			 * 
			 * @param {string} status - 状态值
			 * @returns {string} 样式类名
			 * 
			 * 功能：
			 * 根据状态返回对应的样式类名
			 */
			getStatusClass(status) {
				switch(status) {
					case 'PENDING':
					case 'PROCESSING':
						return 'pending';
					case 'RESOLVED':
						return 'processed';
					default:
						return 'pending';
				}
			},
			/**
			 * 获取状态文本
			 * 
			 * @param {string} status - 状态值
			 * @returns {string} 状态文本
			 * 
			 * 功能：
			 * 根据状态返回对应的中文文本
			 */
			getStatusText(status) {
				switch(status) {
					case 'PENDING':
						return '待处理';
					case 'PROCESSING':
						return '处理中';
					case 'RESOLVED':
						return '已处理';
					default:
						return '待处理';
				}
			},
			/**
			 * 查看举报详情
			 * 
			 * @param {Object} item - 举报项
			 * 
			 * 功能：
			 * 显示举报详情的模态对话框
			 */
			viewDetail(item) {
				const statusText = this.getStatusText(item.status);
				let content = `举报编号：${item.id}\n类型：${item.reportedType}\n目标：${item.reason}\n描述：${item.description}\n提交时间：${this.formatDate(item.createdAt)}\n状态：${statusText}`;
				if (item.status === 'RESOLVED' && item.handleResult) {
					content += `\n处理结果：${item.handleResult}`;
				}
				uni.showModal({
					title: '举报详情',
					content: content,
					showCancel: false
				});
			},
			/**
			 * 格式化日期
			 * 
			 * @param {string} dateStr - 日期字符串
			 * @returns {string} 格式化后的日期字符串
			 * 
			 * 功能：
			 * 将日期格式化为YYYY-MM-DD HH:mm格式
			 */
			formatDate(dateStr) {
				if (!dateStr) return '';
				const date = new Date(dateStr);
				const year = date.getFullYear();
				const month = String(date.getMonth() + 1).padStart(2, '0');
				const day = String(date.getDate()).padStart(2, '0');
				const hour = String(date.getHours()).padStart(2, '0');
				const minute = String(date.getMinutes()).padStart(2, '0');
				return `${year}-${month}-${day} ${hour}:${minute}`;
			}
		}
	}
</script>

<style scoped>
	.history-container {
		min-height: 100vh;
		background-color: #f5f5f5;
	}

	/* 头部 */
	.header {
		height: 88rpx;
		background-color: #fff;
		display: flex;
		align-items: center;
		padding: 0 20rpx;
		box-shadow: 0 2rpx 10rpx rgba(0,0,0,0.05);
		position: sticky;
		top: 0;
		z-index: 100;
	}

	.back {
		font-size: 36rpx;
		color: #333;
		margin-right: 30rpx;
	}

	.title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
	}

	/* 搜索区域 */
	.search-section {
		padding: 20rpx;
		background-color: #fff;
		box-shadow: 0 2rpx 10rpx rgba(0,0,0,0.05);
	}

	.search-box {
		height: 60rpx;
		background-color: #f0f0f0;
		border-radius: 30rpx;
		padding: 0 20rpx;
		display: flex;
		align-items: center;
	}

	.search-icon {
		font-size: 28rpx;
		margin-right: 10rpx;
		color: #999;
	}

	.search-placeholder {
		font-size: 26rpx;
		color: #999;
	}

	/* 筛选区域 */
	.filter-section {
		height: 70rpx;
		background-color: #fff;
		display: flex;
		align-items: center;
		padding: 0 20rpx;
		margin-bottom: 20rpx;
		box-shadow: 0 2rpx 10rpx rgba(0,0,0,0.05);
	}

	.filter-item {
		height: 40rpx;
		line-height: 40rpx;
		padding: 0 30rpx;
		margin-right: 30rpx;
		border-radius: 20rpx;
		font-size: 26rpx;
		color: #666;
		transition: all 0.3s ease;
	}

	.filter-item.active {
		background-color: #4CAF50;
		color: #fff;
	}

	/* 历史列表 */
	.history-list {
		padding: 0 20rpx;
	}

	.history-item {
		background-color: #fff;
		border-radius: 12rpx;
		padding: 20rpx;
		margin-bottom: 20rpx;
		box-shadow: 0 2rpx 10rpx rgba(0,0,0,0.05);
		transition: transform 0.3s ease;
	}

	.history-item:active {
		transform: scale(0.98);
	}

	.item-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 15rpx;
	}

	.report-id {
		font-size: 28rpx;
		font-weight: bold;
		color: #333;
	}

	.status-tag {
		height: 40rpx;
		line-height: 40rpx;
		padding: 0 20rpx;
		border-radius: 20rpx;
		font-size: 22rpx;
		font-weight: 500;
	}

	.status-tag.pending {
		background-color: #fff3e0;
		color: #ff9800;
	}

	.status-tag.processed {
		background-color: #e8f5e9;
		color: #4CAF50;
	}

	.item-content {
		margin-bottom: 15rpx;
	}

	.report-type,
	.report-target,
	.report-time {
		font-size: 26rpx;
		color: #666;
		line-height: 40rpx;
		display: block;
	}

	.item-footer {
		text-align: right;
	}

	.detail-btn {
		font-size: 26rpx;
		color: #4CAF50;
		font-weight: 500;
	}

	.item-handle-result {
		background-color: #e8f5e9;
		border-radius: 8rpx;
		padding: 12rpx 16rpx;
		margin-bottom: 15rpx;
	}

	.handle-label {
		font-size: 24rpx;
		color: #2e7d32;
		font-weight: bold;
	}

	.handle-text {
		font-size: 26rpx;
		color: #333;
	}

	/* 空状态 */
	.empty-state {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		padding: 150rpx 0;
		background-color: #fff;
		margin: 20rpx;
		border-radius: 12rpx;
		box-shadow: 0 2rpx 10rpx rgba(0,0,0,0.05);
	}

	.empty-icon {
		font-size: 100rpx;
		margin-bottom: 40rpx;
	}

	.empty-text {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 20rpx;
	}

	.empty-hint {
		font-size: 26rpx;
		color: #999;
	}
</style>