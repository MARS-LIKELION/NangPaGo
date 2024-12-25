import { Link } from 'react-router-dom';
import { useSelector, useDispatch } from 'react-redux';
import { logout } from '../../slices/loginSlice';
import axiosInstance from '../../api/axiosInstance';

function Header() {
  const loginState = useSelector((state) => state.loginSlice);
  const dispatch = useDispatch(); // Redux dispatch

  const handleLogout = async () => {
    try {
      await axiosInstance.post('/logout');
      dispatch(logout());
    } catch (error) {
      console.error('로그아웃 실패:', error.response?.data || error.message);
    }
  };

  return (
    <header className="sticky top-0 z-10 bg-white px-4 py-4 shadow-md mx-auto w-[375px] mb-5">
      <div className="flex justify-between items-center">
        <Link to="/" className="block">
          <img src="/public/logo.png" alt="냉파고" className="h-16 w-auto" />
        </Link>

        {loginState.email ? (
          <div className="flex items-center space-x-4">
            <span className="text-sm text-gray-600">{loginState.email}</span>
            <button
              onClick={handleLogout}
              className="bg-[var(--primary-color)] hover:bg-[color-mix(in_srgb,var(--primary-color),#000_10%)] text-white px-4 py-2 rounded-lg text-sm"
            >
              로그아웃
            </button>
          </div>
        ) : (
          <Link
            to="/login"
            className="bg-[var(--primary-color)] hover:bg-[color-mix(in_srgb,var(--primary-color),#000_10%)] text-white px-4 py-2 rounded-lg text-sm"
          >
            로그인
          </Link>
        )}
      </div>
    </header>
  );
}

export default Header;
