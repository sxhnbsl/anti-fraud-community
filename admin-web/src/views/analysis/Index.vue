<template>
  <div class="analysis">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>数据分析</span>
        </div>
      </template>

      <!-- 平台概览 -->
      <div class="overview-section">
        <h3>平台概览</h3>
        <div class="overview-cards">
          <el-card shadow="hover" class="overview-card">
            <div class="card-content">
              <div class="card-value">{{ overviewData.totalUsers || 0 }}</div>
              <div class="card-label">总用户数</div>
            </div>
          </el-card>
          <el-card shadow="hover" class="overview-card">
            <div class="card-content">
              <div class="card-value">{{ overviewData.totalPosts || 0 }}</div>
              <div class="card-label">总帖子数</div>
            </div>
          </el-card>
          <el-card shadow="hover" class="overview-card">
            <div class="card-content">
              <div class="card-value">{{ overviewData.totalComments || 0 }}</div>
              <div class="card-label">总评论数</div>
            </div>
          </el-card>
          <el-card shadow="hover" class="overview-card">
            <div class="card-content">
              <div class="card-value">{{ overviewData.totalReports || 0 }}</div>
              <div class="card-label">总举报数</div>
            </div>
          </el-card>
        </div>
      </div>

      <!-- 内容分析 -->
      <div class="section">
        <h3>内容分析</h3>
        <div class="chart-row">
          <el-card shadow="hover" class="chart-card">
            <h4>帖子分类分布</h4>
            <div ref="postCategoryChart" class="chart"></div>
          </el-card>
          <el-card shadow="hover" class="chart-card">
            <h4>知识库分类分布</h4>
            <div ref="knowledgeCategoryChart" class="chart"></div>
          </el-card>
          <el-card shadow="hover" class="chart-card">
            <h4>案例分类分布</h4>
            <div ref="caseCategoryChart" class="chart"></div>
          </el-card>
        </div>
      </div>

      <!-- 互动数据分析 -->
      <div class="section">
        <h3>互动数据分析</h3>
        <div class="chart-row">
          <el-card shadow="hover" class="chart-card">
            <h4>帖子浏览量排行</h4>
            <div ref="postViewRankChart" class="chart"></div>
          </el-card>
          <el-card shadow="hover" class="chart-card">
            <h4>知识库点赞排行</h4>
            <div ref="knowledgeLikeRankChart" class="chart"></div>
          </el-card>
          <el-card shadow="hover" class="chart-card">
            <h4>案例浏览量排行</h4>
            <div ref="caseViewRankChart" class="chart"></div>
          </el-card>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import {
  getPlatformOverview,
  getContentAnalysis,
  getInteractionAnalysis
} from '@/api/analysis'

const overviewData = ref({})

// 图表实例
const postCategoryChart = ref(null)
const knowledgeCategoryChart = ref(null)
const caseCategoryChart = ref(null)
const postViewRankChart = ref(null)
const knowledgeLikeRankChart = ref(null)
const caseViewRankChart = ref(null)

const chartInstances = ref([])

// 初始化图表
const initCharts = () => {
  chartInstances.value = []
  
  const postCategoryInstance = echarts.init(postCategoryChart.value)
  chartInstances.value.push(postCategoryInstance)
  
  const knowledgeCategoryInstance = echarts.init(knowledgeCategoryChart.value)
  chartInstances.value.push(knowledgeCategoryInstance)
  
  const caseCategoryInstance = echarts.init(caseCategoryChart.value)
  chartInstances.value.push(caseCategoryInstance)
  
  const postViewRankInstance = echarts.init(postViewRankChart.value)
  chartInstances.value.push(postViewRankInstance)
  
  const knowledgeLikeRankInstance = echarts.init(knowledgeLikeRankChart.value)
  chartInstances.value.push(knowledgeLikeRankInstance)
  
  const caseViewRankInstance = echarts.init(caseViewRankChart.value)
  chartInstances.value.push(caseViewRankInstance)
  
  return {
    postCategoryInstance,
    knowledgeCategoryInstance,
    caseCategoryInstance,
    postViewRankInstance,
    knowledgeLikeRankInstance,
    caseViewRankInstance
  }
}

// 加载平台概览数据
const loadOverviewData = async () => {
  try {
    const res = await getPlatformOverview()
    if (res && res.data) {
      overviewData.value = res.data
    }
  } catch (error) {
    console.error('加载平台概览数据失败:', error)
  }
}

// 加载内容分析数据
const loadContentData = async (instances) => {
  try {
    const res = await getContentAnalysis()
    if (res && res.data) {
      // 帖子分类分布
      if (res.data.postCategory) {
        const postOption = {
          title: {
            text: '帖子分类分布',
            left: 'center'
          },
          tooltip: {
            trigger: 'item',
            formatter: '{a} <br/>{b}: {c} ({d}%)'
          },
          legend: {
            orient: 'vertical',
            left: 'left',
            data: res.data.postCategory.categories || []
          },
          series: [
            {
              name: '帖子分类',
              type: 'pie',
              radius: '50%',
              data: (res.data.postCategory.categories || []).map((category, index) => ({
                value: res.data.postCategory.counts[index],
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
        instances.postCategoryInstance.setOption(postOption)
      }
      
      // 知识库分类分布
      if (res.data.knowledgeCategory) {
        const knowledgeOption = {
          title: {
            text: '知识库分类分布',
            left: 'center'
          },
          tooltip: {
            trigger: 'item',
            formatter: '{a} <br/>{b}: {c} ({d}%)'
          },
          legend: {
            orient: 'vertical',
            left: 'left',
            data: res.data.knowledgeCategory.categories || []
          },
          series: [
            {
              name: '知识库分类',
              type: 'pie',
              radius: '50%',
              data: (res.data.knowledgeCategory.categories || []).map((category, index) => ({
                value: res.data.knowledgeCategory.counts[index],
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
        instances.knowledgeCategoryInstance.setOption(knowledgeOption)
      }
      
      // 案例分类分布
      if (res.data.caseCategory) {
        const caseOption = {
          title: {
            text: '案例分类分布',
            left: 'center'
          },
          tooltip: {
            trigger: 'item',
            formatter: '{a} <br/>{b}: {c} ({d}%)'
          },
          legend: {
            orient: 'vertical',
            left: 'left',
            data: res.data.caseCategory.categories || []
          },
          series: [
            {
              name: '案例分类',
              type: 'pie',
              radius: '50%',
              data: (res.data.caseCategory.categories || []).map((category, index) => ({
                value: res.data.caseCategory.counts[index],
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
        instances.caseCategoryInstance.setOption(caseOption)
      }
    }
  } catch (error) {
    console.error('加载内容分析数据失败:', error)
  }
}

// 加载互动数据分析
const loadInteractionData = async (instances) => {
  try {
    const res = await getInteractionAnalysis()
    if (res && res.data) {
      // 帖子浏览量排行
      if (res.data.postViewRank) {
        const postViewOption = {
          title: {
            text: '帖子浏览量排行',
            left: 'center'
          },
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'shadow'
            }
          },
          xAxis: {
            type: 'category',
            data: res.data.postViewRank.titles || [],
            axisLabel: {
              interval: 0,
              rotate: 30
            }
          },
          yAxis: {
            type: 'value'
          },
          series: [
            {
              name: '浏览量',
              type: 'bar',
              data: res.data.postViewRank.views || [],
              itemStyle: {
                color: '#409EFF'
              }
            }
          ]
        }
        instances.postViewRankInstance.setOption(postViewOption)
      }
      
      // 知识库点赞排行
      if (res.data.knowledgeLikeRank) {
        const knowledgeLikeOption = {
          title: {
            text: '知识库点赞排行',
            left: 'center'
          },
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'shadow'
            }
          },
          xAxis: {
            type: 'category',
            data: res.data.knowledgeLikeRank.titles || [],
            axisLabel: {
              interval: 0,
              rotate: 30
            }
          },
          yAxis: {
            type: 'value'
          },
          series: [
            {
              name: '点赞数',
              type: 'bar',
              data: res.data.knowledgeLikeRank.likes || [],
              itemStyle: {
                color: '#67C23A'
              }
            }
          ]
        }
        instances.knowledgeLikeRankInstance.setOption(knowledgeLikeOption)
      }
      
      // 案例浏览量排行
      if (res.data.caseViewRank) {
        const caseViewOption = {
          title: {
            text: '案例浏览量排行',
            left: 'center'
          },
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'shadow'
            }
          },
          xAxis: {
            type: 'category',
            data: res.data.caseViewRank.titles || [],
            axisLabel: {
              interval: 0,
              rotate: 30
            }
          },
          yAxis: {
            type: 'value'
          },
          series: [
            {
              name: '浏览量',
              type: 'bar',
              data: res.data.caseViewRank.views || [],
              itemStyle: {
                color: '#E6A23C'
              }
            }
          ]
        }
        instances.caseViewRankInstance.setOption(caseViewOption)
      }
    }
  } catch (error) {
    console.error('加载互动分析数据失败:', error)
  }
}

// 加载所有数据
const loadData = async () => {
  await loadOverviewData()
  
  const instances = initCharts()
  
  await Promise.all([
    loadContentData(instances),
    loadInteractionData(instances)
  ])
}

// 窗口大小改变时重新调整图表
const handleResize = () => {
  chartInstances.value.forEach(instance => {
    instance && instance.resize()
  })
}

onMounted(() => {
  loadData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  chartInstances.value.forEach(instance => {
    instance && instance.dispose()
  })
})
</script>

<style scoped>
.analysis {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.overview-section {
  margin-bottom: 30px;
}

.overview-section h3 {
  margin-bottom: 20px;
  color: #303133;
}

.overview-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.overview-card {
  text-align: center;
}

.card-content {
  padding: 20px;
}

.card-value {
  font-size: 32px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 10px;
}

.card-label {
  font-size: 14px;
  color: #909399;
}

.section {
  margin-bottom: 30px;
}

.section h3 {
  margin-bottom: 20px;
  color: #303133;
}

.chart-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.chart-card {
  min-height: 400px;
}

.chart-card h4 {
  margin-bottom: 15px;
  color: #606266;
}

.chart {
  width: 100%;
  height: 350px;
}
</style>
