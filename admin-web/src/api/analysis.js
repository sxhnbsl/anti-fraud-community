/**
 * 管理员端数据分析API
 * 
 * 功能说明：
 * 1. 提供诈骗类型分析数据接口
 * 2. 提供举报分析数据接口
 * 3. 提供用户行为分析数据接口
 * 4. 提供内容效果分析数据接口
 * 5. 提供系统性能分析数据接口
 * 6. 提供平台概览数据接口
 * 7. 提供内容分析数据接口
 * 8. 提供互动分析数据接口
 * 9. 提供用户活跃度分析数据接口
 * 10. 提供举报类型分析数据接口
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */

import request from '@/utils/request'

/**
 * 获取诈骗类型分析数据
 * 
 * 功能说明：
 * 返回各类诈骗类型的统计数据，包括：
 * - 诈骗类型分布
 * - 各类型数量
 * - 各类型占比
 * 
 * @param {Object} params - 查询参数
 * @param {string} [params.startDate] - 开始日期（可选）
 * @param {string} [params.endDate] - 结束日期（可选）
 * @returns {Promise} 返回诈骗类型分析数据
 */
export const getFraudTypeAnalysis = (params) => {
  return request({
    url: '/analysis/fraud-types',
    method: 'get',
    params
  })
}

/**
 * 获取举报分析数据
 * 
 * 功能说明：
 * 返回举报相关的统计数据，包括：
 * - 举报总数
 * - 处理中的举报数
 * - 已解决的举报数
 * - 举报趋势
 * 
 * @param {Object} params - 查询参数
 * @param {string} [params.startDate] - 开始日期（可选）
 * @param {string} [params.endDate] - 结束日期（可选）
 * @returns {Promise} 返回举报分析数据
 */
export const getReportAnalysis = (params) => {
  return request({
    url: '/analysis/reports',
    method: 'get',
    params
  })
}

/**
 * 获取用户行为分析数据
 * 
 * 功能说明：
 * 返回用户行为相关的统计数据，包括：
 * - 用户活跃度
 * - 用户留存率
 * - 用户使用时长
 * 
 * @param {Object} params - 查询参数
 * @param {string} [params.startDate] - 开始日期（可选）
 * @param {string} [params.endDate] - 结束日期（可选）
 * @returns {Promise} 返回用户行为分析数据
 */
export const getUserBehaviorAnalysis = (params) => {
  return request({
    url: '/analysis/user-behavior',
    method: 'get',
    params
  })
}

/**
 * 获取内容效果分析数据
 * 
 * 功能说明：
 * 返回内容效果相关的统计数据，包括：
 * - 内容浏览量
 * - 内容点赞数
 * - 内容收藏数
 * - 内容分享数
 * 
 * @param {Object} params - 查询参数
 * @param {string} [params.startDate] - 开始日期（可选）
 * @param {string} [params.endDate] - 结束日期（可选）
 * @returns {Promise} 返回内容效果分析数据
 */
export const getContentEffectAnalysis = (params) => {
  return request({
    url: '/analysis/content-effect',
    method: 'get',
    params
  })
}

/**
 * 获取系统性能分析数据
 * 
 * 功能说明：
 * 返回系统性能相关的统计数据，包括：
 * - 系统响应时间
 * - 系统吞吐量
 * - 系统错误率
 * 
 * @param {Object} params - 查询参数
 * @param {string} [params.startDate] - 开始日期（可选）
 * @param {string} [params.endDate] - 结束日期（可选）
 * @returns {Promise} 返回系统性能分析数据
 */
export const getSystemPerformanceAnalysis = (params) => {
  return request({
    url: '/analysis/system-performance',
    method: 'get',
    params
  })
}

/**
 * 获取平台概览数据
 * 
 * 功能说明：
 * 返回平台整体概览数据，包括：
 * - 用户总数
 * - 内容总数
 * - 互动总数
 * - 系统运行状态
 * 
 * @returns {Promise} 返回平台概览数据
 */
export const getPlatformOverview = () => {
  return request({
    url: '/analysis/overview',
    method: 'get'
  })
}

/**
 * 获取内容分析数据
 * 
 * 功能说明：
 * 返回内容相关的统计数据，包括：
 * - 知识库数量
 * - 案例数量
 * - 帖子数量
 * - 评论数量
 * 
 * @returns {Promise} 返回内容分析数据
 */
export const getContentAnalysis = () => {
  return request({
    url: '/analysis/content',
    method: 'get'
  })
}

/**
 * 获取互动分析数据
 * 
 * 功能说明：
 * 返回用户互动相关的统计数据，包括：
 * - 点赞总数
 * - 评论总数
 * - 收藏总数
 * - 分享总数
 * 
 * @returns {Promise} 返回互动分析数据
 */
export const getInteractionAnalysis = () => {
  return request({
    url: '/analysis/interaction',
    method: 'get'
  })
}

/**
 * 获取用户活跃度分析数据
 * 
 * 功能说明：
 * 返回用户活跃度相关的统计数据，包括：
 * - 日活跃用户数
 * - 周活跃用户数
 * - 月活跃用户数
 * - 用户增长趋势
 * 
 * @returns {Promise} 返回用户活跃度分析数据
 */
export const getUserActivityAnalysis = () => {
  return request({
    url: '/analysis/user-activity',
    method: 'get'
  })
}

/**
 * 获取举报类型分析数据
 * 
 * 功能说明：
 * 返回举报类型相关的统计数据，包括：
 * - 各类型举报数量
 * - 各类型举报占比
 * - 举报类型分布
 * 
 * @returns {Promise} 返回举报类型分析数据
 */
export const getReportTypeAnalysis = () => {
  return request({
    url: '/analysis/report-type',
    method: 'get'
  })
}
