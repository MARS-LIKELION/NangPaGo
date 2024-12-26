import { useDispatch } from 'react-redux';
import { login } from './slices/loginSlice';
import axiosInstance from './api/axiosInstance.js';
import { useEffect } from 'react';
import { RouterProvider } from 'react-router-dom';
import router from './routes/Router.jsx';

function App() {
  const dispatch = useDispatch();

  const fetchUserStatus = async () => {
    try {
      const response = await axiosInstance.get('/auth/status');
      const { email } = response.data;

      if (email) {
        dispatch(login({ email }));
      }
    } catch (error) {
      console.error(
        '사용자 상태를 가져오는 데 실패:',
        error.response || error.message,
      );
    }
  };

  useEffect(() => {
    fetchUserStatus();
  }, []);

  return <RouterProvider router={router} />;
}

export default App;
