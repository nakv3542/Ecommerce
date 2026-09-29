import { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../components/Toast';
import api from '../api/axios';

function Stars({ rating, size = 18 }) {
  const full = Math.floor(rating);
  const half = rating % 1 >= 0.5;
  return <span className="stars" style={{ fontSize: size }}>{'★'.repeat(full)}{half ? '½' : ''}{'☆'.repeat(5 - full - (half ? 1 : 0))}</span>;
}

export default function ProductDetail() {
  const { id } = useParams();
  const { user } = useAuth();
  const { addToast } = useToast();
  const navigate = useNavigate();
  const [product, setProduct] = useState(null);
  const [reviews, setReviews] = useState([]);
  const [quantity, setQuantity] = useState(1);
  const [selectedImage, setSelectedImage] = useState(0);
  const [inWishlist, setInWishlist] = useState(false);
  const [loading, setLoading] = useState(true);
  const [reviewForm, setReviewForm] = useState({ rating: 5, comment: '' });
  const [recentlyViewed, setRecentlyViewed] = useState([]);

  useEffect(() => {
    setLoading(true);
    api.get(`/products/${id}`).then(r => {
      setProduct(r.data);
      setSelectedImage(0);
      addToRecentlyViewed(r.data);
    }).catch(() => navigate('/')).finally(() => setLoading(false));

    api.get(`/reviews/product/${id}`).then(r => setReviews(r.data)).catch(() => {});

    if (user) {
      api.get(`/wishlist/check/${id}`).then(r => setInWishlist(r.data.inWishlist)).catch(() => {});
    }

    loadRecentlyViewed();
  }, [id]);

  const addToRecentlyViewed = (prod) => {
    try {
      const stored = JSON.parse(localStorage.getItem('recentlyViewed') || '[]');
      const filtered = stored.filter(item => item.id !== prod.id);
      const updated = [{ id: prod.id, name: prod.name, price: prod.discountPrice || prod.price, image: prod.images?.[0] }, ...filtered].slice(0, 6);
      localStorage.setItem('recentlyViewed', JSON.stringify(updated));
    } catch {}
  };

  const loadRecentlyViewed = () => {
    try {
      const stored = JSON.parse(localStorage.getItem('recentlyViewed') || '[]');
      setRecentlyViewed(stored.filter(item => item.id !== Number(id)));
    } catch {}
  };

  const addToCart = async () => {
    if (!user) { navigate('/login'); return; }
    try {
      await api.post(`/cart/add?productId=${id}&quantity=${quantity}`);
      addToast('Added to cart!');
    } catch (err) {
      addToast(err.response?.data?.message || 'Failed to add to cart', 'error');
    }
  };

  const buyNow = async () => {
    if (!user) { navigate('/login'); return; }
    try {
      await api.post(`/cart/add?productId=${id}&quantity=${quantity}`);
      navigate('/checkout');
    } catch (err) {
      addToast(err.response?.data?.message || 'Failed', 'error');
    }
  };

  const toggleWishlist = async () => {
    if (!user) { navigate('/login'); return; }
    try {
      if (inWishlist) {
        await api.delete(`/wishlist/remove/${id}`);
        setInWishlist(false);
        addToast('Removed from wishlist');
      } else {
        await api.post(`/wishlist/add/${id}`);
        setInWishlist(true);
        addToast('Added to wishlist!');
      }
    } catch (err) {
      addToast(err.response?.data?.message || 'Failed', 'error');
    }
  };

  const submitReview = async (e) => {
    e.preventDefault();
    try {
      const res = await api.post(`/reviews/product/${id}`, reviewForm);
      setReviews([res.data, ...reviews]);
      setReviewForm({ rating: 5, comment: '' });
      addToast('Review submitted!');
    } catch (err) {
      addToast(err.response?.data?.message || 'Failed to submit review', 'error');
    }
  };

  if (loading) return <div className="loading"><div className="spinner"></div></div>;
  if (!product) return null;

  const isOutOfStock = product.stock === 0;
  const discount = product.discountPrice ? Math.round((1 - product.discountPrice / product.price) * 100) : 0;

  return (
    <div className="container">
      <div className="breadcrumb">
        <Link to="/">Home</Link> / <Link to="/products">Products</Link> / <span>{product.name}</span>
      </div>

      <div className="product-detail">
        <div className="image-gallery">
          <div className="main-image">
            <img src={product.images?.[selectedImage]} alt={product.name} />
          </div>
          {product.images?.length > 1 && (
            <div className="thumbnails">
              {product.images.map((img, i) => (
                <img key={i} src={img} alt="" className={i === selectedImage ? 'active' : ''}
                  onClick={() => setSelectedImage(i)} />
              ))}
            </div>
          )}
          {isOutOfStock && <div className="out-of-stock-overlay">Out of Stock</div>}
        </div>

        <div className="info">
          {product.brand && <div className="brand">{product.brand.name}</div>}
          <h1>{product.name}</h1>

          <div className="rating-row">
            <Stars rating={product.averageRating} />
            <span>{product.averageRating} out of 5</span>
            <span>({product.reviewCount} reviews)</span>
          </div>

          <div className="price-box">
            <span className="current">${product.discountPrice || product.price}</span>
            {product.discountPrice && <span className="original">${product.price}</span>}
            {discount > 0 && <span className="discount-badge">-{discount}%</span>}
          </div>

          <p className="description">{product.description}</p>

          {product.categories?.length > 0 && (
            <p className="product-categories">
              Categories: {product.categories.map((c, i) => (
                <span key={c.id}>
                  <Link to={`/products?categoryId=${c.id}`} className="category-link">{c.name}</Link>
                  {i < product.categories.length - 1 ? ', ' : ''}
                </span>
              ))}
            </p>
          )}

          <div className="product-meta">
            <div className="meta-item">
              <span className="meta-label">SKU</span>
              <span className="meta-value">{product.sku}</span>
            </div>
            <div className="meta-item">
              <span className="meta-label">Availability</span>
              <span className={`meta-value ${isOutOfStock ? 'text-danger' : product.stock < 10 ? 'text-warning' : 'text-success'}`}>
                {isOutOfStock ? 'Out of Stock' : product.stock < 10 ? `Only ${product.stock} left` : 'In Stock'}
              </span>
            </div>
          </div>

          {!isOutOfStock && (
            <div className="actions">
              <div className="qty-selector">
                <button onClick={() => setQuantity(Math.max(1, quantity - 1))}>-</button>
                <span>{quantity}</span>
                <button onClick={() => setQuantity(Math.min(product.stock, quantity + 1))}>+</button>
              </div>
              <button className="btn-add-cart" onClick={addToCart}>Add to Cart</button>
              <button className="btn-buy-now" onClick={buyNow}>Buy Now</button>
              <button className="btn-wishlist" onClick={toggleWishlist}
                style={{ color: inWishlist ? '#dc2626' : '#999' }}>
                {inWishlist ? '♥' : '♡'}
              </button>
            </div>
          )}
        </div>
      </div>

      <div className="reviews-section">
        <h2>Customer Reviews ({reviews.length})</h2>

        {user && (
          <form className="review-form" onSubmit={submitReview}>
            <h3>Write a Review</h3>
            <div className="star-select">
              {[1, 2, 3, 4, 5].map(s => (
                <button key={s} type="button" className={s <= reviewForm.rating ? 'active' : ''}
                  onClick={() => setReviewForm({ ...reviewForm, rating: s })}>★</button>
              ))}
            </div>
            <textarea placeholder="Share your thoughts about this product..."
              value={reviewForm.comment}
              onChange={e => setReviewForm({ ...reviewForm, comment: e.target.value })} required />
            <button type="submit" className="btn-submit">Submit Review</button>
          </form>
        )}

        {reviews.length === 0 && <p className="no-reviews">No reviews yet. Be the first to review this product!</p>}

        {reviews.map(r => (
          <div key={r.id} className="review-card">
            <div className="review-header">
              <span className="user">{r.userName}</span>
              <span className="date">{new Date(r.createdAt).toLocaleDateString()}</span>
            </div>
            <Stars rating={r.rating} />
            <p className="comment">{r.comment}</p>
          </div>
        ))}
      </div>

      {recentlyViewed.length > 0 && (
        <section className="home-section">
          <div className="section-header">
            <h2>Recently Viewed</h2>
          </div>
          <div className="mini-product-grid">
            {recentlyViewed.map(item => (
              <Link key={item.id} to={`/products/${item.id}`} className="mini-product-card">
                <div className="mini-image">
                  <img src={item.image} alt={item.name} />
                </div>
                <div className="mini-info">
                  <div className="mini-name">{item.name}</div>
                  <div className="mini-price">${item.price}</div>
                </div>
              </Link>
            ))}
          </div>
        </section>
      )}
    </div>
  );
}
