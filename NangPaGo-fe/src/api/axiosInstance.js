import axios from 'axios';

const axiosInstance = axios.create({
  baseURL: import.meta.env.VITE_HOST, // 서버 URL
  withCredentials: true, // Refresh Token 및 Access Token 쿠키 전송 허용
});

// 요청 인터셉터: 쿠키에서 Access Token 읽기
axiosInstance.interceptors.request.use((config) => {
  const accessToken = getCookie('access'); // Access Token 쿠키에서 가져오기
  if (accessToken) {
    config.headers.Authorization = `Bearer ${accessToken}`; // Authorization 헤더 설정
  }
  return config;
});

// 응답 인터셉터: Access Token 재발급 처리
axiosInstance.interceptors.response.use(
  (response) => {
    // 성공 응답은 그대로 반환
    return response;
  },
  async (error) => {
    const originalRequest = error.config;

    // 401 상태 코드 처리 (Access Token 만료)
    if (
      error.response?.status === 401 && // 인증 실패
      !originalRequest._retry // 재시도 방지 플래그
    ) {
      originalRequest._retry = true; // 재시도 설정

      try {
        // Refresh Token으로 Access Token 재발급 요청
        const response = await axiosInstance.post('/auth/reissue');
        // 재발급 후 Access Token은 서버가 자동으로 쿠키에 설정
        return axiosInstance(originalRequest); // 원래 요청 재시도
      } catch (refreshError) {
        console.error('토큰 재발급 실패:', refreshError);
        // Refresh Token 만료 시 로그인 페이지로 리다이렉트
        window.location.href = '/login'; // 로그인 페이지로 이동
        return Promise.reject(refreshError);
      }
    }

    // 다른 오류 처리
    return Promise.reject(error);
  },
);

// 쿠키에서 특정 키의 값을 가져오는 유틸리티 함수
function getCookie(name) {
  const matches = document.cookie.match(new RegExp(`(?:^|; )${name}=([^;]*)`));
  return matches ? decodeURIComponent(matches[1]) : undefined;
}

export default axiosInstance;
