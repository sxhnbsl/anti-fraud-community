/**
 * 管理员端评论管理API
 * 
 * 功能说明：
 * 1. 提供评论列表查询接口
 * 2. 提供按审核状态筛选评论接口
 * 3. 提供评论审核接口
 * 4. 提供评论删除接口
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */

import request from '@/utils/request'

/**
 * 获取评论列表
 * 
 * @param {Object} params - 查询参数
 * @param {string} [params.auditStatus] - 审核状态（可选）
 *         - PENDING: 待审核
 *         - APPROVED: 已通过
 *         - REJECTED: 已拒绝
 *         - 不传该参数: 查询所有评论
 * @returns {Promise} 返回评论列表数据
 */
export const getCommentList = (params) => {
  if (params && params.auditStatus) {
    return request({
      url: `/comments/admin/status/${params.auditStatus}`,
      method: 'get'
    })
  } else {
    return request({
      url: '/comments/admin/all',
      method: 'get'
    })
  }
}

/**
 * 审核评论
 * 
 * @param {number} id - 评论ID
 * @param {Object} data - 审核数据
 * @param {string} data.status - 审核状态（APPROVED: 通过, REJECTED: 拒绝）
 * @param {string} [data.reason] - 拒绝原因（当status为REJECTED时必填）
 * @returns {Promise} 返回审核结果
 */
export const auditComment = (id, data) => {
  return request({
    url: `/comments/admin/${id}/audit`,
    method: 'post',
    data
  })
}

/**
 * 删除评论
 * 
 * @param {number} id - 评论ID
 * @returns {Promise} 返回删除结果
 */
export const deleteComment = (id) => {
  return request({
    url: `/comments/${id}`,
    method: 'delete'
  })
}
