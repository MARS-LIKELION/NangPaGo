import { Link, useLocation } from 'react-router-dom';
import { useSelector, useDispatch } from 'react-redux';
import { logout } from '../../slices/loginSlice';
import axiosInstance from '../../api/axiosInstance';
import { FaRegUser } from 'react-icons/fa';
import { CgSmartHomeRefrigerator } from 'react-icons/cg';
import { BsFilePost } from 'react-icons/bs';
import { useState, useRef, useEffect } from 'react';

function Header() {
  const loginState = useSelector((state) => state.loginSlice);
  const dispatch = useDispatch();
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const dropdownRef = useRef(null);
  const location = useLocation();

  const handleLogout = async () => {
    try {
      await axiosInstance.post('/api/logout');
      window.location.href = '/';
      dispatch(logout());
    } catch (error) {
      console.error('로그아웃 실패:', error.response?.data || error.message);
    }
  };

  const toggleDropdown = () => {
    setDropdownOpen(!dropdownOpen);
  };

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setDropdownOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  if (!loginState.isInitialized) {
    return null;
  }

  return (
    <header className="sticky top-0 z-10 bg-white px-4 py-2 shadow-md mx-auto w-[375px] mb-4">
      <div className="flex justify-between items-center">
        <Link to="/" className="block">
          <img src="/public/logo.png" alt="냉파고" className="h-12 w-auto" />
        </Link>

        {loginState.isLoggedIn ? (
          <div className="flex items-center space-x-7 m-1">
              <Link
                to="/community"
                className={`flex flex-col items-center text-[10px] mt-1 font-medium ${
                  location.pathname.startsWith('/community')
                    ? 'text-[var(--primary-color)]'
                    : 'text-[var(--secondary-color)]'
                }`}
              >
                <BsFilePost size={28} />
                <span
                  className={`text-[10px] mt-1 font-medium ${
                    location.pathname.startsWith('/community')
                      ? 'text-[var(--primary-color)]'
                      : 'text-[var(--secondary-color)]'
                  }`}
                >
                커뮤니티
              </span>
              </Link>

              <Link
                to="/refrigerator"
                className={`flex flex-col items-center text-[10px] mt-1 font-medium ${
                  location.pathname.startsWith('/refrigerator')
                    ? 'text-[var(--primary-color)]'
                    : 'text-[var(--secondary-color)]'
                  }`}
                >
                  <CgSmartHomeRefrigerator size={28} />
                  <span
                    className={`text-[10px] mt-1 font-medium ${
                      location.pathname.startsWith('/refrigerator')
                        ? 'text-[var(--primary-color)]'
                        : 'text-[var(--secondary-color)]'
                    }`}
                  >
                  내 냉장고
                </span>
                </Link>

            <div ref={dropdownRef}>
              <button
                onClick={toggleDropdown}
                className={`flex flex-col items-center text-[10px] mt-1 font-medium ${
                  location.pathname.startsWith('/my-page')
                    ? 'text-[var(--primary-color)]'
                    : 'text-[var(--secondary-color)]'
                }`}
              >
                <FaRegUser size={28} />
                <span
                  className={`text-[10px] mt-1 font-medium ${
                    location.pathname.startsWith('/my-page')
                      ? 'text-[var(--primary-color)]'
                      : 'text-[var(--secondary-color)]'
                  }`}
                >
                내 프로필
                </span>
              </button>
              <div
                className={`dropdown-menu absolute top-[55px] right-4 mt-3 w-40 bg-white border border-[var(--secondary-color)] rounded-lg shadow-lg overflow-hidden transition-all duration-300 ease-in-out ${
                  dropdownOpen
                    ? 'opacity-100 max-h-60 visible'
                    : 'opacity-0 max-h-0 invisible'
                }`}
              >
                <div className="absolute top-[-10px] right-[10px] w-0 h-0 border-l-[10px] border-l-transparent border-r-[10px] border-r-transparent border-b-[10px] border-b-[var(--secondary-color)]"></div>
                <div className="px-4 py-2 text-gray-700 text-[13px]">
                  {loginState.nickname}
                </div>
                <Link
                  to="/my-page"
                  className="block px-4 py-2 text-gray-700 hover:bg-gray-100 text-[13px]"
                >
                  마이페이지
                </Link>
                <button
                  onClick={handleLogout}
                  className="w-full text-left px-4 py-2 text-gray-700 hover:bg-gray-100 text-[13px]"
                >
                  로그아웃
                </button>
              </div>
            </div>
          </div>
        ) : (
          <Link
            to="/login"
            className="bg-[var(--primary-color)] hover:bg-[color-mix(in_srgb,var(--primary-color),#000_10%)] text-white px-4 py-2 rounded-lg text-sm"
          >
            로그인
          </Link>
        )}
      </div>
    </header>
  );
}

export default Header;
