<template>
  <div class="dashboard">
    <div class="dashboard-header">
      <el-card class="welcome-card">
        <div class="welcome-content">
          <div class="welcome-text">
            <h2>欢迎回来，管理员 👋</h2>
            <p>今天是个美好的工作日，让我们一起维护平台安全！</p>
          </div>
          <div class="welcome-time">
            <div class="current-time">{{ currentTime }}</div>
            <div class="current-date">{{ currentDate }}</div>
          </div>
        </div>
      </el-card>
    </div>

    <div class="stats-section">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon" style="background: linear-gradient(135deg, #409EFF 0%, #66b1ff 100%);">
                <el-icon :size="28"><User /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ statistics.userCount || 0 }}</div>
                <div class="stat-label">总用户数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon" style="background: linear-gradient(135deg, #67C23A 0%, #85ce61 100%);">
                <el-icon :size="28"><Document /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ statistics.knowledgeCount || 0 }}</div>
                <div class="stat-label">知识库数量</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon" style="background: linear-gradient(135deg, #E6A23C 0%, #ebb563 100%);">
                <el-icon :size="28"><Notebook /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ statistics.caseCount || 0 }}</div>
                <div class="stat-label">案例数量</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon" style="background: linear-gradient(135deg, #F56C6C 0%, #f78989 100%);">
                <el-icon :size="28"><Warning /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ statistics.reportCount || 0 }}</div>
                <div class="stat-label">待处理举报</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" style="margin-top: 20px">
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon" style="background: linear-gradient(135deg, #909399 0%, #a6a9ad 100%);">
                <el-icon :size="28"><ChatDotRound /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ statistics.postCount || 0 }}</div>
                <div class="stat-label">总帖子数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon" style="background: linear-gradient(135deg, #8E44AD 0%, #a569bd 100%);">
                <el-icon :size="28"><ChatLineRound /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ statistics.commentCount || 0 }}</div>
                <div class="stat-label">总评论数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon" style="background: linear-gradient(135deg, #16A085 0%, #1abc9c 100%);">
                <el-icon :size="28"><Clock /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ statistics.pendingUsers || 0 }}</div>
                <div class="stat-label">待审核用户</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon" style="background: linear-gradient(135deg, #D35400 0%, #e67e22 100%);">
                <el-icon :size="28"><View /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ statistics.totalViews || 0 }}</div>
                <div class="stat-label">总浏览量</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <el-card class="analysis-card">
      <template #header>
        <div class="card-header">
          <span>数据分析</span>
        </div>
      </template>

      <div class="section">
        <h3>内容分析</h3>
        <div class="chart-row">
          <div class="chart-card">
            <h4>帖子分类分布</h4>
            <div ref="postCategoryChart" class="chart"></div>
          </div>
          <div class="chart-card">
            <h4>知识库分类分布</h4>
            <div ref="knowledgeCategoryChart" class="chart"></div>
          </div>
          <div class="chart-card">
            <h4>案例分类分布</h4>
            <div ref="caseCategoryChart" class="chart"></div>
          </div>
        </div>
      </div>

      <div class="section">
        <h3>互动数据分析</h3>
        <div class="chart-row">
          <div class="chart-card">
            <h4>帖子浏览量排行</h4>
            <div ref="postViewRankChart" class="chart"></div>
          </div>
          <div class="chart-card">
            <h4>知识库点赞排行</h4>
            <div ref="knowledgeLikeRankChart" class="chart"></div>
          </div>
          <div class="chart-card">
            <h4>案例浏览量排行</h4>
            <div ref="caseViewRankChart" class="chart"></div>
          </div>
        </div>
      </div>

      <div class="section">
        <h3>用户活跃度分析</h3>
        <div class="chart-row two-col">
          <div class="chart-card">
            <h4>用户活跃状态</h4>
            <div ref="userActivityChart" class="chart"></div>
          </div>
          <div class="chart-card">
            <h4>活跃度等级分布</h4>
            <div ref="activityLevelChart" class="chart"></div>
          </div>
        </div>
      </div>

      <div class="section">
        <h3>举报分析</h3>
        <div class="chart-row two-col">
          <div class="chart-card">
            <h4>举报类型分布</h4>
            <div ref="reportTypeChart" class="chart"></div>
          </div>
          <div class="chart-card">
            <h4>举报状态分布</h4>
            <div ref="reportStatusChart" class="chart"></div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { 
  getStatistics, 
  getRecentUsers, 
  getRecentPosts 
} from '@/api/statistics'
import {
  getContentAnalysis,
  getInteractionAnalysis,
  getUserActivityAnalysis,
  getReportTypeAnalysis
} from '@/api/analysis'
import { User, Document, Notebook, Warning, ChatDotRound, ChatLineRound, Clock, View, DataAnalysis } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

const loading = ref(false)
const statistics = ref({
  userCount: 0,
  knowledgeCount: 0,
  caseCount: 0,
  reportCount: 0,
  postCount: 0,
  commentCount: 0,
  pendingUsers: 0,
  totalViews: 0
})

const currentTime = ref('')
const currentDate = ref('')
let timeInterval = null

const updateTime = () => {
  const now = new Date()
  const hours = String(now.getHours()).padStart(2, '0')
  const minutes = String(now.getMinutes()).padStart(2, '0')
  const seconds = String(now.getSeconds()).padStart(2, '0')
  currentTime.value = `${hours}:${minutes}:${seconds}`
  
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const day = String(now.getDate()).padStart(2, '0')
  const weekDays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
  const weekDay = weekDays[now.getDay()]
  currentDate.value = `${year}年${month}月${day}日 ${weekDay}`
}

const chartInstances = ref([])

const postCategoryChart = ref(null)
const knowledgeCategoryChart = ref(null)
const caseCategoryChart = ref(null)
const postViewRankChart = ref(null)
const knowledgeLikeRankChart = ref(null)
const caseViewRankChart = ref(null)
const userActivityChart = ref(null)
const activityLevelChart = ref(null)
const reportTypeChart = ref(null)
const reportStatusChart = ref(null)

const loadStatistics = async () => {
  try {
    const res = await getStatistics()
    statistics.value = res.data || {}
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

const initCharts = () => {
  const instances = []
  
  if (postCategoryChart.value) {
    instances.push(echarts.init(postCategoryChart.value))
  }
  if (knowledgeCategoryChart.value) {
    instances.push(echarts.init(knowledgeCategoryChart.value))
  }
  if (caseCategoryChart.value) {
    instances.push(echarts.init(caseCategoryChart.value))
  }
  if (postViewRankChart.value) {
    instances.push(echarts.init(postViewRankChart.value))
  }
  if (knowledgeLikeRankChart.value) {
    instances.push(echarts.init(knowledgeLikeRankChart.value))
  }
  if (caseViewRankChart.value) {
    instances.push(echarts.init(caseViewRankChart.value))
  }
  if (userActivityChart.value) {
    instances.push(echarts.init(userActivityChart.value))
  }
  if (activityLevelChart.value) {
    instances.push(echarts.init(activityLevelChart.value))
  }
  if (reportTypeChart.value) {
    instances.push(echarts.init(reportTypeChart.value))
  }
  if (reportStatusChart.value) {
    instances.push(echarts.init(reportStatusChart.value))
  }
  
  return instances
}

const loadContentData = async (instances) => {
  try {
    const res = await getContentAnalysis()
    const data = res.data || {}
    
    const postCategoryData = data.postCategory || { categories: [], counts: [] }
    const knowledgeCategoryData = data.knowledgeCategory || { categories: [], counts: [] }
    const caseCategoryData = data.caseCategory || { categories: [], counts: [] }
    
    const postCategoryOption = {
      tooltip: {
        trigger: 'item'
      },
      legend: {
        orient: 'vertical',
        left: 'left'
      },
      series: [
        {
          name: '帖子分类',
          type: 'pie',
          radius: '50%',
          data: postCategoryData.categories.map((category, index) => ({
            value: postCategoryData.counts[index],
            name: category
          })),
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }
      ]
    }
    
    const knowledgeCategoryOption = {
      tooltip: {
        trigger: 'item'
      },
      legend: {
        orient: 'vertical',
        left: 'left'
      },
      series: [
        {
          name: '知识库分类',
          type: 'pie',
          radius: '50%',
          data: knowledgeCategoryData.categories.map((category, index) => ({
            value: knowledgeCategoryData.counts[index],
            name: category
          })),
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }
      ]
    }
    
    const caseCategoryOption = {
      tooltip: {
        trigger: 'item'
      },
      legend: {
        orient: 'vertical',
        left: 'left'
      },
      series: [
        {
          name: '案例分类',
          type: 'pie',
          radius: '50%',
          data: caseCategoryData.categories.map((category, index) => ({
            value: caseCategoryData.counts[index],
            name: category
          })),
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }
      ]
    }
    
    if (instances[0]) instances[0].setOption(postCategoryOption)
    if (instances[1]) instances[1].setOption(knowledgeCategoryOption)
    if (instances[2]) instances[2].setOption(caseCategoryOption)
  } catch (error) {
    console.error('加载内容分析数据失败:', error)
  }
}

const loadInteractionData = async (instances) => {
  try {
    const res = await getInteractionAnalysis()
    const data = res.data || {}
    
    const postViewRankData = data.postViewRank || { titles: [], views: [] }
    const knowledgeLikeRankData = data.knowledgeLikeRank || { titles: [], likes: [] }
    const caseViewRankData = data.caseViewRank || { titles: [], views: [] }
    
    const postViewRankOption = {
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        },
        formatter: function(params) {
          const param = params[0]
          return `${param.name}<br/>浏览量: ${param.value}`
        }
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        top: '10%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: postViewRankData.titles,
        axisLabel: {
          rotate: 30,
          interval: 0,
          fontSize: 11
        }
      },
      yAxis: {
        type: 'value',
        name: '浏览量'
      },
      series: [
        {
          name: '浏览量',
          type: 'bar',
          data: postViewRankData.views,
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#83bff6' },
              { offset: 0.5, color: '#188df0' },
              { offset: 1, color: '#188df0' }
            ]),
            borderRadius: [4, 4, 0, 0]
          },
          emphasis: {
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#2378f7' },
                { offset: 0.7, color: '#2378f7' },
                { offset: 1, color: '#83bff6' }
              ])
            }
          },
          label: {
            show: true,
            position: 'top',
            fontSize: 12,
            color: '#666'
          }
        }
      ]
    }
    
    const knowledgeLikeRankOption = {
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        },
        formatter: function(params) {
          const param = params[0]
          return `${param.name}<br/>点赞数: ${param.value}`
        }
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        top: '10%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: knowledgeLikeRankData.titles,
        axisLabel: {
          rotate: 30,
          interval: 0,
          fontSize: 11
        }
      },
      yAxis: {
        type: 'value',
        name: '点赞数'
      },
      series: [
        {
          name: '点赞数',
          type: 'bar',
          data: knowledgeLikeRankData.likes,
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#95d475' },
              { offset: 0.5, color: '#67c23a' },
              { offset: 1, color: '#67c23a' }
            ]),
            borderRadius: [4, 4, 0, 0]
          },
          emphasis: {
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#85ce61' },
                { offset: 0.7, color: '#85ce61' },
                { offset: 1, color: '#95d475' }
              ])
            }
          },
          label: {
            show: true,
            position: 'top',
            fontSize: 12,
            color: '#666'
          }
        }
      ]
    }
    
    const caseViewRankOption = {
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        },
        formatter: function(params) {
          const param = params[0]
          return `${param.name}<br/>浏览量: ${param.value}`
        }
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        top: '10%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: caseViewRankData.titles,
        axisLabel: {
          rotate: 30,
          interval: 0,
          fontSize: 11
        }
      },
      yAxis: {
        type: 'value',
        name: '浏览量'
      },
      series: [
        {
          name: '浏览量',
          type: 'bar',
          data: caseViewRankData.views,
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#f3d19e' },
              { offset: 0.5, color: '#e6a23c' },
              { offset: 1, color: '#e6a23c' }
            ]),
            borderRadius: [4, 4, 0, 0]
          },
          emphasis: {
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#ebb563' },
                { offset: 0.7, color: '#ebb563' },
                { offset: 1, color: '#f3d19e' }
              ])
            }
          },
          label: {
            show: true,
            position: 'top',
            fontSize: 12,
            color: '#666'
          }
        }
      ]
    }
    
    if (instances[3]) instances[3].setOption(postViewRankOption)
    if (instances[4]) instances[4].setOption(knowledgeLikeRankOption)
    if (instances[5]) instances[5].setOption(caseViewRankOption)
  } catch (error) {
    console.error('加载互动分析数据失败:', error)
  }
}

const loadUserActivityData = async (instances) => {
  try {
    const res = await getUserActivityAnalysis()
    const data = res.data || {}
    
    const userActivityData = data.userActivity || { active: 0, inactive: 0 }
    const activityLevelData = data.activityLevel || { highlyActive: 0, moderatelyActive: 0, lowlyActive: 0, inactive: 0 }
    
    const userActivityOption = {
      tooltip: {
        trigger: 'item',
        formatter: '{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        left: 'left'
      },
      series: [
        {
          name: '用户活跃状态',
          type: 'pie',
          radius: ['40%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 10,
            borderColor: '#fff',
            borderWidth: 2
          },
          label: {
            show: true,
            formatter: '{b}: {c}'
          },
          emphasis: {
            label: {
              show: true,
              fontSize: 16,
              fontWeight: 'bold'
            }
          },
          data: [
            { value: userActivityData.active, name: '活跃用户' },
            { value: userActivityData.inactive, name: '未活跃用户' }
          ]
        }
      ]
    }
    
    const activityLevelOption = {
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        },
        formatter: function(params) {
          const param = params[0]
          const levelNames = ['高度活跃', '中度活跃', '低度活跃', '未活跃']
          return `${levelNames[param.dataIndex]}: ${param.value}人`
        }
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        top: '10%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: ['高度活跃', '中度活跃', '低度活跃', '未活跃'],
        axisLabel: {
          interval: 0,
          fontSize: 12
        }
      },
      yAxis: {
        type: 'value',
        name: '用户数'
      },
      series: [
        {
          name: '用户数',
          type: 'bar',
          data: [
            activityLevelData.highlyActive,
            activityLevelData.moderatelyActive,
            activityLevelData.lowlyActive,
            activityLevelData.inactive
          ],
          itemStyle: {
            color: function(params) {
              const colors = ['#5470c6', '#91cc75', '#fac858', '#ee6666']
              return colors[params.dataIndex % colors.length]
            },
            borderRadius: [4, 4, 0, 0]
          },
          label: {
            show: true,
            position: 'top',
            fontSize: 12,
            color: '#666'
          }
        }
      ]
    }
    
    if (instances[6]) instances[6].setOption(userActivityOption)
    if (instances[7]) instances[7].setOption(activityLevelOption)
  } catch (error) {
    console.error('加载用户活跃度数据失败:', error)
  }
}

const loadReportTypeData = async (instances) => {
  try {
    const res = await getReportTypeAnalysis()
    const data = res.data || {}
    
    const reportTypeData = data.reportType || { types: [], counts: [] }
    const reportStatusData = data.reportStatus || { statuses: [], counts: [] }
    
    const reportTypeOption = {
      tooltip: {
        trigger: 'item',
        formatter: '{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        left: 'left'
      },
      series: [
        {
          name: '举报类型',
          type: 'pie',
          radius: '50%',
          data: reportTypeData.types.map((type, index) => ({
            value: reportTypeData.counts[index],
            name: type
          })),
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }
      ]
    }
    
    const reportStatusOption = {
      tooltip: {
        trigger: 'item',
        formatter: '{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        left: 'left'
      },
      series: [
        {
          name: '举报状态',
          type: 'pie',
          radius: '50%',
          data: reportStatusData.statuses.map((status, index) => ({
            value: reportStatusData.counts[index],
            name: status
          })),
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }
      ]
    }
    
    if (instances[8]) instances[8].setOption(reportTypeOption)
    if (instances[9]) instances[9].setOption(reportStatusOption)
  } catch (error) {
    console.error('加载举报类型数据失败:', error)
  }
}

const loadData = async () => {
  await loadStatistics()
  
  const instances = initCharts()
  
  await Promise.all([
    loadContentData(instances),
    loadInteractionData(instances),
    loadUserActivityData(instances),
    loadReportTypeData(instances)
  ])
}

const handleResize = () => {
  chartInstances.value.forEach(instance => {
    instance && instance.resize()
  })
}

onMounted(() => {
  updateTime()
  timeInterval = setInterval(updateTime, 1000)
  loadData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  if (timeInterval) {
    clearInterval(timeInterval)
  }
  window.removeEventListener('resize', handleResize)
  chartInstances.value.forEach(instance => {
    instance && instance.dispose()
  })
})
</script>

<style scoped>
.dashboard {
  padding: 0;
  background: transparent;
}

.dashboard-header {
  margin-bottom: 24px;
}

.welcome-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.3);
}

.welcome-card :deep(.el-card__body) {
  padding: 32px;
}

.welcome-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #fff;
}

.welcome-text h2 {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 8px;
}

.welcome-text p {
  font-size: 14px;
  opacity: 0.9;
}

.welcome-time {
  text-align: right;
}

.current-time {
  font-size: 36px;
  font-weight: 700;
  font-family: 'Courier New', monospace;
  margin-bottom: 4px;
}

.current-date {
  font-size: 14px;
  opacity: 0.9;
}

.stats-section {
  margin-bottom: 24px;
}

.stat-card {
  background: #fff;
  border: none;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
}

.stat-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.12);
}

.stat-card :deep(.el-card__body) {
  padding: 24px;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 28px;
  flex-shrink: 0;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease;
}

.stat-card:hover .stat-icon {
  transform: scale(1.1) rotate(5deg);
}

.stat-info {
  flex: 1;
  min-width: 0;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #1a1a1a;
  line-height: 1.2;
  margin-bottom: 6px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  transition: all 0.3s ease;
}

.stat-card:hover .stat-value {
  color: var(--primary-color);
}

.stat-label {
  font-size: 14px;
  color: #909399;
  font-weight: 500;
  letter-spacing: 0.5px;
}

.analysis-card {
  background: #fff;
  border: none;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.analysis-card :deep(.el-card__header) {
  padding: 20px 24px;
  border-bottom: 1px solid #f0f2f5;
  background: linear-gradient(180deg, #fff 0%, #fafbfc 100%);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header span {
  font-size: 18px;
  font-weight: 700;
  color: #1a1a1a;
  display: flex;
  align-items: center;
  gap: 10px;
}

.card-header span::before {
  content: '';
  width: 4px;
  height: 20px;
  background: linear-gradient(180deg, #409EFF 0%, #66b1ff 100%);
  border-radius: 2px;
}

.analysis-card :deep(.el-card__body) {
  padding: 24px;
}

.section {
  margin-bottom: 36px;
}

.section:last-child {
  margin-bottom: 0;
}

.section h3 {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.section h3::before {
  content: '';
  width: 6px;
  height: 6px;
  background: #409EFF;
  border-radius: 50%;
}

.chart-row {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.chart-row.two-col {
  grid-template-columns: repeat(2, 1fr);
}

.chart-card {
  background: linear-gradient(180deg, #fafbfc 0%, #fff 100%);
  border-radius: 12px;
  padding: 20px;
  border: 1px solid #f0f2f5;
  transition: all 0.3s ease;
}

.chart-card:hover {
  border-color: #d9e6ff;
  box-shadow: 0 4px 16px rgba(64, 158, 255, 0.1);
}

.chart-card h4 {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 16px 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.chart-card h4::after {
  content: '';
  flex: 1;
  height: 1px;
  background: linear-gradient(90deg, #e8ecf2 0%, transparent 100%);
}

.chart {
  width: 100%;
  height: 300px;
}

:deep(.el-table) {
  font-size: 14px;
  border-radius: 12px;
  overflow: hidden;
}

:deep(.el-table th) {
  background: linear-gradient(180deg, #f8f9fa 0%, #f5f7fa 100%);
  color: #606266;
  font-weight: 600;
  font-size: 14px;
}

:deep(.el-table th.el-table__cell) {
  padding: 14px 0;
}

:deep(.el-table td) {
  padding: 12px 0;
}

:deep(.el-table--small) {
  font-size: 13px;
}

:deep(.el-tag) {
  border-radius: 20px;
  padding: 4px 14px;
  font-weight: 500;
  font-size: 12px;
  border: none;
}

:deep(.el-tag--success) {
  background: linear-gradient(135deg, #67C23A 0%, #85ce61 100%);
  color: #fff;
}

:deep(.el-tag--warning) {
  background: linear-gradient(135deg, #E6A23C 0%, #ebb563 100%);
  color: #fff;
}

:deep(.el-tag--danger) {
  background: linear-gradient(135deg, #F56C6C 0%, #f78989 100%);
  color: #fff;
}

:deep(.el-tag--info) {
  background: linear-gradient(135deg, #909399 0%, #a6a9ad 100%);
  color: #fff;
}

:deep(.el-tag--primary) {
  background: linear-gradient(135deg, #409EFF 0%, #66b1ff 100%);
  color: #fff;
}

:deep(.el-tag--small) {
  padding: 2px 10px;
  font-size: 11px;
}

:deep(.el-button--primary.is-link) {
  font-weight: 500;
  font-size: 14px;
}

:deep(.el-button) {
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.2s ease;
}

:deep(.el-button:hover) {
  transform: translateY(-1px);
}

@media (max-width: 1400px) {
  .chart-row {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .chart-row.two-col {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 992px) {
  .chart-row,
  .chart-row.two-col {
    grid-template-columns: 1fr;
  }
  
  .stat-value {
    font-size: 28px;
  }
}
</style>
