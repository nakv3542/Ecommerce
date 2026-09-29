import { useState, useEffect } from 'react';
import api from '../api/axios';
import { useToast } from '../components/Toast';
import AdminNav from '../components/AdminNav';
import Pagination from '../components/Pagination';

const statusColors = {
  PENDING: 'badge-warning', PLACED: 'badge-info', PROCESSING: 'badge-info',
  SHIPPED: 'badge-warning', DELIVERED: 'badge-success', CANCELLED: 'badge-danger',
  RETURNED: 'badge-danger'
};

const allStatuses = ['PENDING', 'PLACED', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED', 'RETURNED'];

export default function AdminOrders() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const { addToast } = useToast();

  const fetchOrders = () => {
    api.get(`/orders/all?page=${page}&size=15`).then(r => {
      setOrders(r.data.content);
      setTotalPages(r.data.totalPages);
    }).catch(() => {}).finally(() => setLoading(false));
  };

  useEffect(() => { fetchOrders(); }, [page]);

  const updateStatus = async (orderId, status) => {
    try {
      await api.put(`/orders/${orderId}/status?status=${status}`);
      addToast(`Order #${orderId} updated to ${status}`);
      fetchOrders();
    } catch (err) {
      addToast(err.response?.data?.message || 'Failed to update status', 'error');
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
              <h2>Orders ({orders.length})</h2>
            </div>

            <div className="admin-card">
              <table className="admin-table">
                <thead>
                  <tr>
                    <th>Order ID</th>
                    <th>Customer</th>
                    <th>Items</th>
                    <th>Total</th>
                    <th>Status</th>
                    <th>Date</th>
                    <th>Payment</th>
                    <th>Tracking</th>
                    <th>Update</th>
                  </tr>
                </thead>
                <tbody>
                  {orders.map(o => (
                    <tr key={o.id}>
                      <td><span className="cell-name">#{o.id}</span></td>
                      <td>
                        <span className="cell-name">{o.userName}</span><br />
                        <small style={{ color: 'var(--text-muted)', fontSize: 12 }}>{o.userEmail}</small>
                      </td>
                      <td>{o.items?.length || 0}</td>
                      <td>${o.totalPrice.toFixed(2)}</td>
                      <td><span className={`badge ${statusColors[o.status] || 'badge-info'}`}>{o.status}</span></td>
                      <td style={{ fontSize: 12, color: 'var(--text-muted)' }}>{new Date(o.createdAt).toLocaleDateString()}</td>
                      <td style={{ fontSize: 12 }}>{o.paymentMethod?.replace(/_/g, ' ')}</td>
                      <td style={{ fontSize: 12, color: 'var(--text-muted)' }}>{o.trackingNumber || '-'}</td>
                      <td>
                        <select className="status-select" value={o.status} onChange={e => updateStatus(o.id, e.target.value)}>
                          {allStatuses.map(s => (
                            <option key={s} value={s}>{s}</option>
                          ))}
                        </select>
                      </td>
                    </tr>
                  ))}
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
