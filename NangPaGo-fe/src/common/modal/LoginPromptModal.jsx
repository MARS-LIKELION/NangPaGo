import React from 'react';

function LoginPromptModal({ isOpen, onConfirm, onClose }) {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-40 z-50">
      <div className="bg-white p-6 rounded-lg w-[calc(100%-32px)] max-w-[400px] mx-4">
        <p className="text-center mb-4 text-lg">
          로그인 하시겠습니까?
        </p>
        <div className="flex justify-center gap-4">
          <button
            onClick={onConfirm}
            className="bg-yello-300 text-black px-5 py-2 rounded-md"
          >
            확인
          </button>
          <button
            onClick={onClose}
            className="bg-yello-300 text-black px-5 py-2 rounded-md"
          >
            취소
          </button>
        </div>
      </div>
    </div>
  );
}

export default LoginPromptModal;
