import { useState, useEffect } from 'react';
import api from '../api/axios';
import { useToast } from '../components/Toast';
import AdminNav from '../components/AdminNav';
import Pagination from '../components/Pagination';

export default function AdminProducts() {
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [brands, setBrands] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [showForm, setShowForm] = useState(false);
  const [editId, setEditId] = useState(null);
  const [form, setForm] = useState({
    name: '', description: '', price: '', discountPrice: '', stock: '', sku: '',
    featured: false, brandId: '', categoryIds: [], imageUrl: ''
  });
  const { addToast } = useToast();

  const fetchProducts = () => {
    api.get(`/products?page=${page}&size=15`).then(r => {
      setProducts(r.data.content);
      setTotalPages(r.data.totalPages);
    }).catch(() => {}).finally(() => setLoading(false));
  };

  useEffect(() => {
    api.get('/categories').then(r => setCategories(r.data)).catch(() => {});
    api.get('/brands').then(r => setBrands(r.data)).catch(() => {});
  }, []);

  useEffect(() => { fetchProducts(); }, [page]);

  const openForm = (p) => {
    if (p) {
      setForm({
        name: p.name, description: p.description || '',
        price: p.price, discountPrice: p.discountPrice || '',
        stock: p.stock, sku: p.sku || '', featured: p.featured,
        brandId: p.brand?.id || '',
        categoryIds: p.categories?.map(c => c.id) || [],
        imageUrl: ''
      });
      setEditId(p.id);
    } else {
      setForm({ name: '', description: '', price: '', discountPrice: '', stock: '', sku: '', featured: false, brandId: '', categoryIds: [], imageUrl: '' });
      setEditId(null);
    }
    setShowForm(true);
  };

  const save = async (e) => {
    e.preventDefault();
    try {
      const body = {
        name: form.name,
        description: form.description,
        price: parseFloat(form.price),
        discountPrice: form.discountPrice ? parseFloat(form.discountPrice) : 0,
        stock: parseInt(form.stock),
        sku: form.sku || ('SKU-' + form.name.substring(0, 3).toUpperCase() + '-' + Math.floor(Math.random() * 10000)),
        featured: form.featured,
        brand: form.brandId ? { id: parseInt(form.brandId) } : null,
        categories: form.categoryIds.map(id => ({ id: parseInt(id) }))
      };
      if (editId) {
        await api.put(`/products/${editId}`, body);
        addToast('Product updated!');
      } else {
        const res = await api.post('/products', body);
        if (form.imageUrl) {
          await api.post(`/products/${res.data.id}/images?imageUrl=${encodeURIComponent(form.imageUrl)}&isPrimary=true`);
        }
        addToast('Product created!');
      }
      fetchProducts();
      setShowForm(false);
    } catch (err) {
      addToast(err.response?.data?.message || 'Failed to save product', 'error');
    }
  };

  const deleteProduct = async (id) => {
    try {
      await api.delete(`/products/${id}`);
      addToast('Product deleted');
      fetchProducts();
    } catch (err) {
      addToast('Failed to delete product', 'error');
    }
  };

  const toggleCategory = (catId) => {
    setForm(prev => ({
      ...prev,
      categoryIds: prev.categoryIds.includes(catId)
        ? prev.categoryIds.filter(id => id !== catId)
        : [...prev.categoryIds, catId]
    }));
  };

  if (loading) return <div className="loading"><div className="spinner"></div></div>;

  return (
    <div className="admin-page">
      <div className="container">
        <div className="admin-layout">
          <AdminNav />
          <div className="admin-content">
            <div className="admin-header">
              <h2>Products ({products.length})</h2>
              <button className="btn-primary" onClick={() => openForm(null)}>+ Add Product</button>
            </div>

            <div className="admin-card">
              <table className="admin-table">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Price</th>
                    <th>Stock</th>
                    <th>Brand</th>
                    <th>Featured</th>
                    <th>Rating</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {products.map(p => (
                    <tr key={p.id}>
                      <td>{p.id}</td>
                      <td><span className="cell-name">{p.name}</span></td>
                      <td>${p.discountPrice || p.price}</td>
                      <td>{p.stock}</td>
                      <td>{p.brand?.name || '-'}</td>
                      <td>{p.featured ? <span className="badge badge-success">Yes</span> : <span className="badge badge-neutral">No</span>}</td>
                      <td>{p.averageRating} ({p.reviewCount})</td>
                      <td>
                        <div className="actions-cell">
                          <button className="btn-icon" title="Edit" onClick={() => openForm(p)}>✏️</button>
                          <button className="btn-icon danger" title="Delete" onClick={() => deleteProduct(p.id)}>🗑️</button>
                        </div>
                      </td>
                    </tr>
                  ))}
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
                    <h2>{editId ? 'Edit Product' : 'Add Product'}</h2>
                    <button className="admin-form-close" onClick={() => setShowForm(false)}>✕</button>
                  </div>
                  <div className="admin-form-body">
                    <form onSubmit={save}>
                      <div className="form-group">
                        <label>Name</label>
                        <input value={form.name} onChange={e => setForm({...form, name: e.target.value})} required />
                      </div>
                      <div className="form-group">
                        <label>Description</label>
                        <textarea rows={3} value={form.description} onChange={e => setForm({...form, description: e.target.value})} />
                      </div>
                      <div className="form-row">
                        <div className="form-group">
                          <label>Price</label>
                          <input type="number" step="0.01" value={form.price} onChange={e => setForm({...form, price: e.target.value})} required />
                        </div>
                        <div className="form-group">
                          <label>Discount Price</label>
                          <input type="number" step="0.01" value={form.discountPrice} onChange={e => setForm({...form, discountPrice: e.target.value})} placeholder="0 = no discount" />
                        </div>
                      </div>
                      <div className="form-row">
                        <div className="form-group">
                          <label>Stock</label>
                          <input type="number" value={form.stock} onChange={e => setForm({...form, stock: e.target.value})} required />
                        </div>
                        <div className="form-group">
                          <label>SKU</label>
                          <input value={form.sku} onChange={e => setForm({...form, sku: e.target.value})} placeholder="Auto-generated if empty" />
                        </div>
                      </div>
                      <div className="form-row">
                        <div className="form-group">
                          <label>Featured</label>
                          <select value={form.featured} onChange={e => setForm({...form, featured: e.target.value === 'true'})}>
                            <option value="false">No</option>
                            <option value="true">Yes</option>
                          </select>
                        </div>
                        <div className="form-group">
                          <label>Brand</label>
                          <select value={form.brandId} onChange={e => setForm({...form, brandId: e.target.value})}>
                            <option value="">No brand</option>
                            {brands.map(b => (
                              <option key={b.id} value={b.id}>{b.name}</option>
                            ))}
                          </select>
                        </div>
                      </div>
                      <div className="form-group">
                        <label>Image URL (new products only)</label>
                        <input value={form.imageUrl} onChange={e => setForm({...form, imageUrl: e.target.value})} placeholder="https://picsum.photos/seed/.../400/400" />
                      </div>
                      <div className="form-group">
                        <label>Categories</label>
                        <div className="checkbox-group">
                          {categories.map(cat => (
                            <label key={cat.id} className={`checkbox-item ${form.categoryIds.includes(cat.id) ? 'checked' : ''}`}>
                              <input type="checkbox" checked={form.categoryIds.includes(cat.id)}
                                onChange={() => toggleCategory(cat.id)} />
                              {cat.name}
                            </label>
                          ))}
                        </div>
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
