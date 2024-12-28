import { useEffect } from 'react';
import { useDispatch } from 'react-redux';
import { fetchUserStatus } from './slices/loginSlice';
import { RouterProvider } from 'react-router-dom';
import router from './routes/Router.jsx';

function App() {
  const dispatch = useDispatch();

  useEffect(() => {
    dispatch(fetchUserStatus());
  }, [dispatch]);

  return <RouterProvider router={router} />;
}

export default App;
