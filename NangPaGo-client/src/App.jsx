import { useEffect } from 'react';
import { useDispatch } from 'react-redux';
import { fetchUserStatus } from './slices/loginSlice';
import { RouterProvider } from 'react-router-dom';
import router from './routes/Router.jsx';

function App() {
  const dispatch = useDispatch();

  useEffect(() => {
    console.log('App mounted, dispatching fetchUserStatus...');
    dispatch(fetchUserStatus())
      .then((result) => {
        console.log('FetchUserStatus result:', result);
      })
      .catch((error) => {
        console.error('FetchUserStatus error:', error);
      });
  }, [dispatch]);

  return <RouterProvider router={router} />;
}

export default App;
