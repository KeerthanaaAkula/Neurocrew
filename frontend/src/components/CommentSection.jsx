import { useState, useEffect, useRef } from "react";
import { getComments, addComment, deleteComment } from "../services/api";

const roleColor = (r) =>
  ({ Founder: "purple", Developer: "blue", Designer: "coral", Investor: "amber" }[r] || "teal");

function timeAgo(ts) {
  const s = Math.floor((Date.now() - new Date(ts).getTime()) / 1000);
  if (s < 60) return "just now";
  if (s < 3600) return `${Math.floor(s / 60)}m ago`;
  if (s < 86400) return `${Math.floor(s / 3600)}h ago`;
  return `${Math.floor(s / 86400)}d ago`;
}

export default function CommentSection({ ideaId, currentUser, showToast }) {
  // ← changed "user" to "currentUser" to match IdeaCard.jsx
  const [comments, setComments] = useState([]);
  const [content, setContent] = useState("");
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [deletingId, setDeletingId] = useState(null);
  const bottomRef = useRef(null);

  // ── Load comments ─────────────────────────────────────────
  useEffect(() => {
    if (ideaId) {
      getComments(ideaId)
        .then(setComments)
        .catch(() => showToast?.("Failed to load comments", "error"))
        .finally(() => setLoading(false));
    }
  }, [ideaId]);

  // ── Add comment ───────────────────────────────────────────
  const submit = async () => {
    if (!content.trim()) return;
    setSubmitting(true);
    try {
      const comment = await addComment(ideaId, content.trim());
      setComments((prev) => [...prev, comment]);
      setContent("");
      showToast?.("Comment added", "success");
      setTimeout(() => {
        bottomRef.current?.scrollIntoView({ behavior: "smooth" });
      }, 100);
    } catch (e) {
      showToast?.(e.message, "error");
    } finally {
      setSubmitting(false);
    }
  };

  // ── Delete comment ────────────────────────────────────────
  const handleDelete = async (commentId) => {
    setDeletingId(commentId);
    try {
      await deleteComment(ideaId, commentId);
      setComments((prev) => prev.filter((c) => c.id !== commentId));
      showToast?.("Comment deleted", "info");
    } catch (e) {
      showToast?.(e.message, "error");
    } finally {
      setDeletingId(null);
    }
  };

  // ── Enter to send ─────────────────────────────────────────
  const handleKeyDown = (e) => {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      submit();
    }
  };

  return (
    <div className="comment-section">

      {/* ── Header ── */}
      <div className="comment-header">
        <svg width="14" height="14" viewBox="0 0 16 16" fill="none">
          <path d="M14 2H2a1 1 0 00-1 1v8a1 1 0 001 1h3l2 2 2-2h5a1 1 0 001-1V3a1 1 0 00-1-1z"
            stroke="currentColor" strokeWidth="1.5" strokeLinejoin="round"/>
        </svg>
        <span>Discussion</span>
        <span className="badge badge-gray">{comments.length}</span>
      </div>

      {/* ── Comments list ── */}
      <div className="comment-list">
        {loading && (
          <div className="comment-loading">
            {[...Array(2)].map((_, i) => (
              <div key={i} className="comment-skeleton">
                <div className="skeleton-line"
                  style={{ width: 28, height: 28, borderRadius: "50%", flexShrink: 0 }} />
                <div style={{ flex: 1 }}>
                  <div className="skeleton-line"
                    style={{ width: "40%", height: 11, marginBottom: 6 }} />
                  <div className="skeleton-line" style={{ width: "80%", height: 11 }} />
                </div>
              </div>
            ))}
          </div>
        )}

        {!loading && comments.length === 0 && (
          <div className="comment-empty">
            No comments yet. Be the first to start the discussion!
          </div>
        )}

        {!loading && comments.map((comment) => (
          <div
            key={comment.id}
            className={`comment-item ${comment.authorId === currentUser?.id ? "comment-own" : ""}`}
          >
            {/* ── Avatar ── */}
            <div className={`avatar avatar-sm avatar-${roleColor(comment.authorRole)}`}>
              {comment.authorName?.[0]?.toUpperCase()}
            </div>

            {/* ── Content ── */}
            <div className="comment-body">
              <div className="comment-meta">
                <span className="comment-author">{comment.authorName}</span>
                <span
                  className={`badge badge-${roleColor(comment.authorRole)}`}
                  style={{ fontSize: 10, padding: "1px 7px" }}
                >
                  {comment.authorRole}
                </span>
                <span className="comment-time">{timeAgo(comment.createdAt)}</span>
                {comment.edited && (
                  <span className="comment-edited">edited</span>
                )}
              </div>
              <div className="comment-text">{comment.content}</div>
            </div>

            {/* ── Delete button (own comments only) ── */}
            {comment.authorId === currentUser?.id && (
              <div className="comment-actions">
                <button
                  className="comment-action-btn comment-action-delete"
                  onClick={() => handleDelete(comment.id)}
                  disabled={deletingId === comment.id}
                  title="Delete comment"
                >
                  <svg width="13" height="13" viewBox="0 0 16 16" fill="none">
                    <path d="M2 4h12M5 4V2h6v2M13 4l-.8 10H3.8L3 4"
                      stroke="currentColor" strokeWidth="1.5"
                      strokeLinecap="round" strokeLinejoin="round"/>
                  </svg>
                </button>
              </div>
            )}
          </div>
        ))}
        <div ref={bottomRef} />
      </div>

      {/* ── Add comment input ── */}
      <div className="comment-input-wrap">
        <div className={`avatar avatar-sm avatar-${roleColor(currentUser?.role)}`}>
          {currentUser?.username?.[0]?.toUpperCase()}
        </div>
        <div className="comment-input-box">
          <textarea
            className="comment-input"
            placeholder="Write a comment... (Enter to send, Shift+Enter for new line)"
            value={content}
            onChange={(e) => setContent(e.target.value)}
            onKeyDown={handleKeyDown}
            maxLength={500}
            rows={1}
          />
          <div className="comment-input-footer">
            <span className={`char-count ${content.length > 450 ? "char-warn" : ""}`}>
              {content.length}/500
            </span>
            <button
              className="btn-comment-send"
              onClick={submit}
              disabled={submitting || !content.trim()}
            >
              {submitting ? (
                <span style={{ fontSize: 11 }}>Sending...</span>
              ) : (
                <svg width="14" height="14" viewBox="0 0 16 16" fill="none">
                  <path d="M14 2L2 7l4 3 5-4-4 5 3 4 4-12z"
                    stroke="currentColor" strokeWidth="1.5"
                    strokeLinecap="round" strokeLinejoin="round"/>
                </svg>
              )}
            </button>
          </div>
        </div>
      </div>

    </div>
  );
}