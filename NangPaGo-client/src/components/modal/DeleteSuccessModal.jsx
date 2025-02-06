import React from 'react';
import Modal from '../common/Modal';

function DeleteSuccessModal({ isOpen, onClose, message }) {
  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title="삭제 완료"
      buttons={{
        primary: {
          text: '확인',
          onClick: onClose,
        },
      }}
    >
      <p>{message}</p>
    </Modal>
  );
}

export default DeleteSuccessModal;
