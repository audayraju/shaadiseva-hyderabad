import { useNavigate } from 'react-router-dom'

export default function CustomerDashboardPage() {
  const navigate = useNavigate()

  const handleLogout = () => {
    localStorage.removeItem('accessToken')
    navigate('/customer/login')
  }

  return (
    <div className="page">
      <h1>ShaadiSeva</h1>
      <h2>Welcome, Customer!</h2>
      <p>Browse and book wedding services in Hyderabad.</p>
      <button onClick={handleLogout}>Logout</button>
    </div>
  )
}
