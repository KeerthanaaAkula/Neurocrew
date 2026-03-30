import { useState, useEffect, useCallback } from "react";
import { getIncomingRequests, getOutgoingRequests, respondToRequest } from "../services/api";

const roleColor = (r) =>
  ({ Founder: "purple", Developer: "blue", Designer: "coral", Investor: "amber" }[r] || "teal");

export default function RequestsPage({ showToast }) {
  const [tab, setTab] = useState("in");
  const [incoming, setIncoming] = useState([]);
  const [outgoing, setOutgoing] = useState([]);
  const [loading, setLoading] = useState(true);
  const [responding, setResponding] = useState(null);

  const load = useCallback(() => {
    setLoading(true);
    Promise.all([getIncomingRequests(), getOutgoingRequests()])
      .then(([inc, out]) => { setIncoming(inc); setOutgoing(out); })
      .finally(() => setLoading(false));
  }, []);

  useEffect(() => { load(); }, [load]);

  const respond = async (id, status) => {
    setResponding(id);
    try {
      await respondToRequest(id, status);
      load();
      showToast?.(
        status === "ACCEPTED" ? "Request accepted" : "Request rejected",
        status === "ACCEPTED" ? "success" : "info"
      );
    } catch (e) {
      showToast?.(e.message, "error");
    } finally {
      setResponding(null);
    }
  };

  if (loading) return (
    <div>
      <div className="page-header">
        <div className="page-title">Requests</div>
      </div>
      <div style={{ display: "flex", flexDirection: "column", gap: 12 }}>
        {[...Array(3)].map((_, i) => (
          <div key={i} className="skeleton-card" style={{ height: 80 }}>
            <div className="skeleton-line desc1" />
            <div className="skeleton-line meta" style={{ marginTop: 10 }} />
          </div>
        ))}
      </div>
    </div>
  );

  const list = tab === "in" ? incoming : outgoing;

  return (
    <div>
      <div className="page-header">
        <div className="page-title">Requests</div>
      </div>

      <div className="tabs-row">
        <button
          className={`tab-btn${tab === "in" ? " active" : ""}`}
          onClick={() => setTab("in")}
        >
          Incoming ({incoming.length})
          {incoming.filter((r) => r.status === "PENDING").length > 0 && (
            <span className="badge badge-amber" style={{ marginLeft: 6 }}>
              {incoming.filter((r) => r.status === "PENDING").length} new
            </span>
          )}
        </button>
        <button
          className={`tab-btn${tab === "out" ? " active" : ""}`}
          onClick={() => setTab("out")}
        >
          Sent ({outgoing.length})
        </button>
      </div>

      {list.length === 0 && (
        <div className="empty">
          {tab === "in"
            ? "No incoming requests yet. Post an idea to start receiving requests!"
            : "You have not sent any requests yet. Browse ideas to get started!"}
        </div>
      )}

      <div className="request-list">
        {list.map((r) => (
          <div key={r.id} className="request-item">
            <div className="request-body">
              <div
                className="request-strip"
                style={{ background: r.type === "COLLAB" ? "#1D9E75" : "#7F77DD" }}
              />
              <div style={{ flex: 1 }}>
                {tab === "in" ? (
                  <>
                    <div className="request-text">
                      <strong>{r.fromName}</strong>
                      <span style={{ color: "var(--muted)" }}>
                        {" · "}
                        {r.type === "COLLAB" ? "Collaboration request" : "Developer request"}
                      </span>
                    </div>
                    <div className="request-sub">For: {r.ideaTitle}</div>
                    <div style={{ marginTop: 4 }}>
                      <span className={`badge badge-${roleColor(r.fromRole)}`}>
                        {r.fromRole}
                      </span>
                    </div>
                    {r.message && (
                      <div className="request-message">"{r.message}"</div>
                    )}
                  </>
                ) : (
                  <>
                    <div className="request-text">
                      {r.type === "COLLAB" ? "Collaboration request" : "Developer request"}
                    </div>
                    <div className="request-sub">For: {r.ideaTitle}</div>
                    {r.message && (
                      <div className="request-message">"{r.message}"</div>
                    )}
                  </>
                )}
              </div>
            </div>

            {tab === "in" && r.status === "PENDING" ? (
              <div className="request-actions">
                <button
                  className="btn-accept"
                  onClick={() => respond(r.id, "ACCEPTED")}
                  disabled={responding === r.id}
                >
                  {responding === r.id ? "..." : "Accept"}
                </button>
                <button
                  className="btn-reject"
                  onClick={() => respond(r.id, "REJECTED")}
                  disabled={responding === r.id}
                >
                  {responding === r.id ? "..." : "Reject"}
                </button>
              </div>
            ) : (
              <span className={`status-${r.status.toLowerCase()}`}>
                {r.status.toLowerCase()}
              </span>
            )}
          </div>
        ))}
      </div>
    </div>
  );
}