import { useState, useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import api from '../api/axios';
import { useToast } from '../components/Toast';
import Pagination from '../components/Pagination';

const statusColors = {
  PENDING: 'badge-warning', PLACED: 'badge-info', PROCESSING: 'badge-info',
  SHIPPED: 'badge-warning', DELIVERED: 'badge-success', CANCELLED: 'badge-danger',
  RETURNED: 'badge-danger'
};

const canCancel = (status) => status === 'PLACED' || status === 'PENDING';

export default function MyOrders() {
  const { id } = useParams();
  const { addToast } = useToast();
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [selectedOrder, setSelectedOrder] = useState(null);
  const [cancelling, setCancelling] = useState(false);

  useEffect(() => {
    if (id) {
      api.get(`/orders/${id}`).then(r => {
        setSelectedOrder(r.data);
      }).catch(() => {}).finally(() => setLoading(false));
    } else {
      api.get(`/orders/my?page=${page}&size=10`).then(r => {
        setOrders(r.data.content);
        setTotalPages(r.data.totalPages);
      }).catch(() => {}).finally(() => setLoading(false));
    }
  }, [id, page]);

  const cancelOrder = async (orderId) => {
    setCancelling(true);
    try {
      await api.post(`/orders/${orderId}/cancel`);
      addToast('Order cancelled successfully');
      if (selectedOrder) {
        setSelectedOrder({ ...selectedOrder, status: 'CANCELLED' });
      } else {
        setOrders(orders.map(o => o.id === orderId ? { ...o, status: 'CANCELLED' } : o));
      }
    } catch (err) {
      addToast(err.response?.data?.message || 'Failed to cancel order', 'error');
    } finally {
      setCancelling(false);
    }
  };

  if (loading) return <div className="loading"><div className="spinner"></div></div>;

  if (selectedOrder) {
    const o = selectedOrder;
    return (
      <div className="container">
        <div className="orders-page">
          <div style={{ display: 'flex', alignItems: 'center', gap: 16, marginBottom: 24 }}>
            <Link to="/orders" style={{ color: '#2563eb', fontSize: 14 }}>&larr; Back to Orders</Link>
            <h1 style={{ margin: 0 }}>Order #{o.id}</h1>
          </div>
          <div className="order-card">
            <div className="order-card-header">
              <span className="order-date">{new Date(o.createdAt).toLocaleDateString('en-US', { year: 'numeric', month: 'long', day: 'numeric' })}</span>
              <span className={`badge ${statusColors[o.status] || 'badge-info'}`}>{o.status}</span>
            </div>
            <div style={{ padding: '12px 20px', background: '#f9fafb', fontSize: 13, color: '#565959', borderBottom: '1px solid #eee' }}>
              {o.trackingNumber && <span>Tracking: <strong>{o.trackingNumber}</strong> &nbsp;|&nbsp; </span>}
              Payment: <strong>{o.paymentMethod?.replace(/_/g, ' ')}</strong>
              {o.paymentMethod === 'PAYPAL' && <span> &nbsp;|&nbsp; <span style={{ color: '#059669' }}>Paid via PayPal</span></span>}
            </div>
            <div className="order-card-body">
              {o.items?.map(item => (
                <div key={item.id} className="order-item">
                  <img src={item.productImage} alt={item.productName} />
                  <div>
                    <div className="item-name">
                      <Link to={`/products/${item.productId}`} style={{ color: '#131921' }}>{item.productName}</Link>
                    </div>
                    <div className="item-qty">Qty: {item.quantity}</div>
                  </div>
                  <div className="item-price">${item.price.toFixed(2)}</div>
                </div>
              ))}
            </div>
            <div className="order-card-footer">
              <div>
                <div style={{ fontSize: 13, color: '#565959' }}>Subtotal: ${o.subtotal.toFixed(2)}</div>
                {o.discount > 0 && <div style={{ fontSize: 13, color: '#059669' }}>Discount: -${o.discount.toFixed(2)}</div>}
                <div style={{ fontSize: 13, color: '#565959' }}>Shipping: {o.shippingCost === 0 ? 'FREE' : `$${o.shippingCost.toFixed(2)}`}</div>
                {o.couponCode && <div style={{ fontSize: 13, color: '#059669' }}>Coupon: {o.couponCode}</div>}
              </div>
              <div style={{ textAlign: 'right' }}>
                <div className="total" style={{ fontSize: 18, fontWeight: 700 }}>Total: ${o.totalPrice.toFixed(2)}</div>
                {canCancel(o.status) && (
                  <button className="btn" style={{ marginTop: 8, background: '#dc2626', color: '#fff', border: 'none', padding: '8px 20px', borderRadius: 6, fontWeight: 600, fontSize: 13 }}
                    onClick={() => cancelOrder(o.id)} disabled={cancelling}>
                    {cancelling ? 'Cancelling...' : 'Cancel Order'}
                  </button>
                )}
              </div>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="container">
      <div className="orders-page">
        <h1>My Orders</h1>
        {orders.length === 0 ? (
          <div className="loading">
            <p style={{ fontSize: 18, color: '#565959' }}>No orders yet</p>
            <Link to="/products" className="btn" style={{ display: 'inline-block', marginTop: 16, background: '#febd69', color: '#131921', padding: '12px 28px', borderRadius: 6, fontWeight: 700 }}>Start Shopping</Link>
          </div>
        ) : (
          <>
          {orders.map(order => (
            <div key={order.id} className="order-card">
              <div className="order-card-header">
                <Link to={`/orders/${order.id}`} className="order-id" style={{ textDecoration: 'none', color: '#2563eb' }}>Order #{order.id}</Link>
                <span className="order-date">{new Date(order.createdAt).toLocaleDateString('en-US', { year: 'numeric', month: 'long', day: 'numeric' })}</span>
                <span className={`badge ${statusColors[order.status] || 'badge-info'}`}>{order.status}</span>
              </div>
              <div className="order-card-body">
                {order.items?.map(item => (
                  <div key={item.id} className="order-item">
                    <img src={item.productImage} alt={item.productName} />
                    <div>
                      <div className="item-name">
                        <Link to={`/products/${item.productId}`} style={{ color: '#131921' }}>{item.productName}</Link>
                      </div>
                      <div className="item-qty">Qty: {item.quantity}</div>
                    </div>
                    <div className="item-price">${item.price.toFixed(2)}</div>
                  </div>
                ))}
              </div>
              <div className="order-card-footer">
                <div>
                  <span className="tracking">{order.trackingNumber ? `Tracking: ${order.trackingNumber}` : ''}</span>
                  {order.couponCode && <span style={{ fontSize: 12, color: '#059669', marginLeft: 16 }}>Coupon: {order.couponCode}</span>}
                </div>
                <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
                  <span className="total">Total: ${order.totalPrice.toFixed(2)}</span>
                  {canCancel(order.status) && (
                    <button className="btn" style={{ background: '#dc2626', color: '#fff', border: 'none', padding: '6px 14px', borderRadius: 4, fontWeight: 600, fontSize: 12 }}
                      onClick={() => cancelOrder(order.id)} disabled={cancelling}>
                      {cancelling ? '...' : 'Cancel'}
                    </button>
                  )}
                </div>
              </div>
            </div>
          ))}
          {totalPages > 1 && (
            <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />
          )}
          </>
        )}
      </div>
    </div>
  );
}
