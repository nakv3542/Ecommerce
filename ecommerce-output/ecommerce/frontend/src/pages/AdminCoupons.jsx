import { useState, useEffect } from 'react';
import api from '../api/axios';
import { useToast } from '../components/Toast';
import AdminNav from '../components/AdminNav';
import Pagination from '../components/Pagination';

export default function AdminCoupons() {
  const [coupons, setCoupons] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [showForm, setShowForm] = useState(false);
  const [editId, setEditId] = useState(null);
  const [form, setForm] = useState({
    code: '', type: 'PERCENTAGE', value: '', minOrderAmount: '0', maxUsage: '100', active: true,
    validFrom: '', validUntil: ''
  });
  const { addToast } = useToast();

  const fetchCoupons = () => {
    setLoading(true);
    api.get(`/coupons?page=${page}&size=15`).then(r => {
      setCoupons(r.data.content);
      setTotalPages(r.data.totalPages);
    }).catch(() => {}).finally(() => setLoading(false));
  };

  useEffect(() => { fetchCoupons(); }, [page]);

  const openForm = (c) => {
    if (c) {
      setForm({
        code: c.code, type: c.type, value: c.value, minOrderAmount: c.minOrderAmount,
        maxUsage: c.maxUsage, active: c.active,
        validFrom: c.validFrom ? c.validFrom.slice(0, 16) : '',
        validUntil: c.validUntil ? c.validUntil.slice(0, 16) : ''
      });
      setEditId(c.id);
    } else {
      setForm({ code: '', type: 'PERCENTAGE', value: '', minOrderAmount: '0', maxUsage: '100', active: true, validFrom: '', validUntil: '' });
      setEditId(null);
    }
    setShowForm(true);
  };

  const save = async (e) => {
    e.preventDefault();
    try {
      const body = {
        code: form.code.toUpperCase(),
        type: form.type,
        value: parseFloat(form.value),
        minOrderAmount: parseFloat(form.minOrderAmount),
        maxUsage: parseInt(form.maxUsage),
        active: form.active,
        validFrom: form.validFrom ? new Date(form.validFrom).toISOString() : null,
        validUntil: form.validUntil ? new Date(form.validUntil).toISOString() : null
      };
      if (editId) {
        await api.put(`/coupons/${editId}`, body);
        addToast('Coupon updated!');
      } else {
        await api.post('/coupons', body);
        addToast('Coupon created!');
      }
      fetchCoupons();
      setShowForm(false);
    } catch (err) {
      addToast(err.response?.data?.message || 'Failed to save coupon', 'error');
    }
  };

  const deleteCoupon = async (id) => {
    try {
      await api.delete(`/coupons/${id}`);
      addToast('Coupon deleted');
      fetchCoupons();
    } catch (err) {
      addToast('Failed to delete coupon', 'error');
    }
  };

  if (loading) return <div className="loading"><div className="spinner"></div></div>;

  return (
    <div className="admin-page">
      <div className="container">
        <div className="admin-layout">
          <AdminNav />
          <div className="admin-content">
            <div className="admin-header">
              <h2>Coupons</h2>
              <button className="btn-primary" onClick={() => openForm(null)}>+ Add Coupon</button>
            </div>

            <div className="admin-card">
              <table className="admin-table">
                <thead>
                  <tr>
                    <th>Code</th>
                    <th>Type</th>
                    <th>Value</th>
                    <th>Min Order</th>
                    <th>Usage</th>
                    <th>Active</th>
                    <th>Valid Until</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {coupons.length === 0 ? (
                    <tr>
                      <td colSpan="8" style={{ textAlign: 'center', padding: '48px 20px', color: 'var(--text-muted)' }}>
                        <div style={{ fontSize: 40, marginBottom: 8, opacity: 0.4 }}>🎫</div>
                        No coupons yet
                      </td>
                    </tr>
                  ) : (
                    coupons.map(c => (
                      <tr key={c.id}>
                        <td><span className="cell-name">{c.code}</span></td>
                        <td>{c.type}</td>
                        <td>{c.type === 'PERCENTAGE' ? `${c.value}%` : `$${c.value}`}</td>
                        <td>${c.minOrderAmount}</td>
                        <td>{c.currentUsage}/{c.maxUsage}</td>
                        <td>{c.active ? <span className="badge badge-success">Active</span> : <span className="badge badge-neutral">Inactive</span>}</td>
                        <td style={{ fontSize: 12, color: 'var(--text-muted)' }}>{c.validUntil ? new Date(c.validUntil).toLocaleDateString() : '-'}</td>
                        <td>
                          <div className="actions-cell">
                            <button className="btn-icon" title="Edit" onClick={() => openForm(c)}>✏️</button>
                            <button className="btn-icon danger" title="Delete" onClick={() => deleteCoupon(c.id)}>🗑️</button>
                          </div>
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>

            {totalPages > 1 && (
              <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />
            )}

            {showForm && (
              <div className="admin-form-overlay" onClick={() => setShowForm(false)}>
                <div className="admin-form" onClick={e => e.stopPropagation()}>
                  <div className="admin-form-header">
                    <h2>{editId ? 'Edit Coupon' : 'Add Coupon'}</h2>
                    <button className="admin-form-close" onClick={() => setShowForm(false)}>✕</button>
                  </div>
                  <div className="admin-form-body">
                    <form onSubmit={save}>
                      <div className="form-group">
                        <label>Code</label>
                        <input value={form.code} onChange={e => setForm({...form, code: e.target.value.toUpperCase()})} required placeholder="e.g. SUMMER25" />
                      </div>
                      <div className="form-row">
                        <div className="form-group">
                          <label>Type</label>
                          <select value={form.type} onChange={e => setForm({...form, type: e.target.value})}>
                            <option value="PERCENTAGE">Percentage</option>
                            <option value="FLAT">Flat Amount</option>
                          </select>
                        </div>
                        <div className="form-group">
                          <label>Value</label>
                          <input type="number" step="0.01" value={form.value} onChange={e => setForm({...form, value: e.target.value})} required />
                        </div>
                      </div>
                      <div className="form-row">
                        <div className="form-group">
                          <label>Min Order Amount</label>
                          <input type="number" step="0.01" value={form.minOrderAmount} onChange={e => setForm({...form, minOrderAmount: e.target.value})} />
                        </div>
                        <div className="form-group">
                          <label>Max Usage</label>
                          <input type="number" value={form.maxUsage} onChange={e => setForm({...form, maxUsage: e.target.value})} />
                        </div>
                      </div>
                      <div className="form-row">
                        <div className="form-group">
                          <label>Valid From</label>
                          <input type="datetime-local" value={form.validFrom} onChange={e => setForm({...form, validFrom: e.target.value})} />
                        </div>
                        <div className="form-group">
                          <label>Valid Until</label>
                          <input type="datetime-local" value={form.validUntil} onChange={e => setForm({...form, validUntil: e.target.value})} />
                        </div>
                      </div>
                      <div className="form-group">
                        <label className="form-checkbox">
                          <input type="checkbox" checked={form.active} onChange={e => setForm({...form, active: e.target.checked})} />
                          Active
                        </label>
                      </div>
                      <div className="form-actions">
                        <button type="button" className="btn-cancel" onClick={() => setShowForm(false)}>Cancel</button>
                        <button type="submit" className="btn-save">{editId ? 'Update' : 'Create'}</button>
                      </div>
                    </form>
                  </div>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
