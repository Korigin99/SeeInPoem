import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL ?? '/api/v1',
  headers: { 'Content-Type': 'application/json' },
})

api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

let isRefreshing = false
let pendingRequests = []

api.interceptors.response.use(
  res => res,
  async err => {
    const original = err.config
    if (err.response?.status === 401 && !original._retry) {
      const refreshToken = localStorage.getItem('refreshToken')
      if (!refreshToken) {
        clearAuth()
        return Promise.reject(err)
      }

      if (isRefreshing) {
        return new Promise((resolve, reject) => {
          pendingRequests.push({ resolve, reject })
        }).then(token => {
          original.headers.Authorization = `Bearer ${token}`
          return api(original)
        })
      }

      original._retry = true
      isRefreshing = true

      try {
        const { data } = await axios.post('/api/v1/auth/refresh', { refreshToken })
        localStorage.setItem('token', data.token)
        localStorage.setItem('refreshToken', data.refreshToken)
        pendingRequests.forEach(p => p.resolve(data.token))
        pendingRequests = []
        original.headers.Authorization = `Bearer ${data.token}`
        return api(original)
      } catch {
        pendingRequests.forEach(p => p.reject())
        pendingRequests = []
        clearAuth()
        return Promise.reject(err)
      } finally {
        isRefreshing = false
      }
    }
    return Promise.reject(err)
  }
)

function clearAuth() {
  localStorage.removeItem('token')
  localStorage.removeItem('refreshToken')
  localStorage.removeItem('user')
  window.location.href = '/login'
}

export const authApi = {
  refresh: (refreshToken) => api.post('/auth/refresh', { refreshToken }),
  logout: () => api.post('/auth/logout'),
}

export const poemApi = {
  getPoems: (params) => api.get('/poems', { params }),
  getPoem: (id) => api.get(`/poems/${id}`),
  getMyPoems: (params) => api.get('/poems/my', { params }),
  createPoem: (data) => api.post('/poems', data),
  updatePoem: (id, data) => api.put(`/poems/${id}`, data),
  deletePoem: (id) => api.delete(`/poems/${id}`),
  toggleLike: (id) => api.post(`/poems/${id}/like`),
}

export const commentApi = {
  addComment: (poemId, data) => api.post(`/poems/${poemId}/comments`, data),
  updateComment: (poemId, commentId, data) => api.put(`/poems/${poemId}/comments/${commentId}`, data),
  deleteComment: (poemId, commentId) => api.delete(`/poems/${poemId}/comments/${commentId}`),
}

export const userApi = {
  getMyProfile: () => api.get('/users/me'),
  updateNickname: (nickname) => api.put('/users/me/nickname', { nickname }),
  deleteAccount: () => api.delete('/users/me'),
}

export default api
