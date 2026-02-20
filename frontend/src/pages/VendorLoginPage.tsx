import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { login } from '../api/auth'

export default function VendorLoginPage() {
  const navigate = useNavigate()
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const res = await login(username, password)
      localStorage.setItem('accessToken', res.data.accessToken)
      navigate('/vendor/dashboard')
    } catch {
      setError('Invalid credentials. Please try again.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="page">
      <h1>ShaadiSeva</h1>
      <h2>Vendor Login</h2>
      <form onSubmit={handleSubmit}>
        <label htmlFor="username">Email / Phone</label>
        <input
          id="username"
          type="text"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
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
      <p style={{ marginTop: '1rem' }}>
        Don&apos;t have an account?{' '}
        <a href="/vendor/apply">Apply as a Vendor</a>
      </p>
    </div>
  )
}
