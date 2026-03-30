import { useState, useEffect } from "react";
import { getMyIdeas } from "../services/api";
import EditIdeaModal from "../components/EditIdeaModal";
import DeleteConfirmModal from "../components/DeleteConfirmModal";

function timeAgo(ts) {
  const s = Math.floor((Date.now() - new Date(ts).getTime()) / 1000);
  if (s < 60) return "just now";
  if (s < 3600) return `${Math.floor(s / 60)}m ago`;
  if (s < 86400) return `${Math.floor(s / 3600)}h ago`;
  return `${Math.floor(s / 86400)}d ago`;
}

const statusColor = (s) =>
  ({ DRAFT: "gray", PUBLISHED: "teal", UNDER_REVIEW: "amber",
     APPROVED: "blue", REJECTED: "coral" }[s] || "gray");

export default function MyIdeasPage({ showToast }) {
  const [ideas, setIdeas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [editingIdea, setEditingIdea] = useState(null);
  const [deletingIdea, setDeletingIdea] = useState(null);

  useEffect(() => {
    getMyIdeas()
      .then((data) => setIdeas(data))
      .finally(() => setLoading(false));
  }, []);

  const handleUpdated = (updated) => {
    setIdeas((prev) => prev.map((i) => i.id === updated.id ? updated : i));
    showToast?.("Idea updated successfully", "success");
  };

  const handleDeleted = (id) => {
    setIdeas((prev) => prev.filter((i) => i.id !== id));
    showToast?.("Idea deleted successfully", "info");
  };

  if (loading) return (
    <div>
      <div className="page-header">
        <div className="page-title">My ideas</div>
      </div>
      <div className="skeleton-grid">
        {[...Array(3)].map((_, i) => (
          <div key={i} className="skeleton-card">
            <div className="skeleton-line title" />
            <div className="skeleton-line desc1" />
            <div className="skeleton-line desc2" />
            <div className="skeleton-line meta" />
          </div>
        ))}
      </div>
    </div>
  );

  return (
    <div>
      <div className="page-header">
        <div className="page-title">My ideas</div>
        <span className="badge badge-gray">{ideas.length} posted</span>
      </div>

      {ideas.length === 0 && (
        <div className="empty">You have not posted any ideas yet.</div>
      )}

      <div className="card-grid">
        {ideas.map((idea) => {
          const pending  = idea.requests?.filter((r) => r.status === "PENDING").length  || 0;
          const accepted = idea.requests?.filter((r) => r.status === "ACCEPTED").length || 0;
          const total    = idea.requests?.length || 0;

          return (
            <div key={idea.id} className="card">
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
                <span>Posted {timeAgo(idea.createdAt)}</span>
                {idea.updatedAt && idea.updatedAt !== idea.createdAt && (
                  <>
                    <span className="sep">·</span>
                    <span>Updated {timeAgo(idea.updatedAt)}</span>
                  </>
                )}
              </div>

              {/* ── Request + comment summary ── */}
              <div style={{ display: "flex", gap: 8, flexWrap: "wrap", marginBottom: 14 }}>
                <span className="badge badge-gray">
                  {total} request{total !== 1 ? "s" : ""}
                </span>
                {pending  > 0 && (
                  <span className="badge badge-amber">{pending} pending</span>
                )}
                {accepted > 0 && (
                  <span className="badge badge-teal">{accepted} accepted</span>
                )}
                {idea.commentCount > 0 && (
                  <span className="badge badge-blue">
                    {idea.commentCount} comment{idea.commentCount !== 1 ? "s" : ""}
                  </span>
                )}
              </div>

              {/* ── Edit / Delete actions ── */}
              <div className="card-actions">
                <button
                  className="btn-edit"
                  onClick={() => setEditingIdea(idea)}
                >
                  <svg width="13" height="13" viewBox="0 0 16 16" fill="none">
                    <path d="M11.5 2.5l2 2L5 13H3v-2L11.5 2.5z"
                      stroke="currentColor" strokeWidth="1.5"
                      strokeLinecap="round" strokeLinejoin="round"/>
                  </svg>
                  Edit
                </button>
                <button
                  className="btn-delete"
                  onClick={() => setDeletingIdea(idea)}
                >
                  <svg width="13" height="13" viewBox="0 0 16 16" fill="none">
                    <path d="M2 4h12M5 4V2h6v2M13 4l-.8 10H3.8L3 4"
                      stroke="currentColor" strokeWidth="1.5"
                      strokeLinecap="round" strokeLinejoin="round"/>
                  </svg>
                  Delete
                </button>
              </div>
            </div>
          );
        })}
      </div>

      {/* ── Modals ── */}
      {editingIdea && (
        <EditIdeaModal
          idea={editingIdea}
          onClose={() => setEditingIdea(null)}
          onUpdated={handleUpdated}
        />
      )}
      {deletingIdea && (
        <DeleteConfirmModal
          idea={deletingIdea}
          onClose={() => setDeletingIdea(null)}
          onDeleted={handleDeleted}
        />
      )}
    </div>
  );
}