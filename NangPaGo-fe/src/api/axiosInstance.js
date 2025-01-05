import axios from 'axios';

const axiosInstance = axios.create({
  baseURL: import.meta.env.VITE_HOST,
  withCredentials: true,
});

// 요청 인터셉터
axiosInstance.interceptors.request.use(
  (config) => {
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
axiosInstance.interceptors.response.use((response) => {
  if (response.data?.message === '인증되지 않은 상태') {
    return axiosInstance
      .post('/api/token/reissue')
      .then(() => {
        return axiosInstance(response.config);
      })
      .catch((error) => {
        console.error('토큰 갱신 실패:', error);
        return Promise.reject(error);
      });
  }
  return response; // 정상 응답 반환
});

export default axiosInstance;
