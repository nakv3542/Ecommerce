import { useState, useEffect } from 'react';
import api from '../api/axios';
import AdminNav from '../components/AdminNav';

export default function AdminDashboard() {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.get('/admin/dashboard/stats').then(r => setStats(r.data)).catch(() => {}).finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="loading"><div className="spinner"></div></div>;
  if (!stats) return <div className="loading">Failed to load dashboard</div>;

  const statusKeys = Object.keys(stats.ordersByStatus || {});
  const statusValues = Object.values(stats.ordersByStatus || {});
  const maxStatus = Math.max(...statusValues, 1);
  const monthEntries = Object.entries(stats.ordersByMonth || {});
  const maxMonth = Math.max(...monthEntries.map(([, c]) => c), 1);

  const chartColors = ['#ff6b6b', '#3498db', '#2ecc71', '#f39c12', '#8b5cf6'];

  return (
    <div className="admin-page">
      <div className="container">
        <div className="admin-layout">
          <AdminNav />
          <div className="admin-content">
            <div className="admin-header">
              <h1>Dashboard</h1>
            </div>

            <div className="stats-grid">
              <div className="stat-card">
                <span className="stat-icon">📦</span>
                <div className="stat-value">{stats.totalProducts}</div>
                <div className="stat-label">Total Products</div>
              </div>
              <div className="stat-card">
                <span className="stat-icon">📋</span>
                <div className="stat-value">{stats.totalOrders}</div>
                <div className="stat-label">Total Orders</div>
              </div>
              <div className="stat-card">
                <span className="stat-icon">👥</span>
                <div className="stat-value">{stats.totalUsers}</div>
                <div className="stat-label">Total Users</div>
              </div>
              <div className="stat-card">
                <span className="stat-icon">💰</span>
                <div className="stat-value">${Math.round(stats.totalRevenue)}</div>
                <div className="stat-label">Total Revenue</div>
              </div>
              <div className="stat-card">
                <span className="stat-icon">📈</span>
                <div className="stat-value">${Math.round(stats.monthlyRevenue)}</div>
                <div className="stat-label">Monthly Revenue</div>
              </div>
              <div className="stat-card">
                <span className="stat-icon">⏳</span>
                <div className="stat-value">{stats.pendingOrders}</div>
                <div className="stat-label">Pending Orders</div>
              </div>
            </div>

            <div className="chart-grid">
              <div className="chart-card">
                <h3>Orders by Status</h3>
                <div className="bar-chart">
                  {statusKeys.map((key, i) => (
                    <div key={key} className="bar-item">
                      <div className="bar-value">{statusValues[i]}</div>
                      <div className="bar" style={{ height: `${(statusValues[i] / maxStatus) * 160}px`, background: chartColors[i % 5] }}></div>
                      <div className="bar-label">{key}</div>
                    </div>
                  ))}
                </div>
              </div>

              <div className="chart-card">
                <h3>Orders by Month</h3>
                <div className="bar-chart">
                  {monthEntries.map(([month, count]) => (
                    <div key={month} className="bar-item">
                      <div className="bar-value">{count}</div>
                      <div className="bar" style={{ height: `${(count / maxMonth) * 160}px`, background: '#2ecc71' }}></div>
                      <div className="bar-label">{month}</div>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
