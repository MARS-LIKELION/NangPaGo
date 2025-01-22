// TODO: 수정해야함 (파일위치이동)
import React from 'react';
import { FaTimes } from 'react-icons/fa';

function FileSizeErrorModal({ isOpen, onClose }) {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50">
      <div className="bg-white p-8 rounded-md relative flex flex-col items-center w-[calc(100% - 32px)] max-w-[400px]">
        <p className="text-center text-lg font-semibold text-black-600">파일 용량이 너무 큽니다!</p>
        <p className="text-center text-sm text-gray-500">10MB 이하의 파일만 업로드 가능합니다.</p>
        <button
          onClick={onClose}
          className="mt-4 bg-primary text-white px-5 py-3"
        >
          확인
        </button>
      </div>
    </div>
  );
}

export default FileSizeErrorModal;
