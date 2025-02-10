import Modal from '../common/Modal';

function AuthErrorModal({ isOpen, onClose, onConfirm }) {
  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title="세션이 만료되었습니다."
      buttons={{
        primary: {
          text: '확인',
          onClick: onConfirm
        }
      }}
    >
      <p>세션이 만료되어 로그아웃됩니다.</p>
    </Modal>
  );
}

export default AuthErrorModal;