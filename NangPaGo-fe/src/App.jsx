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
      console.log('서버 응답:', response); // 서버 응답 로그
      const { email } = response.data;

      if (email) {
        dispatch(login({ email })); // Redux 상태 업데이트
        console.log('로그인 상태 업데이트:', email);
      } else {
        console.warn('서버 응답에 이메일이 없습니다.');
      }
    } catch (error) {
      console.error(
        '사용자 상태를 가져오는 데 실패:',
        error.response || error.message,
      );
    }
  };

  useEffect(() => {
    fetchUserStatus(); // 항상 호출
  }, []);

  return <RouterProvider router={router} />;
}

export default App;
