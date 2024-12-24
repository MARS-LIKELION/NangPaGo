// react-router-dom에서 제공하는 라우팅 관련 컴포넌트 임포트
import { createBrowserRouter, RouterProvider } from 'react-router-dom';

// 페이지 컴포넌트 임포트
import Login from '../pages/login/Login.jsx'; // 로그인 화면
import Home from '../pages/Home.jsx'; // 홈 화면
import RecipeList from '../pages/recipe/RecipeList.jsx'; // 레시피 목록 화면
import Search from '../pages/search/Search.jsx'; // 검색 화면

// 라우터 설정
const router = createBrowserRouter([
  {
    // '/' 경로는 홈 화면(Home)과 연결
    path: '/',
    element: <Home />, // Home 컴포넌트를 렌더링
  },
  {
    // '/login' 경로는 로그인 화면(Login)과 연결
    path: '/login',
    element: <Login />, // Login 컴포넌트를 렌더링
  },
  {
    // '/recipes' 경로는 레시피 목록 화면과 연결
    path: '/recipes',
    element: <RecipeList />, // RecipeList 컴포넌트를 렌더링
  },
  {
    // '/search' 경로는 검색 화면과 연결
    path: '/search',
    element: <Search />, // Search 컴포넌트를 렌더링
  },
]);

// Router 컴포넌트를 정의 및 반환
export default function Router() {
  // RouterProvider는 생성된 라우터(router)를 애플리케이션에 적용
  return <RouterProvider router={router} />;
}
