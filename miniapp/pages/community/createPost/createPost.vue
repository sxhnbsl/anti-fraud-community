<template>
	<view class="create-post-container">
		<view class="header">
			<text class="back" @click="goBack">←</text>
			<text class="title">{{isEdit ? '编辑帖子' : '发布帖子'}}</text>
			<text class="publish" @click="publishPost">{{isEdit ? '更新' : '发布'}}</text>
		</view>
		
		<view class="content">
			<!-- 帖子标题 -->
			<view class="form-item">
				<input type="text" class="title-input" placeholder="请输入帖子标题" v-model="post.title" maxlength="50" />
			</view>
			
			<!-- 帖子内容 -->
			<view class="form-item">
				<textarea class="content-input" placeholder="请输入帖子内容，分享您的防诈骗经验或问题" v-model="post.content" maxlength="500" rows="6"></textarea>
			</view>
			
			<!-- 图片上传 -->
			<view class="form-item">
				<text class="label">添加图片（可选）</text>
				<view class="image-upload">
					<view class="upload-item" v-for="(image, index) in post.images" :key="index">
						<image class="uploaded-image" src="/static/logo.png" mode="aspectFill"></image>
						<view class="remove-image" @click="removeImage(index)">
							<text>×</text>
						</view>
					</view>
					<view v-if="post.images.length < 9" class="upload-btn" @click="chooseImage">
						<text class="upload-icon">+</text>
						<text class="upload-text">添加图片</text>
					</view>
				</view>
			</view>
			
			<!-- 帖子分类 -->
			<view class="form-item">
				<text class="label">选择分类</text>
				<view class="category-list">
					<view class="category-item" :class="{ active: post.category === category }" v-for="category in categories" :key="category" @click="post.category = category">
						{{category}}
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	// 导入API接口配置
	import { API } from '@/utils/api.js'
	// 导入HTTP请求工具
	import { request } from '@/utils/request.js'

	/**
	 * 创建/编辑帖子页面组件
	 * 
	 * 功能说明：
	 * 1. 提供帖子标题和内容输入
	 * 2. 支持图片上传（最多9张）
	 * 3. 提供帖子分类选择
	 * 4. 发布前进行内容安全审核
	 * 5. 支持编辑已有帖子
	 * 6. 审核通过后才能发布
	 * 
	 * @author 反诈骗平台开发团队
	 * @version 1.0
	 */
	export default {
		/**
		 * 组件数据
		 * 
		 * postId: 帖子ID（编辑模式时使用）
		 * isEdit: 是否为编辑模式
		 * post: 帖子数据
		 * - title: 帖子标题
		 * - content: 帖子内容
		 * - images: 图片列表
		 * - category: 帖子分类
		 * 
		 * categories: 可选分类列表
		 */
		data() {
			return {
				postId: '',
				isEdit: false,
				post: {
					title: '',
					content: '',
					images: [],
					category: '经验分享'
				},
				categories: ['经验分享', '问题求助', '诈骗预警', '防骗技巧', '其他']
			}
		},
		
		/**
		 * 页面加载时执行
		 * 
		 * @param {Object} options - 页面参数
		 * @param {string} options.id - 帖子ID（编辑模式）
		 * 
		 * 功能：
		 * 如果传入帖子ID，则进入编辑模式并加载帖子详情
		 */
		onLoad(options) {
			if (options.id) {
				this.postId = options.id
				this.isEdit = true
				this.loadPostDetail()
			}
		},
		
		methods: {
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
			 * 选择图片
			 * 
			 * 功能：
			 * 1. 显示操作菜单（从相册选择、拍照）
			 * 2. 模拟添加图片到图片列表
			 * 3. 显示添加成功提示
			 */
			chooseImage() {
				// 显示操作菜单
				uni.showActionSheet({
					itemList: ['从相册选择', '拍照'],
					success: (res) => {
						if (res.tapIndex === 0 || res.tapIndex === 1) {
							// 模拟添加图片
							this.post.images.push('');
							uni.showToast({
								title: '图片添加成功',
								icon: 'success'
							});
						}
					}
				});
			},
			
			/**
			 * 移除图片
			 * 
			 * @param {number} index - 图片索引
			 * 
			 * 功能：
			 * 从图片列表中移除指定索引的图片
			 */
			removeImage(index) {
				this.post.images.splice(index, 1);
			},
			
			/**
			 * 加载帖子详情
			 * 
			 * 功能：
			 * 1. 调用后端接口获取帖子详情
			 * 2. 填充表单数据
			 * 
			 * 错误处理：
			 * - 捕获网络请求错误
			 * - 显示加载失败提示
			 */
			async loadPostDetail() {
				try {
					const res = await request(API.POST.DETAIL(this.postId), 'GET')
					this.post = {
						title: res.data.title,
						content: res.data.content,
						category: res.data.category || '经验分享',
						images: res.data.image ? [res.data.image] : []
					}
				} catch (error) {
					console.error('加载帖子详情失败', error)
					uni.showToast({
						title: '加载失败',
						icon: 'none'
					})
				}
			},
			
			/**
			 * 检查内容安全性
			 * 
			 * @param {string} content - 待检查的内容
			 * @returns {Object|null} 审核结果对象，失败返回null
			 * 
			 * 功能：
			 * 调用后端内容安全审核接口，检查内容是否包含敏感信息
			 */
			async checkContent(content) {
				try {
					const res = await request(API.POST.CHECK_CONTENT, 'POST', { content });
					return res;
				} catch (error) {
					console.error('内容审核失败', error);
					return null;
				}
			},
			
			/**
			 * 发布或更新帖子
			 * 
			 * 功能：
			 * 1. 验证表单数据（标题、内容、登录状态）
			 * 2. 如果是新建帖子，先进行内容安全审核
			 * 3. 审核通过后调用发布接口
			 * 4. 如果是编辑模式，直接调用更新接口
			 * 5. 成功后返回上一页
			 * 
			 * 错误处理：
			 * - 表单验证失败提示
			 * - 内容审核失败提示
			 * - 网络请求错误提示
			 */
			async publishPost() {
				// 验证标题
				if (!this.post.title) {
					uni.showToast({
						title: '请输入帖子标题',
						icon: 'none'
					});
					return;
				}
				
				// 验证内容
				if (!this.post.content) {
					uni.showToast({
						title: '请输入帖子内容',
						icon: 'none'
					});
					return;
				}
				
				// 验证登录状态
				const userInfo = uni.getStorageSync('userInfo');
				if (!userInfo || !userInfo.id) {
					uni.showToast({
						title: '请先登录',
						icon: 'none'
					});
					return;
				}
				
				// 如果是新建帖子，先进行内容审核
				if (!this.isEdit) {
					uni.showLoading({
						title: '内容审核中...',
						mask: true
					});
					
					// 拼接标题和内容进行审核
					const contentToCheck = this.post.title + ' ' + this.post.content;
					const checkResult = await this.checkContent(contentToCheck);
					
					// 检查审核服务是否正常
					if (!checkResult) {
						uni.hideLoading();
						uni.showToast({
							title: '审核服务异常，请稍后重试',
							icon: 'none'
						});
						return;
					}
					
					// 检查审核是否通过
					if (!checkResult.data || !checkResult.data.passed) {
						uni.hideLoading();
						const riskLevel = checkResult.data?.riskLevel;
						const suggestion = checkResult.data?.suggestion || '内容审核未通过';
						const riskLabels = checkResult.data?.riskLabels || [];
						
						// 构建错误提示信息
						let message = suggestion;
						if (riskLabels.length > 0) {
							message += '\n风险标签: ' + riskLabels.join(', ');
						}
						
						// 显示审核未通过提示
						uni.showModal({
							title: '内容审核未通过',
							content: message,
							showCancel: false,
							confirmText: '我知道了'
						});
						return;
					}
					
					// 审核通过，隐藏加载提示
					uni.hideLoading();
				}
				
				// 显示发布/更新加载提示
				uni.showLoading({
					title: this.isEdit ? '更新中...' : '发布中...'
				});
				
				try {
					// 准备帖子数据
					const postData = {
						title: this.post.title,
						content: this.post.content,
						category: this.post.category
					};
					
					// 根据模式调用不同接口
					if (this.isEdit) {
						// 编辑模式：调用更新接口
						await request(API.POST.UPDATE(this.postId), 'PUT', postData);
					} else {
						// 新建模式：添加作者信息并调用创建接口
						postData.authorId = userInfo.id;
						postData.authorName = userInfo.nickname || userInfo.username || '用户';
						await request(API.POST.CREATE, 'POST', postData);
					}
					
					// 隐藏加载提示
					uni.hideLoading();
					
					// 显示成功提示
					uni.showToast({
						title: this.isEdit ? '更新成功' : '发布成功',
						icon: 'success'
					});
					
					// 延迟返回上一页
					setTimeout(() => {
						uni.navigateBack();
					}, 500);
				} catch (error) {
					// 隐藏加载提示
					uni.hideLoading();
					console.error('发布失败', error);
					
					// 显示失败提示
					uni.showToast({
						title: this.isEdit ? '更新失败' : '发布失败，请重试',
						icon: 'none'
					});
				}
			}
		}
	}
</script>

<style scoped>
	.create-post-container {
		min-height: 100vh;
		background-color: #f5f5f5;
	}

	/* 头部 */
	.header {
		height: 88rpx;
		background-color: #fff;
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 0 20rpx;
		box-shadow: 0 2rpx 10rpx rgba(0,0,0,0.05);
		position: sticky;
		top: 0;
		z-index: 100;
	}

	.back {
		font-size: 36rpx;
		color: #333;
	}

	.title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
	}

	.publish {
		font-size: 28rpx;
		color: #667eea;
		font-weight: 500;
	}

	/* 内容区域 */
	.content {
		padding: 20rpx;
	}

	/* 表单项目 */
	.form-item {
		background-color: #fff;
		border-radius: 12rpx;
		padding: 20rpx;
		margin-bottom: 20rpx;
		box-shadow: 0 2rpx 10rpx rgba(0,0,0,0.05);
	}

	/* 标题输入 */
	.title-input {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
		width: 100%;
		padding: 10rpx 0;
	}

	.title-input::placeholder {
		color: #999;
		font-weight: normal;
	}

	/* 内容输入 */
	.content-input {
		font-size: 28rpx;
		color: #333;
		width: 100%;
		padding: 10rpx 0;
		line-height: 45rpx;
		min-height: 240rpx;
	}

	.content-input::placeholder {
		color: #999;
	}

	/* 标签 */
	.label {
		font-size: 28rpx;
		font-weight: 500;
		color: #333;
		margin-bottom: 15rpx;
		display: block;
	}

	/* 图片上传 */
	.image-upload {
		display: flex;
		flex-wrap: wrap;
	}

	.upload-item {
		height: 160rpx;
		width: 160rpx;
		margin-right: 20rpx;
		margin-bottom: 20rpx;
		position: relative;
	}

	.uploaded-image {
		height: 100%;
		width: 100%;
		border-radius: 8rpx;
	}

	.remove-image {
		position: absolute;
		top: -10rpx;
		right: -10rpx;
		height: 40rpx;
		width: 40rpx;
		background-color: rgba(0,0,0,0.6);
		border-radius: 50%;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.remove-image text {
		color: #fff;
		font-size: 32rpx;
		line-height: 1;
	}

	.upload-btn {
		height: 160rpx;
		width: 160rpx;
		border: 2rpx dashed #ddd;
		border-radius: 8rpx;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
	}

	.upload-icon {
		font-size: 48rpx;
		color: #999;
		margin-bottom: 10rpx;
	}

	.upload-text {
		font-size: 24rpx;
		color: #999;
	}

	/* 分类选择 */
	.category-list {
		display: flex;
		flex-wrap: wrap;
	}

	.category-item {
		height: 60rpx;
		line-height: 60rpx;
		padding: 0 30rpx;
		border: 2rpx solid #ddd;
		border-radius: 30rpx;
		margin-right: 20rpx;
		margin-bottom: 20rpx;
		font-size: 26rpx;
		color: #666;
		transition: all 0.3s ease;
	}

	.category-item.active {
		border-color: #667eea;
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
		color: #fff;
	}
</style>