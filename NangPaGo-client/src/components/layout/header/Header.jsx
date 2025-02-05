import { useNavigate, useLocation } from 'react-router-dom';
import { useSelector, useDispatch } from 'react-redux';
import { logout } from '../../../slices/loginSlice.js';
import axiosInstance from '../../../api/axiosInstance.js';
import { CgSmartHomeRefrigerator } from 'react-icons/cg';
import { CgList } from 'react-icons/cg';
import { CgProfile } from 'react-icons/cg';
import { CgLogIn } from 'react-icons/cg';
import { useState, useRef, useEffect, useCallback } from 'react';
import ProfileDropdown from './ProfileDropdown.jsx';
import NavItem from './NavItem.jsx';

function Header({ isBlocked = false }) {
  const loginState = useSelector((state) => state.loginSlice);
  const dispatch = useDispatch();
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const dropdownRef = useRef(null);
  const location = useLocation();
  const navigate = useNavigate();
  const [isReconnecting, setIsReconnecting] = useState(false);
  const [notifications, setNotifications] = useState([]);

  const handleSseError = useCallback(() => {
    if (!isReconnecting) {
      setIsReconnecting(true);
      setTimeout(() => {
        setIsReconnecting(false);
      }, 3000);
    }
  }, [isReconnecting]);

  // SSE 구독 설정
  useEffect(() => {
    let eventSource = null;

    if (loginState.isLoggedIn) {
      const baseUrl = import.meta.env.VITE_HOST || '';
      eventSource = new EventSource(
        `${baseUrl}/api/user/notification/subscribe`,
        { withCredentials: true }
      );

      const eventName = 'USER_NOTIFICATION_EVENT';

      eventSource.addEventListener(eventName, (event) => {
        const eventData = JSON.parse(event.data);
        setNotifications((prev) => [...prev, eventData]);
      });

      eventSource.onerror = () => {
        eventSource.close();
        handleSseError();
      };
    }

    return () => {
      if (eventSource) {
        eventSource.close();
      }
    };
  }, [loginState.isLoggedIn, handleSseError]);

  const handleUnsavedChanges = (isBlocked) => {
    if (isBlocked) {
      const confirmed = window.confirm(
        '작성 중인 내용을 저장하지 않고 이동하시겠습니까?',
      );
      return confirmed;
    }
    return true;
  };

  const handleLogout = async () => {
    if (!handleUnsavedChanges(isBlocked)) {
      return;
    }

    try {
      await axiosInstance.post('/api/logout');
      dispatch(logout());
      setTimeout(() => {
        navigate('/');
      }, 0);
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

  const handleLinkClick = (path) => {
    if (!handleUnsavedChanges(isBlocked)) {
      return;
    }
    navigate(path);
  };

  if (!loginState.isInitialized) {
    return null;
  }

  return (
    <header className="sticky top-0 z-50 bg-white shadow-md w-full px-1 py-2 mb-4">
      <div className="flex flex-row items-center justify-between px-4">
        <div className="flex items-center justify-center w-17 h-17">
          <img
            src="/logo.png"
            alt="냉파고"
            className="h-12 w-auto cursor-pointer"
            onClick={() => handleLinkClick('/')}
          />
        </div>
        <div className="flex items-center justify-center space-x-4">
          {loginState.isLoggedIn && (
            <NavItem
              to="/refrigerator"
              isActive={isActive('/refrigerator')}
              label="냉장고"
              Icon={CgSmartHomeRefrigerator}
              onClick={() => handleLinkClick('/refrigerator')}
            />
          )}
          <NavItem
            to="/community"
            isActive={isActive('/community')}
            label="커뮤니티"
            Icon={CgList}
            onClick={() => handleLinkClick('/community')}
          />
          {loginState.isLoggedIn ? (
            <ProfileDropdown
              dropdownRef={dropdownRef}
              dropdownOpen={dropdownOpen}
              toggleDropdown={toggleDropdown}
              profileBadgeCount={notifications.length}
              handleLogout={handleLogout}
              handleLinkClick={handleLinkClick}
              isActive={isActive('/my-page')}
              icon={CgProfile}
              nickname={loginState.nickname}
              notifications={notifications}
            />
          ) : (
            <NavItem
              to="/login"
              isActive={isActive('/login')}
              label="로그인"
              Icon={CgLogIn}
              onClick={() => handleLinkClick('/login')}
            />
          )}
        </div>
      </div>
    </header>
  );
}

export default Header;
