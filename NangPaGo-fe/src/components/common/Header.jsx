// src/components/common/Header.jsx
import { Link } from 'react-router-dom';

function Header({ isLoggedIn, user, handleLogout }) {
  return (
    <header className="sticky top-0 z-10 bg-white px-4 py-4 shadow-md">
      <div className="flex justify-between items-center">
        <h1 className="text-3xl font-bold">냉파고</h1>
        {isLoggedIn ? (
          <div className="flex items-center space-x-4">
            <span>{user?.email}</span>
            <button
              onClick={handleLogout}
              className="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded-lg"
            >
              ㅎㅇㅎㅇ 로그아웃
            </button>
          </div>
        ) : (
          <Link
            to="/login"
            className="bg-[var(--primary-color)] hover:bg-[color-mix(in_srgb,var(--primary-color),#000_10%)] text-white px-4 py-2 rounded-lg"
          >
            로그인
          </Link>
        )}
      </div>
    </header>
  );
}

export default Header;
