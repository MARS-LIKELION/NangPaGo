// React의 useState와 useEffect 훅을 import
import { useState, useEffect } from 'react';
// React Router의 BrowserRouter, Routes, Route 컴포넌트를 import
import { BrowserRouter, Routes, Route } from 'react-router-dom';
// 공통 Header 컴포넌트 import
import Header from './components/common/Header';
// 보호된 경로를 위한 ProtectedRoute 컴포넌트 import
import ProtectedRoute from './components/common/ProtectedRoute';
// 로그인 페이지 컴포넌트 import
import Login from './pages/login/Login.jsx';
// 레시피 목록 페이지 컴포넌트 import
import RecipeList from './pages/recipe/RecipeList';
// axios 인스턴스 import
import axiosInstance from './api/axiosInstance.js';

function App() {
  // 사용자 정보와 로그인 상태를 관리하는 상태 변수
  const [user, setUser] = useState(null);
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  // 사용자 정보를 가져오는 비동기 함수
  const fetchUser = async () => {
    try {
      console.log('사용자 정보 가져오는 중...');
      const response = await axiosInstance.get('/auth/me');
      console.log('뭐냐', response);
      console.log('사용자 정보 가져오기 성공:', response.data);
      setUser(response.data); // 사용자 정보 상태 업데이트
      setIsLoggedIn(true); // 로그인 상태 업데이트
    } catch (error) {
      console.error('사용자 정보 가져오기 오류:', error);
      setUser(null); // 사용자 정보 초기화
      setIsLoggedIn(false); // 로그인 상태 초기화
    }
  };

  // 로그아웃을 처리하는 비동기 함수
  const handleLogout = async () => {
    try {
      console.log('로그아웃 중...!!!!!!');
      await axiosInstance.post('/auth/logout'); // 로그아웃 API 호출
      console.log('로그아웃 성공');
      setUser(null); // 사용자 정보 초기화
      setIsLoggedIn(false); // 로그인 상태 초기화
    } catch (error) {
      console.error('로그아웃 중 오류:', error);
    }
  };

  // 컴포넌트가 처음 마운트될 때 사용자 정보를 가져옴
  useEffect(() => {
    fetchUser();
  }, []);

  return (
    <BrowserRouter>
      {/* 현재 경로가 '/login'이 아닐 때만 헤더를 렌더링 */}
      {location.pathname !== '/login' && (
        <Header
          isLoggedIn={isLoggedIn}
          user={user}
          handleLogout={handleLogout}
        />
      )}
      <Routes>
        <Route
          path="/"
          element={
            <ProtectedRoute isLoggedIn={isLoggedIn}>
              <RecipeList />
            </ProtectedRoute>
          }
        />
        <Route path="/login" element={<Login />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
