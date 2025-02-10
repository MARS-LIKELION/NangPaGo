import { BrowserRouter as Router, Routes, Route, Navigate, useNavigate } from 'react-router-dom';
import Login from './pages/Login';
import Home from './pages/Home';
import Users from './pages/Users';
import Audit from './pages/Audit';
import AuthErrorPage from './components/common/AuthErrorPage';
import ProtectedRoute from './components/ProtectedRoute';
import DashboardLayout from './components/DashboardLayout';
import { setNavigate } from './api/axiosInstance';

function NavigationSetter() {
  const navigate = useNavigate();
  setNavigate(navigate);
  return null;
}

function App() {
  return (
    <Router>
      <NavigationSetter />
      <Routes>
        <Route path="/login" element={<Login />} />

        <Route
          path="/dashboard"
          element={
            <ProtectedRoute>
              <DashboardLayout>
                <Home />
              </DashboardLayout>
            </ProtectedRoute>
          }
        />
        <Route
          path="/dashboard/users"
          element={
            <ProtectedRoute>
              <DashboardLayout>
                <Users />
              </DashboardLayout>
            </ProtectedRoute>
          }
        />
        <Route
          path="/dashboard/audit"
          element={
            <ProtectedRoute>
              <DashboardLayout>
                <Audit />
              </DashboardLayout>
            </ProtectedRoute>
          }
        />
        <Route path="/auth-error" element={<AuthErrorPage />} />

        <Route path="/" element={<Navigate to="/login" replace />} />
      </Routes>
    </Router>
  );
}
export default App;
