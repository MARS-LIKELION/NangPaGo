import Modal from '../common/Modal';

function LogoutModal({ isOpen, onClose, onConfirm }) {
  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title="로그아웃"
      buttons={{
        secondary: {
          text: '취소',
          onClick: onClose
        },
        primary: {
          text: '확인',
          onClick: onConfirm
        }
      }}
    >
      <p>정말 로그아웃 하시겠습니까?</p>
    </Modal>
  );
}

export default LogoutModal;
