import { useNavigate } from 'react-router-dom'

export default function VendorDashboardPage() {
  const navigate = useNavigate()

  const handleLogout = () => {
    localStorage.removeItem('accessToken')
    navigate('/vendor/login')
  }

  return (
    <div className="page">
      <h1>ShaadiSeva</h1>
      <h2>Welcome, Vendor!</h2>
      <p>Manage your services and bookings.</p>
      <button onClick={handleLogout}>Logout</button>
    </div>
  )
}
