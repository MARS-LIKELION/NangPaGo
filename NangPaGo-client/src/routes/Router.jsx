import { createBrowserRouter } from 'react-router-dom';
import Login from '../pages/login/Login.jsx';
import RecipeSearch from '../pages/search/RecipeSearch.jsx';
import DetailPage from '../pages/common/DetailPage';
import Modify from '../pages/profile/Modify.jsx';
import Refrigerator from '../pages/refrigerator/Refrigerator.jsx';
import RefrigeratorSearch from '../pages/search/RefrigeratorSearch.jsx';
import Profile from '../pages/profile/Profile.jsx';
import ListPage from '../pages/common/ListPage.jsx';
import CreateCommunity from '../pages/community/CreateCommunity.jsx';
import ModifyCommunity from '../pages/community/ModifyCommunity.jsx';
import Error from '../pages/error/Error.jsx';
import AuthenticatedRoute from './AuthenticatedRoute';
import UnauthenticatedAccess from '../pages/error/UnauthenticatedAccess';
import NotFound from '../pages/error/NotFound';
import LoginExpired from '../pages/error/LoginExpired.jsx';
import CreateUserRecipe from '../pages/userRecipe/CreateUserRecipe.jsx';
import ModifyUserRecipe from '../pages/userRecipe/ModifyUserRecipe';

const router = createBrowserRouter([
  {
    path: '/',
    element: <ListPage type="recipe" />,
  },
  {
    path: '/login',
    element: <Login />,
  },
  {
    path: '/my-page',
    element: (
      <AuthenticatedRoute>
        <Profile />
      </AuthenticatedRoute>
    ),
  },
  {
    path: '/my-page/modify',
    element: (
      <AuthenticatedRoute>
        <Modify />
      </AuthenticatedRoute>
    ),
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
        element: <DetailPage type="recipe" />,
      },
    ],
  },
  {
    path: '/refrigerator',
    children: [
      {
        index: true,
        element: (
          <AuthenticatedRoute>
            <Refrigerator />
          </AuthenticatedRoute>
        ),
      },
      {
        path: 'recipe',
        element: (
          <AuthenticatedRoute>
            <Refrigerator />
          </AuthenticatedRoute>
        ),
      },
      {
        path: 'search',
        element: (
          <AuthenticatedRoute>
            <RefrigeratorSearch />
          </AuthenticatedRoute>
        ),
      },
    ],
  },
  {
    path: '/community',
    children: [
      {
        index: true,
        element: <ListPage type="community" />,
      },
      {
        path: ':id',
        element: <DetailPage type="community" />,
      },
      {
        path: 'create',
        element: (
          <AuthenticatedRoute>
            <CreateCommunity />
          </AuthenticatedRoute>
        ),
      },
      {
        path: ':id/modify',
        element: (
          <AuthenticatedRoute>
            <ModifyCommunity />
          </AuthenticatedRoute>
        ),
      },
    ],
  },
  {
    path: '/user-recipe',
    children: [
      {
        index: true,
        element: <ListPage type="user-recipe" />
      },
      {
        path: ':id',
        element: <DetailPage type="user-recipe" />,
      },
      {
        path: 'create',
        element: (
          <AuthenticatedRoute>
            <CreateUserRecipe />
          </AuthenticatedRoute>
        ),
      },
      {
        path: ':id/modify',
        element: (
          <AuthenticatedRoute>
            <ModifyUserRecipe />
          </AuthenticatedRoute>
        ),
      },
    ],
  },
  {
    path: '/error',
    element: <Error />,
  },
  {
    path: '/unauthenticated',
    element: <UnauthenticatedAccess />,
  },
  {
    path: '/login/expired',
    element: <LoginExpired />,
  },
  {
    path: '*',
    element: <NotFound />,
  },
]);

export default router;
