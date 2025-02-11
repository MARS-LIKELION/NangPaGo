import React from 'react';
import AuthErrorModal from '../modal/AuthErrorModal';
import { useNavigate, useLocation } from 'react-router-dom';

function AuthErrorPage() {
  const navigate = useNavigate();
  const location = useLocation();
  
  // URL의 search 파라미터에서 에러 메시지 추출
  const searchParams = new URLSearchParams(location.search);
  const errorData = searchParams.get('error');
  let errorMessage = '인증 정보가 없어 로그아웃됩니다.';
  
  try {
    if (errorData) {
      const parsedError = JSON.parse(decodeURIComponent(errorData));
      errorMessage = parsedError.message || errorMessage;
    }
  } catch (e) {
    console.error('Error parsing error message:', e);
  }

  const handleConfirm = () => {
    navigate('/login');
  };

  return (
    <div
      className="fixed inset-0 flex items-center justify-center"
      style={{ backgroundColor: 'rgb(84, 69, 69, 0.5)' }}
    >
      <AuthErrorModal
        isOpen={true}
        onClose={handleConfirm}
        onConfirm={handleConfirm}
        errorMessage={errorMessage}
      />
    </div>
  );
}

export default AuthErrorPage;
