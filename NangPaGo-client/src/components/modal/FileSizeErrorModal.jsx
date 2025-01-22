import React from 'react';
import Modal from '../common/Modal';

function FileSizeErrorModal({ isOpen, onClose }) {
  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title="파일 용량이 너무 큽니다!"
      description="10MB 이하의 파일만 업로드 가능합니다."
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

export default FileSizeErrorModal;
