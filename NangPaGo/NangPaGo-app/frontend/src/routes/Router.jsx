import { createBrowserRouter } from 'react-router-dom';
import RecipeList from '../pages/recipe/RecipeList.jsx';
import Login from '../pages/login/Login.jsx';
import RecipeSearch from '../pages/search/RecipeSearch.jsx';
import RecipeDetail from '../pages/recipe/RecipeDetail';
import Modify from '../pages/profile/Modify.jsx';
import Refrigerator from '../pages/refrigerator/Refrigerator.jsx';
import RefrigeratorSearch from '../pages/search/RefrigeratorSearch.jsx';
import Profile from '../pages/profile/Profile.jsx';
import CommunityList from '../pages/community/CommunityList.jsx';
import CommunityDetail from '../pages/community/CommunityDetail';
import CreateCommunity from '../pages/community/CreateCommunity.jsx';
import ModifyCommunity from '../pages/community/ModifyCommunity.jsx';
import OAuthError from '../pages/error/OAuthError.jsx';
import AuthenticatedRoute from './AuthenticatedRoute';
import UnauthenticatedAccess from '../pages/error/UnauthenticatedAccess';

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
    element: <AuthenticatedRoute><Profile /></AuthenticatedRoute>,
  },
  {
    path: '/my-page/modify',
    element: <AuthenticatedRoute><Modify /></AuthenticatedRoute>,
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
        element: <AuthenticatedRoute><Refrigerator /></AuthenticatedRoute>,
      },
      {
        path: 'recipe',
        element: <AuthenticatedRoute><Refrigerator /></AuthenticatedRoute>,
      },
      {
        path: 'search',
        element: <AuthenticatedRoute><RefrigeratorSearch /></AuthenticatedRoute>,
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
        element: <AuthenticatedRoute><CreateCommunity /></AuthenticatedRoute>,
      },
      {
        path: ':id/modify',
        element: <AuthenticatedRoute><ModifyCommunity /></AuthenticatedRoute>,
      },
    ],
  },
  {
    path: '/oauth/error',
    element: <OAuthError />,
  },
  {
    path: '/unauthenticated',
    element: <UnauthenticatedAccess />,
  },
]);

export default router;
