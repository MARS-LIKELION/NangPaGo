import axios from 'axios';

const axiosInstance = axios.create({
  baseURL: import.meta.env.VITE_HOST,
  withCredentials: true,
});

// 에러 핸들러 함수 (실제 구현은 다른 파일에서)
let errorHandler = (error) => console.error(error);

// 에러 핸들러 설정 함수
export const setErrorHandler = (handler) => {
  errorHandler = handler;
};

axiosInstance.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response && error.response.status === 401) {
      errorHandler(error);
    }
    return Promise.reject(error);
  }
);

export default axiosInstance;