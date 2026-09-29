import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../components/Toast';
import api from '../api/axios';

export default function Profile() {
  const { user, updateUser } = useAuth();
  const { addToast } = useToast();
  const [addresses, setAddresses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [editId, setEditId] = useState(null);
  const [showPasswordForm, setShowPasswordForm] = useState(false);
  const [saving, setSaving] = useState(false);

  const [name, setName] = useState(user?.name || '');
  const [phone, setPhone] = useState(user?.phone || '');
  const [passwordForm, setPasswordForm] = useState({ currentPassword: '', newPassword: '', confirmPassword: '' });

  const [form, setForm] = useState({
    fullName: '', phone: '', street: '', city: '', state: '', zipCode: '', country: 'US', isDefault: false
  });

  const fetchAddresses = () => {
    api.get('/addresses').then(r => setAddresses(r.data)).catch(() => {}).finally(() => setLoading(false));
  };

  useEffect(() => { fetchAddresses(); }, []);

  const saveProfile = async () => {
    setSaving(true);
    try {
      const updates = {};
      if (name !== user?.name) updates.name = name;
      if (phone !== (user?.phone || '')) updates.phone = phone;
      if (Object.keys(updates).length === 0) { setSaving(false); return; }
      const res = await api.put('/auth/profile', updates);
      updateUser(res.data);
      addToast('Profile updated successfully!');
    } catch (err) {
      addToast(err.response?.data?.message || 'Failed to update profile', 'error');
    } finally {
      setSaving(false);
    }
  };

  const savePassword = async (e) => {
    e.preventDefault();
    if (passwordForm.newPassword !== passwordForm.confirmPassword) {
      addToast('Passwords do not match', 'error');
      return;
    }
    if (passwordForm.newPassword.length < 6) {
      addToast('Password must be at least 6 characters', 'error');
      return;
    }
    setSaving(true);
    try {
      const res = await api.put('/auth/profile', {
        currentPassword: passwordForm.currentPassword,
        newPassword: passwordForm.newPassword
      });
      updateUser(res.data);
      setShowPasswordForm(false);
      setPasswordForm({ currentPassword: '', newPassword: '', confirmPassword: '' });
      addToast('Password changed successfully!');
    } catch (err) {
      addToast(err.response?.data?.message || 'Failed to change password', 'error');
    } finally {
      setSaving(false);
    }
  };

  const openForm = (addr) => {
    if (addr) {
      setForm({ fullName: addr.fullName, phone: addr.phone, street: addr.street, city: addr.city, state: addr.state, zipCode: addr.zipCode, country: addr.country, isDefault: addr.default });
      setEditId(addr.id);
    } else {
      setForm({ fullName: '', phone: '', street: '', city: '', state: '', zipCode: '', country: 'US', isDefault: false });
      setEditId(null);
    }
    setShowForm(true);
  };

  const saveAddress = async (e) => {
    e.preventDefault();
    try {
      if (editId) {
        await api.put(`/addresses/${editId}`, form);
        addToast('Address updated!');
      } else {
        await api.post('/addresses', form);
        addToast('Address added!');
      }
      fetchAddresses();
      setShowForm(false);
    } catch (err) {
      addToast(err.response?.data?.message || 'Failed to save address', 'error');
    }
  };

  const deleteAddress = async (id) => {
    try {
      await api.delete(`/addresses/${id}`);
      addToast('Address deleted');
      fetchAddresses();
    } catch (err) {
      addToast('Failed to delete address', 'error');
    }
  };

  return (
    <div className="container">
      <div className="profile-page">
        <h1>My Account</h1>

        <div className="profile-grid">
          <div className="profile-card">
            <h2>Account Details</h2>
            <div className="form-group">
              <label>Name</label>
              <input value={name} onChange={e => setName(e.target.value)} />
            </div>
            <div className="form-group">
              <label>Phone</label>
              <input value={phone} onChange={e => setPhone(e.target.value)} placeholder="Add phone number" />
            </div>
            <div className="form-group">
              <label>Email</label>
              <input value={user?.email || ''} disabled />
            </div>
            <div className="form-group">
              <label>Role</label>
              <input value={user?.role || ''} disabled />
            </div>
            <button className="btn" style={{ background: '#2563eb', color: '#fff', border: 'none', padding: '10px 20px', borderRadius: 6, fontWeight: 600, marginTop: 8 }}
              onClick={saveProfile} disabled={saving}>
              {saving ? 'Saving...' : 'Save Changes'}
            </button>

            <button className="btn" style={{ background: 'transparent', color: '#2563eb', border: '1px solid #2563eb', padding: '10px 20px', borderRadius: 6, fontWeight: 600, marginTop: 12, display: 'block', width: '100%' }}
              onClick={() => setShowPasswordForm(!showPasswordForm)}>
              {showPasswordForm ? 'Cancel' : 'Change Password'}
            </button>

            {showPasswordForm && (
              <form onSubmit={savePassword} style={{ marginTop: 16, padding: 16, background: '#f9fafb', borderRadius: 8 }}>
                <div className="form-group">
                  <label>Current Password</label>
                  <input type="password" required value={passwordForm.currentPassword}
                    onChange={e => setPasswordForm({...passwordForm, currentPassword: e.target.value})} />
                </div>
                <div className="form-group">
                  <label>New Password</label>
                  <input type="password" required value={passwordForm.newPassword}
                    onChange={e => setPasswordForm({...passwordForm, newPassword: e.target.value})} />
                </div>
                <div className="form-group">
                  <label>Confirm New Password</label>
                  <input type="password" required value={passwordForm.confirmPassword}
                    onChange={e => setPasswordForm({...passwordForm, confirmPassword: e.target.value})} />
                </div>
                <button type="submit" className="btn" style={{ background: '#059669', color: '#fff', border: 'none', padding: '10px 20px', borderRadius: 6, fontWeight: 600 }}
                  disabled={saving}>
                  {saving ? 'Saving...' : 'Update Password'}
                </button>
              </form>
            )}
          </div>

          <div className="profile-card">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
              <h2 style={{ margin: 0 }}>Addresses</h2>
              <button className="btn" style={{ background: '#2563eb', color: '#fff', border: 'none', padding: '8px 16px', borderRadius: 6, fontWeight: 600, fontSize: 13 }}
                onClick={() => openForm(null)}>+ Add</button>
            </div>

            {addresses.length === 0 ? (
              <p style={{ color: '#565959', fontSize: 14 }}>No addresses yet. Add one to start shopping.</p>
            ) : (
              <div className="address-list">
                {addresses.map(a => (
                  <div key={a.id} className="address-item">
                    <div className="name">{a.fullName}</div>
                    <div className="detail">{a.street}, {a.city}, {a.state} {a.zipCode}</div>
                    <div className="detail">{a.phone}</div>
                    {a.default && <span className="default-badge">Default</span>}
                    <div className="actions">
                      <button onClick={() => openForm(a)}>Edit</button>
                      <button className="delete" onClick={() => deleteAddress(a.id)}>Delete</button>
                    </div>
                  </div>
                ))}
              </div>
            )}

            {showForm && (
              <form onSubmit={saveAddress}>
                <div className="form-row" style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
                  <div className="form-group">
                    <label>Full Name</label>
                    <input required value={form.fullName} onChange={e => setForm({...form, fullName: e.target.value})} />
                  </div>
                  <div className="form-group">
                    <label>Phone</label>
                    <input required value={form.phone} onChange={e => setForm({...form, phone: e.target.value})} />
                  </div>
                </div>
                <div className="form-group">
                  <label>Street</label>
                  <input required value={form.street} onChange={e => setForm({...form, street: e.target.value})} />
                </div>
                <div className="form-row" style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
                  <div className="form-group">
                    <label>City</label>
                    <input required value={form.city} onChange={e => setForm({...form, city: e.target.value})} />
                  </div>
                  <div className="form-group">
                    <label>State</label>
                    <input required value={form.state} onChange={e => setForm({...form, state: e.target.value})} />
                  </div>
                </div>
                <div className="form-row" style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
                  <div className="form-group">
                    <label>ZIP Code</label>
                    <input required value={form.zipCode} onChange={e => setForm({...form, zipCode: e.target.value})} />
                  </div>
                  <div className="form-group">
                    <label>Country</label>
                    <input value={form.country} onChange={e => setForm({...form, country: e.target.value})} />
                  </div>
                </div>
                <div style={{ display: 'flex', gap: 12, marginTop: 12 }}>
                  <button type="submit" className="btn" style={{ background: '#2563eb', color: '#fff', border: 'none', padding: '10px 20px', borderRadius: 6, fontWeight: 600 }}>
                    {editId ? 'Update' : 'Save'}
                  </button>
                  <button type="button" className="btn" style={{ background: '#f0f0f0', border: 'none', padding: '10px 20px', borderRadius: 6 }}
                    onClick={() => setShowForm(false)}>Cancel</button>
                </div>
              </form>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
