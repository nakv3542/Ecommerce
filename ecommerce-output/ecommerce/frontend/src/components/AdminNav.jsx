import { Link, useLocation } from 'react-router-dom';

export default function AdminNav() {
  const location = useLocation();
  const links = [
    { path: '/admin', label: 'Dashboard', icon: '📊' },
    { path: '/admin/products', label: 'Products', icon: '📦' },
    { path: '/admin/orders', label: 'Orders', icon: '📋' },
    { path: '/admin/categories', label: 'Categories', icon: '🏷️' },
    { path: '/admin/brands', label: 'Brands', icon: '🏢' },
    { path: '/admin/coupons', label: 'Coupons', icon: '🎫' },
    { path: '/admin/reviews', label: 'Reviews', icon: '⭐' },
    { path: '/admin/users', label: 'Users', icon: '👥' },
  ];

  return (
    <div className="admin-sidebar">
      <div className="admin-sidebar-header">Admin Panel</div>
      <div className="admin-nav-links">
        {links.map(link => (
          <Link key={link.path} to={link.path}
            className={`admin-nav-link ${location.pathname === link.path ? 'active' : ''}`}>
            <span className="admin-nav-icon">{link.icon}</span>
            {link.label}
          </Link>
        ))}
      </div>
    </div>
  );
}
