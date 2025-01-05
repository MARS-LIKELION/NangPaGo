import axios from 'axios';

const axiosInstance = axios.create({
  baseURL: import.meta.env.VITE_HOST,
  withCredentials: true,
});

let isRefreshing = false;
let refreshSubscribers = [];

const hasRefreshToken = () => {
  return document.cookie
    .split('; ')
    .some(row => row.startsWith('refresh'));
};

// 토큰 재발급이 완료된 후 기존 요청들을 재시도
const onRefreshed = () => {
  refreshSubscribers.forEach(callback => callback());
  refreshSubscribers = [];
};

// 토큰 재발급 실패 시 대기 중인 요청들을 reject
const onRefreshError = (error) => {
  refreshSubscribers.forEach(callback => callback(error));
  refreshSubscribers = [];
};

// 요청 인터셉터
axiosInstance.interceptors.request.use(
  async (config) => {
    if (!config.url?.includes('/api/token/reissue')) {
        const token = document.cookie
        .split('; ')
        .find(row => row.startsWith('access'))
        ?.split('=')[1];
      
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 응답 인터셉터
axiosInstance.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && !originalRequest._retry && hasRefreshToken()) {
      originalRequest._retry = true;

      if (!isRefreshing) {
        isRefreshing = true;

        try {
          await axiosInstance.post('/api/token/reissue');
          isRefreshing = false;
          onRefreshed();
          return axiosInstance(originalRequest);
        } catch (refreshError) {
          isRefreshing = false;
          onRefreshError(refreshError);
          console.error('토큰 갱신 실패:', refreshError);
          return Promise.reject(refreshError);
        }
      }

      // 토큰 재발급 진행 중일 때의 요청들은 대기열에 추가
      return new Promise((resolve, reject) => {
        refreshSubscribers.push((error) => {
          if (error) {
            reject(error);
          } else {
            resolve(axiosInstance(originalRequest));
          }
        });
      });
    }
    
    return Promise.reject(error);
  }
);

export default axiosInstance;