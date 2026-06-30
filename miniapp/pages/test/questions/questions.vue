<template>
	<view class="questions-container">
		<!-- 顶部导航 -->
		<view class="nav-bar">
			<view class="nav-left" @click="showQuestionCard = true">
				<text class="nav-icon">📋</text>
				<text class="nav-progress">{{currentQuestion + 1}}/{{questions.length}}</text>
			</view>
			<text class="nav-title">防诈骗知识测试</text>
			<view class="nav-right">
				<text class="timer-icon">⏱️</text>
				<text class="timer-value" :class="{'time-warning': remainingTime < 300}">{{formatTime(remainingTime)}}</text>
			</view>
		</view>

		<!-- 进度条 -->
		<view class="progress-bar">
			<view class="progress-fill" :style="{width: progressPercent + '%'}"></view>
			<view class="progress-text">
				<text>已完成 {{answeredCount}}/{{questions.length}} 题</text>
			</view>
		</view>

		<!-- 题目内容 -->
		<view class="question-card" :key="currentQuestion">
			<view class="question-header">
				<view class="question-badge">
					<text class="badge-text">第{{currentQuestion + 1}}题</text>
				</view>
				<view class="question-type">
					<text class="type-text">单选题</text>
				</view>
			</view>
			
			<text class="question-text">{{currentQuestionData.question}}</text>

			<!-- 选项 -->
			<view class="options">
				<view 
					v-for="(option, index) in currentQuestionData.options" 
					:key="index"
					:class="['option-item', 
						{'selected': selectedOptions[currentQuestion] === index},
						{'correct': showResult && index === currentQuestionData.answer},
						{'wrong': showResult && selectedOptions[currentQuestion] === index && index !== currentQuestionData.answer}
					]"
					@click="selectOption(index)"
				>
					<view class="option-letter" :class="{'letter-selected': selectedOptions[currentQuestion] === index}">
						{{String.fromCharCode(65 + index)}}
					</view>
					<text class="option-text">{{option}}</text>
					<view v-if="showResult && index === currentQuestionData.answer" class="option-icon correct-icon">✓</view>
					<view v-else-if="showResult && selectedOptions[currentQuestion] === index && index !== currentQuestionData.answer" class="option-icon wrong-icon">✗</view>
				</view>
			</view>

			<!-- 解析 -->
			<view v-if="showResult" class="explanation">
				<view class="explanation-header">
					<text class="explanation-icon">💡</text>
					<text class="explanation-title">答案解析</text>
				</view>
				<text class="explanation-text">{{currentQuestionData.explanation}}</text>
			</view>
		</view>

		<!-- 底部按钮 -->
		<view class="bottom-buttons">
			<button 
				class="nav-button prev-btn" 
				:disabled="currentQuestion === 0"
				@click="prevQuestion"
			>
				<text class="btn-icon">◀</text>
				上一题
			</button>
			<button 
				v-if="currentQuestion < questions.length - 1"
				class="nav-button next-btn" 
				@click="nextQuestion"
			>
				下一题
				<text class="btn-icon">▶</text>
			</button>
			<button 
				v-else
				class="submit-button" 
				@click="checkAndSubmit"
			>
				<text class="btn-icon">✓</text>
				提交答卷
			</button>
		</view>

		<!-- 答题卡弹窗 -->
		<view v-if="showQuestionCard" class="question-card-modal" @click="showQuestionCard = false">
			<view class="card-content" @click.stop>
				<view class="card-header">
					<text class="card-title">答题卡</text>
					<text class="card-close" @click="showQuestionCard = false">✕</text>
				</view>
				<view class="card-grid">
					<view 
						v-for="(q, index) in questions" 
						:key="index"
						:class="['card-item', 
							{'answered': selectedOptions[index] !== undefined},
							{'current': index === currentQuestion}
						]"
						@click="goToQuestion(index)"
					>
						<text class="card-number">{{index + 1}}</text>
					</view>
				</view>
				<view class="card-legend">
					<view class="legend-item">
						<view class="legend-dot answered-dot"></view>
						<text>已答</text>
					</view>
					<view class="legend-item">
						<view class="legend-dot unanswered-dot"></view>
						<text>未答</text>
					</view>
					<view class="legend-item">
						<view class="legend-dot current-dot"></view>
						<text>当前</text>
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
	 * 防诈骗知识测试答题页面组件
	 * 
	 * 功能说明：
	 * 1. 展示测试题目，支持单选题
	 * 2. 显示答题进度和剩余时间
	 * 3. 支持答题卡功能，快速跳转到指定题目
	 * 4. 支持上一题、下一题导航
	 * 5. 提交前检查未答题目
	 * 6. 时间到自动提交
	 * 
	 * @author 反诈骗平台开发团队
	 * @version 1.0
	 */
	export default {
		/**
		 * 组件数据
		 * 
		 * currentQuestion: 当前题目索引
		 * selectedOptions: 用户选择的选项数组
		 * showResult: 是否显示结果（暂未使用）
		 * totalTime: 总时间（秒）
		 * remainingTime: 剩余时间（秒）
		 * timer: 定时器引用
		 * questions: 题目列表
		 * - id: 题目ID
		 * - questionText: 题目内容
		 * - optionA: 选项A
		 * - optionB: 选项B
		 * - optionC: 选项C
		 * - optionD: 选项D
		 * - correctAnswer: 正确答案（A/B/C/D）
		 * - explanation: 解析
		 * 
		 * loading: 加载状态标识
		 * startTime: 开始时间
		 * showQuestionCard: 是否显示答题卡
		 */
		data() {
			return {
				currentQuestion: 0,
				selectedOptions: [],
				showResult: false,
				totalTime: 30 * 60,
				remainingTime: 30 * 60,
				timer: null,
				questions: [],
				loading: true,
				startTime: null,
				showQuestionCard: false
			}
		},
		/**
		 * 计算属性
		 * 
		 * currentQuestionData: 当前题目数据
		 * progressPercent: 进度百分比
		 * answeredCount: 已答题数
		 */
		computed: {
			/**
			 * 获取当前题目数据
			 * 
			 * 功能：
			 * 将后端返回的题目数据转换为前端使用的格式
			 */
			currentQuestionData() {
				if (this.questions.length === 0) return { question: '', options: [], answer: 0, explanation: '' }
				const q = this.questions[this.currentQuestion]
				return {
					question: q.questionText,
					options: [q.optionA, q.optionB, q.optionC, q.optionD],
					answer: q.correctAnswer.charCodeAt(0) - 65,
					explanation: q.explanation,
					id: q.id
				}
			},
			/**
			 * 计算进度百分比
			 * 
			 * 功能：
			 * 根据已答题数计算进度百分比
			 */
			progressPercent() {
				if (this.questions.length === 0) return 0
				return Math.round((this.answeredCount / this.questions.length) * 100)
			},
			/**
			 * 计算已答题数
			 * 
			 * 功能：
			 * 统计已选择的选项数量
			 */
			answeredCount() {
				return this.selectedOptions.filter(option => option !== undefined).length
			}
		},
		/**
		 * 页面加载时执行
		 * 功能：加载题目并开始计时
		 */
		onLoad() {
			this.loadQuestions()
		},
		/**
		 * 页面卸载时执行
		 * 功能：清除定时器
		 */
		onUnload() {
			if (this.timer) {
				clearInterval(this.timer)
			}
		},
		methods: {
			/**
			 * 加载题目
			 * 
			 * 功能：
			 * 1. 调用后端接口获取题目列表
			 * 2. 初始化选中选项数组
			 * 3. 开始计时
			 * 
			 * 错误处理：
			 * - 加载失败提示并返回
			 */
			async loadQuestions() {
				try {
					uni.showLoading({ title: '加载题目中...' })
					const res = await request(API.TEST.QUESTIONS + '?count=20', 'GET')
					uni.hideLoading()
					
					if (res.success && res.data) {
						this.questions = res.data
						this.selectedOptions = new Array(this.questions.length).fill(undefined)
						this.startTime = Date.now()
						this.startTimer()
					} else {
						uni.showToast({ title: '加载题目失败', icon: 'none' })
						setTimeout(() => uni.navigateBack(), 1500)
					}
				} catch (error) {
					uni.hideLoading()
					console.error('加载题目失败:', error)
					uni.showToast({ title: '加载题目失败', icon: 'none' })
					setTimeout(() => uni.navigateBack(), 1500)
				}
			},
			/**
			 * 开始计时
			 * 
			 * 功能：
			 * 每秒递减剩余时间，时间到自动提交
			 */
			startTimer() {
				this.timer = setInterval(() => {
					if (this.remainingTime > 0) {
						this.remainingTime--
					} else {
						clearInterval(this.timer)
						this.submitTest()
					}
				}, 1000)
			},
			/**
			 * 格式化时间
			 * 
			 * @param {number} seconds - 秒数
			 * @returns {string} 格式化后的时间字符串
			 * 
			 * 功能：
			 * 将秒数转换为MM:SS格式
			 */
			formatTime(seconds) {
				const minutes = Math.floor(seconds / 60)
				const remainingSeconds = seconds % 60
				return `${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}`
			},
			/**
			 * 选择选项
			 * 
			 * @param {number} index - 选项索引
			 * 
			 * 功能：
			 * 更新当前题目的选中选项
			 */
			selectOption(index) {
				if (!this.showResult) {
					this.$set(this.selectedOptions, this.currentQuestion, index)
				}
			},
			/**
			 * 上一题
			 * 
			 * 功能：
			 * 切换到上一题
			 */
			prevQuestion() {
				if (this.currentQuestion > 0) {
					this.currentQuestion--
				}
			},
			/**
			 * 下一题
			 * 
			 * 功能：
			 * 切换到下一题
			 */
			nextQuestion() {
				if (this.currentQuestion < this.questions.length -1) {
					this.currentQuestion++
				}
			},
			/**
			 * 跳转到指定题目
			 * 
			 * @param {number} index - 题目索引
			 * 
			 * 功能：
			 * 切换到指定题目并关闭答题卡
			 */
			goToQuestion(index) {
				this.currentQuestion = index
				this.showQuestionCard = false
			},
			/**
			 * 检查并提交
			 * 
			 * 功能：
			 * 1. 检查是否有未答题目
			 * 2. 如果有未答题目，显示确认对话框
			 * 3. 如果没有未答题目，直接提交
			 */
			checkAndSubmit() {
				const unAnswered = this.selectedOptions.filter(option => option === undefined).length
				if (unAnswered > 0) {
					uni.showModal({
						title: '提示',
						content: `还有${unAnswered}道题目未完成，确定要提交吗？`,
						confirmText: '确定提交',
						cancelText: '继续答题',
						success: (res) => {
							if (res.confirm) {
								this.submitTest()
							}
						}
					})
				} else {
					this.submitTest()
				}
			},
			/**
			 * 提交测试
			 * 
			 * 功能：
			 * 1. 清除定时器
			 * 2. 计算用时
			 * 3. 构建答案数据
			 * 4. 调用后端接口提交答案
			 * 5. 跳转到结果页面
			 * 
			 * 错误处理：
			 * - 提交失败提示
			 */
			async submitTest() {
				if (this.timer) {
					clearInterval(this.timer)
				}
				
				const timeUsed = Math.floor((Date.now() - this.startTime) / 1000)
				
				const answers = {}
				this.questions.forEach((question, index) => {
					if (this.selectedOptions[index] !== undefined) {
						answers[question.id] = String.fromCharCode(65 + this.selectedOptions[index])
					}
				})
				
				try {
					uni.showLoading({ title: '提交中...' })
					const res = await request(API.TEST.SUBMIT, 'POST', {
						answers: answers,
						timeUsed: timeUsed
					})
					uni.hideLoading()
					
					if (res.success && res.data) {
						const record = res.data
						uni.redirectTo({
							url: `/pages/test/result/result?score=${record.score}&total=${record.totalQuestions}&correct=${record.correctCount}&wrong=${record.wrongCount}&recordId=${record.id}`
						})
					} else {
						uni.showToast({ title: res.message || '提交失败', icon: 'none' })
					}
				} catch (error) {
					uni.hideLoading()
					console.error('提交失败:', error)
					uni.showToast({ title: '提交失败', icon: 'none' })
				}
			}
		}
	}
</script>

<style scoped>
	.questions-container {
		min-height: 100vh;
		background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
		padding-bottom: 140rpx;
	}

	.nav-bar {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 30rpx 30rpx 20rpx;
		color: #fff;
	}

	.nav-left {
		display: flex;
		align-items: center;
		background: rgba(255,255,255,0.2);
		padding: 10rpx 20rpx;
		border-radius: 30rpx;
	}

	.nav-icon {
		font-size: 28rpx;
		margin-right: 8rpx;
	}

	.nav-progress {
		font-size: 26rpx;
		font-weight: 500;
	}

	.nav-title {
		font-size: 34rpx;
		font-weight: bold;
	}

	.nav-right {
		display: flex;
		align-items: center;
		background: rgba(255,255,255,0.2);
		padding: 10rpx 20rpx;
		border-radius: 30rpx;
	}

	.timer-icon {
		font-size: 28rpx;
		margin-right: 8rpx;
	}

	.timer-value {
		font-size: 26rpx;
		font-weight: bold;
		font-family: 'Courier New', monospace;
	}

	.time-warning {
		color: #FF6B6B;
		animation: pulse 1s infinite;
	}

	@keyframes pulse {
		0%, 100% { opacity: 1; }
		50% { opacity: 0.5; }
	}

	.progress-bar {
		margin: 0 30rpx 20rpx;
		background: rgba(255,255,255,0.3);
		border-radius: 20rpx;
		height: 40rpx;
		position: relative;
		overflow: hidden;
	}

	.progress-fill {
		height: 100%;
		background: linear-gradient(90deg, #4CAF50 0%, #8BC34A 100%);
		border-radius: 20rpx;
		transition: width 0.3s ease;
	}

	.progress-text {
		position: absolute;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.progress-text text {
		font-size: 22rpx;
		color: #fff;
		font-weight: 500;
	}

	.question-card {
		background-color: #fff;
		margin: 0 20rpx;
		padding: 30rpx;
		border-radius: 24rpx;
		box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.1);
		animation: slideIn 0.3s ease;
	}

	@keyframes slideIn {
		from {
			opacity: 0;
			transform: translateX(30rpx);
		}
		to {
			opacity: 1;
			transform: translateX(0);
		}
	}

	.question-header {
		display: flex;
		align-items: center;
		justify-content: space-between;
		margin-bottom: 24rpx;
	}

	.question-badge {
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
		padding: 8rpx 24rpx;
		border-radius: 20rpx;
	}

	.badge-text {
		font-size: 24rpx;
		color: #fff;
		font-weight: 500;
	}

	.question-type {
		background: #f0f0f0;
		padding: 8rpx 20rpx;
		border-radius: 16rpx;
	}

	.type-text {
		font-size: 22rpx;
		color: #666;
	}

	.question-text {
		font-size: 32rpx;
		color: #333;
		margin-bottom: 30rpx;
		line-height: 48rpx;
		font-weight: 500;
	}

	.options {
		margin-bottom: 20rpx;
	}

	.option-item {
		display: flex;
		align-items: center;
		padding: 24rpx;
		border: 3rpx solid #e8e8e8;
		border-radius: 16rpx;
		margin-bottom: 20rpx;
		transition: all 0.3s ease;
		background: #fafafa;
	}

	.option-item:active {
		transform: scale(0.98);
	}

	.option-item.selected {
		border-color: #667eea;
		background: linear-gradient(135deg, rgba(102,126,234,0.1) 0%, rgba(118,75,162,0.1) 100%);
	}

	.option-item.correct {
		border-color: #4CAF50;
		background: linear-gradient(135deg, rgba(76,175,80,0.1) 0%, rgba(139,195,74,0.1) 100%);
	}

	.option-item.wrong {
		border-color: #FF6B6B;
		background: linear-gradient(135deg, rgba(255,107,107,0.1) 0%, rgba(255,107,107,0.05) 100%);
	}

	.option-letter {
		width: 56rpx;
		height: 56rpx;
		border-radius: 50%;
		background: #e0e0e0;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 28rpx;
		font-weight: bold;
		color: #666;
		margin-right: 20rpx;
		transition: all 0.3s ease;
	}

	.letter-selected {
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
		color: #fff;
	}

	.option-text {
		flex: 1;
		font-size: 28rpx;
		color: #333;
		line-height: 40rpx;
	}

	.option-icon {
		width: 44rpx;
		height: 44rpx;
		border-radius: 50%;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 24rpx;
		font-weight: bold;
	}

	.correct-icon {
		background-color: #4CAF50;
		color: #fff;
	}

	.wrong-icon {
		background-color: #FF6B6B;
		color: #fff;
	}

	.explanation {
		background: linear-gradient(135deg, #fff9e6 0%, #fff3cd 100%);
		padding: 24rpx;
		border-radius: 16rpx;
		margin-top: 20rpx;
		border-left: 6rpx solid #FFB800;
	}

	.explanation-header {
		display: flex;
		align-items: center;
		margin-bottom: 12rpx;
	}

	.explanation-icon {
		font-size: 28rpx;
		margin-right: 10rpx;
	}

	.explanation-title {
		font-size: 26rpx;
		font-weight: bold;
		color: #FF8C00;
	}

	.explanation-text {
		font-size: 26rpx;
		color: #666;
		line-height: 40rpx;
	}

	.bottom-buttons {
		display: flex;
		padding: 20rpx;
		background: #fff;
		border-top: 1rpx solid #f0f0f0;
		position: fixed;
		bottom: 0;
		left: 0;
		right: 0;
		box-shadow: 0 -4rpx 20rpx rgba(0,0,0,0.05);
	}

	.nav-button {
		flex: 1;
		height: 80rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 28rpx;
		border-radius: 40rpx;
		margin: 0 10rpx;
		border: none;
		transition: all 0.3s ease;
	}

	.prev-btn {
		color: #667eea;
		background: #f0f4ff;
		border: 2rpx solid #667eea;
	}

	.prev-btn:disabled {
		color: #ccc;
		background: #f5f5f5;
		border-color: #e0e0e0;
	}

	.next-btn {
		color: #fff;
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	}

	.submit-button {
		flex: 1;
		height: 80rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 28rpx;
		color: #fff;
		background: linear-gradient(135deg, #4CAF50 0%, #8BC34A 100%);
		border-radius: 40rpx;
		margin: 0 10rpx;
		border: none;
	}

	.btn-icon {
		margin: 0 8rpx;
	}

	.question-card-modal {
		position: fixed;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		background: rgba(0,0,0,0.5);
		display: flex;
		align-items: center;
		justify-content: center;
		z-index: 999;
	}

	.card-content {
		width: 90%;
		max-height: 80vh;
		background: #fff;
		border-radius: 24rpx;
		padding: 30rpx;
		animation: zoomIn 0.3s ease;
	}

	@keyframes zoomIn {
		from {
			opacity: 0;
			transform: scale(0.8);
		}
		to {
			opacity: 1;
			transform: scale(1);
		}
	}

	.card-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 30rpx;
		padding-bottom: 20rpx;
		border-bottom: 1rpx solid #f0f0f0;
	}

	.card-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
	}

	.card-close {
		font-size: 36rpx;
		color: #999;
		padding: 10rpx;
	}

	.card-grid {
		display: flex;
		flex-wrap: wrap;
		gap: 16rpx;
	}

	.card-item {
		width: 64rpx;
		height: 64rpx;
		border-radius: 12rpx;
		background: #f5f5f5;
		display: flex;
		align-items: center;
		justify-content: center;
		border: 2rpx solid #e0e0e0;
	}

	.card-item.answered {
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
		border-color: #667eea;
	}

	.card-item.answered .card-number {
		color: #fff;
	}

	.card-item.current {
		border-color: #FFB800;
		border-width: 3rpx;
	}

	.card-number {
		font-size: 26rpx;
		color: #666;
		font-weight: 500;
	}

	.card-legend {
		display: flex;
		justify-content: center;
		gap: 40rpx;
		margin-top: 30rpx;
		padding-top: 20rpx;
		border-top: 1rpx solid #f0f0f0;
	}

	.legend-item {
		display: flex;
		align-items: center;
	}

	.legend-dot {
		width: 24rpx;
		height: 24rpx;
		border-radius: 6rpx;
		margin-right: 10rpx;
	}

	.answered-dot {
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	}

	.unanswered-dot {
		background: #f5f5f5;
		border: 2rpx solid #e0e0e0;
	}

	.current-dot {
		background: #fff;
		border: 3rpx solid #FFB800;
	}

	.legend-item text {
		font-size: 24rpx;
		color: #666;
	}
</style>
