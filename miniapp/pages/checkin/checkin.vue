<template>
  <view class="check-in-page">
    <view class="header">
      <text class="title">每日签到</text>
      <text class="subtitle">连续签到可获得更多积分</text>
    </view>
    
    <view class="check-in-card" v-if="!checkedInToday">
      <view class="check-in-btn" @click="doCheckIn">
        <text class="check-in-text">立即签到</text>
        <text class="points-text">+{{ todayPoints }}积分</text>
        <view class="ripple-effect"></view>
      </view>
      <view class="check-in-info">
        <text class="info-text">连续签到第{{ consecutiveDays }}天</text>
        <text class="info-text">已获得{{ totalPoints }}积分</text>
      </view>
      <view class="streak-indicator">
        <view class="streak-bar">
          <view class="streak-progress" :style="{ width: (consecutiveDays % 7) * 14.28 + '%' }"></view>
        </view>
        <text class="streak-tip">距离下一级奖励还有{{ 7 - (consecutiveDays % 7) }}天</text>
      </view>
    </view>
    
    <view class="checked-in-card" v-else>
      <view class="checked-in-icon">🎉</view>
      <text class="checked-in-text">今日已签到</text>
      <text class="checked-in-info">连续签到第{{ consecutiveDays }}天</text>
      <text class="checked-in-info">已获得{{ totalPoints }}积分</text>
      <text class="checked-in-tip">明天再来签到吧，连续签到积分更多哦～</text>
      <view class="reward-badge">
        <text class="reward-text">+{{ todayPoints }}积分已到账</text>
      </view>
    </view>
    
    <view class="check-in-rules">
      <view class="rules-header">
        <text class="rules-icon">📋</text>
        <text class="rules-title">签到规则</text>
      </view>
      <view class="rule-item" v-for="(rule, index) in rules" :key="index">
        <view class="rule-number">{{ index + 1 }}</view>
        <text class="rule-text">{{ rule }}</text>
        <view class="rule-reward" v-if="index === 1 && consecutiveDays <= 2">
          <text class="reward-tag">当前</text>
        </view>
        <view class="rule-reward" v-else-if="index === 2 && consecutiveDays >= 3 && consecutiveDays <= 6">
          <text class="reward-tag">当前</text>
        </view>
        <view class="rule-reward" v-else-if="index === 3 && consecutiveDays >= 7">
          <text class="reward-tag">当前</text>
        </view>
      </view>
    </view>
    
    <view class="check-in-history">
      <view class="history-header">
        <text class="history-icon">📅</text>
        <text class="history-title">签到历史</text>
      </view>
      <view class="history-list" v-if="checkInHistory.length > 0">
        <view class="history-item" v-for="(item, index) in checkInHistory" :key="index">
          <view class="history-date-section">
            <text class="history-date">{{ item.date }}</text>
            <text class="history-weekday">{{ getWeekday(item.date) }}</text>
          </view>
          <view class="history-points-section">
            <text class="history-points">+{{ item.points }}积分</text>
            <view class="points-animation" v-if="index === 0 && checkedInToday"></view>
          </view>
        </view>
      </view>
      <view class="empty-history" v-else>
        <text class="empty-icon">📭</text>
        <text class="empty-text">暂无签到记录</text>
        <text class="empty-subtext">开始你的签到之旅吧！</text>
      </view>
    </view>
  </view>
</template>

<script>
import { checkInApi } from '@/utils/request.js'

export default {
  data() {
    return {
      checkedInToday: false,
      consecutiveDays: 0,
      totalPoints: 0,
      todayPoints: 1,
      checkInHistory: [],
      rules: [
        '每日签到可获得1-5积分',
        '连续签到1-2天：1积分',
        '连续签到3-6天：3积分',
        '连续签到7天及以上：5积分',
        '断签后连续签到天数重置为1天'
      ]
    }
  },
  onLoad() {
    this.loadCheckInStat()
    this.loadCheckInHistory()
  },
  methods: {
    getWeekday(dateStr) {
      const date = new Date(dateStr)
      const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
      return weekdays[date.getDay()]
    },
    async loadCheckInStat() {
      try {
        const response = await checkInApi.getCheckInStat()
        if (response.success) {
          const data = response.data
          this.checkedInToday = data.checkedInToday || false
          this.consecutiveDays = data.currentConsecutiveDays || 0
          this.totalPoints = data.totalPoints || 0
          
          if (this.consecutiveDays >= 7) {
            this.todayPoints = 5
          } else if (this.consecutiveDays >= 3) {
            this.todayPoints = 3
          } else {
            this.todayPoints = 1
          }
        }
      } catch (error) {
        console.error('加载签到统计失败:', error)
      }
    },
    
    async loadCheckInHistory() {
      try {
        const response = await checkInApi.getCheckInHistory()
        if (response.success) {
          this.checkInHistory = response.data || []
        }
      } catch (error) {
        console.error('加载签到历史失败:', error)
      }
    },
    
    async doCheckIn() {
      try {
        const response = await checkInApi.doCheckIn()
        if (response.success) {
          uni.showToast({
            title: '签到成功！获得' + response.data.points + '积分',
            icon: 'success'
          })
          
          this.loadCheckInStat()
          this.loadCheckInHistory()
        } else {
          if (response.message === '今日已签到' || response.message.includes('Duplicate entry') || response.message.includes('重复') || response.message.includes('已签')) {
            uni.showToast({
              title: '亲爱的，您今天已经签到过啦！连续签到才有更多积分奖励哦～',
              icon: 'none'
            })
            // 重新加载签到状态
            this.loadCheckInStat()
          } else {
            uni.showToast({
              title: response.message || '签到失败',
              icon: 'none'
            })
          }
        }
      } catch (error) {
        console.error('签到失败:', error)
        // 处理网络错误或其他错误
        if ((error.data && error.data.message && (error.data.message.includes('Duplicate entry') || error.data.message.includes('重复') || error.data.message.includes('已签') || error.data.message === '今日已签到')) ||
            (error.message && (error.message.includes('Duplicate entry') || error.message.includes('重复') || error.message.includes('已签') || error.message === '今日已签到'))) {
          uni.showToast({
            title: '亲爱的，您今天已经签到过啦！连续签到才有更多积分奖励哦～',
            icon: 'none'
          })
          // 重新加载签到状态
          this.loadCheckInStat()
        } else {
          uni.showToast({
            title: '签到失败，请稍后重试',
            icon: 'none'
          })
        }
      }
    }
  }
}
</script>

<style scoped>
.check-in-page {
  padding: 20rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  min-height: 100vh;
}

.header {
  text-align: center;
  margin-bottom: 30rpx;
  padding: 20rpx 0;
}

.title {
  font-size: 40rpx;
  font-weight: bold;
  color: #ffffff;
  margin-bottom: 10rpx;
  text-shadow: 0 4rpx 8rpx rgba(0, 0, 0, 0.3);
}

.subtitle {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.8);
}

.check-in-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 24rpx;
  padding: 50rpx;
  text-align: center;
  margin-bottom: 30rpx;
  box-shadow: 0 12rpx 32rpx rgba(0, 0, 0, 0.15);
  backdrop-filter: blur(10rpx);
  border: 1rpx solid rgba(255, 255, 255, 0.2);
}

.check-in-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  font-size: 36rpx;
  font-weight: bold;
  padding: 35rpx;
  border-radius: 50rpx;
  margin-bottom: 30rpx;
  box-shadow: 0 10rpx 25rpx rgba(102, 126, 234, 0.4);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.check-in-btn:active {
  transform: scale(0.95);
  box-shadow: 0 6rpx 15rpx rgba(102, 126, 234, 0.4);
}

.ripple-effect {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.5);
  transform: translate(-50%, -50%);
  transition: width 0.6s, height 0.6s;
}

.check-in-btn:active .ripple-effect {
  width: 300rpx;
  height: 300rpx;
}

.check-in-text {
  display: block;
  margin-bottom: 10rpx;
  position: relative;
  z-index: 1;
}

.points-text {
  font-size: 26rpx;
  opacity: 0.9;
  position: relative;
  z-index: 1;
}

.check-in-info {
  margin-top: 20rpx;
}

.info-text {
  display: block;
  font-size: 26rpx;
  color: #666;
  margin-bottom: 10rpx;
}

.streak-indicator {
  margin-top: 30rpx;
}

.streak-bar {
  width: 100%;
  height: 12rpx;
  background-color: #f0f0f0;
  border-radius: 6rpx;
  overflow: hidden;
  margin-bottom: 15rpx;
}

.streak-progress {
  height: 100%;
  background: linear-gradient(90deg, #4CAF50 0%, #81C784 100%);
  border-radius: 6rpx;
  transition: width 0.5s ease;
}

.streak-tip {
  font-size: 22rpx;
  color: #888;
  text-align: center;
}

.checked-in-card {
  background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
  border-radius: 24rpx;
  padding: 50rpx;
  text-align: center;
  margin-bottom: 30rpx;
  border: 2rpx solid #90caf9;
  box-shadow: 0 8rpx 24rpx rgba(144, 202, 249, 0.3);
  position: relative;
  overflow: hidden;
}

.checked-in-card::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(45deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transform: rotate(45deg);
  animation: shine 3s infinite;
}

@keyframes shine {
  0% { transform: translateX(-100%) rotate(45deg); }
  100% { transform: translateX(100%) rotate(45deg); }
}

.checked-in-icon {
  font-size: 90rpx;
  margin-bottom: 20rpx;
  animation: pulse 2s infinite;
  position: relative;
  z-index: 1;
}

@keyframes pulse {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
  100% {
    transform: scale(1);
  }
}

.checked-in-text {
  font-size: 36rpx;
  font-weight: bold;
  color: #1976d2;
  margin-bottom: 10rpx;
  position: relative;
  z-index: 1;
}

.checked-in-info {
  font-size: 26rpx;
  color: #666;
  margin-bottom: 10rpx;
  position: relative;
  z-index: 1;
}

.checked-in-tip {
  font-size: 24rpx;
  color: #999;
  margin-top: 15rpx;
  position: relative;
  z-index: 1;
}

.reward-badge {
  background: linear-gradient(135deg, #FFD700 0%, #FFA500 100%);
  color: #fff;
  padding: 15rpx 30rpx;
  border-radius: 25rpx;
  margin-top: 20rpx;
  display: inline-block;
  position: relative;
  z-index: 1;
  box-shadow: 0 4rpx 12rpx rgba(255, 215, 0, 0.3);
  animation: bounce 1s ease;
}

@keyframes bounce {
  0% { transform: translateY(-20rpx); opacity: 0; }
  50% { transform: translateY(5rpx); }
  70% { transform: translateY(-5rpx); }
  100% { transform: translateY(0); opacity: 1; }
}

.reward-text {
  font-size: 24rpx;
  font-weight: bold;
}

.check-in-rules {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 24rpx;
  padding: 30rpx;
  margin-bottom: 30rpx;
  box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10rpx);
  border: 1rpx solid rgba(255, 255, 255, 0.2);
}

.rules-header {
  display: flex;
  align-items: center;
  margin-bottom: 25rpx;
  padding-bottom: 20rpx;
  border-bottom: 2rpx solid #f0f0f0;
}

.rules-icon {
  font-size: 32rpx;
  margin-right: 15rpx;
}

.rules-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
}

.rule-item {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
  padding: 15rpx;
  border-radius: 12rpx;
  transition: all 0.2s ease;
  position: relative;
}

.rule-item:hover {
  background-color: #f8f9fa;
}

.rule-number {
  width: 40rpx;
  height: 40rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22rpx;
  font-weight: bold;
  margin-right: 20rpx;
  flex-shrink: 0;
}

.rule-text {
  flex: 1;
  font-size: 24rpx;
  color: #666;
  line-height: 1.5;
}

.rule-reward {
  margin-left: 20rpx;
}

.reward-tag {
  background: #4CAF50;
  color: white;
  padding: 5rpx 15rpx;
  border-radius: 15rpx;
  font-size: 20rpx;
  font-weight: bold;
  animation: pulse 1.5s infinite;
}

.check-in-history {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 24rpx;
  padding: 30rpx;
  box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10rpx);
  border: 1rpx solid rgba(255, 255, 255, 0.2);
}

.history-header {
  display: flex;
  align-items: center;
  margin-bottom: 25rpx;
  padding-bottom: 20rpx;
  border-bottom: 2rpx solid #f0f0f0;
}

.history-icon {
  font-size: 32rpx;
  margin-right: 15rpx;
}

.history-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
}

.history-list {
  border-top: 1rpx solid #f0f0f0;
  padding-top: 20rpx;
}

.history-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx;
  border-bottom: 1rpx solid #f5f5f5;
  transition: all 0.2s ease;
  border-radius: 12rpx;
  margin-bottom: 10rpx;
}

.history-item:last-child {
  border-bottom: none;
  margin-bottom: 0;
}

.history-item:active {
  background-color: #f8f9fa;
  transform: translateX(10rpx);
}

.history-date-section {
  display: flex;
  flex-direction: column;
}

.history-date {
  font-size: 24rpx;
  color: #333;
  font-weight: 500;
}

.history-weekday {
  font-size: 20rpx;
  color: #999;
  margin-top: 5rpx;
}

.history-points-section {
  position: relative;
  display: flex;
  align-items: center;
}

.history-points {
  font-size: 26rpx;
  color: #4CAF50;
  font-weight: bold;
  background: rgba(76, 175, 80, 0.1);
  padding: 8rpx 16rpx;
  border-radius: 15rpx;
}

.points-animation {
  position: absolute;
  right: 0;
  top: -50rpx;
  width: 20rpx;
  height: 20rpx;
  background: #4CAF50;
  border-radius: 50%;
  animation: floatUp 2s ease-out forwards;
}

@keyframes floatUp {
  0% {
    transform: translateY(0) scale(1);
    opacity: 1;
  }
  100% {
    transform: translateY(-100rpx) scale(0);
    opacity: 0;
  }
}

.empty-history {
  text-align: center;
  padding: 60rpx 0;
  color: #999;
}

.empty-icon {
  font-size: 60rpx;
  display: block;
  margin-bottom: 20rpx;
  opacity: 0.5;
}

.empty-text {
  display: block;
  font-size: 26rpx;
  margin-bottom: 10rpx;
}

.empty-subtext {
  font-size: 22rpx;
  opacity: 0.8;
}
</style>
