/**
 * HTTP请求封装工具
 * 
 * 功能说明：
 * 1. 封装uni.request方法，提供统一的请求接口
 * 2. 自动添加JWT token到请求头
 * 3. 统一处理请求成功和失败的响应
 * 4. 自动显示错误提示
 * 
 * 使用示例：
 * request('/api/posts', 'POST', { title: 'test' })
 *   .then(data => console.log(data))
 *   .catch(err => console.error(err))
 * 
 * @param {string} url - 请求的URL地址
 * @param {string} method - HTTP请求方法，默认为'GET'
 * @param {object} data - 请求参数数据，默认为空对象
 * @returns {Promise} 返回Promise对象，成功时解析响应数据，失败时拒绝错误
 */
import { API } from './api.js'
export const request = (url, method = 'GET', data = {}) => {
  return new Promise((resolve, reject) => {
    const token = uni.getStorageSync('token')
    console.log('=== 请求调试信息 ===')
    console.log('请求URL:', url)
    console.log('请求方法:', method)
    console.log('请求数据:', data)
    console.log('Token:', token ? `${token.substring(0, 20)}...` : '无')
    console.log('Authorization Header:', token ? `Bearer ${token.substring(0, 20)}...` : '无')
    
    uni.request({
      url: url,
      method: method,
      data: data,
      header: {
        'Content-Type': 'application/json',
        'Authorization': token ? `Bearer ${token}` : ''
      },
      timeout: 10000,
      success: (res) => {
        console.log('=== 响应调试信息 ===')
        console.log('响应状态码:', res.statusCode)
        console.log('响应数据:', res.data)
        
        if (res.statusCode === 200) {
          resolve(res.data)
        } else {
          console.error('请求失败:', res)
          let errorMessage = '请求失败'
          if (res.data) {
            if (typeof res.data === 'string') {
              errorMessage = res.data
            } else if (res.data.message) {
              errorMessage = res.data.message
            } else if (res.data.error) {
              errorMessage = res.data.error
            }
          }
          // 不在这里显示toast，让调用者自己处理错误提示
          const error = new Error(errorMessage)
          error.response = res
          reject(error)
        }
      },
      fail: (err) => {
        console.error('=== 网络请求失败 ===')
        console.error('错误信息:', err)
        console.error('错误详情:', err.errMsg)
        // 不在这里显示toast，让调用者自己处理错误提示
        const error = new Error(err.errMsg || '网络错误')
        error.originalError = err
        reject(error)
      }
    })
  })
}

/**
 * 签到相关API方法
 */
export const checkInApi = {
  /**
   * 执行签到
   * @returns {Promise} 签到结果
   */
  doCheckIn: () => {
    return request(API.CHECK_IN.DO_CHECK_IN, 'POST')
  },
  
  /**
   * 获取签到统计信息
   * @returns {Promise} 签到统计信息
   */
  getCheckInStat: () => {
    return request(API.CHECK_IN.GET_STAT, 'GET')
  },
  
  /**
   * 获取签到历史记录
   * @returns {Promise} 签到历史记录
   */
  getCheckInHistory: () => {
    return request(API.CHECK_IN.GET_HISTORY, 'GET')
  }
}

