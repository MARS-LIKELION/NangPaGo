import { createBrowserRouter } from 'react-router-dom';
import RecipeList from '../pages/recipe/RecipeList.jsx';
import Login from '../pages/login/Login.jsx';
import Search from '../pages/search/Search.jsx';
import RecipeDetail from '../pages/recipe/RecipeDetail';
import UserInfoModify from '../components/mypage/UserInfoModify';
import Refrigerator from '../pages/refrigerator/Refrigerator.jsx';
import RefrigeratorSearch from '../pages/search/RefrigeratorSearch.jsx';

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
    path: '/my-page/modify',
    element: <UserInfoModify />,
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
    path: '/refrigerator',
    children: [
      {
        index: true,
        element: <Refrigerator />,
      },
      {
        path: 'recipes',
        element: <Refrigerator />,
      },
      {
        path: 'search',
        element: <RefrigeratorSearch />,
      },
    ],
  },
]);

export default router;
