import SocialLoginButton from '../../components/login/SocialLoginButton.jsx';
import Modal from '../../common/Modal.jsx';
import { useState } from 'react';

function LoginPage() {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const HOST = import.meta.env.VITE_HOST;

  const handleLoginClick = (provider) => {
    window.location.href = `http://${HOST}/oauth2/authorization/${provider}`;
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

      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} />
    </div>
  );
}

export default LoginPage;
