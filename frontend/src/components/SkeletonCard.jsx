export default function SkeletonCard() {
  return (
    <div className="skeleton-card">
      <div style={{ display: "flex", justifyContent: "space-between", marginBottom: 14 }}>
        <div className="skeleton-line title" />
        <div className="skeleton-line badge" />
      </div>
      <div className="skeleton-line desc1" />
      <div className="skeleton-line desc2" style={{ marginBottom: 16 }} />
      <div className="skeleton-line meta" />
    </div>
  );
}
