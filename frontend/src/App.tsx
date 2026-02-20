import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import CustomerOtpLoginPage from './pages/CustomerOtpLoginPage'
import VendorApplyPage from './pages/VendorApplyPage'
import VendorLoginPage from './pages/VendorLoginPage'
import AdminLoginPage from './pages/AdminLoginPage'
import CustomerDashboardPage from './pages/CustomerDashboardPage'
import VendorDashboardPage from './pages/VendorDashboardPage'
import AdminDashboardPage from './pages/AdminDashboardPage'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Navigate to="/customer/login" replace />} />
        <Route path="/customer/login" element={<CustomerOtpLoginPage />} />
        <Route path="/vendor/apply" element={<VendorApplyPage />} />
        <Route path="/vendor/login" element={<VendorLoginPage />} />
        <Route path="/admin/login" element={<AdminLoginPage />} />
        <Route path="/customer/dashboard" element={<CustomerDashboardPage />} />
        <Route path="/vendor/dashboard" element={<VendorDashboardPage />} />
        <Route path="/admin/dashboard" element={<AdminDashboardPage />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
