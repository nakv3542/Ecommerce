import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import api from '../api/axios';

function Stars({ rating }) {
  const full = Math.floor(rating);
  const half = rating % 1 >= 0.5;
  const empty = 5 - full - (half ? 1 : 0);
  return (
    <span className="stars">
      {'★'.repeat(full)}{half ? '½' : ''}{'☆'.repeat(empty)}
    </span>
  );
}

const slides = [
  {
    title: 'Summer Sale is Live!',
    subtitle: 'Up to 50% off on Electronics',
    desc: 'Grab the best deals on laptops, phones, and accessories. Limited time offer!',
    cta: 'Shop Electronics',
    link: '/products?categoryId=1',
    img: 'https://picsum.photos/seed/summer24/800/450',
    color: '#1a1a2e',
  },
  {
    title: 'New Fashion Collection',
    subtitle: 'Trending Styles for Everyone',
    desc: 'Discover the latest trends in fashion. From casual wear to formal attire.',
    cta: 'Explore Fashion',
    link: '/products?categoryId=8',
    img: 'https://picsum.photos/seed/fashion24/800/450',
    color: '#16213e',
  },
  {
    title: 'Home & Kitchen Deals',
    subtitle: 'Transform Your Space',
    desc: 'Upgrade your home with premium kitchen appliances and home decor.',
    cta: 'Shop Home',
    link: '/products?categoryId=15',
    img: 'https://picsum.photos/seed/home24/800/450',
    color: '#0f3460',
  },
  {
    title: 'Welcome to MegaShop',
    subtitle: 'Your One-Stop Shopping Destination',
    desc: 'Discover amazing products at unbeatable prices. From electronics to fashion, we have everything you need.',
    cta: 'Shop Now',
    link: '/products',
    img: 'https://picsum.photos/seed/herobanner/800/450',
    color: '#131921',
  },
];

export default function Home() {
  const { user } = useAuth();
  const [featured, setFeatured] = useState([]);
  const [topRated, setTopRated] = useState([]);
  const [newArrivals, setNewArrivals] = useState([]);
  const [categories, setCategories] = useState([]);
  const [brands, setBrands] = useState([]);
  const [slideIndex, setSlideIndex] = useState(0);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([
      api.get('/products/featured'),
      api.get('/products/top-rated'),
      api.get('/products/new-arrivals'),
      api.get('/categories/roots'),
      api.get('/brands'),
    ]).then(([f, t, n, c, b]) => {
      setFeatured(f.data);
      setTopRated(t.data);
      setNewArrivals(n.data);
      setCategories(c.data);
      setBrands(b.data);
    }).catch(() => {}).finally(() => setLoading(false));
  }, []);

  useEffect(() => {
    const timer = setInterval(() => {
      setSlideIndex(i => (i + 1) % slides.length);
    }, 5000);
    return () => clearInterval(timer);
  }, []);

  const categoryIcons = {
    'Electronics': '💻',
    'Clothing': '👕',
    'Home & Kitchen': '🏠',
    'Books': '📚',
    'Sports & Outdoors': '⚽',
    'Beauty & Personal Care': '💄',
    'Toys & Games': '🎮',
    'Automotive': '🚗',
    'Pet Supplies': '🐾',
    'Office Supplies': '📎',
    'Health & Household': '💊',
    'Grocery & Gourmet Food': '🍎',
    'Musical Instruments': '🎸',
    'Jewelry': '💍',
    'Baby Products': '🍼',
    'Tools & Home Improvement': '🔧',
  };

  const toggleWishlist = async (e, productId, inWishlist) => {
    e.preventDefault();
    if (!user) return;
    try {
      if (inWishlist) {
        await api.delete(`/wishlist/remove/${productId}`);
      } else {
        await api.post(`/wishlist/add/${productId}`);
      }
    } catch {}
  };

  if (loading) return <div className="loading"><div className="spinner"></div></div>;

  return (
    <div className="home-page">
      {/* Hero Carousel */}
      <div className="hero-carousel" style={{ background: slides[slideIndex].color }}>
        <div className="hero-content">
          <div className="hero-text">
            <p className="hero-subtitle">{slides[slideIndex].subtitle}</p>
            <h1>{slides[slideIndex].title}</h1>
            <p className="hero-desc">{slides[slideIndex].desc}</p>
            <Link to={slides[slideIndex].link} className="btn">{slides[slideIndex].cta}</Link>
          </div>
          <div className="hero-image">
            <img src={slides[slideIndex].img} alt="Hero" />
          </div>
        </div>
        <div className="hero-dots">
          {slides.map((_, i) => (
            <button key={i} className={`dot ${i === slideIndex ? 'active' : ''}`}
              onClick={() => setSlideIndex(i)} />
          ))}
        </div>
      </div>

      {/* Trust Badges */}
      <div className="trust-bar">
        <div className="container">
          <div className="trust-grid">
            <div className="trust-item">
              <span className="trust-icon">🚚</span>
              <div><strong>Free Shipping</strong><small>On orders over $100</small></div>
            </div>
            <div className="trust-item">
              <span className="trust-icon">🔒</span>
              <div><strong>Secure Payment</strong><small>100% secure checkout</small></div>
            </div>
            <div className="trust-item">
              <span className="trust-icon">↩️</span>
              <div><strong>Easy Returns</strong><small>30-day return policy</small></div>
            </div>
            <div className="trust-item">
              <span className="trust-icon">💬</span>
              <div><strong>24/7 Support</strong><small>Dedicated customer service</small></div>
            </div>
          </div>
        </div>
      </div>

      <div className="container">
        {/* Categories Section */}
        {categories.length > 0 && (
          <section className="home-section">
            <div className="section-header">
              <h2>Shop by Category</h2>
              <Link to="/products" className="section-link">Browse All Categories →</Link>
            </div>
            <div className="categories-grid">
              {categories.map(cat => (
                <Link key={cat.id} to={`/products?categoryId=${cat.id}`} className="category-card">
                  <span className="cat-icon">{categoryIcons[cat.name] || '🛍️'}</span>
                  <span className="cat-name">{cat.name}</span>
                </Link>
              ))}
            </div>
          </section>
        )}

        {/* Featured Products */}
        {featured.length > 0 && (
          <section className="home-section">
            <div className="section-header">
              <h2>Featured Products</h2>
              <Link to="/products?sortBy=rating" className="section-link">View All →</Link>
            </div>
            <div className="product-grid">
              {featured.map(p => (
                <Link key={p.id} to={`/products/${p.id}`} className="product-card">
                  <div className="badge-featured">Featured</div>
                  {p.discountPrice && <div className="badge-discount">SALE</div>}
                  <div className="image-wrap">
                    <img src={p.images?.[0]} alt={p.name} />
                  </div>
                  <div className="info">
                    {p.brand && <div className="brand">{p.brand.name}</div>}
                    <div className="name">{p.name}</div>
                    <div className="rating">
                      <Stars rating={p.averageRating} />
                      <span className="count">({p.reviewCount})</span>
                    </div>
                    <div className="price-row">
                      <span className="current">${p.discountPrice || p.price}</span>
                      {p.discountPrice && <span className="original">${p.price}</span>}
                    </div>
                    <div className={`stock ${p.stock > 10 ? 'in-stock' : p.stock > 0 ? 'low-stock' : 'out-of-stock'}`}>
                      {p.stock > 10 ? '✓ In Stock' : p.stock > 0 ? 'Only ' + p.stock + ' left' : 'Out of Stock'}
                    </div>
                  </div>
                </Link>
              ))}
            </div>
          </section>
        )}

        {/* Deal Banner */}
        <section className="deal-banner">
          <div className="deal-content">
            <span className="deal-tag">Limited Time Offer</span>
            <h2>Big Sale on Electronics</h2>
            <p>Save up to 40% on top brands. Don't miss out on these incredible deals!</p>
            <Link to="/products?categoryId=1" className="btn">Shop Electronics →</Link>
          </div>
          <div className="deal-image">
            <img src="https://picsum.photos/seed/dealbanner/500/350" alt="Deal" />
          </div>
        </section>

        {/* Top Rated Products */}
        {topRated.length > 0 && (
          <section className="home-section">
            <div className="section-header">
              <h2>Top Rated</h2>
              <Link to="/products?minRating=4" className="section-link">View All →</Link>
            </div>
            <div className="product-grid">
              {topRated.map(p => (
                <Link key={p.id} to={`/products/${p.id}`} className="product-card">
                  {p.discountPrice && <div className="badge-discount">SALE</div>}
                  <div className="image-wrap">
                    <img src={p.images?.[0]} alt={p.name} />
                  </div>
                  <div className="info">
                    {p.brand && <div className="brand">{p.brand.name}</div>}
                    <div className="name">{p.name}</div>
                    <div className="rating">
                      <Stars rating={p.averageRating} />
                      <span className="count">({p.reviewCount})</span>
                    </div>
                    <div className="price-row">
                      <span className="current">${p.discountPrice || p.price}</span>
                      {p.discountPrice && <span className="original">${p.price}</span>}
                    </div>
                  </div>
                </Link>
              ))}
            </div>
          </section>
        )}

        {/* Brands Showcase */}
        {brands.length > 0 && (
          <section className="home-section">
            <div className="section-header">
              <h2>Shop by Brand</h2>
              <Link to="/products" className="section-link">All Brands →</Link>
            </div>
            <div className="brands-grid">
              {brands.slice(0, 12).map(b => (
                <Link key={b.id} to={`/products?brandId=${b.id}`} className="brand-card">
                  {b.logoUrl ? (
                    <img src={b.logoUrl} alt={b.name} />
                  ) : (
                    <span className="brand-initial">{b.name.charAt(0)}</span>
                  )}
                  <span className="brand-name">{b.name}</span>
                </Link>
              ))}
            </div>
          </section>
        )}

        {/* New Arrivals */}
        {newArrivals.length > 0 && (
          <section className="home-section">
            <div className="section-header">
              <h2>New Arrivals</h2>
              <Link to="/products?sortBy=newest" className="section-link">View All →</Link>
            </div>
            <div className="product-grid">
              {newArrivals.slice(0, 8).map(p => (
                <Link key={p.id} to={`/products/${p.id}`} className="product-card">
                  <div className="badge-new">New</div>
                  <div className="image-wrap">
                    <img src={p.images?.[0]} alt={p.name} />
                  </div>
                  <div className="info">
                    {p.brand && <div className="brand">{p.brand.name}</div>}
                    <div className="name">{p.name}</div>
                    <div className="rating">
                      <Stars rating={p.averageRating} />
                      <span className="count">({p.reviewCount})</span>
                    </div>
                    <div className="price-row">
                      <span className="current">${p.discountPrice || p.price}</span>
                      {p.discountPrice && <span className="original">${p.price}</span>}
                    </div>
                  </div>
                </Link>
              ))}
            </div>
          </section>
        )}

        {/* Testimonials */}
        <section className="home-section">
          <div className="section-header">
            <h2>What Our Customers Say</h2>
          </div>
          <div className="testimonials-grid">
            <div className="testimonial-card">
              <div className="testimonial-stars">★★★★★</div>
              <p className="testimonial-text">"Amazing shopping experience! The products arrived quickly and were exactly as described. Highly recommend!"</p>
              <div className="testimonial-author">
                <div className="testimonial-avatar">S</div>
                <div>
                  <strong>Sarah Johnson</strong>
                  <small>Verified Buyer</small>
                </div>
              </div>
            </div>
            <div className="testimonial-card">
              <div className="testimonial-stars">★★★★★</div>
              <p className="testimonial-text">"Best prices on electronics I've found anywhere. Customer service was incredibly helpful when I had a question about my order."</p>
              <div className="testimonial-author">
                <div className="testimonial-avatar">M</div>
                <div>
                  <strong>Michael Chen</strong>
                  <small>Verified Buyer</small>
                </div>
              </div>
            </div>
            <div className="testimonial-card">
              <div className="testimonial-stars">★★★★★</div>
              <p className="testimonial-text">"The checkout process was smooth and easy. Free shipping arrived in just 3 days. Will definitely shop here again!"</p>
              <div className="testimonial-author">
                <div className="testimonial-avatar">E</div>
                <div>
                  <strong>Emily Rodriguez</strong>
                  <small>Verified Buyer</small>
                </div>
              </div>
            </div>
          </div>
        </section>

        {/* Newsletter */}
        <section className="newsletter-section">
          <div className="newsletter-content">
            <h2>Stay in the Loop</h2>
            <p>Subscribe to our newsletter and get exclusive deals, new arrivals, and special offers straight to your inbox.</p>
            <form className="newsletter-form" onSubmit={e => e.preventDefault()}>
              <input type="email" placeholder="Enter your email address" required />
              <button type="submit">Subscribe</button>
            </form>
          </div>
        </section>
      </div>
    </div>
  );
}
