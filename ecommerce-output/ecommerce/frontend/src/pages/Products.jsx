import { useState, useEffect, useRef } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import api from '../api/axios';

function Stars({ rating }) {
  const full = Math.floor(rating);
  const half = rating % 1 >= 0.5;
  return <span className="stars">{'★'.repeat(full)}{half ? '½' : ''}{'☆'.repeat(5 - full - (half ? 1 : 0))}</span>;
}

export default function Products() {
  const [searchParams, setSearchParams] = useSearchParams();
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [brands, setBrands] = useState([]);
  const [loading, setLoading] = useState(true);
  const [loadingMore, setLoadingMore] = useState(false);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);
  const sentinelRef = useRef(null);
  const pageRef = useRef(0);

  const keyword = searchParams.get('keyword') || '';
  const categoryId = searchParams.get('categoryId') || '';
  const brandId = searchParams.get('brandId') || '';
  const minPrice = searchParams.get('minPrice') || '';
  const maxPrice = searchParams.get('maxPrice') || '';
  const minRating = searchParams.get('minRating') || '';
  const sortBy = searchParams.get('sortBy') || 'id';

  const filterKey = `${keyword}|${categoryId}|${brandId}|${minPrice}|${maxPrice}|${minRating}|${sortBy}`;

  useEffect(() => {
    api.get('/categories').then(r => setCategories(r.data)).catch(() => {});
    api.get('/brands').then(r => setBrands(r.data)).catch(() => {});
  }, []);

  useEffect(() => {
    setPage(0);
    pageRef.current = 0;
    setProducts([]);
    setLoading(true);
  }, [filterKey]);

  useEffect(() => {
    let cancelled = false;
    const load = async () => {
      const params = new URLSearchParams();
      if (keyword) params.set('keyword', keyword);
      if (categoryId) params.set('categoryId', categoryId);
      if (brandId) params.set('brandId', brandId);
      if (minPrice) params.set('minPrice', minPrice);
      if (maxPrice) params.set('maxPrice', maxPrice);
      if (minRating) params.set('minRating', minRating);
      params.set('sortBy', sortBy);
      params.set('page', page);
      params.set('size', '12');
      try {
        const r = await api.get(`/products/filter?${params}`);
        if (cancelled) return;
        if (page === 0) {
          setProducts(r.data.content);
          window.scrollTo({ top: 0, behavior: 'smooth' });
        } else {
          setProducts(prev => [...prev, ...r.data.content]);
        }
        setTotalPages(r.data.totalPages);
        setTotalElements(r.data.totalElements);
      } catch {
        // ignore
      } finally {
        if (!cancelled) {
          setLoading(false);
          setLoadingMore(false);
        }
      }
    };
    load();
    return () => { cancelled = true; };
  }, [page, filterKey]);

  useEffect(() => {
    const sentinel = sentinelRef.current;
    if (!sentinel || loading || loadingMore) return;
    const observer = new IntersectionObserver(entries => {
      if (entries[0].isIntersecting && pageRef.current < totalPages - 1) {
        setLoadingMore(true);
        const nextPage = pageRef.current + 1;
        pageRef.current = nextPage;
        setPage(nextPage);
      }
    }, { rootMargin: '200px' });
    observer.observe(sentinel);
    return () => observer.disconnect();
  }, [loading, loadingMore, totalPages]);

  const updateFilter = (key, value) => {
    const params = new URLSearchParams(searchParams);
    if (value) params.set(key, value);
    else params.delete(key);
    setSearchParams(params);
  };

  const clearFilters = () => setSearchParams({});

  return (
    <div className="container">
      <div className="products-page">
        <aside className="filter-sidebar">
          <h3>Filters</h3>
          <div className="filter-section">
            <h4>Category</h4>
            <select value={categoryId} onChange={e => updateFilter('categoryId', e.target.value)}>
              <option value="">All Categories</option>
              {categories.map(cat => (
                <option key={cat.id} value={cat.id}>{cat.name}</option>
              ))}
            </select>
          </div>
          <div className="filter-section">
            <h4>Brand</h4>
            <select value={brandId} onChange={e => updateFilter('brandId', e.target.value)}>
              <option value="">All Brands</option>
              {brands.map(b => (
                <option key={b.id} value={b.id}>{b.name}</option>
              ))}
            </select>
          </div>
          <div className="filter-section">
            <h4>Price Range</h4>
            <div className="price-inputs">
              <input type="number" placeholder="Min" value={minPrice}
                onChange={e => updateFilter('minPrice', e.target.value)} />
              <span>-</span>
              <input type="number" placeholder="Max" value={maxPrice}
                onChange={e => updateFilter('maxPrice', e.target.value)} />
            </div>
          </div>
          <div className="filter-section">
            <h4>Rating</h4>
            <select value={minRating} onChange={e => updateFilter('minRating', e.target.value)}>
              <option value="">All Ratings</option>
              {[4, 3, 2].map(r => (
                <option key={r} value={r}>{r}+ Stars</option>
              ))}
            </select>
          </div>
          <button className="btn-apply" onClick={clearFilters}>Clear All Filters</button>
        </aside>

        <main className="products-main">
          <div className="results-info">
            <span>{totalElements} products found</span>
            <select value={sortBy} onChange={e => updateFilter('sortBy', e.target.value)}>
              <option value="id">Default</option>
              <option value="price_asc">Price: Low to High</option>
              <option value="price_desc">Price: High to Low</option>
              <option value="rating">Top Rated</option>
              <option value="newest">Newest</option>
            </select>
          </div>

          {loading ? (
            <div className="loading"><div className="spinner"></div></div>
          ) : products.length === 0 ? (
            <div className="loading"><p>No products found</p></div>
          ) : (
            <>
              <div className="product-grid">
                {products.map(p => (
                  <Link key={p.id} to={`/products/${p.id}`} className="product-card">
                    {p.featured && <div className="badge-featured">Featured</div>}
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
                        {p.stock > 10 ? 'In Stock' : p.stock > 0 ? 'Only ' + p.stock + ' left' : 'Out of Stock'}
                      </div>
                    </div>
                  </Link>
                ))}
              </div>
              {loadingMore && (
                <div className="loading-more"><div className="spinner"></div><span>Loading more...</span></div>
              )}
              {page < totalPages - 1 && !loadingMore && <div ref={sentinelRef} className="scroll-sentinel" />}
            </>
          )}
        </main>
      </div>
    </div>
  );
}
