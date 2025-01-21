import React from 'react';
import { FaTimes } from 'react-icons/fa';

function FileSizeErrorModal({ isOpen, onClose }) {
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
        <p className="text-center text-lg font-semibold text-black-600">파일 용량이 너무 큽니다!</p>
        <p className="text-center text-sm text-gray-500">10MB 이하의 파일만 업로드 가능합니다.</p>
        <button
          onClick={onClose}
          className="mt-4 bg-[var(--primary-color)] text-white px-5 py-3 rounded-lg"
        >
          닫기
        </button>
      </div>
    </div>
  );
}

export default FileSizeErrorModal;
