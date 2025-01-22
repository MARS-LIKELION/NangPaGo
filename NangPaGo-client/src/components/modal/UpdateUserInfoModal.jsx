import React from 'react';
import Modal from '../common/Modal';

function UpdateUserInfoModal({ isOpen, onClose }) {
  return (
    <Modal
    isOpen={isOpen}
    onClose={onClose}
    title="회원 정보를 수정하였습니다."
    buttons={{
      primary: {
        text: '닫기',
        onClick: onClose
        }
      }}
    >
    </Modal>
  );
}

export default UpdateUserInfoModal;
