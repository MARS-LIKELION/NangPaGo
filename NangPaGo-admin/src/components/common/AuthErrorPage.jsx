import React from 'react';
import AuthErrorModal from '../modal/AuthErrorModal';
import { useNavigate } from 'react-router-dom';

function AuthErrorPage() {
  const navigate = useNavigate();

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
      />
    </div>
  );
}

export default AuthErrorPage;
