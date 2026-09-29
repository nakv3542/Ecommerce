import { useState, useEffect, useMemo } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { PayPalButtons } from '@paypal/react-paypal-js';
import { Elements, PaymentElement, useStripe, useElements } from '@stripe/react-stripe-js';
import { loadStripe } from '@stripe/stripe-js';
import api from '../api/axios';
import { useToast } from '../components/Toast';

export default function Checkout() {
  const navigate = useNavigate();
  const location = useLocation();
  const { addToast } = useToast();
  const [step, setStep] = useState(1);
  const [cart, setCart] = useState(null);
  const [addresses, setAddresses] = useState([]);
  const [selectedAddress, setSelectedAddress] = useState(null);
  const [paymentMethod, setPaymentMethod] = useState(location.state?.paymentMethod || 'CASH_ON_DELIVERY');
  const [loading, setLoading] = useState(true);
  const [placing, setPlacing] = useState(false);
  const [couponCode, setCouponCode] = useState('');
  const [appliedCoupon, setAppliedCoupon] = useState(null);
  const [couponError, setCouponError] = useState('');
  const [couponLoading, setCouponLoading] = useState(false);
  const [paypalError, setPaypalError] = useState('');
  const [qrCode, setQrCode] = useState(null);
  const [qrLoading, setQrLoading] = useState(false);
  const [qrError, setQrError] = useState('');
  const [stripePk, setStripePk] = useState(null);
  const [stripeClientSecret, setStripeClientSecret] = useState(null);
  const [stripeLoading, setStripeLoading] = useState(false);
  const [stripeError, setStripeError] = useState('');
  const [addressForm, setAddressForm] = useState({
    fullName: '', phone: '', street: '', city: '', state: '', zipCode: '', country: 'US', isDefault: false
  });
  const [showAddressForm, setShowAddressForm] = useState(false);

  const stripePromise = useMemo(() => stripePk ? loadStripe(stripePk) : null, [stripePk]);

  useEffect(() => {
    Promise.all([
      api.get('/cart'),
      api.get('/addresses'),
    ]).then(([c, a]) => {
      setCart(c.data);
      setAddresses(a.data);
      const def = a.data.find(ad => ad.default) || a.data[0];
      if (def) setSelectedAddress(def.id);
    }).catch(() => navigate('/cart'))
    .finally(() => setLoading(false));
  }, []);

  useEffect(() => {
    if (step === 3 && paymentMethod === 'BAKONG' && !qrCode) {
      setQrLoading(true);
      setQrError('');
      api.post('/payments/create-bakong-qr')
        .then(res => setQrCode(res.data.qrCode))
        .catch(err => setQrError(err.response?.data?.message || 'Failed to generate QR code'))
        .finally(() => setQrLoading(false));
    }
  }, [step, paymentMethod]);

  useEffect(() => {
    if (paymentMethod === 'STRIPE') {
      if (!stripePk) {
        api.get('/payments/stripe-publishable-key')
          .then(res => setStripePk(res.data.publishableKey))
          .catch(() => setStripeError('Failed to load payment configuration'));
      }
      if (step === 3 && !stripeClientSecret && !stripeLoading) {
        setStripeLoading(true);
        setStripeError('');
        api.post('/payments/create-stripe-payment-intent')
          .then(res => {
            setStripeClientSecret(res.data.clientSecret);
          })
          .catch(err => {
            const msg = err.response?.data?.message || err.message || 'Failed to initialize payment';
            setStripeError(msg);
            console.error('Stripe payment intent error:', msg);
          })
          .finally(() => setStripeLoading(false));
      }
    }
  }, [paymentMethod, step, stripePk, stripeClientSecret, stripeLoading]);

  const subtotal = cart?.items?.reduce((sum, item) =>
    sum + ((item.product?.discountPrice || item.product?.price) * item.quantity), 0) || 0;
  const shipping = subtotal >= 100 ? 0 : 9.99;
  const discount = appliedCoupon?.discountAmount || 0;
  const total = Math.max(0, subtotal - discount + shipping);

  const applyCoupon = async () => {
    if (!couponCode.trim()) return;
    setCouponLoading(true);
    setCouponError('');
    try {
      const res = await api.post('/coupons/validate', { code: couponCode.trim(), orderTotal: subtotal });
      setAppliedCoupon(res.data);
      addToast('Coupon applied!');
    } catch (err) {
      setCouponError(err.response?.data?.message || 'Invalid coupon');
      setAppliedCoupon(null);
    } finally {
      setCouponLoading(false);
    }
  };

  const saveAddress = async (e) => {
    e.preventDefault();
    try {
      const res = await api.post('/addresses', addressForm);
      setAddresses([...addresses, res.data]);
      setSelectedAddress(res.data.id);
      setShowAddressForm(false);
      setAddressForm({ fullName: '', phone: '', street: '', city: '', state: '', zipCode: '', country: 'US', isDefault: false });
      addToast('Address saved!');
    } catch (err) {
      addToast(err.response?.data?.message || 'Failed to save address', 'error');
    }
  };

  const placeOrder = async () => {
    if (!selectedAddress) {
      addToast('Please select a shipping address', 'error');
      return;
    }
    if (paymentMethod === 'CREDIT_CARD' || paymentMethod === 'DEBIT_CARD') {
      addToast('Card payments are not available yet. Please use Cash on Delivery or PayPal.', 'error');
      return;
    }
    setPlacing(true);
    try {
      const params = new URLSearchParams();
      if (selectedAddress) params.set('addressId', selectedAddress);
      params.set('paymentMethod', paymentMethod);
      if (appliedCoupon) params.set('couponCode', couponCode.trim());
      await api.post(`/orders/place?${params}`);
      addToast('Order placed successfully!');
      navigate('/orders');
    } catch (err) {
      addToast(err.response?.data?.message || 'Failed to place order', 'error');
      setPlacing(false);
    }
  };

  const createPayPalOrder = async () => {
    try {
      const res = await api.post('/payments/create-paypal-order');
      return res.data.id;
    } catch (err) {
      setPaypalError('Failed to create PayPal order. Please try Cash on Delivery instead.');
      throw new Error(err.response?.data?.message || 'PayPal error');
    }
  };

  const onPayPalApprove = async (data) => {
    setPlacing(true);
    try {
      const params = new URLSearchParams();
      params.set('paypalOrderId', data.orderID);
      if (selectedAddress) params.set('addressId', selectedAddress);
      if (appliedCoupon) params.set('couponCode', couponCode.trim());
      params.set('paymentMethod', 'PAYPAL');
      await api.post(`/payments/capture-paypal-order?${params}`);
      addToast('Payment successful! Order placed.');
      navigate('/orders');
    } catch (err) {
      setPaypalError('Payment was approved but order placement failed. Please contact support.');
      setPlacing(false);
    }
  };

  if (loading) return <div className="loading"><div className="spinner"></div></div>;
  if (!cart?.items?.length) { navigate('/cart'); return null; }

  return (
    <div className="container">
      <div className="checkout-page">
        <h1>Checkout</h1>

        <div className="checkout-steps">
          <div className={`step ${step >= 1 ? step === 1 ? 'active' : 'completed' : ''}`}>
            <span className="num">{step > 1 ? '✓' : '1'}</span> Shipping
          </div>
          <div className={`step ${step >= 2 ? step === 2 ? 'active' : 'completed' : ''}`}>
            <span className="num">{step > 2 ? '✓' : '2'}</span> Payment
          </div>
          <div className={`step ${step >= 3 ? step === 3 ? 'active' : 'completed' : ''}`}>
            <span className="num">3</span> Review & Order
          </div>
        </div>

        {step === 1 && (
          <div className="checkout-form">
            <h2>Shipping Address</h2>
            {addresses.length > 0 ? addresses.map(a => (
              <div key={a.id} className={`address-card ${selectedAddress === a.id ? 'selected' : ''}`}
                onClick={() => setSelectedAddress(a.id)}>
                <div className="name">{a.fullName}</div>
                <div className="detail">{a.street}, {a.city}, {a.state} {a.zipCode}, {a.country}</div>
                <div className="detail">{a.phone}</div>
                {a.default && <span className="default-badge">Default</span>}
              </div>
            )) : (
              <p style={{ color: '#999', fontSize: 14, marginBottom: 12 }}>No saved addresses. Please add one below.</p>
            )}
            {showAddressForm ? (
              <form onSubmit={saveAddress}>
                <div className="form-row">
                  <div className="form-group">
                    <label>Full Name</label>
                    <input required value={addressForm.fullName} onChange={e => setAddressForm({...addressForm, fullName: e.target.value})} />
                  </div>
                  <div className="form-group">
                    <label>Phone</label>
                    <input required value={addressForm.phone} onChange={e => setAddressForm({...addressForm, phone: e.target.value})} />
                  </div>
                </div>
                <div className="form-group">
                  <label>Street Address</label>
                  <input required value={addressForm.street} onChange={e => setAddressForm({...addressForm, street: e.target.value})} />
                </div>
                <div className="form-row">
                  <div className="form-group">
                    <label>City</label>
                    <input required value={addressForm.city} onChange={e => setAddressForm({...addressForm, city: e.target.value})} />
                  </div>
                  <div className="form-group">
                    <label>State</label>
                    <input required value={addressForm.state} onChange={e => setAddressForm({...addressForm, state: e.target.value})} />
                  </div>
                </div>
                <div className="form-row">
                  <div className="form-group">
                    <label>ZIP Code</label>
                    <input required value={addressForm.zipCode} onChange={e => setAddressForm({...addressForm, zipCode: e.target.value})} />
                  </div>
                  <div className="form-group">
                    <label>Country</label>
                    <input value={addressForm.country} onChange={e => setAddressForm({...addressForm, country: e.target.value})} />
                  </div>
                </div>
                <div style={{ display: 'flex', gap: 12, marginTop: 12 }}>
                  <button type="submit" className="btn" style={{ background: '#2563eb', color: '#fff', border: 'none', padding: '10px 24px', borderRadius: 6, fontWeight: 600 }}>Save Address</button>
                  <button type="button" className="btn" style={{ background: '#f0f0f0', border: 'none', padding: '10px 24px', borderRadius: 6 }} onClick={() => setShowAddressForm(false)}>Cancel</button>
                </div>
              </form>
            ) : (
              <button className="btn" style={{ background: '#f0f0f0', border: 'none', padding: '10px 20px', borderRadius: 6, fontWeight: 600, marginTop: 8 }}
                onClick={() => setShowAddressForm(true)}>+ Add New Address</button>
            )}
            <div style={{ marginTop: 24 }}>
              <button className="btn" style={{ background: '#febd69', color: '#131921', border: 'none', padding: '12px 32px', borderRadius: 6, fontWeight: 700, fontSize: 15 }}
                onClick={() => {
                  if (!selectedAddress) { addToast('Please select a shipping address', 'error'); return; }
                  setStep(2);
                }}>Continue to Payment</button>
            </div>
          </div>
        )}

        {step === 2 && (
          <div className="checkout-form">
            <h2>Payment Method</h2>
            <div className="form-group">
              <select value={paymentMethod} onChange={e => setPaymentMethod(e.target.value)}
                style={{ width: '100%', padding: 12, border: '1px solid #ddd', borderRadius: 6, fontSize: 14 }}>
                <option value="CASH_ON_DELIVERY">Cash on Delivery</option>
                <option value="STRIPE">Credit / Debit Card (Stripe)</option>
                <option value="PAYPAL">PayPal</option>
                <option value="BAKONG">Bakong QR</option>
              </select>
              {paymentMethod === 'CASH_ON_DELIVERY' && (
                <p style={{ fontSize: 13, color: '#565959', marginTop: 8 }}>Pay when you receive your order. No extra fees.</p>
              )}
              {paymentMethod === 'STRIPE' && (
                <p style={{ fontSize: 13, color: '#565959', marginTop: 8 }}>Pay securely with your credit or debit card via Stripe.</p>
              )}
              {paymentMethod === 'PAYPAL' && (
                <p style={{ fontSize: 13, color: '#565959', marginTop: 8 }}>Pay securely with your PayPal account.</p>
              )}
              {paymentMethod === 'BAKONG' && (
                <p style={{ fontSize: 13, color: '#565959', marginTop: 8 }}>Pay using Bakong KHQR. Scan the QR code with your Bakong app.</p>
              )}
            </div>
            <div style={{ display: 'flex', gap: 12, marginTop: 24 }}>
              <button className="btn" style={{ background: '#f0f0f0', border: 'none', padding: '12px 24px', borderRadius: 6, fontWeight: 600 }}
                onClick={() => setStep(1)}>Back</button>
              <button className="btn" style={{ background: '#febd69', color: '#131921', border: 'none', padding: '12px 32px', borderRadius: 6, fontWeight: 700, fontSize: 15 }}
                onClick={() => setStep(3)}>Continue to Review</button>
            </div>
          </div>
        )}

        {step === 3 && (
          <div>
            <div className="checkout-review">
              <h3>Order Summary</h3>
              {cart.items.map(item => (
                <div key={item.id} className="review-item">
                  <span>{item.product?.name} × {item.quantity}</span>
                  <span>${((item.product?.discountPrice || item.product?.price) * item.quantity).toFixed(2)}</span>
                </div>
              ))}
              <div className="review-item"><span>Subtotal</span><span>${subtotal.toFixed(2)}</span></div>
              <div className="review-item"><span>Shipping</span><span>{shipping === 0 ? 'FREE' : `$${shipping.toFixed(2)}`}</span></div>
              {appliedCoupon && (
                <div className="review-item" style={{ color: '#059669' }}>
                  <span>Coupon: {couponCode.toUpperCase()}</span>
                  <span>-${discount.toFixed(2)}</span>
                </div>
              )}
              <div className="review-item review-total"><span>Total</span><span>${total.toFixed(2)}</span></div>
              <p style={{ fontSize: 13, color: '#565959', marginTop: 8 }}>Payment: {paymentMethod === 'PAYPAL' ? 'PayPal' : paymentMethod === 'BAKONG' ? 'Bakong QR' : paymentMethod === 'STRIPE' ? 'Credit / Debit Card (Stripe)' : 'Cash on Delivery'}</p>
              <p style={{ fontSize: 13, color: '#565959' }}>Shipping to: {addresses.find(a => a.id === selectedAddress)?.fullName || 'Selected address'}</p>

              <div className="coupon-section" style={{ margin: '16px 0', padding: 12, background: '#f9fafb', borderRadius: 6 }}>
                <label style={{ fontWeight: 600, fontSize: 14 }}>Have a coupon?</label>
                <div style={{ display: 'flex', gap: 8, marginTop: 6 }}>
                  <input value={couponCode} onChange={e => setCouponCode(e.target.value)}
                    placeholder="Enter coupon code" style={{ flex: 1, padding: '8px 12px', border: '1px solid #ddd', borderRadius: 4, fontSize: 14 }} />
                  <button onClick={applyCoupon} disabled={couponLoading || !couponCode.trim()}
                    style={{ background: '#2563eb', color: '#fff', border: 'none', padding: '8px 16px', borderRadius: 4, fontWeight: 600, cursor: 'pointer' }}>
                    {couponLoading ? '...' : 'Apply'}
                  </button>
                </div>
                {couponError && <p style={{ color: '#dc2626', fontSize: 12, marginTop: 4 }}>{couponError}</p>}
              </div>

              {paymentMethod === 'STRIPE' ? (
                <div>
                  {stripeError && (
                    <div style={{ background: '#fef2f2', border: '1px solid #fecaca', padding: '12px 16px', borderRadius: 6, marginBottom: 12 }}>
                      <p style={{ color: '#dc2626', fontSize: 13, margin: 0 }}>{stripeError}</p>
                    </div>
                  )}
                  {stripeLoading && <div className="loading"><div className="spinner"></div></div>}
                  {stripeClientSecret && stripePromise && (
                    <Elements stripe={stripePromise} options={{ clientSecret: stripeClientSecret }}>
                      <StripeCheckoutForm
                        total={total}
                        selectedAddress={selectedAddress}
                        couponCode={couponCode}
                        onSuccess={() => navigate('/orders')}
                        addToast={addToast}
                      />
                    </Elements>
                  )}
                </div>
              ) : paymentMethod === 'PAYPAL' ? (
                <div>
                  {paypalError && (
                    <div style={{ background: '#fef2f2', border: '1px solid #fecaca', padding: '12px 16px', borderRadius: 6, marginBottom: 12 }}>
                      <p style={{ color: '#dc2626', fontSize: 13, margin: 0 }}>{paypalError}</p>
                    </div>
                  )}
                  <PayPalButtons
                    createOrder={createPayPalOrder}
                    onApprove={onPayPalApprove}
                    onError={(err) => setPaypalError('PayPal transaction failed. Please try Cash on Delivery instead.')}
                    style={{ layout: 'vertical', shape: 'rect', color: 'gold', label: 'paypal' }}
                    disabled={placing}
                  />
                </div>
              ) : paymentMethod === 'BAKONG' ? (
                <div>
                  {qrLoading && <div className="loading"><div className="spinner"></div></div>}
                  {qrError && (
                    <div style={{ background: '#fef2f2', border: '1px solid #fecaca', padding: '12px 16px', borderRadius: 6, marginBottom: 12 }}>
                      <p style={{ color: '#dc2626', fontSize: 13, margin: 0 }}>{qrError}</p>
                    </div>
                  )}
                  {qrCode && (
                    <div style={{ textAlign: 'center', margin: '16px 0' }}>
                      <img src={qrCode} alt="Bakong QR" style={{ width: 240, height: 240, border: '1px solid #ddd', borderRadius: 8 }} />
                      <p style={{ fontSize: 13, color: '#565959', marginTop: 8 }}>Scan this QR with your Bakong app to pay <strong>${total.toFixed(2)}</strong></p>
                    </div>
                  )}
                  <button className="btn-place" onClick={placeOrder} disabled={placing || !qrCode}>
                    {placing ? 'Placing Order...' : `I've Paid - Confirm Order $${total.toFixed(2)}`}
                  </button>
                </div>
              ) : (
                <button className="btn-place" onClick={placeOrder} disabled={placing}>
                  {placing ? 'Placing Order...' : `Place Order - Pay $${total.toFixed(2)} on Delivery`}
                </button>
              )}
              <button className="btn" style={{ width: '100%', background: '#f0f0f0', border: 'none', padding: 12, borderRadius: 6, marginTop: 8, fontWeight: 600 }}
                onClick={() => setStep(2)}>Back</button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

function StripeCheckoutForm({ total, selectedAddress, couponCode, onSuccess, addToast }) {
  const stripe = useStripe();
  const elements = useElements();
  const [processing, setProcessing] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!stripe || !elements) return;
    setProcessing(true);
    setError('');

    const { error: submitError } = await elements.submit();
    if (submitError) {
      setError(submitError.message);
      setProcessing(false);
      return;
    }

    const { error: payError, paymentIntent } = await stripe.confirmPayment({
      elements,
      redirect: 'if_required',
    });

    if (payError) {
      setError(payError.message);
      setProcessing(false);
      return;
    }

    if (paymentIntent.status === 'succeeded') {
      try {
        const params = new URLSearchParams();
        if (selectedAddress) params.set('addressId', selectedAddress);
        params.set('paymentMethod', 'STRIPE');
        if (couponCode) params.set('couponCode', couponCode.trim());
        await api.post(`/orders/place?${params}`);
        addToast('Payment successful! Order placed.');
        onSuccess();
      } catch (err) {
        setError('Payment was successful but order placement failed. Please contact support.');
        setProcessing(false);
      }
    } else {
      setError('Payment was not completed. Please try again.');
      setProcessing(false);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <div style={{ marginBottom: 16 }}>
        <PaymentElement />
      </div>
      {error && (
        <div style={{ background: '#fef2f2', border: '1px solid #fecaca', padding: '12px 16px', borderRadius: 6, marginBottom: 12 }}>
          <p style={{ color: '#dc2626', fontSize: 13, margin: 0 }}>{error}</p>
        </div>
      )}
      <button type="submit" className="btn-place" disabled={!stripe || processing} style={{ opacity: !stripe || processing ? 0.6 : 1 }}>
        {processing ? 'Processing...' : `Pay $${total.toFixed(2)}`}
      </button>
    </form>
  );
}
