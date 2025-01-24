function DeleteAccountConfirmModal({ isOpen, onConfirm, onCancel }) {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50">
      <div className="bg-white p-8 rounded-md flex flex-col items-center max-w-[400px]">
        <p className="text-center text-lg mb-4">
          정말로 회원탈퇴 하시겠습니까?
        </p>
        <div className="flex gap-4">
          <button
            onClick={onConfirm}
            className="bg-gray-300 text-white px-4 py-2 rounded"
          >
            확인
          </button>
          <button
            onClick={onCancel}
            className="bg-yellow text-white px-4 py-2 rounded"
          >
            취소
          </button>
        </div>
      </div>
    </div>
  );
}

export default DeleteAccountConfirmModal;
