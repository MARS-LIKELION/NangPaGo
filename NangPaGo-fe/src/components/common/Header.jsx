import { Link } from 'react-router-dom';
import { useSelector, useDispatch } from 'react-redux';
import { logout } from '../../slices/loginSlice';
import axiosInstance from '../../api/axiosInstance';
import { FaRegUser } from 'react-icons/fa';
import { IoReceiptOutline } from 'react-icons/io5';
import { CgSmartHomeRefrigerator } from 'react-icons/cg';
import { useState, useRef, useEffect } from 'react';

function Header() {
  const loginState = useSelector((state) => state.loginSlice);
  const dispatch = useDispatch();
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const dropdownRef = useRef(null);

  const handleLogout = async () => {
    try {
      await axiosInstance.post('/api/logout');
      dispatch(logout());
    } catch (error) {
      console.error('로그아웃 실패:', error.response?.data || error.message);
    }
  };

  const toggleDropdown = () => {
    setDropdownOpen(!dropdownOpen);
  };

  const getUsername = (email) => email.split('@')[0];

  // Close dropdown when clicking outside
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
    return null; // 초기화 중에는 렌더링하지 않음
  }

  return (
    <header className="sticky top-0 z-10 bg-white px-4 py-2 shadow-md mx-auto w-[375px] mb-4">
      <div className="flex justify-between items-center">
        <Link to="/" className="block">
          <img src="/public/logo.png" alt="냉파고" className="h-12 w-auto" />
        </Link>

        {loginState.isLoggedIn ? (
          <div className="flex items-center space-x-7">
            <Link to="/recipes" className="text-[var(--primary-color)]">
              <IoReceiptOutline size={26} />
            </Link>
            <Link to="/fridge" className="text-[var(--primary-color)]">
              <CgSmartHomeRefrigerator size={28} />
            </Link>
            <div className="relative" ref={dropdownRef}>
              <button
                onClick={toggleDropdown}
                className="text-[var(--primary-color)] text-[16px]"
              >
                <FaRegUser size={26} />
              </button>
              <div
                className={`dropdown-menu absolute top-[25px] right-0 mt-2 w-40 bg-white border border-[var(--secondary-color)] rounded-lg shadow-lg overflow-hidden transition-all duration-300 ease-in-out ${
                  dropdownOpen
                    ? 'opacity-100 max-h-60 visible'
                    : 'opacity-0 max-h-0 invisible'
                }`}
              >
                <div className="absolute top-[-10px] right-[10px] w-0 h-0 border-l-[10px] border-l-transparent border-r-[10px] border-r-transparent border-b-[10px] border-b-[var(--secondary-color)]"></div>
                <div className="px-4 py-2 text-gray-700 text-[13px]">
                  {getUsername(loginState.email)}
                </div>
                <Link
                  to="/profile"
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
