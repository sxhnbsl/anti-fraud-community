<template>
	<view class="questions-container">
		<!-- 顶部导航：显示测试标题和答题进度 -->
		<view class="nav-bar">
			<text class="nav-title">防诈骗知识测试</text>
			<text class="nav-progress">{{currentQuestion + 1}}/{{questions.length}}</text>
		</view>

		<!-- 计时器：显示剩余答题时间 -->
		<view class="timer">
			<text class="timer-label">剩余时间：</text>
			<text class="timer-value">{{formatTime(remainingTime)}}</text>
		</view>

		<!-- 题目内容区域：显示当前题目、选项和解析 -->
		<view class="question-content">
			<text class="question-number">第{{currentQuestion + 1}}题</text>
			<text class="question-text">{{currentQuestionData.question}}</text>

			<!-- 选项列表：显示所有选项，支持点击选择 -->
			<view class="options">
				<view 
					v-for="(option, index) in currentQuestionData.options" 
					:key="index"
					:class="['option-item', { 'selected': selectedOptions[currentQuestion] === index, 'correct': showResult && index === currentQuestionData.answer, 'wrong': showResult && selectedOptions[currentQuestion] !== undefined && selectedOptions[currentQuestion] !== currentQuestionData.answer }]"
					@click="selectOption(index)"
				>
					<view class="option-letter">{{String.fromCharCode(65 + index)}}</view>
					<text class="option-text">{{option}}</text>
					<!-- 显示正确/错误图标 -->
					<view v-if="showResult && index === currentQuestionData.answer" class="option-icon correct-icon">✓</view>
					<view v-else-if="showResult && selectedOptions[currentQuestion] !== undefined && selectedOptions[currentQuestion] !== currentQuestionData.answer && selectedOptions[currentQuestion] === index" class="option-icon wrong-icon">✗</view>
				</view>
			</view>

			<!-- 解析区域：显示题目解析（仅在显示结果时显示） -->
			<view v-if="showResult" class="explanation">
				<text class="explanation-title">解析：</text>
				<text class="explanation-text">{{currentQuestionData.explanation}}</text>
			</view>
		</view>

		<!-- 底部按钮：导航和提交功能 -->
		<view class="bottom-buttons">
			<button 
				class="nav-button" 
				:disabled="currentQuestion === 0 || showResult"
				@click="prevQuestion"
			>
				上一题
			</button>
			<button 
				class="nav-button" 
				:disabled="currentQuestion === questions.length - 1 || showResult"
				@click="nextQuestion"
			>
				下一题
			</button>
			<button 
				class="submit-button" 
				:disabled="showResult"
				@click="submitTest"
			>
				{{currentQuestion === questions.length - 1 ? '提交测试' : '跳过'}}
			</button>
		</view>
	</view>
</template>

<script>
/**
 * 防诈骗知识测试答题页面组件
 * 
 * 功能说明：
 * 1. 展示测试题目，支持单选题
 * 2. 显示答题进度和剩余时间
 * 3. 支持上一题、下一题导航
 * 4. 支持跳过当前题目
 * 5. 提交测试并计算分数
 * 6. 时间到自动提交
 * 7. 显示答题结果和解析
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
export default {
	/**
	 * 组件数据
	 * 
	 * currentQuestion: 当前题目索引（从0开始）
	 * selectedOptions: 用户选择的选项数组，存储每题选择的选项索引
	 * showResult: 是否显示结果（用于显示正确答案和解析）
	 * totalTime: 总时间（秒），默认30分钟
	 * remainingTime: 剩余时间（秒）
	 * timer: 定时器引用，用于计时
	 * questions: 题目列表
	 *   - question: 题目内容
	 *   - options: 选项数组
	 *   - answer: 正确答案索引（0-3）
	 *   - explanation: 题目解析
	 */
	data() {
		return {
			currentQuestion: 0,
			selectedOptions: [],
			showResult: false,
			totalTime: 30 * 60, // 30分钟
			remainingTime: 30 * 60,
			timer: null,
			questions: [
				{
					question: '以下哪种是常见的诈骗手段？',
					options: ['电话诈骗', '短信诈骗', '网络诈骗', '以上都是'],
					answer: 3,
					explanation: '电话诈骗、短信诈骗和网络诈骗都是常见的诈骗手段，需要提高警惕。'
				},
				{
					question: '当接到自称是银行客服的电话，要求你提供银行卡密码时，你应该怎么做？',
					options: ['立即提供密码', '挂断电话并联系银行官方客服', '按照对方要求操作', '忽略电话'],
					answer: 1,
					explanation: '银行客服不会要求你提供银行卡密码，这是典型的诈骗手段，应挂断电话并联系银行官方客服核实。'
				},
				{
					question: '收到陌生短信称你中了大奖，需要先缴纳手续费才能领取，你应该怎么做？',
					options: ['立即缴纳手续费', '忽略短信', '联系发送短信的号码核实', '分享给朋友'],
					answer: 1,
					explanation: '这是典型的中奖诈骗，不要相信，也不要点击任何链接或缴纳任何费用。'
				},
				{
					question: '在网上购物时，遇到卖家要求你通过微信或支付宝直接转账，你应该怎么做？',
					options: ['直接转账', '通过平台支付', '先转账后确认收货', '忽略要求'],
					answer: 1,
					explanation: '在网上购物时，应通过平台支付，不要直接转账给卖家，以保障资金安全。'
				},
				{
					question: '当收到自称是公检法的电话，称你涉嫌犯罪需要转账时，你应该怎么做？',
					options: ['立即转账', '挂断电话并联系当地警方', '按照对方要求操作', '害怕并服从'],
					answer: 1,
					explanation: '公检法不会通过电话要求转账，这是典型的诈骗手段，应挂断电话并联系当地警方核实。'
				}
			]
		}
	},
	/**
	 * 计算属性
	 * 
	 * currentQuestionData: 获取当前题目数据
	 *   返回当前索引对应的题目对象
	 */
	computed: {
		currentQuestionData() {
			return this.questions[this.currentQuestion];
		}
	},
	/**
	 * 页面加载时执行
	 * 1. 初始化选中选项数组，长度与题目数量相同
	 * 2. 开始计时
	 */
	onLoad() {
		// 初始化选中选项数组
		this.selectedOptions = new Array(this.questions.length).fill(undefined);
		// 开始计时
		this.startTimer();
	},
	/**
	 * 页面卸载时执行
	 * 清除定时器，防止内存泄漏
	 */
	onUnload() {
		// 清除定时器
		if (this.timer) {
			clearInterval(this.timer);
		}
	},
	/**
	 * 组件方法
	 */
	methods: {
		/**
		 * 开始计时
		 * 每秒更新剩余时间，时间到时自动提交测试
		 */
		startTimer() {
			this.timer = setInterval(() => {
				if (this.remainingTime > 0) {
					this.remainingTime--;
				} else {
					// 时间到，自动提交
					clearInterval(this.timer);
					this.submitTest();
				}
			}, 1000);
		},
		/**
		 * 格式化时间
		 * @param {number} seconds - 秒数
		 * @returns {string} 格式化后的时间字符串（MM:SS）
		 */
		formatTime(seconds) {
			const minutes = Math.floor(seconds / 60);
			const remainingSeconds = seconds % 60;
			return `${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}`;
		},
		/**
		 * 选择选项
		 * @param {number} index - 选项索引
		 * 在未显示结果时，记录用户选择的选项
		 */
		selectOption(index) {
			if (!this.showResult) {
				this.selectedOptions[this.currentQuestion] = index;
			}
		},
		/**
		 * 上一题
		 * 切换到上一题（如果存在）
		 */
		prevQuestion() {
			if (this.currentQuestion > 0) {
				this.currentQuestion--;
			}
		},
		/**
		 * 下一题
		 * 切换到下一题（如果存在）
		 */
		nextQuestion() {
			if (this.currentQuestion < this.questions.length - 1) {
				this.currentQuestion++;
			}
		},
		/**
		 * 提交测试
		 * 1. 计算分数（每题20分）
		 * 2. 统计正确和错误的题目
		 * 3. 清除定时器
		 * 4. 跳转到结果页面，传递测试结果数据
		 */
		submitTest() {
			// 计算分数
			let score = 0;
			const correctAnswers = [];
			const wrongAnswers = [];
			
			this.questions.forEach((question, index) => {
				if (this.selectedOptions[index] === question.answer) {
					score += 20; // 每题20分
					correctAnswers.push(index);
				} else {
					wrongAnswers.push(index);
				}
			});
			
			// 清除定时器
			if (this.timer) {
				clearInterval(this.timer);
			}
			
			// 跳转到结果页面
			uni.navigateTo({
				url: `/pages/test-result/test-result?score=${score}&total=${this.questions.length}&correct=${correctAnswers.length}&wrong=${wrongAnswers.length}&correctAnswers=${JSON.stringify(correctAnswers)}&wrongAnswers=${JSON.stringify(wrongAnswers)}`
			});
		}
	}
}
</script>

<style>
	.questions-container {
		min-height: 100vh;
		background-color: #f5f5f5;
	}

	.nav-bar {
		height: 80rpx;
		background-color: #4CAF50;
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 0 30rpx;
		color: #fff;
	}

	.nav-title {
		font-size: 32rpx;
		font-weight: bold;
	}

	.nav-progress {
		font-size: 28rpx;
	}

	.timer {
		background-color: #fff;
		padding: 20rpx 30rpx;
		border-bottom: 1rpx solid #f0f0f0;
		display: flex;
		align-items: center;
	}

	.timer-label {
		font-size: 26rpx;
		color: #666;
		margin-right: 10rpx;
	}

	.timer-value {
		font-size: 26rpx;
		font-weight: bold;
		color: #FF6B6B;
	}

	.question-content {
		background-color: #fff;
		margin: 20rpx;
		padding: 30rpx;
		border-radius: 10rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
	}

	.question-number {
		font-size: 28rpx;
		font-weight: bold;
		color: #4CAF50;
		margin-bottom: 20rpx;
		display: