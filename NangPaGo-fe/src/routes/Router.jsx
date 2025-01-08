import { createBrowserRouter } from 'react-router-dom';
import RecipeList from '../pages/recipe/RecipeList.jsx';
import Login from '../pages/login/Login.jsx';
import RecipeSearch from '../pages/search/RecipeSearch.jsx';
import RecipeDetail from '../pages/recipe/RecipeDetail';
import UserInfoModify from '../components/mypage/UserInfoModify';
import Refrigerator from '../pages/refrigerator/Refrigerator.jsx';
import RefrigeratorSearch from '../pages/search/RefrigeratorSearch.jsx';
import CommunityList from '../pages/community/CommunityList.jsx';
import CommunityDetail from '../pages/community/CommunityDetail';

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
    path: '/profile',
    element: <UserInfoModify />,
  },
  {
    path: '/recipe',
    children: [
      {
        path: 'search',
        element: <RecipeSearch />,
      },
      {
        path: ':id',
        element: <RecipeDetail />,
      },
    ],
  },
  {
    path: '/refrigerator',
    children: [
      {
        index: true,
        element: <Refrigerator />,
      },
      {
        path: 'recipe',
        element: <Refrigerator />,
      },
      {
        path: 'search',
        element: <RefrigeratorSearch />,
      },
    ],
  },
  {
    path: '/community',
    children: [
      {
        index: true,
        element: <CommunityList />,
      },
      {
        path: ':id',
        element: <CommunityDetail />,
      },
    ],
  },
]);

export default router;
