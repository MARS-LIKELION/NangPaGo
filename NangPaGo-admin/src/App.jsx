import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom'
import Login from './pages/Login'
import Home from './pages/Home'
import Users from './pages/Users'
import Audit from './pages/Audit'
import ProtectedRoute from './components/ProtectedRoute'
import DashboardLayout from './components/DashboardLayout'

function App() {
  return (
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
  )
}

export default App
