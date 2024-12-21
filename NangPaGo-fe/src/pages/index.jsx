import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import Login from '../pages/login/LoginPage.jsx';

const router = createBrowserRouter([
  {
    path: '/',
    element: <Login />,
  },
  {
    path: '/login',
    element: <Login />,
  },
]);

export default function Router() {
  return <RouterProvider router={router} />;
}
