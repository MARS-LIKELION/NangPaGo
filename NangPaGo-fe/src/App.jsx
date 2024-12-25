import { RouterProvider } from 'react-router-dom';
import router from './routes/Router.jsx';
import { useDispatch } from 'react-redux';
import axiosInstance from './api/axiosInstance.js';
import { login, logout } from './slices/loginSlice.js';
import { useEffect } from 'react';

function App() {
  const dispatch = useDispatch();

  const fetchUserStatus = async () => {
    try {
      const response = await axiosInstance.get('/auth/status');
      const { email, role } = response.data;
      console.log('사용자 상태:', { email, role });
    } catch (error) {
      console.error('사용자 상태를 가져오는 데 실패했습니다:', error);
    }
  };

  useEffect(() => {
    fetchUserStatus(); // 컴포넌트 로드 시 호출
  }, []);

  return <RouterProvider router={router} />;
}

export default App;
