import { createBrowserRouter } from 'react-router-dom';
import RecipeList from '../pages/recipe/RecipeList.jsx';
import Login from '../pages/login/Login.jsx';
import Search from '../pages/search/Search.jsx';
import RecipeDetail from '../pages/recipe/RecipeDetail';
import UserInfoModify from '../components/mypage/UserInfoModify';

const router = createBrowserRouter([
  {
    path: '/',
    element: <RecipeList />,
  },
  {
    path: '/login',
    element: <Login />,
  },
  {
    path: '/search',
    element: <Search />,
  },
  {
    path: '/recipe/:id',
    element: <RecipeDetail />,
  },
  {
    path: '/mypage/modify',
    element: <UserInfoModify />,
  },
]);

export default router;
