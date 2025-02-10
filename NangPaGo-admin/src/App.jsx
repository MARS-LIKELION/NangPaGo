import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom'
import Login from './pages/Login'
import Home from './pages/Home'
import Users from './pages/Users'
import Audit from './pages/Audit'
import ProtectedRoute from './components/ProtectedRoute'
import DashboardLayout from './components/DashboardLayout'
import React, { useState, useEffect } from 'react';
import { setErrorHandler } from './api/axiosInstance';
import AuthErrorModal from './components/modal/AuthErrorModal';

function App() {
  const [isAuthErrorModalOpen, setIsAuthErrorModalOpen] = useState(false);

  useEffect(() => {
    setErrorHandler(() => {
      setIsAuthErrorModalOpen(true);
    });
  }, []);

  const handleCloseModal = () => {
    setIsAuthErrorModalOpen(false);
  };

  const handleConfirmModal = () => {
    setIsAuthErrorModalOpen(false);
    window.location.href = '/login';
  };

  return (
    <div>
      <AuthErrorModal
        isOpen={isAuthErrorModalOpen}
        onClose={handleCloseModal}
        onConfirm={handleConfirmModal}
      />
      <Router>
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
          <Route path="/" element={<Navigate to="/login" replace />} />
        </Routes>
      </Router>
    </div>
  )
}

export default App
