import React from 'react';
import { FaTimes } from 'react-icons/fa';

function LoginModal({ isOpen, onConfirm, onClose }) {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50">
      <div className="bg-white p-8 rounded-lg relative flex flex-col items-center w-[calc(100% - 32px)] max-w-[400px]">
        <button
          onClick={onClose}
          className="absolute top-2 right-2 text-gray-500 hover:text-gray-700"
        >
          <FaTimes className="w-6 h-6" />
        </button>
        <p className="text-center mt-5 mb-5">로그인 하시겠습니까?</p>
        <button
            onClick={onConfirm}
            className="bg-yello-300 text-black px-5 py-2 rounded-md"
          >
            로그인
        </button>
      </div>
    </div>
  );
}

export default LoginModal;
