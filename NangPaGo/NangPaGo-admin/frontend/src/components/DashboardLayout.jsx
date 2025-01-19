import { Link, useLocation, useNavigate } from 'react-router-dom';
import { HomeIcon, UserGroupIcon, ShieldCheckIcon, ClipboardDocumentListIcon } from '@heroicons/react/24/outline';

function NavLink({ to, children, icon: Icon }) {
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
      <Icon className="mr-3 flex-shrink-0 h-6 w-6" />
      {children}
    </Link>
  );
}

export default function DashboardLayout({ children }) {
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem('isAuthenticated');
    navigate('/login');
  };

  return (
    <div className="min-h-screen bg-gray-100">
      <nav className="bg-white shadow-lg">
        <div className="max-w-full mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16">
            <div className="flex items-center">
              <h1 className="text-2xl font-bold text-gray-900">Admin Dashboard</h1>
            </div>
            <div className="flex items-center">
              <button
                onClick={handleLogout}
                className="ml-4 px-4 py-2 text-sm font-medium text-white bg-indigo-600 rounded-md hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
              >
                로그아웃
              </button>
            </div>
          </div>
        </div>
      </nav>

      <div className="flex h-[calc(100vh-4rem)]">
        {/* 사이드바 */}
        <div className="w-64 bg-white shadow-lg">
          <div className="flex-1 flex flex-col">
            <nav className="flex-1 px-2 py-4 space-y-1">
              <NavLink to="/dashboard" icon={HomeIcon}>
                Home
              </NavLink>
              <NavLink to="/dashboard/users" icon={UserGroupIcon}>
                Users
              </NavLink>
              <NavLink to="/dashboard/security" icon={ShieldCheckIcon}>
                Security
              </NavLink>
              <NavLink to="/dashboard/audit" icon={ClipboardDocumentListIcon}>
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
  );
} 