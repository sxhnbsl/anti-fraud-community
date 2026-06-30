/**
 * 静态资源URL处理工具
 * 
 * 功能：
 * 1. 处理静态资源的URL，确保正确访问
 * 2. 支持相对路径和绝对路径
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */

/**
 * 获取静态资源的完整URL
 * 
 * @param {string} url - 资源URL
 * @returns {string} 完整的资源URL
 */
export function getStaticUrl(url) {
  if (!url) {
    return ''
  }
  
  // 如果是绝对路径，直接返回
  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url
  }
  
  // 如果是相对路径，添加基础URL
  // 这里可以根据实际部署情况修改
  const baseUrl = 'http://localhost:8080'
  return url.startsWith('/') ? `${baseUrl}${url}` : `${baseUrl}/${url}`
}
