/**
 * 管理员端统计数据API
 * 
 * 功能说明：
 * 1. 提供平台统计数据查询接口
 * 2. 提供最新用户列表查询接口
 * 3. 提供最新帖子列表查询接口
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */

import request from '@/utils/request'

/**
 * 获取平台统计数据
 * 
 * 功能说明：
 * 返回平台的关键统计数据，包括：
 * - 用户总数
 * - 帖子总数
 * - 知识库总数
 * - 案例总数
 * - 举报总数
 * - 今日新增用户数
 * - 今日新增帖子数
 * 
 * @returns {Promise} 返回统计数据对象
 */
export const getStatistics = () => {
  return request({
    url: '/admin/stats',
    method: 'get'
  })
}

/**
 * 获取最新用户列表
 * 
 * @param {Object} params - 查询参数
 * @param {number} [params.page] - 页码，默认为1
 * @param {number} [params.size] - 每页数量，默认为5
 * @returns {Promise} 返回最新用户列表数据
 */
export const getRecentUsers = (params) => {
  return request({
    url: '/admin/users',
    method: 'get',
    params: { ...params, limit: 5 }
  })
}

/**
 * 获取最新帖子列表
 * 
 * @param {Object} params - 查询参数
 * @param {number} [params.page] - 页码，默认为1
 * @param {number} [params.size] - 每页数量，默认为5
 * @returns {Promise} 返回最新帖子列表数据
 */
export const getRecentPosts = (params) => {
  return request({
    url: '/admin/posts',
    method: 'get',
    params: { ...params, limit: 5 }
  })
}
