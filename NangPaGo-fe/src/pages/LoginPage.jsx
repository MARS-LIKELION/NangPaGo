import React, { useState } from 'react';
import Modal from '../common/Modal';
import SocialLoginButton from '../components/SocialLoginButton';
import { isValidProvider } from '../common/utils/validation';

function LoginPage() {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');

  const handleLoginClick = async (provider) => {
    if (!isValidProvider(provider)) {
      setErrorMessage(`잘못된 소셜 로그인 제공자입니다: ${provider}`);
      setIsModalOpen(true);
      return;
    }

    try {
      const response = await fetch(
        `http://localhost:8080/oauth2/authorization/${provider}`,
        {
          method: 'GET',
          credentials: 'include',
        },
      );

      if (response.ok) {
        const data = await response.json();
        console.log('로그인 성공:', data);
        window.location.href = '/home';
      } else {
        const errorData = await response.json();
        setErrorMessage(
          `로그인 실패: ${errorData.message || '알 수 없는 오류'}`,
        );
        setIsModalOpen(true);
      }
    } catch (error) {
      setErrorMessage(`서버와의 통신 중 오류 발생: ${error.message}`);
      setIsModalOpen(true);
    }
  };

  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-gray-50">
      <img src="/logo.png" alt="회사 로고" className="w-32 h-auto mb-6" />

      <div className="flex flex-col items-center space-y-4 w-full max-w-xs px-3">
        <SocialLoginButton
          provider="google"
          onClick={() => handleLoginClick('google')}
        />
        <SocialLoginButton
          provider="naver"
          onClick={() => handleLoginClick('naver')}
        />
        <SocialLoginButton
          provider="kakao"
          onClick={() => handleLoginClick('kakao')}
        />
      </div>

      <Modal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        message={errorMessage}
      />
    </div>
  );
}

export default LoginPage;
