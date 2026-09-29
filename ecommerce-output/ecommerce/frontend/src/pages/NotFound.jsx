import { Link } from 'react-router-dom';

export default function NotFound() {
  return (
    <div className="container" style={{ textAlign: 'center', padding: '80px 20px' }}>
      <div style={{ fontSize: 80, fontWeight: 700, color: '#febd69', marginBottom: 16 }}>404</div>
      <h1 style={{ marginBottom: 12 }}>Page Not Found</h1>
      <p style={{ color: '#565959', fontSize: 16, marginBottom: 32, maxWidth: 400, margin: '0 auto 32px' }}>
        The page you're looking for doesn't exist or has been moved.
      </p>
      <Link to="/" className="btn" style={{
        background: '#febd69', color: '#131921', padding: '12px 32px',
        borderRadius: 6, fontWeight: 700, fontSize: 15, display: 'inline-block'
      }}>
        Go to Homepage
      </Link>
    </div>
  );
}
