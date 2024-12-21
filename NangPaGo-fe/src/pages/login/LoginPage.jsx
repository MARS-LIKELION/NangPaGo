import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import SocialLoginButton from '../../components/login/SocialLoginButton.jsx';
import Modal from '../../common/Modal.jsx';

function LoginPage() {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');
  const navigate = useNavigate();

  const handleLoginClick = async (provider) => {
    try {
      const response = await fetch(
        `http://${AUTH_HOST}/oauth2/authorization/${provider}`,
        {
          method: 'GET',
          headers: { 'Content-Type': 'application/json' },
        },
      );

      if (response.ok) {
        const data = await response.json();
        console.log('로그인 성공:', data);
        navigate('/');
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
