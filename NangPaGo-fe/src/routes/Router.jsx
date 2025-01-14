import { createBrowserRouter } from 'react-router-dom';
import RecipeList from '../pages/recipe/RecipeList.jsx';
import Login from '../pages/login/Login.jsx';
import RecipeSearch from '../pages/search/RecipeSearch.jsx';
import RecipeDetail from '../pages/recipe/RecipeDetail';
import Modify from '../pages/mypage/Modify.jsx';
import Refrigerator from '../pages/refrigerator/Refrigerator.jsx';
import RefrigeratorSearch from '../pages/search/RefrigeratorSearch.jsx';
import Profile from '../pages/mypage/Profile.jsx';
import CommunityList from '../pages/community/CommunityList.jsx';
import CommunityDetail from '../pages/community/CommunityDetail';
import CreateCommunity from '../pages/community/CreateCommunity.jsx';
import ModifyCommunity from '../pages/community/ModifyCommunity.jsx';
import OAuthError from '../pages/error/OAuthError.jsx';
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
    path: '/my-page',
    element: <Profile />,
  },
  {
    path: '/my-page/modify',
    element: <Modify />,
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
      {
        path: 'new',
        element: <CreateCommunity />,
      },
      {
        path: ':id/modify',
        element: <ModifyCommunity />,
      },
    ],
  },
  {
    path: '/oauth/error',
    element: <OAuthError />,
  },
]);

export default router;
