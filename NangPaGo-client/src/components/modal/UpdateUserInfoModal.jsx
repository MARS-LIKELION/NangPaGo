import React from 'react';
import Modal from '../common/Modal';
import { useNavigate } from 'react-router-dom';

function UpdateUserInfoModal({ isOpen, onClose }) {
  const navigate = useNavigate();

  const handleClose = () => {
    onClose();
    navigate('/my-page');
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={handleClose}
      title="회원 정보를 수정하였습니다."
      buttons={{
        primary: {
          text: '닫기',
          onClick: handleClose,
          }
        }}
      >
      </Modal>
    );
  }

export default UpdateUserInfoModal;
