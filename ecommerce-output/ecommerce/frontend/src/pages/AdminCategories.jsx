import { useState, useEffect } from 'react';
import api from '../api/axios';
import { useToast } from '../components/Toast';
import AdminNav from '../components/AdminNav';
import Pagination from '../components/Pagination';

export default function AdminCategories() {
  const [categories, setCategories] = useState([]);
  const [allCategories, setAllCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [showForm, setShowForm] = useState(false);
  const [editId, setEditId] = useState(null);
  const [form, setForm] = useState({ name: '', slug: '', parentId: '' });
  const { addToast } = useToast();

  const fetchCategories = () => {
    setLoading(true);
    api.get(`/categories?page=${page}&size=15`).then(r => {
      setCategories(r.data.content);
      setTotalPages(r.data.totalPages);
    }).catch(() => {}).finally(() => setLoading(false));
  };

  const fetchAllCategories = () => {
    api.get('/categories').then(r => setAllCategories(r.data)).catch(() => {});
  };

  useEffect(() => {
    fetchAllCategories();
  }, []);

  useEffect(() => { fetchCategories(); }, [page]);

  const openForm = (cat) => {
    if (cat) {
      setForm({ name: cat.name, slug: cat.slug, parentId: cat.parentId || '' });
      setEditId(cat.id);
    } else {
      setForm({ name: '', slug: '', parentId: '' });
      setEditId(null);
    }
    setShowForm(true);
  };

  const save = async (e) => {
    e.preventDefault();
    try {
      const params = new URLSearchParams();
      params.set('name', form.name);
      params.set('slug', form.slug || form.name.toLowerCase().replace(/\s+/g, '-'));
      if (form.parentId) params.set('parentId', form.parentId);
      if (editId) {
        await api.put(`/categories/${editId}?${params}`);
        addToast('Category updated!');
      } else {
        await api.post(`/categories?${params}`);
        addToast('Category created!');
      }
      fetchCategories();
      fetchAllCategories();
      setShowForm(false);
    } catch (err) {
      addToast(err.response?.data?.message || 'Failed to save category', 'error');
    }
  };

  const deleteCat = async (id) => {
    try {
      await api.delete(`/categories/${id}`);
      addToast('Category deleted');
      fetchCategories();
      fetchAllCategories();
    } catch (err) {
      addToast('Failed to delete category', 'error');
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
              <h2>Categories</h2>
              <button className="btn-primary" onClick={() => openForm(null)}>+ Add Category</button>
            </div>

            <div className="admin-card">
              <table className="admin-table">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Slug</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {categories.length === 0 ? (
                    <tr>
                      <td colSpan="4" style={{ textAlign: 'center', padding: '48px 20px', color: 'var(--text-muted)' }}>
                        <div style={{ fontSize: 40, marginBottom: 8, opacity: 0.4 }}>🏷️</div>
                        No categories yet
                      </td>
                    </tr>
                  ) : (
                    categories.map(cat => (
                      <tr key={cat.id}>
                        <td>{cat.id}</td>
                        <td>
                          <span className="cell-name">{cat.name}</span>
                          {cat.children?.length > 0 && <span className="badge badge-info" style={{ marginLeft: 8 }}>{cat.children.length} sub</span>}
                        </td>
                        <td style={{ color: 'var(--text-muted)', fontSize: 12 }}>{cat.slug}</td>
                        <td>
                          <div className="actions-cell">
                            <button className="btn-icon" title="Edit" onClick={() => openForm(cat)}>✏️</button>
                            <button className="btn-icon danger" title="Delete" onClick={() => deleteCat(cat.id)}>🗑️</button>
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
                    <h2>{editId ? 'Edit Category' : 'Add Category'}</h2>
                    <button className="admin-form-close" onClick={() => setShowForm(false)}>✕</button>
                  </div>
                  <div className="admin-form-body">
                    <form onSubmit={save}>
                      <div className="form-group">
                        <label>Name</label>
                        <input value={form.name} onChange={e => setForm({...form, name: e.target.value, slug: editId ? form.slug : e.target.value.toLowerCase().replace(/\s+/g, '-')})} required />
                      </div>
                      <div className="form-group">
                        <label>Slug</label>
                        <input value={form.slug} onChange={e => setForm({...form, slug: e.target.value})} required />
                      </div>
                      <div className="form-group">
                        <label>Parent Category (optional)</label>
                        <select value={form.parentId} onChange={e => setForm({...form, parentId: e.target.value})}>
                          <option value="">None (root category)</option>
                          {allCategories.filter(c => c.id !== editId).map(cat => (
                            <option key={cat.id} value={cat.id}>{cat.name}</option>
                          ))}
                        </select>
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
