import React from 'react';
import Modal from '../common/Modal';

function DeleteModal({ isOpen, onClose, onDelete }) {
  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title="정말로 삭제하시겠습니까?"
      buttons={{
        primary: {
          text: '삭제',
          onClick: onDelete
        },
        secondary: {
          text: '취소',
          onClick: onClose
        }
      }}
    >
    </Modal>
  );
}

export default DeleteModal;
