import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 30000
})

api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

api.interceptors.response.use(
  response => response.data,
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('userId')
      localStorage.removeItem('username')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export const authAPI = {
  login: (data) => api.post('/auth/login', data),
  register: (data) => api.post('/auth/register', data)
}

export const dashboardAPI = {
  getStats: () => api.get('/dashboard/stats')
}

export const companyAPI = {
  getAll: () => api.get('/companies'),
  getById: (id) => api.get(`/companies/${id}`),
  search: (keyword) => api.get('/companies/search', { params: { keyword } }),
  create: (data) => api.post('/companies', data),
  update: (id, data) => api.put(`/companies/${id}`, data),
  delete: (id) => api.delete(`/companies/${id}`)
}

export const applicationAPI = {
  getAll: () => api.get('/applications'),
  getById: (id) => api.get(`/applications/${id}`),
  create: (data) => api.post('/applications', data),
  update: (id, data) => api.put(`/applications/${id}`, data),
  updateStatus: (id, status) => api.patch(`/applications/${id}/status`, null, { params: { status } }),
  delete: (id) => api.delete(`/applications/${id}`)
}

export const interviewRecordAPI = {
  getAll: () => api.get('/interview-records'),
  getById: (id) => api.get(`/interview-records/${id}`),
  getByApplication: (applicationId) => api.get(`/interview-records/application/${applicationId}`),
  create: (data) => api.post('/interview-records', data),
  update: (id, data) => api.put(`/interview-records/${id}`, data),
  updateResult: (id, result) => api.patch(`/interview-records/${id}/result`, null, { params: { result } }),
  delete: (id) => api.delete(`/interview-records/${id}`)
}

export const interviewScheduleAPI = {
  getAll: () => api.get('/interview-schedules'),
  getById: (id) => api.get(`/interview-schedules/${id}`),
  getByApplication: (applicationId) => api.get(`/interview-schedules/application/${applicationId}`),
  create: (data) => api.post('/interview-schedules', data),
  update: (id, data) => api.put(`/interview-schedules/${id}`, data),
  updateStatus: (id, status) => api.patch(`/interview-schedules/${id}/status`, null, { params: { status } }),
  delete: (id) => api.delete(`/interview-schedules/${id}`)
}

export const chatAPI = {
  chat: (message) => api.post('/chat/query', null, { params: { message } })
}
