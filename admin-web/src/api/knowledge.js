/**
 * 管理员端知识库管理API
 * 
 * 功能说明：
 * 1. 提供知识库列表查询接口
 * 2. 提供知识库创建接口
 * 3. 提供知识库更新接口
 * 4. 提供知识库删除接口
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */

import request from '@/utils/request'

/**
 * 获取知识库列表
 * 
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码，默认为1
 * @param {number} params.size - 每页数量，默认为10
 * @param {string} [params.keyword] - 搜索关键词（可选）
 * @param {string} [params.category] - 分类（可选）
 * @returns {Promise} 返回知识库列表数据
 */
export const getKnowledgeList = (params) => {
  return request({
    url: '/knowledge',
    method: 'get',
    params
  })
}

/**
 * 创建知识库
 * 
 * @param {Object} data - 知识库数据
 * @param {string} data.title - 标题
 * @param {string} data.content - 内容
 * @param {string} data.category - 分类
 * @param {string} [data.tags] - 标签（可选）
 * @param {string} [data.videoUrl] - 视频URL（可选）
 * @param {string} [data.videoCover] - 视频封面URL（可选）
 * @param {string} [data.images] - 图片URL列表（可选）
 * @returns {Promise} 返回创建的知识库数据
 */
export const createKnowledge = (data) => {
  return request({
    url: '/knowledge',
    method: 'post',
    data
  })
}

/**
 * 更新知识库
 * 
 * @param {number} id - 知识库ID
 * @param {Object} data - 知识库数据
 * @param {string} [data.title] - 标题（可选）
 * @param {string} [data.content] - 内容（可选）
 * @param {string} [data.category] - 分类（可选）
 * @param {string} [data.tags] - 标签（可选）
 * @param {string} [data.videoUrl] - 视频URL（可选）
 * @param {string} [data.videoCover] - 视频封面URL（可选）
 * @param {string} [data.images] - 图片URL列表（可选）
 * @returns {Promise} 返回更新后的知识库数据
 */
export const updateKnowledge = (id, data) => {
  return request({
    url: `/knowledge/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除知识库
 * 
 * @param {number} id - 知识库ID
 * @returns {Promise} 返回删除结果
 */
export const deleteKnowledge = (id) => {
  return request({
    url: `/knowledge/${id}`,
    method: 'delete'
  })
}
