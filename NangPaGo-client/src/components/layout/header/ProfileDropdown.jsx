import { useState, useRef, useEffect } from 'react';
import clsx from 'clsx';
import { HEADER_STYLES } from '../../../common/styles/Header';
import { BiChevronLeft } from "react-icons/bi";
import LogoutModal from '../../modal/LogoutModal';
import { FaRegUser, FaSignOutAlt, FaRegBell } from 'react-icons/fa';
import { getNotificationList } from '../../../api/notification';

const ProfileDropdown = ({
  isActive,
  Icon,
  unreadCount,
  onLogout,
  onLinkClick,
  onNotificationsRead,
}) => {
  const [isOpen, setIsOpen] = useState(false);
  const [notificationOpen, setNotificationOpen] = useState(false);
  const [isLogoutModalOpen, setIsLogoutModalOpen] = useState(false);
  const [notifications, setNotifications] = useState([]);
  const menuRef = useRef(null);
  const notificationCount = unreadCount;

  const fetchNotifications = async () => {
    try {
      const response = await getNotificationList();
      setNotifications(response);
    } catch (error) {
      console.error('Failed to fetch notifications:', error);
    }
  };

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
    fetchNotifications();
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
            {unreadCount > 0 && (
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
            notificationCount={notificationCount}
            onNicknameClick={handleNicknameClick}
            onMyPageClick={() => onLinkClick('/my-page')}
            onLogout={handleLogoutClick}
          />
        )}
        {notificationOpen && (
          <NotificationPanel
            onBack={handleBackClick}
            notifications={notifications}
            onNotificationsRead={onNotificationsRead}
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
    <div className={HEADER_STYLES.dropdownContent}>
      {children}
    </div>
  </div>
);

const DropdownMenu = ({ notificationCount, onNicknameClick, onMyPageClick, onLogout }) => (
  <DropdownContainer>
    <div className={HEADER_STYLES.dropdownContent}>
      <div className="group">
        <div className={HEADER_STYLES.arrowContainer}></div>
        <button 
          onClick={onNicknameClick} 
          className={clsx(HEADER_STYLES.dropdownItem, "font-medium")}
        >
          <FaRegBell />
          알림
          {notificationCount > 0 && (
            <span className="inline-flex items-center justify-center bg-red-500 text-white text-xs w-5 h-5 rounded-full">
              {notificationCount}
            </span>
          )}
        </button>
      </div>
      <button onClick={onMyPageClick} className={HEADER_STYLES.dropdownItem}>
        <FaRegUser />
        마이페이지
      </button>
      <div className="border-t border-gray-200">
        <button onClick={onLogout} className={clsx(HEADER_STYLES.dropdownItem, "text-red-400")}>
          <FaSignOutAlt />
          로그아웃
        </button>
      </div>
    </div>
  </DropdownContainer>
);

const NotificationPanel = ({ onBack, notifications, onNotificationsRead }) => {
  useEffect(() => {
    const markAsRead = async () => {
      onNotificationsRead();  
    };

    if (notifications.length > 0) {
      markAsRead();
    }
  }, [notifications, onNotificationsRead]);

  return (
    <DropdownContainer width="w-64">
      <div className="px-4 py-2">
        <button onClick={onBack} className={HEADER_STYLES.back}>
          <BiChevronLeft size={25} className="mr-auto" />
        </button>
        {notifications.length > 0 ? (
          <ul className="max-h-60 overflow-y-auto overflow-x-hidden -mr-2 pr-2">
            {notifications.map((notification, index) => (
              <li key={index} className={clsx(HEADER_STYLES.notificationMessage, "border-b border-secondary last:border-b-0")}>
                <p className="text-sm">
                  {notification.postType === 'COMMUNITY' ? '커뮤니티' : '레시피'} 게시물에 새로운 알림이 있습니다.
                </p>
                <span className="text-xs text-gray-500">
                  {new Date(notification.timestamp).toLocaleString()}
                </span>
              </li>
            ))}
          </ul>
        ) : (
          <p className={HEADER_STYLES.notificationStatus}>새로운 알림이 없습니다.</p>
        )}
      </div>
    </DropdownContainer>
  );
};

export default ProfileDropdown;
