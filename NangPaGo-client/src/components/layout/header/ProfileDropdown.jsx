import { useState, useRef, useEffect } from 'react';
import clsx from 'clsx';
import { HEADER_STYLES } from '../../../common/styles/Header';
import { BiChevronLeft } from "react-icons/bi";
import LogoutModal from '../../modal/LogoutModal';

const ProfileDropdown = ({
  isActive,
  Icon,
  nickname,
  notifications,
  onLogout,
  onLinkClick,
}) => {
  const [isOpen, setIsOpen] = useState(false);
  const [notificationOpen, setNotificationOpen] = useState(false);
  const [isLogoutModalOpen, setIsLogoutModalOpen] = useState(false);
  const menuRef = useRef(null);
  const notificationCount = notifications.length;

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
            HEADER_STYLES.responsiveButton,
            (isOpen || notificationOpen || isActive) ? HEADER_STYLES.activeButton : HEADER_STYLES.inactiveButton
          )}
          aria-haspopup={true}
          aria-expanded={isOpen || notificationOpen}
        >
          <span className="relative">
            <Icon
              className={clsx(
                HEADER_STYLES.iconSize,
                HEADER_STYLES.buttonIcon,
                (isOpen || notificationOpen || isActive) ? "text-primary" : "text-text-900 group-hover:text-primary"
              )}
            />
            {notificationCount > 0 && (
              <span className={HEADER_STYLES.notificationDot}></span>
            )}
          </span>
          <span className={clsx(
            HEADER_STYLES.buttonText,
            (isOpen || notificationOpen || isActive) ? "text-primary" : "text-text-900 group-hover:text-primary"
          )}>
            프로필
          </span>
        </button>
        {isOpen && !notificationOpen && (
          <DropdownMenu
            nickname={nickname}
            notificationCount={notificationCount}
            onNicknameClick={handleNicknameClick}
            onMyPageClick={() => onLinkClick('/my-page')}
            onLogout={handleLogoutClick}
          />
        )}
        {notificationOpen && (
          <NotificationPanel
            onBack={handleBackClick}
            notificationCount={notificationCount}
            notifications={notifications}
          />
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

const DropdownContainer = ({ children, width = 'w-48' }) => (
  <div className={clsx(HEADER_STYLES.dropdownContainer, HEADER_STYLES.dropdownVisible, width)}>
    <div className={HEADER_STYLES.arrowContainer}></div>
    <div className={HEADER_STYLES.dropdownContent}>
      {children}
    </div>
  </div>
);

const DropdownMenu = ({ nickname, notificationCount, onNicknameClick, onMyPageClick, onLogout }) => (
  <DropdownContainer width="w-40">
    <button onClick={onNicknameClick} className={clsx(HEADER_STYLES.dropdownItem, "flex items-center justify-between w-full")}>
      <span>{nickname}</span>
      {notificationCount > 0 && (
        <span className="ml-2 inline-flex items-center justify-center w-4 h-4 bg-red-500 text-[0.6rem] text-white font-bold rounded-full">
          {notificationCount}
        </span>
      )}
    </button>
    <div className="overflow-hidden">
      <button onClick={onMyPageClick} className={HEADER_STYLES.dropdownItem}>
        마이페이지
      </button>
      <button onClick={onLogout} className={HEADER_STYLES.dropdownItem}>
        로그아웃
      </button>
    </div>
  </DropdownContainer>
);

const NotificationPanel = ({ onBack, notificationCount, notifications }) => (
  <DropdownContainer width="w-64">
    <div className="px-4 py-2">
      <button onClick={onBack} className={HEADER_STYLES.back}>
        <BiChevronLeft size={25} className="mr-auto" />
      </button>
      {notificationCount > 0 ? (
        <ul className="max-h-60 overflow-y-auto overflow-x-hidden -mr-2 pr-2">
          {notifications.map((notification, index) => (
            <li key={index} className={clsx(HEADER_STYLES.notificationMessage, "border-b border-secondary last:border-b-0")}>
              <p className="text-sm">{notification.message}</p>
              <span className="text-xs text-gray-500">{new Date(notification.timestamp).toLocaleString()}</span>
            </li>
          ))}
        </ul>
      ) : (
        <p className={HEADER_STYLES.notificationStatus}>새로운 알림이 없습니다.</p>
      )}
    </div>
  </DropdownContainer>
);

export default ProfileDropdown;
