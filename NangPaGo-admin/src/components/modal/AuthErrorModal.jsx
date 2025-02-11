import Modal from '../common/Modal';

function AuthErrorModal({ isOpen, onClose, onConfirm, errorMessage }) {
  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title="인증 오류"
      buttons={{
        primary: {
          text: '확인',
          onClick: onConfirm
        }
      }}
    >
      <p>{errorMessage || '인증 정보가 없어 로그아웃됩니다.'}</p>
    </Modal>
  );
}

export default AuthErrorModal;