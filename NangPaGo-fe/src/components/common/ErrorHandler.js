// src/common/errorHandler.js
import { toast } from 'react-toastify';

/**
 * 에러 메시지를 처리하고, 필요한 경우 화면 전환.
 * @param {Object} error - Axios 에러 객체 또는 일반 에러 객체
 * @param {Function} navigate - React Router의 navigate 함수
 */
export const handleError = (error, navigate) => {
  const errorMessage = error.response?.data?.message || '알 수 없는 에러가 발생했습니다.';
  const statusCode = error.response?.status;

  // 사용자에게 알림
  toast.error(errorMessage);

  // 특정 상태 코드에 따른 동작
  if (statusCode === 401 || statusCode === 403) {
    // 인증 에러 처리: 로그인 페이지로 이동
    navigate('/login');
  } else if (statusCode === 409) {
    // 중복 에러 처리
    console.warn('중복된 사용자입니다.');
  }
};
