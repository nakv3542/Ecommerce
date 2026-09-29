export default function Pagination({ page, totalPages, onPageChange }) {
  const maxVisible = 5;
  let start = Math.max(0, Math.min(page - Math.floor(maxVisible / 2), totalPages - maxVisible));
  if (start < 0) start = 0;
  const end = Math.min(start + maxVisible, totalPages);
  const pages = [];
  for (let i = start; i < end; i++) pages.push(i);

  return (
    <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', gap: 6, marginTop: 24 }}>
      <button
        onClick={() => onPageChange(0)}
        disabled={page === 0}
        style={btnStyle(disabled)}>&laquo;</button>
      <button
        onClick={() => onPageChange(page - 1)}
        disabled={page === 0}
        style={btnStyle(disabled)}>&lsaquo;</button>
      {pages.map(p => (
        <button
          key={p}
          onClick={() => onPageChange(p)}
          style={{
            ...btnStyle(p === page),
            background: p === page ? '#2563eb' : '#f0f0f0',
            color: p === page ? '#fff' : '#333',
            fontWeight: p === page ? 700 : 400
          }}>{p + 1}</button>
      ))}
      <button
        onClick={() => onPageChange(page + 1)}
        disabled={page >= totalPages - 1}
        style={btnStyle(disabled)}>&rsaquo;</button>
      <button
        onClick={() => onPageChange(totalPages - 1)}
        disabled={page >= totalPages - 1}
        style={btnStyle(disabled)}>&raquo;</button>
    </div>
  );
}

const disabled = true;

function btnStyle(disabled) {
  return {
    padding: '8px 12px',
    border: '1px solid #ddd',
    borderRadius: 4,
    background: disabled ? '#f9f9f9' : '#fff',
    color: disabled ? '#ccc' : '#333',
    cursor: disabled ? 'not-allowed' : 'pointer',
    fontSize: 14,
    fontWeight: 600,
    minWidth: 36,
    textAlign: 'center'
  };
}
