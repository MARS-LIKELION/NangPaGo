import clsx from 'clsx';
import { HEADER_STYLES } from '../../../common/styles/Header';
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
}) {
  return (
    <div ref={dropdownRef} className="relative">
      <NavItem
        to="#"
        isActive={isActive}
        label="프로필"
        Icon={icon}
        onClick={toggleDropdown}
        additionalProps={{
          'aria-haspopup': true,
          'aria-expanded': dropdownOpen,
        }}
      />
      {dropdownOpen && (
        <div className="absolute -bottom-1 right-6 z-10">
          <div className={clsx(HEADER_STYLES.arrowBase, HEADER_STYLES.arrowOuter)}></div>
          <div className={clsx(HEADER_STYLES.arrowBase, HEADER_STYLES.arrowInner)}></div>
        </div>
      )}
      <div
        className={clsx(
          HEADER_STYLES.dropdownContainer,
          dropdownOpen ? HEADER_STYLES.dropdownVisible : HEADER_STYLES.dropdownHidden
        )}
      >
        <div className="px-4 py-2 text-text-900 overflow-hidden text-ellipsis whitespace-nowrap">
          {nickname}
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
    </div>
  );
}

export default ProfileDropdown;
