/**
 * 管理员端用户管理API
 * 
 * 功能说明：
 * 1. 提供管理员登录接口
 * 2. 提供用户列表查询接口
 * 3. 提供待审核用户查询接口
 * 4. 提供用户审核接口
 * 5. 提供用户删除接口
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */

import request from '@/utils/request'

/**
 * 管理员登录
 * 
 * @param {Object} data - 登录数据
 * @param {string} data.username - 用户名
 * @param {string} data.password - 密码
 * @returns {Promise} 返回登录结果，包含token和用户信息
 */
export const login = (data) => {
  return request({
    url: '/admin/login',
    method: 'post',
    data
  })
}

/**
 * 获取用户列表
 * 
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码，默认为1
 * @param {number} params.size - 每页数量，默认为10
 * @param {string} [params.keyword] - 搜索关键词（可选）
 * @returns {Promise} 返回用户列表数据
 */
export const getUserList = (params) => {
  return request({
    url: '/admin/users',
    method: 'get',
    params
  })
}

/**
 * 获取待审核用户列表
 * 
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码，默认为1
 * @param {number} params.size - 每页数量，默认为10
 * @returns {Promise} 返回待审核用户列表数据
 */
export const getPendingUsers = (params) => {
  return request({
    url: '/admin/users/pending',
    method: 'get',
    params
  })
}

/**
 * 审核用户
 * 
 * @param {number} id - 用户ID
 * @param {Object} data - 审核数据
 * @param {string} data.status - 审核状态（approved: 通过, rejected: 拒绝）
 * @param {string} [data.reason] - 拒绝原因（当status为rejected时必填）
 * @returns {Promise} 返回审核结果
 */
export const auditUser = (id, data) => {
  return request({
    url: `/admin/users/${id}/audit`,
    method: 'post',
    data
  })
}

/**
 * 删除用户
 * 
 * @param {number} id - 用户ID
 * @returns {Promise} 返回删除结果
 */
export const deleteUser = (id) => {
  return request({
    url: `/admin/users/${id}`,
    method: 'delete'
  })
}

/**
 * 重置用户密码
 * 
 * @param {number} id - 用户ID
 * @param {Object} data - 重置数据
 * @param {string} data.password - 新密码，默认为123456
 * @returns {Promise} 返回重置结果
 */
export const resetUserPassword = (id, data) => {
  return request({
    url: `/admin/users/${id}/reset-password`,
    method: 'post',
    data
  })
}

/**
 * 获取待审核的用户资料变更记录
 * 
 * @returns {Promise} 返回待审核变更记录列表
 */
export const getPendingProfileChanges = () => {
  return request({
    url: '/admin/profile-changes/pending',
    method: 'get'
  })
}

/**
 * 审核用户资料变更
 * 
 * @param {number} id - 变更记录ID
 * @param {Object} data - 审核数据
 * @param {string} data.action - 审核操作（APPROVE-通过,REJECT-拒绝）
 * @returns {Promise} 返回审核结果
 */
export const auditProfileChange = (id, data) => {
  return request({
    url: `/admin/profile-changes/${id}/audit`,
    method: 'post',
    data
  })
}
