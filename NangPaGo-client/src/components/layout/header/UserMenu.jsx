import React, { useState, useRef, useEffect } from 'react';
import clsx from 'clsx';
import { HEADER_STYLES } from '../../../common/styles/Header';
import { BiChevronLeft } from "react-icons/bi";
import LogoutModal from '../../modal/LogoutModal';

const UserMenu = ({
  nickname,
  onLogout,
  onLinkClick,
  Icon,
  notifications
}) => {
  const [isOpen, setIsOpen] = useState(false);
  const [notificationOpen, setNotificationOpen] = useState(false);
  const [isLogoutModalOpen, setIsLogoutModalOpen] = useState(false);
  const menuRef = useRef(null);

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (menuRef.current && !menuRef.current.contains(event.target)) {
        setIsOpen(false);
        setNotificationOpen(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  const toggle = () => {
    setIsOpen(prev => !prev);
    setNotificationOpen(false);
  };

  const handleNicknameClick = () => {
    setNotificationOpen(true);
    setIsOpen(false);
  };

  const handleBackClick = () => {
    setNotificationOpen(false);
    setIsOpen(true);
  };

  const handleLogoutClick = () => {
    setIsLogoutModalOpen(true);
    setIsOpen(false);
  };

  const handleLogoutConfirm = () => {
    onLogout();
    setIsLogoutModalOpen(false);
  };

  return (
    <>
      <div ref={menuRef} className="relative">
        <button
          onClick={toggle}
          className={clsx(
            HEADER_STYLES.baseButton,
            isOpen ? HEADER_STYLES.activeButton : HEADER_STYLES.inactiveButton
          )}
          aria-haspopup={true}
          aria-expanded={isOpen}
        >
          <span className="inline-flex items-center justify-center">
            {Icon}
            {notifications.length > 0 && (
              <span className="absolute top-0 right-[6px] bg-red-500 w-1 h-1 rounded-full"></span>
            )}
          </span>
          <span>프로필</span>
        </button>
        {isOpen && !notificationOpen && (
          <DropdownMenu
            nickname={nickname}
            notificationCount={notifications.length}
            onNicknameClick={handleNicknameClick}
            onMyPageClick={() => onLinkClick('/my-page')}
            onLogout={handleLogoutClick}
          />
        )}
        {notificationOpen && (
          <NotificationPanel onBack={handleBackClick} notifications={notifications} />
        )}
      </div>
      <LogoutModal
        isOpen={isLogoutModalOpen}
        onClose={() => setIsLogoutModalOpen(false)}
        onConfirm={handleLogoutConfirm}
      />
    </>
  );
};

const DropdownMenu = ({ nickname, notificationCount, onNicknameClick, onMyPageClick, onLogout }) => (
  <>
    <div className="absolute -bottom-1 right-6 z-10">
      <div className={clsx(HEADER_STYLES.arrowBase, HEADER_STYLES.arrowOuter)}></div>
      <div className={clsx(HEADER_STYLES.arrowBase, HEADER_STYLES.arrowInner)}></div>
    </div>
    <div className={clsx(HEADER_STYLES.dropdownContainer, HEADER_STYLES.dropdownVisible)}>
      <div className="px-4 py-2 text-text-900 flex items-center justify-between cursor-pointer" onClick={onNicknameClick}>
        <span>{nickname}</span>
        {notificationCount > 0 && (
          <span className="ml-2 inline-flex items-center justify-center w-4 h-4 bg-red-500 text-[0.6rem] text-white font-bold rounded-full">
            {notificationCount}
          </span>
        )}
      </div>
      <div className="max-h-30 overflow-hidden">
        <button onClick={onMyPageClick} className={HEADER_STYLES.dropdownItem}>
          마이페이지
        </button>
        <button onClick={onLogout} className={HEADER_STYLES.dropdownItem}>
          로그아웃
        </button>
      </div>
    </div>
  </>
);

const NotificationPanel = ({ onBack, notifications }) => (
  <div className="absolute top-full right-3 mt-1 p-4 w-64 bg-white shadow-lg rounded-lg border border-secondary">
    <button onClick={onBack} className="mb-1 bg-white text-secondary">
      <BiChevronLeft size={25} />
    </button>
    {notifications.length > 0 ? (
      <ul className="max-h-60 overflow-y-auto">
        {notifications.map((notification, index) => (
          <li key={index} className="mb-2 pb-2 border-b border-gray-200 last:border-b-0">
            <p className="text-sm">{notification.message}</p>
            <span className="text-xs text-gray-500">{new Date(notification.timestamp).toLocaleString()}</span>
          </li>
        ))}
      </ul>
    ) : (
      <p>새로운 알림이 없습니다.</p>
    )}
  </div>
);

export default UserMenu;
