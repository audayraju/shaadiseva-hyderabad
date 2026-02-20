import { useState } from 'react'
import { applyAsVendor, VendorApplyRequest } from '../api/vendor'

export default function VendorApplyPage() {
  const [form, setForm] = useState<VendorApplyRequest>({
    email: '',
    phone: '',
    password: '',
    businessName: '',
    categoryId: '',
    gstNumber: '',
    panNumber: '',
    address: '',
    city: '',
    bio: '',
    yearsExperience: undefined,
  })
  const [error, setError] = useState('')
  const [applicationId, setApplicationId] = useState('')
  const [loading, setLoading] = useState(false)

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target
    setForm((prev) => ({
      ...prev,
      [name]: name === 'yearsExperience' ? (value === '' ? undefined : Number(value)) : value,
    }))
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setApplicationId('')
    setLoading(true)
    try {
      const res = await applyAsVendor(form)
      setApplicationId(res.data.applicationId)
    } catch {
      setError('Failed to submit application. Please try again.')
    } finally {
      setLoading(false)
    }
  }

  if (applicationId) {
    return (
      <div className="page">
        <h1>ShaadiSeva</h1>
        <h2>Application Submitted</h2>
        <p className="success">
          Your application has been received!<br />
          Application ID: <strong>{applicationId}</strong>
        </p>
        <p>Our team will review your application and contact you shortly.</p>
      </div>
    )
  }

  return (
    <div className="page">
      <h1>ShaadiSeva</h1>
      <h2>Vendor Application</h2>
      <form onSubmit={handleSubmit}>
        <label htmlFor="email">Email *</label>
        <input id="email" name="email" type="email" value={form.email} onChange={handleChange} required />

        <label htmlFor="phone">Phone *</label>
        <input id="phone" name="phone" type="tel" value={form.phone} onChange={handleChange} required />

        <label htmlFor="password">Password *</label>
        <input id="password" name="password" type="password" value={form.password} onChange={handleChange} required />

        <label htmlFor="businessName">Business Name *</label>
        <input id="businessName" name="businessName" type="text" value={form.businessName} onChange={handleChange} required />

        <label htmlFor="categoryId">Category ID *</label>
        <input id="categoryId" name="categoryId" type="text" placeholder="UUID" value={form.categoryId} onChange={handleChange} required />

        <label htmlFor="gstNumber">GST Number</label>
        <input id="gstNumber" name="gstNumber" type="text" value={form.gstNumber ?? ''} onChange={handleChange} />

        <label htmlFor="panNumber">PAN Number</label>
        <input id="panNumber" name="panNumber" type="text" value={form.panNumber ?? ''} onChange={handleChange} />

        <label htmlFor="address">Address</label>
        <input id="address" name="address" type="text" value={form.address ?? ''} onChange={handleChange} />

        <label htmlFor="city">City</label>
        <input id="city" name="city" type="text" value={form.city ?? ''} onChange={handleChange} />

        <label htmlFor="bio">Bio</label>
        <textarea id="bio" name="bio" rows={3} value={form.bio ?? ''} onChange={handleChange} />

        <label htmlFor="yearsExperience">Years of Experience</label>
        <input
          id="yearsExperience"
          name="yearsExperience"
          type="number"
          min={0}
          value={form.yearsExperience ?? ''}
          onChange={handleChange}
        />

        {error && <p className="error">{error}</p>}
        <button type="submit" disabled={loading}>
          {loading ? 'Submitting…' : 'Apply as Vendor'}
        </button>
      </form>
    </div>
  )
}
