function DeleteModal({ isOpen, onClose, onDelete }) {
  if (!isOpen) {
    return null;
  }

  return (
    <div className="fixed inset-0 flex items-center justify-center min-w-80 bg-black bg-opacity-50 z-50">
      <div className="bg-white p-8 rounded-md relative flex flex-col items-center w-[calc(100% - 32px)] max-w-[400px]">
        <p className="text-center">정말로 삭제하시겠습니까?</p>
        <div className="mt-4 flex gap-4">
          <button
            onClick={onDelete}
            className="bg-primary text-white px-5 py-3 rounded-lg"
          >
            삭제
          </button>
          <button
            onClick={onClose}
            className="bg-primary text-white px-5 py-3 rounded-lg"
          >
            취소
          </button>
        </div>
      </div>
    </div>
  );
}

export default DeleteModal;
