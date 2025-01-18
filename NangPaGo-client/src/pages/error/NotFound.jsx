import React from 'react';
import { Link } from 'react-router-dom';

function UnauthenticatedAccess() {
  return (
    <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50">
      <div className="bg-white p-8 rounded-lg relative flex flex-col items-center max-w-[300px] w-[calc(100%-32px)]">
        <p className="text-center text-base font-semibold text-gray-600 mb-2">
          404 - 페이지를 찾을 수 없습니다
        </p>
        <p className="text-center text-sm text-gray-500">
          요청하신 페이지가 존재하지 않거나,<br />주소를 잘못 입력하셨습니다.
        </p>
        <Link
          to="/"
          className="mt-4 bg-primary text-white px-5 py-3 rounded-lg"
        >
          홈으로 이동
        </Link>
      </div>
    </div>
  );
}

export default UnauthenticatedAccess;
