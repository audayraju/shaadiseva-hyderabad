import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { sendOtp, verifyOtp } from '../api/auth'

export default function CustomerOtpLoginPage() {
  const navigate = useNavigate()
  const [phone, setPhone] = useState('')
  const [otp, setOtp] = useState('')
  const [step, setStep] = useState<'phone' | 'otp'>('phone')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSendOtp = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      await sendOtp(phone)
      setStep('otp')
    } catch {
      setError('Failed to send OTP. Please check your phone number.')
    } finally {
      setLoading(false)
    }
  }

  const handleVerifyOtp = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const res = await verifyOtp(phone, otp)
      localStorage.setItem('accessToken', res.data.accessToken)
      navigate('/customer/dashboard')
    } catch {
      setError('Invalid OTP. Please try again.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="page">
      <h1>ShaadiSeva</h1>
      <h2>Customer Login</h2>
      {step === 'phone' ? (
        <form onSubmit={handleSendOtp}>
          <label htmlFor="phone">Phone Number</label>
          <input
            id="phone"
            type="tel"
            placeholder="10-digit mobile number"
            value={phone}
            onChange={(e) => setPhone(e.target.value)}
            required
          />
          {error && <p className="error">{error}</p>}
          <button type="submit" disabled={loading}>
            {loading ? 'Sending…' : 'Send OTP'}
          </button>
        </form>
      ) : (
        <form onSubmit={handleVerifyOtp}>
          <label htmlFor="otp">Enter OTP sent to {phone}</label>
          <input
            id="otp"
            type="text"
            placeholder="6-digit OTP"
            value={otp}
            onChange={(e) => setOtp(e.target.value)}
            required
          />
          {error && <p className="error">{error}</p>}
          <button type="submit" disabled={loading}>
            {loading ? 'Verifying…' : 'Verify'}
          </button>
          <button
            type="button"
            style={{ marginLeft: '0.5rem', background: '#888' }}
            onClick={() => { setStep('phone'); setOtp(''); setError('') }}
          >
            Back
          </button>
        </form>
      )}
    </div>
  )
}
