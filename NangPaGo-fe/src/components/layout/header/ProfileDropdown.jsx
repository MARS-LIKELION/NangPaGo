import { FaRegUser } from 'react-icons/fa';
import { Link } from 'react-router-dom';

function ProfileDropdown({
  dropdownOpen,
  toggleDropdown,
  dropdownRef,
  handleLogout,
  isActive,
  nickname,
}) {
  return (
    <div ref={dropdownRef} className="relative">
      <button
        onClick={toggleDropdown}
        className={`group inline-flex flex-col items-center justify-center gap-1 text-sm font-medium rounded-md overflow-hidden 
          ${isActive ? 'text-primary' : 'text-text-400 bg-white'}`}
        aria-haspopup="true"
        aria-expanded={dropdownOpen}
      >
        <span className="inline-flex items-center justify-center">
          <FaRegUser size={24} />
        </span>
        <span>프로필</span>
      </button>
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
          <Link
            to="/my-page"
            className="block px-4 py-2 text-text-900 hover:bg-secondary"
          >
            마이페이지
          </Link>
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
