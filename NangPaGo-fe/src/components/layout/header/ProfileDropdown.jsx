import NavItem from './NavItem.jsx';

function ProfileDropdown({
  dropdownOpen,
  toggleDropdown,
  dropdownRef,
  handleLogout,
  handleLinkClick,
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
          <div className="w-0 h-0 border-l-[7px] border-l-transparent border-r-[7px] border-r-transparent border-b-[7px] border-b-secondary"></div>
          <div className="absolute top-[1px] w-0 h-0 border-l-[7px] border-l-transparent border-r-[7px] border-r-transparent border-b-[7px] border-b-white"></div>
        </div>
      )}
      <div
        className={`absolute right-3 mt-1 w-40 bg-white border border-secondary rounded-md shadow-md overflow-hidden transition-all ${
          dropdownOpen ? 'opacity-100 visible' : 'opacity-0 invisible'
        }`}
      >
        <div className="px-4 py-2 text-text-900">{nickname}</div>
        <div className="max-h-30 overflow-hidden">
        <button
          to="/my-page"
          onClick={() => handleLinkClick('/my-page')}
          className="w-full text-left px-4 py-2 text-text-900 bg-white hover:bg-secondary overflow-hidden rounded-none"
        >
          마이페이지
        </button>
        <button
          onClick={handleLogout}
          className="w-full text-left px-4 py-2 text-text-900 bg-white hover:bg-secondary overflow-hidden rounded-none"
        >
          로그아웃
        </button>
        </div>
      </div>
    </div>
  );
}

export default ProfileDropdown;
