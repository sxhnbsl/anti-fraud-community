/**
 * API接口配置文件
 * 
 * 功能说明：
 * 1. 集中管理所有后端API接口地址
 * 2. 提供统一的BASE_URL配置
 * 3. 按功能模块分类组织接口
 * 4. 支持动态参数的接口路径
 * 
 * 使用示例：
 * import { API } from '@/utils/api.js'
 * const url = API.POST.LIST
 * const detailUrl = API.POST.DETAIL(123)
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */

const BASE_URL = 'http://localhost:8080/api'
const STATIC_BASE_URL = 'http://localhost:8080'

export const API = {
  /**
   * 用户认证相关接口
   */
  AUTH: {
    REGISTER: `${BASE_URL}/auth/register`,
    LOGIN: `${BASE_URL}/auth/login`
  },
  
  /**
   * 诈骗案例相关接口
   */
  CASE: {
    LIST: `${BASE_URL}/cases`,
    DETAIL: (id) => `${BASE_URL}/cases/${id}`,
    LIKE: (id) => `${BASE_URL}/cases/${id}/like`,
    DELETE: (id) => `${BASE_URL}/cases/${id}`,
    CATEGORY: (category) => `${BASE_URL}/cases/category/${category}`,
    SEARCH: `${BASE_URL}/cases/search`
  },
  
  /**
   * 社区帖子相关接口
   */
  POST: {
    LIST: `${BASE_URL}/posts`,
    DETAIL: (id) => `${BASE_URL}/posts/${id}`,
    LIKE: (id) => `${BASE_URL}/posts/${id}/like`,
    DELETE: (id) => `${BASE_URL}/posts/${id}`,
    AUTHOR: (authorId) => `${BASE_URL}/posts/author/${authorId}`,
    MY: `${BASE_URL}/posts/my`,
    CREATE: `${BASE_URL}/posts`,
    UPDATE: (id) => `${BASE_URL}/posts/${id}`,
    CHECK_CONTENT: `${BASE_URL}/posts/check-content`
  },
  
  /**
   * 评论相关接口
   */
  COMMENT: {
    LIST: (postId) => `${BASE_URL}/comments/post/${postId}`,
    CREATE: `${BASE_URL}/comments`,
    LIKE: (id) => `${BASE_URL}/comments/${id}/like`,
    DELETE: (id) => `${BASE_URL}/comments/${id}`,
    CHECK_CONTENT: `${BASE_URL}/comments/check-content`
  },
  
  /**
   * 防诈骗知识相关接口
   */
  KNOWLEDGE: {
    LIST: `${BASE_URL}/knowledge`,
    DETAIL: (id) => `${BASE_URL}/knowledge/${id}`,
    LIKE: (id) => `${BASE_URL}/knowledge/${id}/like`,
    DELETE: (id) => `${BASE_URL}/knowledge/${id}`,
    CATEGORY: (category) => `${BASE_URL}/knowledge/category/${category}`,
    TAG: (tag) => `${BASE_URL}/knowledge/tag/${tag}`,
    CREATE: `${BASE_URL}/knowledge`,
    SEARCH: `${BASE_URL}/knowledge/search`
  },
  
  /**
   * 举报相关接口
   */
  REPORT: {
    CREATE: `${BASE_URL}/reports`,
    LIST: `${BASE_URL}/reports`,
    DETAIL: (id) => `${BASE_URL}/reports/${id}`,
    STATUS: (status) => `${BASE_URL}/reports/status/${status}`,
    UPDATE_STATUS: (id) => `${BASE_URL}/reports/${id}/status`,
    DELETE: (id) => `${BASE_URL}/reports/${id}`,
    REPORTER: (reporterId) => `${BASE_URL}/reports/reporter/${reporterId}`
  },
  
  /**
   * 用户信息相关接口
   */
  USER: {
    INFO: `${BASE_URL}/users/info`,
    UPDATE: `${BASE_URL}/users/update`,
    CHANGE_PASSWORD: `${BASE_URL}/users/change-password`,
    PROFILE_CHANGE_STATUS: `${BASE_URL}/users/profile-change-status`
  },
  
  /**
   * 文件上传相关接口
   */
  UPLOAD: {
    AVATAR: `${BASE_URL}/upload`
  },
  
  /**
   * 收藏相关接口
   */
  COLLECTION: {
    LIST: `${BASE_URL}/collections`,
    TOGGLE: `${BASE_URL}/collections/toggle`,
    CHECK: (id, type) => `${BASE_URL}/collections/check/${type}/${id}`,
    DELETE: (id) => `${BASE_URL}/collections/${id}`
  },
  
  /**
   * 防诈骗测试相关接口
   */
  TEST: {
    QUESTIONS: `${BASE_URL}/test/questions`,
    SUBMIT: `${BASE_URL}/test/submit`,
    RECORDS: `${BASE_URL}/test/records`,
    RANKING: `${BASE_URL}/test/ranking`,
    DETAIL: (id) => `${BASE_URL}/test/detail/${id}`
  },

  /**
   * 轮播图相关接口
   */
  BANNER: {
    ACTIVE: `${BASE_URL}/banners/active`,
    LIST: `${BASE_URL}/banners`,
    DETAIL: (id) => `${BASE_URL}/banners/${id}`,
    CREATE: `${BASE_URL}/banners`,
    UPDATE: (id) => `${BASE_URL}/banners/${id}`,
    UPDATE_STATUS: (id) => `${BASE_URL}/banners/${id}/status`,
    DELETE: (id) => `${BASE_URL}/banners/${id}`
  },

  /**
   * 浏览记录相关接口
   */
  LEARNING_RECORD: {
    CREATE: `${BASE_URL}/learning-records`,
    USER: (userId) => `${BASE_URL}/learning-records/user/${userId}`,
    USER_PAGE: (userId) => `${BASE_URL}/learning-records/user/${userId}/page`,
    USER_TYPE: (userId, contentType) => `${BASE_URL}/learning-records/user/${userId}/type/${contentType}`,
    USER_RECENT: (userId) => `${BASE_URL}/learning-records/user/${userId}/recent`,
    USER_COUNT: (userId) => `${BASE_URL}/learning-records/user/${userId}/count`,
    USER_CONTENT_COUNT: (userId) => `${BASE_URL}/learning-records/user/${userId}/count/content`,
    DELETE: (id) => `${BASE_URL}/learning-records/${id}`,
    DELETE_USER: (userId) => `${BASE_URL}/learning-records/user/${userId}`
  },

  /**
   * AI防诈骗问答相关接口
   */
  AI: {
    SESSION: `${BASE_URL}/ai/session`,
    CHAT: `${BASE_URL}/ai/chat`,
    HISTORY: `${BASE_URL}/ai/history`
  },
  
  /**
   * 签到相关接口
   */
  CHECK_IN: {
    DO_CHECK_IN: `${BASE_URL}/check-in/do-check-in`,
    GET_STAT: `${BASE_URL}/check-in/get-stat`,
    GET_HISTORY: `${BASE_URL}/check-in/get-history`
  }
}

/**
 * 获取完整的静态资源URL
 * 
 * @param {string} url - 相对路径URL
 * @returns {string} 完整的静态资源URL
 */
export const getStaticUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url
  }
  if (url.startsWith('/')) {
    return STATIC_BASE_URL + url
  }
  return STATIC_BASE_URL + '/' + url
}
