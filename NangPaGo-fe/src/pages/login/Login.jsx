import React, { useState } from 'react';
import SocialLoginButton from '../../components/login/SocialLoginButton.jsx';
import { SOCIAL_BUTTON_STYLES } from '../../components/util/auth.js';
import LoginModal from "../../common/modal/LoginModal.jsx";

const API_HOST = import.meta.env.VITE_HOST;

function Login() {
  const [isModalOpen, setModalOpen] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');

  // API_HOST 확인 로그 추가
  console.log("DEBUG: API_HOST value is:", API_HOST);

  const handleLoginClick = async (provider) => {
    console.log("DEBUG: Selected provider is:", provider); // 선택한 provider 로그

    const redirectUrl = `${API_HOST}/api/oauth2/authorization/${provider}`;
    console.log("DEBUG: Redirect URL is:", redirectUrl); // 생성된 Redirect URL 로그

    try {
      window.location.href = redirectUrl; // 리다이렉트 시도
      console.log("DEBUG: Redirect initiated to:", redirectUrl); // 리다이렉트 시도 로그
    } catch (error) {
      console.error("DEBUG: Error occurred during login request:", error); // 에러 로그

      // 서버로부터 401 또는 302 응답일 경우
      if (error.response?.status === 401 || error.response?.status === 302) {
        console.error('DEBUG: Login failed, redirecting to /login. Error details:', error.response);
        window.location.href = '/login'; // 로그인 페이지로 리다이렉트
      } else {
        console.error('DEBUG: An unexpected error occurred:', error);
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
