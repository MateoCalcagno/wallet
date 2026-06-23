import { Routes, Route, Navigate } from 'react-router-dom'
import Login from './pages/Login'
import Register from './pages/Register'
import Dashboard from './pages/Dashboard'
import Alias from './pages/Alias'
import Deposit from './pages/Deposit'
import Transfer from './pages/Transfer'
import Withdraw from './pages/Withdraw'
import ForgotPassword from './pages/ForgotPassword'
import TransactionHistory from './pages/TransactionHistory'
import Statistics from './pages/Statistics'

const PrivateRoute = ({ children }) => {
  const token = localStorage.getItem('token')
  return token ? children : <Navigate to="/login" />
}

function App() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/login" />} />
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route path="/dashboard" element={<PrivateRoute><Dashboard /></PrivateRoute>} />
      <Route path="/alias" element={<PrivateRoute><Alias /></PrivateRoute>} />
      <Route path="/history" element={<PrivateRoute><TransactionHistory /></PrivateRoute>} />
      <Route path="/statistics" element={<PrivateRoute><Statistics /></PrivateRoute>} /> 
      <Route path="/deposit" element={<PrivateRoute><Deposit /></PrivateRoute>} />
      <Route path="/withdraw" element={<PrivateRoute><Withdraw /></PrivateRoute>} />
      <Route path="/transfer" element={<PrivateRoute><Transfer /></PrivateRoute>} />
      <Route path="/forgot-password" element={<ForgotPassword />} />
    </Routes>
  )
}

export default App