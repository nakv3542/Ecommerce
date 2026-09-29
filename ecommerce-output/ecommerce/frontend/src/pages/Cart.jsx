import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import api from '../api/axios';
import { useToast } from '../components/Toast';

const paymentMethods = [
  { value: 'CASH_ON_DELIVERY', label: 'Cash on Delivery', icon: '💵', desc: 'Pay when you receive your order' },
  { value: 'STRIPE', label: 'Credit / Debit Card', icon: '💳', desc: 'Pay securely via Stripe' },
  { value: 'PAYPAL', label: 'PayPal', icon: '🅿️', desc: 'Pay with your PayPal account' },
  { value: 'BAKONG', label: 'Bakong QR', icon: '📱', desc: 'Scan QR with Bakong app' },
];

export default function Cart() {
  const navigate = useNavigate();
  const { addToast } = useToast();
  const [cart, setCart] = useState(null);
  const [loading, setLoading] = useState(true);
  const [couponCode, setCouponCode] = useState('');
  const [coupon, setCoupon] = useState(null);
  const [couponMsg, setCouponMsg] = useState('');
  const [couponError, setCouponError] = useState('');
  const [selectedPayment, setSelectedPayment] = useState('CASH_ON_DELIVERY');

  const fetchCart = () => {
    api.get('/cart').then(r => setCart(r.data)).catch(() => {}).finally(() => setLoading(false));
  };

  useEffect(() => { fetchCart(); }, []);

  const updateQty = async (itemId, newQty) => {
    if (newQty < 1) return;
    try {
      await api.put(`/cart/update/${itemId}?quantity=${newQty}`);
      fetchCart();
    } catch (err) {
      addToast(err.response?.data?.message || 'Failed to update quantity', 'error');
    }
  };

  const removeItem = async (itemId) => {
    try {
      await api.delete(`/cart/remove/${itemId}`);
      addToast('Item removed from cart');
      fetchCart();
      setCoupon(null);
      setCouponMsg('');
    } catch (err) {
      addToast('Failed to remove item', 'error');
    }
  };

  const subtotal = cart?.items?.reduce((sum, item) =>
    sum + ((item.product?.discountPrice || item.product?.price) * item.quantity), 0) || 0;

  const discount = coupon
    ? (coupon.type === 'PERCENTAGE' ? subtotal * (coupon.value / 100) : Math.min(coupon.value, subtotal))
    : 0;
  const shipping = subtotal >= 100 ? 0 : 9.99;
  const total = Math.max(0, subtotal - discount + shipping);

  const applyCoupon = async () => {
    if (!couponCode.trim()) return;
    setCouponMsg('');
    setCouponError('');
    try {
      const res = await api.post('/coupons/validate', { code: couponCode.trim(), orderTotal: subtotal });
      setCoupon(res.data);
      setCouponMsg(`Coupon applied!`);
      addToast('Coupon applied!');
    } catch (err) {
      setCouponError(err.response?.data?.error || 'Invalid coupon');
      setCoupon(null);
    }
  };

  if (loading) return <div className="loading"><div className="spinner"></div></div>;

  return (
    <div className="container">
      <div className="cart-page">
        <h1>Shopping Cart ({cart?.items?.length || 0} items)</h1>

        {!cart?.items?.length ? (
          <div className="cart-empty">
            <div className="icon">🛒</div>
            <p>Your cart is empty</p>
            <Link to="/products" className="btn">Shop Now</Link>
          </div>
        ) : (
          <>
            <div className="cart-items">
              {cart.items.map(item => (
                <div key={item.id} className="cart-item">
                  <img src={item.product?.images?.[0] || 'https://picsum.photos/200'} alt={item.product?.name} />
                  <div className="item-info">
                    <Link to={`/products/${item.product?.id}`} className="name">{item.product?.name}</Link>
                    <div className="price">${(item.product?.discountPrice || item.product?.price)} each</div>
                    <div className="stock">In Stock</div>
                  </div>
                  <div className="item-actions">
                    <button className="qty-btn" onClick={() => updateQty(item.id, item.quantity - 1)}>-</button>
                    <span className="qty-val">{item.quantity}</span>
                    <button className="qty-btn" onClick={() => updateQty(item.id, item.quantity + 1)}>+</button>
                    <span className="item-total">${((item.product?.discountPrice || item.product?.price) * item.quantity).toFixed(2)}</span>
                    <button className="btn-remove" onClick={() => removeItem(item.id)}>Delete</button>
                  </div>
                </div>
              ))}
            </div>

            <div className="cart-summary">
              <h2>Order Summary</h2>
              <div className="row"><span>Subtotal</span><span>${subtotal.toFixed(2)}</span></div>
              {discount > 0 && <div className="row discount"><span>Discount</span><span>-${discount.toFixed(2)}</span></div>}
              <div className="row"><span>Shipping</span><span>{shipping === 0 ? 'FREE' : `$${shipping.toFixed(2)}`}</span></div>
              <div className="row total"><span>Total</span><span>${total.toFixed(2)}</span></div>

              <div className="coupon-input">
                <input type="text" placeholder="Coupon code" value={couponCode}
                  onChange={e => setCouponCode(e.target.value.toUpperCase())} />
                <button onClick={applyCoupon}>Apply</button>
              </div>
              {couponMsg && <div className="coupon-msg success">{couponMsg}</div>}
              {couponError && <div className="coupon-msg error">{couponError}</div>}

              <div className="cart-payment-methods">
                <h3>Payment Method</h3>
                {paymentMethods.map(pm => (
                  <label key={pm.value} className={`payment-option ${selectedPayment === pm.value ? 'selected' : ''}`}>
                    <input type="radio" name="payment" value={pm.value}
                      checked={selectedPayment === pm.value}
                      onChange={() => setSelectedPayment(pm.value)} />
                    <span className="payment-icon">{pm.icon}</span>
                    <span className="payment-info">
                      <span className="payment-label">{pm.label}</span>
                      <span className="payment-desc">{pm.desc}</span>
                    </span>
                  </label>
                ))}
              </div>

              <button className="btn-checkout" onClick={() => {
                if (cart?.items?.length === 0) { addToast('Your cart is empty', 'error'); return; }
                navigate('/checkout', { state: { paymentMethod: selectedPayment } });
              }}>
                Proceed to Checkout
              </button>
            </div>
          </>
        )}
      </div>
    </div>
  );
}
