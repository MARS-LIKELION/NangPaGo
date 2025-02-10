import React, { useEffect } from 'react';
import Modal from '../common/Modal';
import { useNavigate } from 'react-router-dom';

function DeletePostSuccessModal({ isOpen, onClose, type }) {
  const navigate = useNavigate();

  useEffect(() => {
    if (isOpen) {
      setTimeout(() => {
        onClose();
        navigate(`/${type}`);
      }, 1000);
    }
  }, [isOpen, navigate, onClose, type]);

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title={`${type === 'user-recipe' ? '유저 레시피' : '커뮤니티'} 게시물이 삭제되었습니다.`}
    />
  );
}

export default DeletePostSuccessModal;
