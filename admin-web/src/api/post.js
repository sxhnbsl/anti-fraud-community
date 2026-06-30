/**
 * 管理员端帖子管理API
 * 
 * 功能说明：
 * 1. 提供帖子列表查询接口
 * 2. 提供按审核状态筛选帖子接口
 * 3. 提供帖子审核接口
 * 4. 提供帖子删除接口
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */

import request from '@/utils/request'

/**
 * 获取帖子列表
 * 
 * @param {Object} params - 查询参数
 * @param {string} [params.auditStatus] - 审核状态（可选）
 *         - PENDING: 待审核
 *         - APPROVED: 已通过
 *         - REJECTED: 已拒绝
 *         - 不传该参数: 查询所有帖子
 * @returns {Promise} 返回帖子列表数据
 */
export const getPostList = (params) => {
  if (params && params.auditStatus) {
    return request({
      url: `/posts/admin/status/${params.auditStatus}`,
      method: 'get'
    })
  } else {
    return request({
      url: '/posts/admin/all',
      method: 'get'
    })
  }
}

/**
 * 审核帖子
 * 
 * @param {number} id - 帖子ID
 * @param {Object} data - 审核数据
 * @param {string} data.status - 审核状态（APPROVED: 通过, REJECTED: 拒绝）
 * @param {string} [data.reason] - 拒绝原因（当status为REJECTED时必填）
 * @returns {Promise} 返回审核结果
 */
export const auditPost = (id, data) => {
  return request({
    url: `/posts/admin/${id}/audit`,
    method: 'post',
    data
  })
}

/**
 * 删除帖子
 * 
 * @param {number} id - 帖子ID
 * @returns {Promise} 返回删除结果
 */
export const deletePost = (id) => {
  return request({
    url: `/posts/${id}`,
    method: 'delete'
  })
}
