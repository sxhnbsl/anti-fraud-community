<template>
	<view class="result-container">
		<!-- 顶部背景 -->
		<view class="header-bg" :class="scoreLevelClass">
			<view class="header-content">
				<text class="result-title">测试完成</text>
				<view class="score-ring">
					<view class="score-inner">
						<text class="score-number">{{score}}</text>
						<text class="score-unit">分</text>
					</view>
				</view>
				<view class="level-badge">
					<text class="level-icon">{{levelIcon}}</text>
					<text class="level-text">{{scoreLevel}}</text>
				</view>
				<text class="score-desc">{{scoreDesc}}</text>
			</view>
		</view>

		<!-- 答题统计卡片 -->
		<view class="stats-card">
			<view class="stats-row">
				<view class="stat-item">
					<view class="stat-icon total-icon">📋</view>
					<view class="stat-info">
						<text class="stat-value">{{total}}</text>
						<text class="stat-label">总题数</text>
					</view>
				</view>
				<view class="stat-divider"></view>
				<view class="stat-item">
					<view class="stat-icon correct-icon">✓</view>
					<view class="stat-info">
						<text class="stat-value correct">{{correct}}</text>
						<text class="stat-label">正确</text>
					</view>
				</view>
				<view class="stat-divider"></view>
				<view class="stat-item">
					<view class="stat-icon wrong-icon">✗</view>
					<view class="stat-info">
						<text class="stat-value wrong">{{wrong}}</text>
						<text class="stat-label">错误</text>
					</view>
				</view>
			</view>
			<view class="accuracy-bar">
				<view class="accuracy-fill" :style="{width: accuracy + '%'}"></view>
			</view>
			<text class="accuracy-text">正确率 {{accuracy}}%</text>
		</view>

		<!-- 分析建议 -->
		<view class="analysis-card">
			<view class="card-header">
				<text class="card-icon">💡</text>
				<text class="card-title">分析建议</text>
			</view>
			<text class="analysis-content">{{analysisContent}}</text>
		</view>

		<!-- 提示卡片 -->
		<view class="tips-card">
			<view class="card-header">
				<text class="card-icon">🎯</text>
				<text class="card-title">温馨提示</text>
			</view>
			<view class="tips-list">
				<view class="tip-item">
					<text class="tip-dot">•</text>
					<text class="tip-text">定期参加防诈骗测试，保持警惕性</text>
				</view>
				<view class="tip-item">
					<text class="tip-dot">•</text>
					<text class="tip-text">关注最新诈骗手段，提高识别能力</text>
				</view>
				<view class="tip-item">
					<text class="tip-dot">•</text>
					<text class="tip-text">遇到可疑情况，及时报警求助</text>
				</view>
			</view>
		</view>

		<!-- 操作按钮 -->
		<view class="action-buttons">
			<button class="action-button retest" @click="retest">
				<text class="btn-icon">🔄</text>
				再测一次
			</button>
			<button class="action-button home" @click="goHome">
				<text class="btn-icon">🏠</text>
				返回首页
			</button>
		</view>
	</view>
</template>

<script>
	/**
	 * 测试结果页面组件
	 * 
	 * 功能说明：
	 * 1. 展示测试分数和等级评价
	 * 2. 显示答题统计（总题数、正确数、错误数、正确率）
	 * 3. 根据分数提供个性化分析建议
	 * 4. 提供温馨提示
	 * 5. 支持重新测试和返回首页
	 * 
	 * @author 反诈骗平台开发团队
	 * @version 1.0
	 */
	export default {
		/**
		 * 组件数据
		 * 
		 * score: 测试分数
		 * total: 总题数
		 * correct: 正确数
		 * wrong: 错误数
		 * recordId: 测试记录ID
		 * scoreLevel: 分数等级（优秀、良好、及格、需加强）
		 * scoreDesc: 分数描述
		 * analysisContent: 分析建议内容
		 * levelIcon: 等级图标
		 */
		data() {
			return {
				score: 0,
				total: 0,
				correct: 0,
				wrong: 0,
				recordId: null,
				scoreLevel: '',
				scoreDesc: '',
				analysisContent: '',
				levelIcon: ''
			}
		},
		/**
		 * 计算属性
		 * 
		 * accuracy: 正确率
		 * scoreLevelClass: 分数等级样式类
		 */
		computed: {
			/**
			 * 计算正确率
			 * 
			 * 功能：
			 * 根据正确数和总题数计算正确率百分比
			 */
			accuracy() {
				if (this.total === 0) return 0
				return Math.round((this.correct / this.total) * 100)
			},
			/**
			 * 获取分数等级样式类
			 * 
			 * 功能：
			 * 根据分数返回对应的样式类
			 */
			scoreLevelClass() {
				if (this.score >= 90) return 'level-excellent'
				if (this.score >= 70) return 'level-good'
				if (this.score >= 60) return 'level-pass'
				return 'level-fail'
			}
		},
		/**
		 * 页面加载时执行
		 * 
		 * @param {Object} options - 页面参数
		 * @param {string} options.score - 分数
		 * @param {string} options.total - 总题数
		 * @param {string} options.correct - 正确数
		 * @param {string} options.wrong - 错误数
		 * @param {string} options.recordId - 测试记录ID
		 * 
		 * 功能：
		 * 1. 获取页面参数
		 * 2. 计算分数等级
		 * 3. 生成分析建议
		 */
		onLoad(options) {
			if (options) {
				this.score = parseInt(options.score) || 0
				this.total = parseInt(options.total) || 0
				this.correct = parseInt(options.correct) || 0
				this.wrong = parseInt(options.wrong) || 0
				this.recordId = options.recordId || null
			}
			
			this.calculateScoreLevel()
			this.generateAnalysis()
		},
		methods: {
			/**
			 * 计算分数等级
			 * 
			 * 功能：
			 * 根据分数设置等级、图标和描述
			 */
			calculateScoreLevel() {
				if (this.score >= 90) {
					this.scoreLevel = '优秀'
					this.levelIcon = '🏆'
					this.scoreDesc = '太棒了！你的防诈骗意识非常强！'
				} else if (this.score >= 70) {
					this.scoreLevel = '良好'
					this.levelIcon = '👍'
					this.scoreDesc = '不错！继续保持学习态度！'
				} else if (this.score >= 60) {
					this.scoreLevel = '及格'
					this.levelIcon = '📝'
					this.scoreDesc = '还需努力，加强防诈骗知识学习！'
				} else {
					this.scoreLevel = '需加强'
					this.levelIcon = '📚'
					this.scoreDesc = '建议多学习防诈骗知识，保护自己！'
				}
			},
			/**
			 * 生成分析建议
			 * 
			 * 功能：
			 * 根据错误题数生成个性化的分析建议
			 */
			generateAnalysis() {
				if (this.wrong === 0) {
					this.analysisContent = '恭喜你！你在测试中表现出色，全部答对！这说明你对防诈骗知识掌握得非常扎实。请继续保持这种警惕性，定期复习防诈骗知识，以应对不断变化的诈骗手段。同时，也欢迎你将这些知识分享给身边的亲友，共同提高防诈骗意识。'
				} else if (this.wrong <= Math.floor(this.total / 3)) {
					this.analysisContent = '你在测试中表现良好，大部分题目都答对了！但仍有一些知识盲区需要注意。建议你重点复习那些答错的题目类型，了解相关的防诈骗技巧。可以通过阅读防诈骗案例、关注官方防诈骗公众号等方式，进一步提高自己的识别能力。'
				} else if (this.wrong <= Math.floor(this.total * 2 / 3)) {
					this.analysisContent = '你在测试中存在较多知识盲区，需要系统性地学习防诈骗知识。建议你：1. 仔细阅读每道题的解析，理解正确答案的原因；2. 多关注新闻中的诈骗案例，了解最新诈骗手段；3. 学习官方发布的防诈骗指南；4. 遇到可疑情况时，多与家人朋友商量或咨询警方。'
				} else {
					this.analysisContent = '你的防诈骗知识较为薄弱，建议你立即加强学习！请务必重视防诈骗教育，可以通过以下方式提升：1. 系统学习防诈骗知识手册；2. 关注国家反诈中心官方账号；3. 下载安装国家反诈中心APP；4. 参加社区组织的防诈骗讲座。记住：提高警惕，保护好自己的财产安全！'
				}
			},
			/**
			 * 重新测试
			 * 
			 * 功能：
			 * 跳转到答题页面
			 */
			retest() {
				uni.redirectTo({
					url: '/pages/test/questions/questions'
				})
			},
			/**
			 * 返回首页
			 * 
			 * 功能：
			 * 使用uni.switchTab跳转到首页
			 */
			goHome() {
				uni.switchTab({
					url: '/pages/index/index'
				})
			}
		}
	}
</script>

<style scoped>
	.result-container {
		min-height: 100vh;
		background: linear-gradient(180deg, #f8f9fa 0%, #e9ecef 100%);
		padding-bottom: 40rpx;
	}

	.header-bg {
		padding: 60rpx 30rpx 80rpx;
		position: relative;
		overflow: hidden;
	}

	.header-bg::before {
		content: '';
		position: absolute;
		top: -50%;
		left: -50%;
		width: 200%;
		height: 200%;
		background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 60%);
		animation: rotate 20s linear infinite;
	}

	@keyframes rotate {
		from { transform: rotate(0deg); }
		to { transform: rotate(360deg); }
	}

	.level-excellent {
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	}

	.level-good {
		background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
	}

	.level-pass {
		background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
	}

	.level-fail {
		background: linear-gradient(135deg, #ff6b6b 0%, #ee5a24 100%);
	}

	.header-content {
		display: flex;
		flex-direction: column;
		align-items: center;
		position: relative;
		z-index: 1;
	}

	.result-title {
		font-size: 32rpx;
		color: rgba(255,255,255,0.9);
		margin-bottom: 30rpx;
	}

	.score-ring {
		width: 220rpx;
		height: 220rpx;
		border-radius: 50%;
		background: rgba(255,255,255,0.2);
		display: flex;
		align-items: center;
		justify-content: center;
		margin-bottom: 24rpx;
		border: 6rpx solid rgba(255,255,255,0.3);
	}

	.score-inner {
		display: flex;
		flex-direction: column;
		align-items: center;
	}

	.score-number {
		font-size: 72rpx;
		font-weight: bold;
		color: #fff;
		line-height: 1;
	}

	.score-unit {
		font-size: 28rpx;
		color: rgba(255,255,255,0.9);
		margin-top: 4rpx;
	}

	.level-badge {
		display: flex;
		align-items: center;
		background: rgba(255,255,255,0.2);
		padding: 10rpx 30rpx;
		border-radius: 30rpx;
		margin-bottom: 16rpx;
	}

	.level-icon {
		font-size: 32rpx;
		margin-right: 10rpx;
	}

	.level-text {
		font-size: 32rpx;
		font-weight: bold;
		color: #fff;
	}

	.score-desc {
		font-size: 26rpx;
		color: rgba(255,255,255,0.9);
		text-align: center;
	}

	.stats-card {
		background: #fff;
		margin: -40rpx 20rpx 20rpx;
		padding: 30rpx;
		border-radius: 20rpx;
		box-shadow: 0 8rpx 32rpx rgba(0,0,0,0.08);
		position: relative;
		z-index: 2;
	}

	.stats-row {
		display: flex;
		align-items: center;
		justify-content: space-around;
		margin-bottom: 24rpx;
	}

	.stat-item {
		display: flex;
		align-items: center;
	}

	.stat-icon {
		width: 48rpx;
		height: 48rpx;
		border-radius: 12rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 24rpx;
		margin-right: 12rpx;
	}

	.total-icon {
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	}

	.correct-icon {
		background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
		color: #fff;
		font-weight: bold;
	}

	.wrong-icon {
		background: linear-gradient(135deg, #ff6b6b 0%, #ee5a24 100%);
		color: #fff;
		font-weight: bold;
	}

	.stat-info {
		display: flex;
		flex-direction: column;
	}

	.stat-value {
		font-size: 36rpx;
		font-weight: bold;
		color: #333;
	}

	.stat-value.correct {
		color: #11998e;
	}

	.stat-value.wrong {
		color: #ff6b6b;
	}

	.stat-label {
		font-size: 22rpx;
		color: #999;
	}

	.stat-divider {
		width: 1rpx;
		height: 60rpx;
		background: #f0f0f0;
	}

	.accuracy-bar {
		height: 12rpx;
		background: #f0f0f0;
		border-radius: 6rpx;
		overflow: hidden;
		margin-bottom: 12rpx;
	}

	.accuracy-fill {
		height: 100%;
		background: linear-gradient(90deg, #11998e 0%, #38ef7d 100%);
		border-radius: 6rpx;
		transition: width 0.5s ease;
	}

	.accuracy-text {
		font-size: 24rpx;
		color: #666;
		text-align: center;
		display: block;
	}

	.analysis-card, .tips-card {
		background: #fff;
		margin: 0 20rpx 20rpx;
		padding: 24rpx;
		border-radius: 16rpx;
		box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.06);
	}

	.card-header {
		display: flex;
		align-items: center;
		margin-bottom: 16rpx;
	}

	.card-icon {
		font-size: 32rpx;
		margin-right: 12rpx;
	}

	.card-title {
		font-size: 30rpx;
		font-weight: bold;
		color: #333;
	}

	.analysis-content {
		font-size: 26rpx;
		color: #666;
		line-height: 44rpx;
	}

	.tips-list {
		display: flex;
		flex-direction: column;
	}

	.tip-item {
		display: flex;
		align-items: flex-start;
		margin-bottom: 16rpx;
	}

	.tip-item:last-child {
		margin-bottom: 0;
	}

	.tip-dot {
		color: #667eea;
		font-size: 28rpx;
		margin-right: 12rpx;
	}

	.tip-text {
		font-size: 26rpx;
		color: #666;
		line-height: 40rpx;
		flex: 1;
	}

	.action-buttons {
		padding: 0 20rpx;
	}

	.action-button {
		height: 88rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 30rpx;
		border-radius: 44rpx;
		margin-bottom: 20rpx;
		border: none;
		transition: all 0.3s ease;
	}

	.action-button:active {
		transform: scale(0.98);
	}

	.retest {
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
		color: #fff;
		box-shadow: 0 8rpx 24rpx rgba(102,126,234,0.4);
	}

	.home {
		background: #fff;
		color: #666;
		border: 2rpx solid #e0e0e0;
	}

	.btn-icon {
		margin-right: 10rpx;
	}
</style>
