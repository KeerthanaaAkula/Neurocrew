import { useState } from "react";
import { deleteIdea } from "../services/api";

export default function DeleteConfirmModal({ idea, onClose, onDeleted }) {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const confirm = async () => {
    setLoading(true);
    setError("");
    try {
      await deleteIdea(idea.id);
      onDeleted(idea.id);
      onClose();
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-card modal-sm" onClick={(e) => e.stopPropagation()}>

        {/* ── Icon ── */}
        <div className="delete-icon">
          <svg width="28" height="28" viewBox="0 0 24 24" fill="none">
            <path d="M3 6h18M8 6V4h8v2M19 6l-1 14H6L5 6"
              stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
            <path d="M10 11v6M14 11v6"
              stroke="currentColor" strokeWidth="1.5" strokeLinecap="round"/>
          </svg>
        </div>

        <div className="modal-title" style={{ textAlign: "center" }}>Delete idea</div>
        <div className="modal-desc">
          Are you sure you want to delete <strong>"{idea.title}"</strong>?
          This will also delete all comments and requests. This action cannot be undone.
        </div>

        {error && <div className="form-error">{error}</div>}

        <div className="modal-actions">
          <button className="btn-outline" onClick={onClose} disabled={loading}>
            Cancel
          </button>
          <button className="btn-delete" onClick={confirm} disabled={loading}>
            {loading ? "Deleting..." : "Delete idea"}
          </button>
        </div>

      </div>
    </div>
  );
}