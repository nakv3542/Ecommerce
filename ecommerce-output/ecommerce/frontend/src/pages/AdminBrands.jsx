import { useState, useEffect } from 'react';
import api from '../api/axios';
import { useToast } from '../components/Toast';
import AdminNav from '../components/AdminNav';
import Pagination from '../components/Pagination';

export default function AdminBrands() {
  const [brands, setBrands] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [showForm, setShowForm] = useState(false);
  const [editId, setEditId] = useState(null);
  const [form, setForm] = useState({ name: '', logoUrl: '' });
  const { addToast } = useToast();

  const fetchBrands = () => {
    setLoading(true);
    api.get(`/brands?page=${page}&size=15`).then(r => {
      setBrands(r.data.content);
      setTotalPages(r.data.totalPages);
    }).catch(() => {}).finally(() => setLoading(false));
  };

  useEffect(() => { fetchBrands(); }, [page]);

  const openForm = (b) => {
    if (b) {
      setForm({ name: b.name, logoUrl: b.logoUrl || '' });
      setEditId(b.id);
    } else {
      setForm({ name: '', logoUrl: '' });
      setEditId(null);
    }
    setShowForm(true);
  };

  const save = async (e) => {
    e.preventDefault();
    try {
      const params = new URLSearchParams();
      params.set('name', form.name);
      if (form.logoUrl) params.set('logoUrl', form.logoUrl);
      if (editId) {
        await api.put(`/brands/${editId}?${params}`);
        addToast('Brand updated!');
      } else {
        await api.post(`/brands?${params}`);
        addToast('Brand created!');
      }
      fetchBrands();
      setShowForm(false);
    } catch (err) {
      addToast(err.response?.data?.message || 'Failed to save brand', 'error');
    }
  };

  const deleteBrand = async (id) => {
    try {
      await api.delete(`/brands/${id}`);
      addToast('Brand deleted');
      fetchBrands();
    } catch (err) {
      addToast('Failed to delete brand', 'error');
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
              <h2>Brands</h2>
              <button className="btn-primary" onClick={() => openForm(null)}>+ Add Brand</button>
            </div>

            <div className="admin-card">
              <table className="admin-table">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Logo</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {brands.length === 0 ? (
                    <tr>
                      <td colSpan="4" style={{ textAlign: 'center', padding: '48px 20px', color: 'var(--text-muted)' }}>
                        <div style={{ fontSize: 40, marginBottom: 8, opacity: 0.4 }}>🏢</div>
                        No brands yet
                      </td>
                    </tr>
                  ) : (
                    brands.map(b => (
                      <tr key={b.id}>
                        <td>{b.id}</td>
                        <td><span className="cell-name">{b.name}</span></td>
                        <td>{b.logoUrl ? <a href={b.logoUrl} target="_blank" rel="noreferrer" style={{ color: 'var(--accent)', fontSize: 13, fontWeight: 500 }}>View</a> : '-'}</td>
                        <td>
                          <div className="actions-cell">
                            <button className="btn-icon" title="Edit" onClick={() => openForm(b)}>✏️</button>
                            <button className="btn-icon danger" title="Delete" onClick={() => deleteBrand(b.id)}>🗑️</button>
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
                    <h2>{editId ? 'Edit Brand' : 'Add Brand'}</h2>
                    <button className="admin-form-close" onClick={() => setShowForm(false)}>✕</button>
                  </div>
                  <div className="admin-form-body">
                    <form onSubmit={save}>
                      <div className="form-group">
                        <label>Name</label>
                        <input value={form.name} onChange={e => setForm({...form, name: e.target.value})} required />
                      </div>
                      <div className="form-group">
                        <label>Logo URL (optional)</label>
                        <input value={form.logoUrl} onChange={e => setForm({...form, logoUrl: e.target.value})} placeholder="https://example.com/logo.png" />
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
