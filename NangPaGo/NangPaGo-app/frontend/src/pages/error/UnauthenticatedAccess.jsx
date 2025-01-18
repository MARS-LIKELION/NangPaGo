import React from 'react';
import { Link } from 'react-router-dom';

function UnauthenticatedAccess() {
  return (
    <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50">
      <div className="bg-white p-8 rounded-lg relative flex flex-col items-center max-w-[300px] w-[calc(100%-32px)]">
        <p className="text-center text-lg font-semibold text-gray-600 mb-2">
          로그인 후 이용해주세요
        </p>
        <p className="text-center text-sm text-gray-500">
          이 페이지에 접근하려면<br />로그인이 필요합니다.<br />로그인 후 다시 시도해주세요.
        </p>
        <Link
          to="/login"
          className="mt-4 bg-primary text-white px-5 py-3 rounded-lg"
        >
          로그인 페이지로 이동
        </Link>
      </div>
    </div>
  );
}

export default UnauthenticatedAccess;
