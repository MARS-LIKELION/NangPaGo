import React from 'react';
import Modal from '../common/Modal';

function OnlyImageUploadModal({ isOpen, onClose }) {
  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title="이미지 파일만 업로드 가능합니다."
      description=""
      buttons={{
        primary: {
          text: '확인',
          onClick: onClose
        },
      }}
    >
    </Modal>
  );
}

export default OnlyImageUploadModal;
