import request from '@/utils/request'

/**
 * 上传文件
 * @param {File} file - 要上传的文件
 * @param {string} type - 文件类型 ('image' 或 'video')
 * @returns {Promise} - 返回上传结果
 */
export const uploadFile = (file, type) => {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('type', type)
  
  return request({
    url: '/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}