import { Link, useLocation } from 'react-router-dom';
import { useSelector, useDispatch } from 'react-redux';
import { logout } from '../../../slices/loginSlice.js';
import axiosInstance from '../../../api/axiosInstance.js';
import { CgSmartHomeRefrigerator } from 'react-icons/cg';
import { BsFilePost } from 'react-icons/bs';
import { useState, useRef, useEffect } from 'react';
import ProfileDropdown from './ProfileDropdown.jsx';
import NavItem from './NavItem.jsx';

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
    setDropdownOpen((prev) => !prev);
  };

  const isActive = (path) => location.pathname.startsWith(path);

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
    <header className="sticky top-0 z-10 bg-white shadow-md w-full px-1 py-2 mb-4">
      <div className="flex flex-row items-center justify-between">
        <div className="flex items-center justify-center w-17 h-17">
          <Link to="/">
            <img src="/public/logo.png" alt="냉파고" className="w-auto h-12" />
          </Link>
        </div>

        {loginState.isLoggedIn ? (
          <div className="grid grid-cols-3 gap-5 items-center">
            <NavItem
              to="/community"
              isActive={isActive('/community')}
              label="커뮤니티"
              Icon={BsFilePost}
            />
            <NavItem
              to="/refrigerator"
              isActive={isActive('/refrigerator')}
              label="냉장고"
              Icon={CgSmartHomeRefrigerator}
            />
            <ProfileDropdown
              dropdownOpen={dropdownOpen}
              toggleDropdown={toggleDropdown}
              dropdownRef={dropdownRef}
              handleLogout={handleLogout}
              isActive={isActive('/my-page')}
              nickname={loginState.nickname}
            />
          </div>
        ) : (
          <Link
            to="/login"
            className="bg-primary text-white px-4 py-2 mr-3 rounded-md text-sm shadow-md"
          >
            로그인
          </Link>
        )}
      </div>
    </header>
  );
}

export default Header;
