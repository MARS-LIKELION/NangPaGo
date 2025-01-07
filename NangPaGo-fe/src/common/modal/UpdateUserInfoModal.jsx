import React from 'react';

function UpdateUserInfoModal({ isOpen }) {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50">
      <div className="bg-white p-8 rounded-lg relative flex flex-col items-center w-[calc(100% - 32px)] max-w-[400px]">
        <p className="text-center">회원 정보를 수정하였습니다.</p>
        <div className="mt-4 flex gap-4">
          <button
            onClick={() => (window.location.href = '/')}
            className="bg-[var(--primary-color)] text-white px-5 py-3 rounded-lg"
          >
            확인
          </button>
        </div>
      </div>
    </div>
  );
}

export default UpdateUserInfoModal;
