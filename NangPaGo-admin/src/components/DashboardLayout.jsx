import {Link, useLocation, useNavigate} from 'react-router-dom';
import axiosInstance from '../api/axiosInstance.js';
import {
  UserGroupIcon,
  ChartBarIcon,
  ClipboardDocumentListIcon
} from '@heroicons/react/24/outline';
import { SOCIAL_BUTTON_STYLES } from '../common/styles/CommonButton';

function NavLink({to, children, icon: Icon}) {
  const location = useLocation();
  const isActive = location.pathname === to;

  return (
      <Link
          to={to}
          className={`flex items-center px-4 py-2 text-sm font-medium rounded-md ${
              isActive
                  ? 'bg-gray-200 text-gray-900'
                  : 'text-gray-600 hover:bg-gray-100 hover:text-gray-900'
          }`}
      >
        <Icon className="mr-3 flex-shrink-0 h-6 w-6"/>
        {children}
      </Link>
  );
}

export default function DashboardLayout({children}) {
  const navigate = useNavigate();

  const handleLogout = async () => {
    try {
      const response = await axiosInstance.post('/api/logout');
      localStorage.setItem('isAuthenticated', 'false');
      navigate('/')
    } catch (error) {
      console.error('로그아웃 실패:', error.response?.data || error.message);
    }
  };

  return (
      <div className="min-h-screen bg-gray-100 flex justify-center">
        <div className="w-[1920px] min-h-screen bg-gray-100 flex flex-col">
          <nav className="bg-white shadow-lg">
            <div className="max-w-full mx-auto px-4 sm:px-6 lg:px-8">
              <div className="flex justify-between h-16">
                <div className="flex items-center">
                  <img
                    src="/logo.png"
                    alt="로고"
                    className="h-10 w-auto mr-1"
                  />
                  <h1 className="text-2xl font-bold text-gray-900">
                    냉파고 - Admin
                  </h1>
                </div>
                <div className="flex items-center">
                  <button
                      onClick={handleLogout}
                      className={`${SOCIAL_BUTTON_STYLES.common.btn} ml-4 px-4 py-2 text-sm font-medium`}
                  >
                    로그아웃
                  </button>
                </div>
              </div>
            </div>
          </nav>

          <div className="flex flex-1">
            {/* 사이드바 */}
            <div className="w-64 bg-white shadow-lg">
              <div className="flex-1 flex flex-col">
                <nav className="flex-1 px-2 py-4 space-y-1">
                  <NavLink to="/dashboard" icon={ChartBarIcon}>
                    Dashboard
                  </NavLink>
                  <NavLink to="/dashboard/users" icon={UserGroupIcon}>
                    Users
                  </NavLink>
                  <NavLink to="/dashboard/audit"
                           icon={ClipboardDocumentListIcon}>
                    Audit
                  </NavLink>
                </nav>
              </div>
            </div>

            {/* 메인 콘텐츠 */}
            <div className="flex-1 overflow-auto">
              {children}
            </div>
          </div>
        </div>
      </div>
  );
}
