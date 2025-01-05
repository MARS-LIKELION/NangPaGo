import axios from 'axios';

const axiosInstance = axios.create({
  baseURL: import.meta.env.VITE_HOST,
  withCredentials: true,
});

const hasAccessToken = () => {
  return document.cookie
    .split('; ')
    .some(row => row.startsWith('access'));
};

const hasRefreshToken = () => {
  return document.cookie
    .split('; ')
    .some(row => row.startsWith('refresh'));
};

// 요청 인터셉터
axiosInstance.interceptors.request.use(
  async (config) => {
    // access 토큰이 없지만 refresh 토큰이 있는 경우
    if (!config.url?.includes('/api/token/reissue') && !hasAccessToken() && hasRefreshToken()) {
      try {
        await axiosInstance.post('/api/token/reissue');
      } catch (error) {
        console.error('토큰 재발급 실패:', error);
      }
    }

    const token = document.cookie
      .split('; ')
      .find(row => row.startsWith('access'))
      ?.split('=')[1];
    
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 응답 인터셉터: 토큰 만료 시 쿠키를 사용하여 토큰 갱신
axiosInstance.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // 토큰 재발급 요청이 실패한 경우는 재시도하지 않음
    if (error.response?.status === 401 && !originalRequest._retry && hasRefreshToken()) {
      originalRequest._retry = true;
      
      try {
        await axiosInstance.post('/api/token/reissue');
        return axiosInstance(originalRequest);
      } catch (refreshError) {
        console.error('토큰 갱신 실패:', refreshError);
        return Promise.reject(refreshError);
      }
    }
    
    return Promise.reject(error);
  }
);

export default axiosInstance;
