/**
 * 管理员端举报管理API
 * 
 * 功能说明：
 * 1. 提供举报列表查询接口
 * 2. 提供按状态筛选举报接口
 * 3. 提供举报处理接口
 * 4. 提供举报删除接口
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */

import request from '@/utils/request'

/**
 * 获取举报列表
 * 
 * @param {Object} params - 查询参数
 * @param {string} [params.status] - 举报状态（可选）
 *         - 空字符串: 查询所有举报
 *         - PENDING: 待处理
 *         - PROCESSING: 处理中
 *         - RESOLVED: 已解决
 * @returns {Promise} 返回举报列表数据
 */
export const getReportList = (params) => {
  if (params && params.status && params.status !== '') {
    return request({
      url: `/reports/status/${params.status}`,
      method: 'get'
    })
  } else {
    return request({
      url: '/reports',
      method: 'get'
    })
  }
}

/**
 * 获取管理员举报列表
 * 
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码，默认为1
 * @param {number} params.size - 每页数量，默认为10
 * @param {string} [params.status] - 举报状态（可选）
 * @returns {Promise} 返回举报列表数据
 */
export const getReports = (params) => {
  return request({
    url: '/admin/reports',
    method: 'get',
    params
  })
}

/**
 * 处理举报
 * 
 * @param {number} id - 举报ID
 * @param {Object} data - 处理数据
 * @param {string} data.status - 处理状态（RESOLVED: 已解决）
 * @param {string} data.handleResult - 处理结果说明
 * @returns {Promise} 返回处理结果
 */
export const handleReport = (id, data) => {
  return request({
    url: `/reports/${id}/status`,
    method: 'put',
    data
  })
}

/**
 * 删除举报
 * 
 * @param {number} id - 举报ID
 * @returns {Promise} 返回删除结果
 */
export const deleteReport = (id) => {
  return request({
    url: `/reports/${id}`,
    method: 'delete'
  })
}
