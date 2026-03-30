import { useState } from "react";
import { sendRequest } from "../services/api";
import CommentSection from "./CommentSection";
import ResumeUploadModal from "./ResumeUploadModal";

const roleColor = (r) =>
  ({ Founder: "purple", Developer: "blue", Designer: "coral", Investor: "amber" }[r] || "teal");

const statusColor = (s) =>
  ({ DRAFT: "gray", PUBLISHED: "teal", UNDER_REVIEW: "amber",
     APPROVED: "blue", REJECTED: "coral" }[s] || "gray");

function timeAgo(ts) {
  const s = Math.floor((Date.now() - new Date(ts).getTime()) / 1000);
  if (s < 60) return "just now";
  if (s < 3600) return `${Math.floor(s / 60)}m ago`;
  if (s < 86400) return `${Math.floor(s / 3600)}h ago`;
  return `${Math.floor(s / 86400)}d ago`;
}

export default function IdeaCard({ idea, currentUser, onRequestSent, showToast }) {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [showComments, setShowComments] = useState(false);     // ← toggle comments
  const [resumeModalOpen, setResumeModalOpen] = useState(false);
  const [pendingRequestId, setPendingRequestId] = useState(null);

  const isOwn = idea.authorId === currentUser.id;
  const myCollab = idea.myCollabRequest;
  const myDev = idea.myDevRequest;

  const send = async (type) => {
    setLoading(true);
    setError("");
    try {
      const created = await sendRequest(idea.id, type);
      onRequestSent();
      showToast?.("Request sent successfully", "success");
      setPendingRequestId(created?.id);
      setResumeModalOpen(true);
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="card">
      <div className="card-top">
        <div className="card-title">{idea.title}</div>
        <div style={{ display: "flex", gap: 6, flexWrap: "wrap" }}>
          {idea.status && (
            <span className={`badge badge-${statusColor(idea.status)}`}>
              {idea.status.replace("_", " ")}
            </span>
          )}
          {idea.openToCollab
            ? <span className="badge badge-teal">Open</span>
            : <span className="badge badge-gray">Closed</span>}
        </div>
      </div>

      <div className="card-desc">{idea.description}</div>

      <div className="card-meta">
        <div className={`avatar avatar-sm avatar-${roleColor(idea.authorRole)}`}>
          {idea.authorName[0].toUpperCase()}
        </div>
        <span>{idea.authorName}</span>
        <span className="sep">·</span>
        <span className={`badge badge-${roleColor(idea.authorRole)}`}>{idea.authorRole}</span>
        <span className="sep">·</span>
        <span>{timeAgo(idea.createdAt)}</span>
        {idea.totalRequests > 0 && (
          <>
            <span className="sep">·</span>
            <span className="badge badge-gray">
              {idea.totalRequests} request{idea.totalRequests !== 1 ? "s" : ""}
            </span>
          </>
        )}
      </div>

      <div className="card-actions">
        {isOwn ? (
          <span className="own-label">Your idea</span>
        ) : !idea.openToCollab ? (
          <span className="own-label">Not accepting requests</span>
        ) : (
          <>
            {!myCollab ? (
              <button className="btn-teal" onClick={() => send("COLLAB")} disabled={loading}>
                Request collab
              </button>
            ) : (
              <span className="sent-label">
                Collab: <span className={`status-${myCollab.toLowerCase()}`}>
                  {myCollab.toLowerCase()}
                </span>
              </span>
            )}
            {currentUser.role === "Developer" && (
              <>
                {!myDev ? (
                  <button className="btn-purple" onClick={() => send("DEV")} disabled={loading}>
                    Join as developer
                  </button>
                ) : (
                  <span className="sent-label">
                    Dev: <span className={`status-${myDev.toLowerCase()}`}>
                      {myDev.toLowerCase()}
                    </span>
                  </span>
                )}
              </>
            )}
          </>
        )}

        {/* ── Comment toggle button ── */}
        <button
          className={`btn-comment-toggle ${showComments ? "active" : ""}`}
          onClick={() => setShowComments((prev) => !prev)}
        >
          <svg width="13" height="13" viewBox="0 0 16 16" fill="none">
            <path d="M14 2H2a1 1 0 00-1 1v8a1 1 0 001 1h3l2 2 2-2h5a1 1 0 001-1V3a1 1 0 00-1-1z"
              stroke="currentColor" strokeWidth="1.5" strokeLinejoin="round"/>
          </svg>
          {idea.commentCount > 0 ? idea.commentCount : ""} Comment{idea.commentCount !== 1 ? "s" : ""}
        </button>
      </div>

      {error && <div className="form-error" style={{ marginTop: 8 }}>{error}</div>}

      {/* ── Comment Section ── */}
      {showComments && (
        <div className="comment-section-wrap">
          <CommentSection
            ideaId={idea.id}
            currentUser={currentUser}
            showToast={showToast}
          />
        </div>
      )}

      {resumeModalOpen && (
        <ResumeUploadModal
          onClose={() => setResumeModalOpen(false)}
          showToast={showToast}
          requestId={pendingRequestId}
        />
      )}
    </div>
  );
}