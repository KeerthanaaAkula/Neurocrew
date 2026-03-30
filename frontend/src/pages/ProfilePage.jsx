import { useEffect, useMemo, useState } from "react";
import {
  deleteResume,
  getMyIdeas,
  getMyProfile,
  updateProfile,
  uploadResume,
} from "../services/api";

const roleColor = (r) =>
  ({ Founder: "purple", Developer: "blue", Designer: "coral", Investor: "amber" }[r] || "teal");

const skillsToString = (skills) =>
  Array.isArray(skills) ? skills.map((s) => String(s).trim()).filter(Boolean).join(", ") : "";

export default function ProfilePage({ user, showToast }) {
  const [profileLoading, setProfileLoading] = useState(true);
  const [ideasLoading, setIdeasLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [uploading, setUploading] = useState(false);

  const [profile, setProfile] = useState(null);
  const [ideas, setIdeas] = useState([]);

  const [form, setForm] = useState({
    bio: "",
    location: "",
    website: "",
    linkedin: "",
    github: "",
    phone: "",
    skills: "",
    contactVisible: false,
  });

  useEffect(() => {
    setProfileLoading(true);
    getMyProfile()
      .then((p) => {
        setProfile(p);
        setForm({
          bio: p.bio || "",
          location: p.location || "",
          website: p.website || "",
          linkedin: p.linkedin || "",
          github: p.github || "",
          phone: p.phone || "",
          skills: skillsToString(p.skills),
          contactVisible: !!p.contactVisible,
        });
      })
      .catch(() => showToast?.("Failed to load profile", "error"))
      .finally(() => setProfileLoading(false));
  }, []);

  useEffect(() => {
    setIdeasLoading(true);
    getMyIdeas()
      .then(setIdeas)
      .catch(() => showToast?.("Failed to load ideas", "error"))
      .finally(() => setIdeasLoading(false));
  }, []);

  const currentRoleColor = useMemo(() => roleColor(user?.role), [user?.role]);

  const completionPercent = profile?.completionPercent ?? 0;

  const set = (k) => (e) =>
    setForm((f) => ({
      ...f,
      [k]: k === "contactVisible" ? e.target.checked : e.target.value,
    }));

  const onSave = async () => {
    setSaving(true);
    try {
      const updated = await updateProfile({
        bio: form.bio,
        location: form.location,
        website: form.website,
        linkedin: form.linkedin,
        github: form.github,
        phone: form.phone,
        skills: form.skills,
        contactVisible: form.contactVisible,
      });
      setProfile(updated);
      showToast?.("Profile updated", "success");
    } catch (e) {
      showToast?.(e.message, "error");
    } finally {
      setSaving(false);
    }
  };

  const onUploadResume = async (file) => {
    if (!file) return;
    setUploading(true);
    try {
      const updated = await uploadResume(file);
      setProfile(updated);
      showToast?.("Resume uploaded", "success");
    } catch (e) {
      showToast?.(e.message, "error");
    } finally {
      setUploading(false);
    }
  };

  const onDeleteResume = async () => {
    setUploading(true);
    try {
      const updated = await deleteResume();
      setProfile(updated);
      showToast?.("Resume deleted", "info");
    } catch (e) {
      showToast?.(e.message, "error");
    } finally {
      setUploading(false);
    }
  };

  return (
    <div className="profile-wrap">
      <div className="page-header">
        <div className="page-title">Profile</div>
      </div>

      {profileLoading ? (
        <div className="profile-card">Loading profile...</div>
      ) : (
        <>
          <div className="profile-header">
            <div
              className={`profile-avatar avatar-${currentRoleColor}`}
              title={`Role: ${user?.role}`}
            >
              {user?.username?.[0]?.toUpperCase()}
            </div>

            <div className="profile-info">
              <div className="profile-username">{profile?.username || user?.username}</div>
              <div className={`badge badge-${currentRoleColor}`}>{profile?.role || user?.role}</div>

              <div style={{ marginTop: 14 }}>
                <div className="profile-completion completion-label">
                  Profile completion: <strong>{completionPercent}%</strong>
                </div>
                <div className="completion-bar">
                  <div
                    className="completion-fill"
                    style={{ width: `${completionPercent}%` }}
                  />
                </div>
              </div>
            </div>
          </div>

          <div className="profile-body">
            <div className="profile-card">
              <div className="profile-section-title">Edit profile</div>

              <div className="field">
                <label>Bio</label>
                <textarea
                  value={form.bio}
                  onChange={set("bio")}
                  placeholder="Tell others what you're building and what you need"
                />
              </div>

              <div className="field">
                <label>Location</label>
                <input value={form.location} onChange={set("location")} placeholder="e.g. Bangalore" />
              </div>

              <div className="profile-grid">
                <div className="field">
                  <label>Website</label>
                  <input value={form.website} onChange={set("website")} placeholder="https://..." />
                </div>
                <div className="field">
                  <label>LinkedIn</label>
                  <input value={form.linkedin} onChange={set("linkedin")} placeholder="https://linkedin.com/in/..." />
                </div>
              </div>

              <div className="profile-grid">
                <div className="field">
                  <label>GitHub</label>
                  <input value={form.github} onChange={set("github")} placeholder="https://github.com/..." />
                </div>
                <div className="field">
                  <label>Phone</label>
                  <input value={form.phone} onChange={set("phone")} placeholder="+91..." />
                </div>
              </div>

              <div className="field">
                <label>Skills (comma separated)</label>
                <input
                  value={form.skills}
                  onChange={set("skills")}
                  placeholder="React, Node, ML, SQL"
                />
                {profile?.skills?.length > 0 && (
                  <div style={{ marginTop: 10 }} className="skills-wrap">
                    {profile.skills.map((s, idx) => (
                      <span key={`${s}-${idx}`} className="skill-tag">
                        {s}
                      </span>
                    ))}
                  </div>
                )}
              </div>

              <div className="contact-toggle">
                <div>
                  <div className="contact-toggle-label">Show phone to others</div>
                  <div className="contact-toggle-sub">
                    {form.contactVisible ? "Visible on your profile" : "Hidden on your profile"}
                  </div>
                </div>
                <label className="toggle-switch">
                  <input
                    type="checkbox"
                    checked={form.contactVisible}
                    onChange={set("contactVisible")}
                  />
                  <span className="toggle-slider" />
                </label>
              </div>

              <div className="modal-actions" style={{ marginTop: 8 }}>
                <button className="btn-outline" disabled={saving} onClick={() => showToast?.("Profile is editable — changes not saved yet.", "info")}>
                  Tip
                </button>
                <button className="btn-primary" style={{ width: "auto", padding: "10px 24px" }} onClick={onSave} disabled={saving}>
                  {saving ? "Saving..." : "Save changes"}
                </button>
              </div>
            </div>

            <div className="profile-card">
              <div className="profile-section-title">📋 Contact Details</div>
              
              <div className="contact-details-section">
                {form.contactVisible && profile?.phone && (
                  <div className="contact-detail-item">
                    <div className="contact-detail-icon">📱</div>
                    <div className="contact-detail-content">
                      <div className="contact-detail-label">Phone</div>
                      <div className="contact-detail-value">{profile.phone}</div>
                    </div>
                  </div>
                )}
                
                {profile?.website && (
                  <div className="contact-detail-item">
                    <div className="contact-detail-icon">🌐</div>
                    <div className="contact-detail-content">
                      <div className="contact-detail-label">Website</div>
                      <a href={profile.website} target="_blank" rel="noreferrer" className="contact-detail-link">
                        {profile.website}
                      </a>
                    </div>
                  </div>
                )}
                
                {profile?.linkedin && (
                  <div className="contact-detail-item">
                    <div className="contact-detail-icon">💼</div>
                    <div className="contact-detail-content">
                      <div className="contact-detail-label">LinkedIn</div>
                      <a href={profile.linkedin} target="_blank" rel="noreferrer" className="contact-detail-link">
                        View Profile
                      </a>
                    </div>
                  </div>
                )}
                
                {profile?.github && (
                  <div className="contact-detail-item">
                    <div className="contact-detail-icon">💻</div>
                    <div className="contact-detail-content">
                      <div className="contact-detail-label">GitHub</div>
                      <a href={profile.github} target="_blank" rel="noreferrer" className="contact-detail-link">
                        View Repository
                      </a>
                    </div>
                  </div>
                )}
                
                {!form.contactVisible && !profile?.website && !profile?.linkedin && !profile?.github && (
                  <div className="contact-empty-state">
                    <div className="contact-empty-emoji">👤</div>
                    <div className="contact-empty-text">No contact details shared yet</div>
                    <div className="contact-empty-sub">Add contact information above to share with the community</div>
                  </div>
                )}
              </div>
            </div>

            <div className="profile-card">
              <div className="profile-section-title">📄 Resume</div>

              {profile?.hasResume ? (
                <div className="resume-card-modern">
                  <div className="resume-card-header">
                    <div className="resume-icon-modern">📄</div>
                    <div className="resume-info">
                      <div className="resume-name">{profile.resumeFileName || "Resume.pdf"}</div>
                      <div className="resume-label">Ready to share with collaborators</div>
                    </div>
                  </div>
                  <div className="resume-actions-modern">
                    <button
                      className="btn-outline"
                      onClick={() => showToast?.("Download uses a raw endpoint (not wired in UI).", "info")}
                      disabled={uploading}
                    >
                      📥 Download
                    </button>
                    <button className="btn-delete" onClick={onDeleteResume} disabled={uploading}>
                      {uploading ? "..." : "🗑️ Delete"}
                    </button>
                  </div>
                </div>
              ) : (
                <div className="resume-empty-modern">
                  <div className="resume-empty-emoji">📤</div>
                  <div className="resume-empty-text">No resume uploaded yet</div>
                  <div className="resume-empty-sub">Upload a PDF to increase profile completion and attract collaborators</div>
                </div>
              )}

              <div className="field" style={{ marginTop: 16 }}>
                <label>Upload resume (PDF, max 5MB)</label>
                <div className="resume-upload-wrapper">
                  <input
                    type="file"
                    accept="application/pdf"
                    onChange={(e) => onUploadResume(e.target.files?.[0])}
                    disabled={uploading}
                    className="resume-upload-input"
                  />
                  <div className="resume-upload-zone">
                    <div className="resume-upload-icon">📎</div>
                    <div className="resume-upload-text">
                      {uploading ? "Uploading..." : "Drop your resume here or click to select"}
                    </div>
                    <div className="resume-upload-sub">PDF files up to 5MB</div>
                  </div>
                </div>
              </div>
            </div>

          </div>

          <div style={{ marginTop: 22 }}>
            <div className="page-header">
              <div className="page-title">My ideas</div>
              <span className="badge badge-gray">{ideas.length} ideas</span>
            </div>

            {ideasLoading ? (
              <div className="skeleton-grid">
                {[...Array(3)].map((_, i) => (
                  <div key={i} className="skeleton-card">
                    <div className="skeleton-line title" />
                    <div className="skeleton-line desc1" />
                    <div className="skeleton-line meta" />
                  </div>
                ))}
              </div>
            ) : (
              <div className="card-grid">
                {ideas.slice(0, 6).map((idea) => (
                  <div key={idea.id} className="card">
                    <div className="card-title">{idea.title}</div>
                    <div className="card-desc">{idea.description}</div>
                    <div className="card-meta">
                      {idea.status && (
                        <span className="badge badge-purple">{idea.status.replaceAll("_", " ")}</span>
                      )}
                      <span className="badge badge-gray">{idea.openToCollab ? "Open" : "Closed"}</span>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </>
      )}
    </div>
  );
}
