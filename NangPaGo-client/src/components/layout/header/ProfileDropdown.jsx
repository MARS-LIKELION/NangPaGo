import { useState } from 'react';
import clsx from 'clsx';
import { HEADER_STYLES } from '../../../common/styles/Header';
import { BiChevronLeft } from "react-icons/bi";
import NavItem from './NavItem.jsx';

function ProfileDropdown({
  dropdownOpen,
  toggleDropdown,
  dropdownRef,
  handleLinkClick,
  handleLogout,
  isActive,
  icon,
  nickname,
  profileBadgeCount = 0,
}) {
  const [notificationOpen, setNotificationOpen] = useState(false);

  const handleNicknameClick = () => {
    setNotificationOpen(true);
    toggleDropdown(false);
  };

  const handleBackClick = () => {
    setNotificationOpen(false);
    toggleDropdown(true);
  };

  return (
    <div ref={dropdownRef} className="relative">
      <NavItem
        to="#"
        isActive={isActive}
        label="프로필"
        Icon={icon}
        onClick={() => toggleDropdown(!dropdownOpen)}
        badgeCount={profileBadgeCount}
        additionalProps={{
          'aria-haspopup': true,
          'aria-expanded': dropdownOpen,
        }}
      />

      {/* 일반 드롭다운 */}
      {dropdownOpen && !notificationOpen && (
        <>
          <div className="absolute -bottom-1 right-6 z-10">
            <div className={clsx(HEADER_STYLES.arrowBase, HEADER_STYLES.arrowOuter)}></div>
            <div className={clsx(HEADER_STYLES.arrowBase, HEADER_STYLES.arrowInner)}></div>
          </div>

          <div className={clsx(HEADER_STYLES.dropdownContainer, HEADER_STYLES.dropdownVisible)}>
            <div className="px-4 py-2 text-text-900 flex items-center justify-between cursor-pointer" onClick={handleNicknameClick}>
              <span>{nickname}</span>
              {profileBadgeCount > 0 && (
                <span className="ml-2 inline-flex items-center justify-center w-4 h-4 bg-red-500 text-[0.6rem] text-white font-bold rounded-full">
                  {profileBadgeCount}
                </span>
              )}
            </div>
            <div className="max-h-30 overflow-hidden">
              <button
                to="/my-page"
                onClick={() => handleLinkClick('/my-page')}
                className={HEADER_STYLES.dropdownItem}
              >
                마이페이지
              </button>
              <button
                onClick={handleLogout}
                className={HEADER_STYLES.dropdownItem}
              >
                로그아웃
              </button>
            </div>
          </div>
        </>
      )}

      {/* 알림 드롭다운 */}
      {notificationOpen && (
        <div
          className="absolute top-full right-3 mt-1 p-4 w-64 bg-white shadow-lg rounded-lg border border-secondary"
        >
          <button onClick={handleBackClick} className="mb-1 bg-white text-secondary">
            <BiChevronLeft size={25} />
          </button>
          <p>새로운 알림이 있습니다!</p>
          {/* 추가 알림 내용 */}
        </div>
      )}
    </div>
  );
}

export default ProfileDropdown;
