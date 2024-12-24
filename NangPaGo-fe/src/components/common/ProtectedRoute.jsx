import { Navigate } from 'react-router-dom';

// 함수 선언 방식으로 ProtectedRoute 컴포넌트 정의
function ProtectedRoute({ isLoggedIn, children }) {
  // 현재 로그인 상태를 콘솔에 출력
  console.log('ProtectedRoute: isLoggedIn =', isLoggedIn);

  // 로그인 상태에 따라 자식 컴포넌트를 렌더링하거나, '/' 경로로 리다이렉트
  return isLoggedIn ? children : <Navigate to="/" />;
}

export default ProtectedRoute;
