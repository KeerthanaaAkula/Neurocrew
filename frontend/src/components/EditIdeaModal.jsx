import { useState } from "react";
import { updateIdea } from "../services/api";

export default function EditIdeaModal({ idea, onClose, onUpdated }) {
  const [form, setForm] = useState({
    title: idea.title,
    description: idea.description,
    openToCollab: idea.openToCollab,
    status: idea.status || "DRAFT",
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const set = (k) => (e) =>
    setForm((f) => ({
      ...f,
      [k]: k === "openToCollab" ? e.target.value === "true" : e.target.value,
    }));

  const submit = async () => {
    if (!form.title || !form.description) {
      setError("Title and description are required.");
      return;
    }
    if (form.title.length < 3) {
      setError("Title must be at least 3 characters.");
      return;
    }
    if (form.description.length < 10) {
      setError("Description must be at least 10 characters.");
      return;
    }
    setLoading(true);
    setError("");
    try {
      const updated = await updateIdea(idea.id, form);
      onUpdated(updated);
      onClose();
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-card" onClick={(e) => e.stopPropagation()}>

        {/* ── Header ── */}
        <div className="modal-header">
          <div className="modal-title">Edit idea</div>
          <button className="modal-close" onClick={onClose}>
            <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
              <path d="M4 4l8 8M12 4l-8 8" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round"/>
            </svg>
          </button>
        </div>

        {/* ── Form ── */}
        <div className="field">
          <div className="field-label-row">
            <label>Title</label>
            <span className={`char-count ${form.title.length > 90 ? "char-warn" : ""}`}>
              {form.title.length}/100
            </span>
          </div>
          <input
            value={form.title}
            onChange={set("title")}
            placeholder="Idea title"
            maxLength={100}
          />
        </div>

        <div className="field">
          <div className="field-label-row">
            <label>Description</label>
            <span className={`char-count ${form.description.length > 1800 ? "char-warn" : ""}`}>
              {form.description.length}/2000
            </span>
          </div>
          <textarea
            value={form.description}
            onChange={set("description")}
            placeholder="Describe your idea..."
            maxLength={2000}
          />
        </div>

        <div className="field">
          <label>Status</label>
          <select value={form.status} onChange={set("status")}>
            <option value="DRAFT">Draft</option>
            <option value="PUBLISHED">Published</option>
            <option value="UNDER_REVIEW">Under Review</option>
            <option value="APPROVED">Approved</option>
            <option value="REJECTED">Rejected</option>
          </select>
        </div>

        <div className="field">
          <label>Open to collaboration</label>
          <select value={String(form.openToCollab)} onChange={set("openToCollab")}>
            <option value="true">Yes — accepting requests</option>
            <option value="false">No — just sharing</option>
          </select>
        </div>

        {error && <div className="form-error">{error}</div>}

        {/* ── Actions ── */}
        <div className="modal-actions">
          <button className="btn-outline" onClick={onClose} disabled={loading}>
            Cancel
          </button>
          <button className="btn-primary" style={{ width: "auto", padding: "10px 24px" }}
            onClick={submit} disabled={loading}>
            {loading ? "Saving..." : "Save changes"}
          </button>
        </div>

      </div>
    </div>
  );
}