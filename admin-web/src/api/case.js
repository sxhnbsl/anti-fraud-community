/**
 * 管理员端案例管理API
 * 
 * 功能说明：
 * 1. 提供案例列表查询接口
 * 2. 提供案例创建接口
 * 3. 提供案例更新接口
 * 4. 提供案例删除接口
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */

import request from '@/utils/request'

/**
 * 获取案例列表
 * 
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码，默认为1
 * @param {number} params.size - 每页数量，默认为10
 * @param {string} [params.keyword] - 搜索关键词（可选）
 * @param {string} [params.category] - 分类（可选）
 * @returns {Promise} 返回案例列表数据
 */
export const getCaseList = (params) => {
  return request({
    url: '/cases',
    method: 'get',
    params
  })
}

/**
 * 创建案例
 * 
 * @param {Object} data - 案例数据
 * @param {string} data.title - 标题
 * @param {string} data.content - 内容
 * @param {string} data.category - 分类
 * @param {string} [data.tags] - 标签（可选）
 * @param {string} [data.videoUrl] - 视频URL（可选）
 * @param {string} [data.videoCover] - 视频封面URL（可选）
 * @param {string} [data.images] - 图片URL列表（可选）
 * @returns {Promise} 返回创建的案例数据
 */
export const createCase = (data) => {
  return request({
    url: '/cases',
    method: 'post',
    data
  })
}

/**
 * 更新案例
 * 
 * @param {number} id - 案例ID
 * @param {Object} data - 案例数据
 * @param {string} [data.title] - 标题（可选）
 * @param {string} [data.content] - 内容（可选）
 * @param {string} [data.category] - 分类（可选）
 * @param {string} [data.tags] - 标签（可选）
 * @param {string} [data.videoUrl] - 视频URL（可选）
 * @param {string} [data.videoCover] - 视频封面URL（可选）
 * @param {string} [data.images] - 图片URL列表（可选）
 * @returns {Promise} 返回更新后的案例数据
 */
export const updateCase = (id, data) => {
  return request({
    url: `/cases/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除案例
 * 
 * @param {number} id - 案例ID
 * @returns {Promise} 返回删除结果
 */
export const deleteCase = (id) => {
  return request({
    url: `/cases/${id}`,
    method: 'delete'
  })
}
