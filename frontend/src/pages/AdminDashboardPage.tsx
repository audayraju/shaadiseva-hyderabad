import { useNavigate } from 'react-router-dom'

export default function AdminDashboardPage() {
  const navigate = useNavigate()

  const handleLogout = () => {
    localStorage.removeItem('accessToken')
    navigate('/admin/login')
  }

  return (
    <div className="page">
      <h1>ShaadiSeva</h1>
      <h2>Welcome, Admin!</h2>
      <p>Manage vendors, customers, and platform settings.</p>
      <button onClick={handleLogout}>Logout</button>
    </div>
  )
}
