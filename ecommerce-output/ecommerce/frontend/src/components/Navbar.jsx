import { Link, useNavigate } from 'react-router-dom';
import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { useTheme } from '../context/ThemeContext';
import api from '../api/axios';

export default function Navbar() {
  const { user, logout } = useAuth();
  const { theme, toggleTheme } = useTheme();
  const navigate = useNavigate();
  const [searchQuery, setSearchQuery] = useState('');
  const [categories, setCategories] = useState([]);
  const [cartCount, setCartCount] = useState(0);
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const [searchOpen, setSearchOpen] = useState(false);

  useEffect(() => {
    api.get('/categories/roots').then(r => setCategories(r.data)).catch(() => {});
    if (user) {
      api.get('/cart').then(r => {
        const count = r.data?.items?.length || 0;
        setCartCount(count);
      }).catch(() => {});
    }
  }, [user]);

  const handleSearch = (e) => {
    e.preventDefault();
    if (searchQuery.trim()) {
      navigate(`/products?keyword=${encodeURIComponent(searchQuery.trim())}`);
      setSearchOpen(false);
    }
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav className="navbar">
      <div className="navbar-top">
        <button className="navbar-hamburger" onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
          aria-label="Toggle menu">
          <span></span><span></span><span></span>
        </button>

        <Link to="/" className="navbar-logo">Mega<span>Shop</span></Link>

        <form className={`navbar-search ${searchOpen ? 'open' : ''}`} onSubmit={handleSearch}>
          <input
            type="text"
            placeholder="Search products..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
          <button type="submit">Search</button>
        </form>

        <div className="navbar-links">
          {user ? (
            <>
              <Link to="/wishlist" className="nav-icon-link" title="Wishlist">Wishlist</Link>
              <Link to="/orders" className="nav-icon-link" title="Orders">Orders</Link>
              <Link to="/cart" className="nav-icon-link nav-cart" title="Cart">
                Cart
                {cartCount > 0 && <span className="cart-badge">{cartCount}</span>}
              </Link>
              <Link to="/profile" className="nav-user">
                <small>Hello,</small>
                {user.name}
              </Link>
              {user.role === 'ADMIN' && (
                <Link to="/admin" className="nav-admin-link">Admin</Link>
              )}
              <button onClick={handleLogout} className="nav-logout">Logout</button>
            </>
          ) : (
            <>
              <Link to="/login" className="nav-signin">Sign in</Link>
              <Link to="/register" className="nav-register">Register</Link>
            </>
          )}
          <button className="theme-toggle" onClick={toggleTheme}
            aria-label={`Switch to ${theme === 'light' ? 'dark' : 'light'} mode`}>
            {theme === 'light' ? '🌙' : '☀️'}
          </button>
          <button className="navbar-search-toggle" onClick={() => setSearchOpen(!searchOpen)}
            aria-label="Toggle search">🔍</button>
        </div>
      </div>

      <div className={`navbar-bottom ${mobileMenuOpen ? 'open' : ''}`}>
        <div className="navbar-bottom-inner">
          <div className="category-dropdown">
            <a href="/products">📂 Categories</a>
            <div className="category-dropdown-content">
              {categories.map(cat => (
                <Link key={cat.id} to={`/products?categoryId=${cat.id}`}
                  onClick={() => setMobileMenuOpen(false)}>{cat.name}</Link>
              ))}
            </div>
          </div>
          <Link to="/products?sortBy=newest" onClick={() => setMobileMenuOpen(false)}>🔥 New Arrivals</Link>
          <Link to="/products?minRating=4" onClick={() => setMobileMenuOpen(false)}>⭐ Top Rated</Link>
          <Link to="/products?sortBy=price_asc" onClick={() => setMobileMenuOpen(false)}>🏷️ Best Deals</Link>
          <Link to="/products?categoryId=1" onClick={() => setMobileMenuOpen(false)}>💻 Electronics</Link>
          <Link to="/products?categoryId=8" onClick={() => setMobileMenuOpen(false)}>👕 Fashion</Link>
          <Link to="/products?categoryId=15" onClick={() => setMobileMenuOpen(false)}>🏠 Home & Kitchen</Link>

          <div className="mobile-menu-account">
            {user ? (
              <>
                <span className="mobile-user">Hello, {user.name}</span>
                <Link to="/cart" onClick={() => setMobileMenuOpen(false)}>
                  🛒 Cart {cartCount > 0 && <span className="cart-badge">{cartCount}</span>}
                </Link>
                <Link to="/wishlist" onClick={() => setMobileMenuOpen(false)}>❤️ Wishlist</Link>
                <Link to="/orders" onClick={() => setMobileMenuOpen(false)}>📦 Orders</Link>
                <Link to="/profile" onClick={() => setMobileMenuOpen(false)}>👤 Profile</Link>
                {user.role === 'ADMIN' && (
                  <Link to="/admin" className="mobile-admin" onClick={() => setMobileMenuOpen(false)}>
                    ⚙️ Admin
                  </Link>
                )}
                <button onClick={handleLogout} className="mobile-logout">Logout</button>
              </>
            ) : (
              <>
                <Link to="/login" onClick={() => setMobileMenuOpen(false)}>Sign in</Link>
                <Link to="/register" onClick={() => setMobileMenuOpen(false)}>Register</Link>
              </>
            )}
          </div>
        </div>
      </div>
    </nav>
  );
}
