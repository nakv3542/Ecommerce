import { Link } from 'react-router-dom';

export default function Footer() {
  return (
    <footer className="footer">
      <div className="footer-top">
        <a href="#" onClick={(e) => { e.preventDefault(); window.scrollTo({ top: 0, behavior: 'smooth' }); }}
          className="footer-back-top">Back to top</a>
      </div>
      <div className="footer-middle">
        <div className="container footer-links">
          <div className="footer-col">
            <h4>Get to Know Us</h4>
            <Link to="/">About MegaShop</Link>
            <a href="#">Careers</a>
            <a href="#">Press Center</a>
            <a href="#">Blog</a>
            <a href="#">Investor Relations</a>
          </div>
          <div className="footer-col">
            <h4>Customer Service</h4>
            <Link to="/">Help Center</Link>
            <Link to="/orders">Your Orders</Link>
            <Link to="/cart">Shopping Cart</Link>
            <a href="#">Returns & Exchanges</a>
            <a href="#">Shipping Info</a>
          </div>
          <div className="footer-col">
            <h4>Make Money</h4>
            <a href="#">Sell Products</a>
            <a href="#">Become an Affiliate</a>
            <a href="#">Advertise</a>
            <a href="#">Bulk Purchases</a>
          </div>
          <div className="footer-col">
            <h4>Let Us Help You</h4>
            <Link to="/profile">Your Account</Link>
            <Link to="/wishlist">Your Wishlist</Link>
            <a href="#">Shipping Rates</a>
            <a href="#">Help & Support</a>
            <a href="#">Accessibility</a>
          </div>
          <div className="footer-col">
            <h4>Connect With Us</h4>
            <div className="footer-social">
              <a href="#" className="social-link" title="Facebook">📘</a>
              <a href="#" className="social-link" title="Instagram">📸</a>
              <a href="#" className="social-link" title="Twitter">🐦</a>
              <a href="#" className="social-link" title="YouTube">▶️</a>
              <a href="#" className="social-link" title="LinkedIn">🔗</a>
            </div>
          </div>
        </div>
      </div>
      <div className="footer-divider">
        <div className="container">
          <div className="footer-payment">
            <span>We Accept</span>
            <span className="payment-icons">
              <span className="payment-icon visa">VISA</span>
              <span className="payment-icon mc">MC</span>
              <span className="payment-icon amex">AMEX</span>
              <span className="payment-icon pp">PayPal</span>
              <span className="payment-icon bk">KHQR</span>
            </span>
          </div>
        </div>
      </div>
      <div className="footer-bottom">
        <div className="container">
          <Link to="/" className="footer-logo">Mega<span>Shop</span></Link>
          <div className="footer-bottom-links">
            <a href="#">Terms of Service</a>
            <a href="#">Privacy Policy</a>
            <a href="#">Cookie Policy</a>
          </div>
          <p className="footer-copy">&copy; {new Date().getFullYear()} MegaShop. All rights reserved.</p>
        </div>
      </div>
    </footer>
  );
}
