import React from "react";

const roleColor = (r) =>
  ({ Founder: "purple", Developer: "blue", Designer: "coral", Investor: "amber" }[r] || "teal");

export default function Navbar({ user, currentPage, onNavigate, onLogout }) {
  return (
    <nav className="navbar">

      {/* ── Left: Logo ── */}
      <div className="nav-logo" onClick={() => onNavigate("feed")}>
        <span className="logo-text">NeuroCrew</span>
      </div>

      {/* ── Center: Nav links ── */}
      <div className="nav-links">
        <button className={`nav-btn ${currentPage === "feed" ? "active" : ""}`}
          onClick={() => onNavigate("feed")}>Feed</button>
        <button className={`nav-btn ${currentPage === "post" ? "active" : ""}`}
          onClick={() => onNavigate("post")}>Post idea</button>
        <button className={`nav-btn ${currentPage === "my-ideas" ? "active" : ""}`}
          onClick={() => onNavigate("my-ideas")}>My ideas</button>
        <button className={`nav-btn ${currentPage === "requests" ? "active" : ""}`}
          onClick={() => onNavigate("requests")}>Requests</button>
      </div>

      {/* ── Right: Avatar + Profile + Logout ── */}
      <div className="nav-right">
        <div
          className={`nav-avatar avatar-${roleColor(user?.role)} ${currentPage === "profile" ? "nav-avatar-active" : ""}`}
          onClick={() => onNavigate("profile")}          // ← click avatar to go to profile
          title="View profile"
        >
          {user?.username?.[0]?.toUpperCase()}
        </div>
        <span className="nav-username">{user?.username}</span>
        <button className="btn-outline" onClick={onLogout}>Sign out</button>
      </div>

    </nav>
  );
}