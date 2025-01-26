import React from 'react';
import Modal from '../common/Modal';

function LoginModal({ isOpen, onClose, description }) {
  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title="로그인 하시겠습니까?"
      description={description}
      buttons={{
        primary: {
          text: '로그인',
          onClick: () => {
            onClose();
            window.location.href = '/login'
          }
        },
        secondary: {
          text: '취소',
          onClick: onClose,
        },
      }}
    >
    </Modal>
  );
}

export default LoginModal;
