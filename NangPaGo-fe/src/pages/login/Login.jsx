import React, { useState } from 'react';
import SocialLoginButton from '../../components/login/SocialLoginButton.jsx';
import { SOCIAL_BUTTON_STYLES } from '../../components/util/auth.js';
import LoginModal from "../../common/modal/LoginModal.jsx";

const API_HOST = import.meta.env.VITE_HOST;

function Login() {
  const [isModalOpen, setModalOpen] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');

  const handleLoginClick = async (provider) => {
    try {
      // OAuth2 로그인 요청
      window.location.href = `${API_HOST}/api/oauth2/authorization/${provider}`;
    } catch (error) {
      // 서버에서 401 응답을 받은 경우
      if (error.response?.status === 401) {
        const errorMessage = error.response?.data?.error || '로그인 실패: 권한이 없습니다.';
        console.error('로그인 실패:', errorMessage);
  
        // 로그인 페이지로 리다이렉트
        window.location.href = '/login';
      } else {
        console.error('로그인 요청 중 문제가 발생했습니다:', error);
        alert('로그인 요청 중 문제가 발생했습니다.');
      }
    }
  };
    
  return (
    <div className="bg-white shadow-md mx-auto w-[375px] min-h-screen flex flex-col items-center justify-center">
      <img src="/logo.png" alt="Logo" className="w-32 h-auto mb-6" />
      <div className="flex flex-col items-center space-y-4 w-full max-w-xs px-4">
        {Object.keys(SOCIAL_BUTTON_STYLES).map((provider) => (
          <SocialLoginButton
            key={provider}
            provider={provider}
            onClick={() => handleLoginClick(provider)}
          />
        ))}
      </div>
      {/* 에러 발생 시 모달 */}
      <LoginModal
        isOpen={isModalOpen}
        onClose={() => setModalOpen(false)}
        message={errorMessage}
      />
    </div>
  );
}

export default Login;
