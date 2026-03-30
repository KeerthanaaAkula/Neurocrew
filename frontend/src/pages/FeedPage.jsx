import { useState, useEffect, useCallback } from "react";
import IdeaCard from "../components/IdeaCard";
import SkeletonCard from "../components/SkeletonCard";
import { getIdeas } from "../services/api";

export default function FeedPage({ user, showToast }) {
  const [ideas, setIdeas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [search, setSearch] = useState("");
  const [filter, setFilter] = useState("all");

  const load = useCallback(() => {
    setLoading(true);
    getIdeas()
      .then((data) => setIdeas(data))
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  }, []);

  useEffect(() => { load(); }, [load]);

  const filtered = [...ideas]
    .reverse()
    .filter((idea) => {
      const matchSearch =
        idea.title.toLowerCase().includes(search.toLowerCase()) ||
        idea.description.toLowerCase().includes(search.toLowerCase());
      const matchFilter =
        filter === "all" ||
        (filter === "open" && idea.openToCollab) ||
        (filter === "mine" && idea.authorId === user?.id);
      return matchSearch && matchFilter;
    });

  return (
    <div>
      <div className="page-header">
        <div className="page-title">All startup ideas</div>
        <span className="badge badge-gray">{ideas.length} ideas</span>
      </div>

      {/* ── Search & Filter ── */}
      <div className="feed-controls">
        <input
          className="search-input"
          placeholder="Search ideas by title or description..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
        <div className="filter-tabs">
          {["all", "open", "mine"].map((f) => (
            <button
              key={f}
              className={`tab-btn${filter === f ? " active" : ""}`}
              onClick={() => setFilter(f)}
            >
              {f === "all" ? "All Ideas" : f === "open" ? "Open to Collab" : "My Ideas"}
            </button>
          ))}
        </div>
      </div>

      {/* ── Skeleton loading ── */}
      {loading && (
        <div className="skeleton-grid">
          {[...Array(6)].map((_, i) => <SkeletonCard key={i} />)}
        </div>
      )}

      {error && <div className="form-error">{error}</div>}

      {!loading && !error && filtered.length === 0 && (
        <div className="empty">
          {search
            ? `No ideas found for "${search}"`
            : "No ideas posted yet. Be the first to post one."}
        </div>
      )}

      {!loading && !error && filtered.length > 0 && (
        <div className="card-grid">
          {filtered.map((idea) => (
            <IdeaCard
              key={idea.id}
              idea={idea}
              currentUser={user}
              onRequestSent={() => {
                load();
                showToast?.("Request sent successfully", "success");
              }}
              showToast={showToast}
            />
          ))}
        </div>
      )}
    </div>
  );
}