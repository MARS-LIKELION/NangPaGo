import { useSelector } from 'react-redux';
import { Navigate } from 'react-router-dom';

const AuthenticatedRoute = ({ children }) => {
  const { isLoggedIn, isInitialized } = useSelector((state) => state.loginSlice || {});

  if (!isInitialized) {
    return (
      <div className="flex justify-center items-center h-screen">
        <div className="animate-spin rounded-full h-16 w-16 border-t-4 border-gray"></div>
      </div>
    );
  }

  if (!isLoggedIn) {
    return <Navigate to="/unauthenticated" replace />;
  }

  return children;
};

export default AuthenticatedRoute;
