import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { login } from '../api/auth'

export default function AdminLoginPage() {
  const navigate = useNavigate()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const res = await login(email, password)
      localStorage.setItem('accessToken', res.data.accessToken)
      navigate('/admin/dashboard')
    } catch {
      setError('Invalid credentials. Please try again.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="page">
      <h1>ShaadiSeva</h1>
      <h2>Admin Login</h2>
      <form onSubmit={handleSubmit}>
        <label htmlFor="email">Email</label>
        <input
          id="email"
          type="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />
        <label htmlFor="password">Password</label>
        <input
          id="password"
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
        {error && <p className="error">{error}</p>}
        <button type="submit" disabled={loading}>
          {loading ? 'Logging in…' : 'Login'}
        </button>
      </form>
    </div>
  )
}
