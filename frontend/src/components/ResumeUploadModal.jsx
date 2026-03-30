import { useState } from "react";
import { uploadRequestResume } from "../services/api";

export default function ResumeUploadModal({ requestId, onClose, showToast, onUploaded }) {
  const [file, setFile] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const maxBytes = 5 * 1024 * 1024; // 5MB (keep consistent with backend)

  const onPickFile = (f) => {
    setError("");
    setFile(f || null);
  };

  const submit = async () => {
    setError("");
    if (!requestId) {
      setError("Request ID missing.");
      return;
    }
    if (!file) {
      setError("Please choose a PDF resume.");
      return;
    }
    if (file.type !== "application/pdf") {
      setError("Resume must be a PDF.");
      return;
    }
    if (file.size > maxBytes) {
      setError("File size must be less than 5MB.");
      return;
    }

    setLoading(true);
    try {
      const updated = await uploadRequestResume(requestId, file);
      showToast?.("Resume uploaded successfully", "success");
      onUploaded?.(updated);
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
        <div className="modal-header">
          <div className="modal-title">Boost your request</div>
          <button className="modal-close" onClick={onClose} aria-label="Close">
            <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
              <path d="M4 4l8 8M12 4l-8 8" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" />
            </svg>
          </button>
        </div>

        <div className="modal-desc" style={{ marginTop: 0 }}>
          Upload your resume for this request.
        </div>

        <div className="field" style={{ marginBottom: 12 }}>
          <label>Resume (PDF, max 5MB)</label>
          <input
            type="file"
            accept="application/pdf"
            onChange={(e) => onPickFile(e.target.files?.[0])}
            disabled={loading}
          />
        </div>

        {error && <div className="form-error">{error}</div>}

        <div className="modal-actions">
          <button className="btn-outline" onClick={onClose} disabled={loading}>
            Skip for now
          </button>
          <button className="btn-primary" style={{ width: "auto", padding: "10px 24px" }} onClick={submit} disabled={loading}>
            {loading ? "Uploading..." : "Upload resume"}
          </button>
        </div>
      </div>
    </div>
  );
}

