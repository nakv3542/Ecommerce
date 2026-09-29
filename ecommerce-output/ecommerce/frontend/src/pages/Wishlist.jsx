import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../api/axios';
import { useToast } from '../components/Toast';

export default function Wishlist() {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [addingToCart, setAddingToCart] = useState(null);
  const { addToast } = useToast();

  const fetchWishlist = () => {
    api.get('/wishlist').then(r => setItems(r.data)).catch(() => {}).finally(() => setLoading(false));
  };

  useEffect(() => { fetchWishlist(); }, []);

  const remove = async (productId) => {
    try {
      await api.delete(`/wishlist/remove/${productId}`);
      addToast('Removed from wishlist');
      fetchWishlist();
    } catch (err) {
      addToast(err.response?.data?.message || 'Failed to remove item', 'error');
    }
  };

  const addToCart = async (productId) => {
    setAddingToCart(productId);
    try {
      await api.post(`/cart/add?productId=${productId}&quantity=1`);
      addToast('Added to cart!');
    } catch (err) {
      addToast(err.response?.data?.message || 'Failed to add to cart', 'error');
    } finally {
      setAddingToCart(null);
    }
  };

  if (loading) return <div className="loading"><div className="spinner"></div></div>;

  return (
    <div className="container">
      <div className="wishlist-page">
        <div className="section-header">
          <h1>My Wishlist ({items.length})</h1>
        </div>
        {items.length === 0 ? (
          <div className="loading" style={{ padding: '60px 20px' }}>
            <p style={{ fontSize: 20, color: '#565959', marginBottom: 8 }}>Your wishlist is empty</p>
            <p style={{ fontSize: 14, color: '#999', marginBottom: 20 }}>Save items you love and come back to them anytime!</p>
            <Link to="/products" className="btn" style={{ display: 'inline-block', background: '#febd69', color: '#131921', padding: '14px 32px', borderRadius: 50, fontWeight: 700 }}>Explore Products</Link>
          </div>
        ) : (
          <div className="wishlist-grid">
            {items.map(item => (
              <div key={item.id} className="product-card">
                <button className="wishlist-btn" onClick={() => remove(item.productId)} style={{ opacity: 1, top: 10, right: 10, fontSize: 14 }} title="Remove from wishlist">🗑️</button>
                {item.stock > 0 && (
                  <button
                    className="wishlist-add-cart"
                    onClick={() => addToCart(item.productId)}
                    disabled={addingToCart === item.productId}
                    title="Add to Cart">
                    {addingToCart === item.productId ? '...' : '🛒'}
                  </button>
                )}
                <Link to={`/products/${item.productId}`}>
                  <div className="image-wrap">
                    <img src={item.productImage} alt={item.productName} />
                  </div>
                  <div className="info">
                    <div className="name">{item.productName}</div>
                    <div className="rating" style={{ marginBottom: 4 }}>
                      <span className="stars" style={{ color: '#febd69' }}>{'★'.repeat(Math.round(item.averageRating || 0))}{'☆'.repeat(5 - Math.round(item.averageRating || 0))}</span>
                    </div>
                    <div className="price-row">
                      <span className="current">${item.discountPrice || item.productPrice}</span>
                      {item.discountPrice && <span className="original">${item.productPrice}</span>}
                    </div>
                    <div className={`stock ${item.stock > 0 ? 'in-stock' : 'out-of-stock'}`}>
                      {item.stock > 0 ? '✓ In Stock' : 'Out of Stock'}
                    </div>
                  </div>
                </Link>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
