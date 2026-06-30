export default {
  state: {
    isLoggedIn: false,
    token: '',
    userInfo: null
  },
  mutations: {
    SET_LOGIN(state, payload) {
      console.log('SET_LOGIN 收到的 payload:', payload)
      
      if (!payload) {
        console.error('payload 为空')
        return
      }
      
      const { token, userInfo } = payload
      console.log('解构后的 token:', token)
      console.log('解构后的 userInfo:', userInfo)
      
      state.isLoggedIn = true
      state.token = token
      state.userInfo = userInfo
      uni.setStorageSync('token', token)
      uni.setStorageSync('userInfo', userInfo)
      uni.setStorageSync('isLoggedIn', true)
      uni.setStorageSync('userId', userInfo.id)
    },
    SET_LOGOUT(state) {
      state.isLoggedIn = false
      state.token = ''
      state.userInfo = null
      uni.removeStorageSync('token')
      uni.removeStorageSync('userInfo')
      uni.removeStorageSync('isLoggedIn')
      uni.removeStorageSync('userId')
    }
  }
}
