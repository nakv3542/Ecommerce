import { useState, useEffect } from 'react';
import api from '../api/axios';
import { useToast } from '../components/Toast';
import AdminNav from '../components/AdminNav';
import Pagination from '../components/Pagination';

function Stars({ rating }) {
  return <span className="stars">{'★'.repeat(rating)}{'☆'.repeat(5 - rating)}</span>;
}

export default function AdminReviews() {
  const [reviews, setReviews] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [deleting, setDeleting] = useState(null);
  const { addToast } = useToast();

  const fetchReviews = () => {
    setLoading(true);
    api.get(`/reviews/all?page=${page}&size=15`)
      .then(r => {
        setReviews(r.data.content);
        setTotalPages(r.data.totalPages);
      })
      .catch(() => addToast('Failed to load reviews', 'error'))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchReviews();
  }, [page]);

  const handleDelete = async (id) => {
    if (!confirm('Delete this review?')) return;
    setDeleting(id);
    try {
      await api.delete(`/reviews/${id}`);
      setReviews(prev => prev.filter(r => r.id !== id));
      addToast('Review deleted', 'success');
    } catch {
      addToast('Failed to delete review', 'error');
    } finally {
      setDeleting(null);
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
              <h2>Reviews</h2>
            </div>

            <div className="admin-card">
              <table className="admin-table">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Product</th>
                    <th>User</th>
                    <th>Rating</th>
                    <th>Comment</th>
                    <th>Date</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {reviews.length === 0 ? (
                    <tr>
                      <td colSpan="7" style={{ textAlign: 'center', padding: '48px 20px', color: 'var(--text-muted)' }}>
                        <div style={{ fontSize: 40, marginBottom: 8, opacity: 0.4 }}>💬</div>
                        No reviews yet
                      </td>
                    </tr>
                  ) : (
                    reviews.map(r => (
                      <tr key={r.id}>
                        <td>{r.id}</td>
                        <td style={{ maxWidth: 200, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}><span className="cell-name">{r.productName}</span></td>
                        <td>{r.userName}</td>
                        <td><Stars rating={r.rating} /></td>
                        <td style={{ maxWidth: 300, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap', color: 'var(--text-muted)' }}>{r.comment || '-'}</td>
                        <td style={{ fontSize: 12, color: 'var(--text-muted)' }}>{r.createdAt ? new Date(r.createdAt).toLocaleDateString() : '-'}</td>
                        <td>
                          <button className="btn-sm danger" onClick={() => handleDelete(r.id)} disabled={deleting === r.id}>
                            {deleting === r.id ? 'Deleting...' : 'Delete'}
                          </button>
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
          </div>
        </div>
      </div>
    </div>
  );
}
