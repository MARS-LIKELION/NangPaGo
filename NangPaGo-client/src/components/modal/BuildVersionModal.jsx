import React from 'react';
import Modal from '../common/Modal';

function BuildVersionModal({ isOpen, onClose, version }) {
  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title="배포 버전"
      description={version.version}
      buttons={{
        primary: {
          text: '닫기',
          onClick: onClose
          },
      }}
    >
    </Modal>
  );
}

export default BuildVersionModal;
