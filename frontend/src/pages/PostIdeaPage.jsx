import { useState } from "react";
import { postIdea } from "../services/api";

export default function PostIdeaPage({ onSuccess }) {
  const [form, setForm] = useState({ title: "", description: "", openToCollab: true });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const set = (k) => (e) =>
    setForm((f) => ({
      ...f,
      [k]: k === "openToCollab" ? e.target.value === "yes" : e.target.value,
    }));

  const submit = async () => {
    if (!form.title || !form.description) {
      setError("Title and description are required."); return;
    }
    if (form.title.length < 3) {
      setError("Title must be at least 3 characters."); return;
    }
    if (form.description.length < 10) {
      setError("Description must be at least 10 characters."); return;
    }
    setLoading(true); setError("");
    try {
      await postIdea(form.title, form.description, form.openToCollab);
      onSuccess();
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <div className="post-wrap">
        <div className="page-title">Post a startup idea</div>
        <div className="post-sub">Share with the NeuroCrew community and find collaborators</div>

        <div className="field">
          <div className="field-label-row">
            <label>Idea title</label>
            <span className={`char-count ${form.title.length > 90 ? "char-warn" : ""}`}>
              {form.title.length}/100
            </span>
          </div>
          <input
            value={form.title}
            onChange={set("title")}
            placeholder="e.g. AI-powered peer tutoring platform"
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
            placeholder="What problem does it solve? What kind of collaborators are you looking for?"
            maxLength={2000}
          />
        </div>

        <div className="field">
          <label>Open to collaboration</label>
          <select value={form.openToCollab ? "yes" : "no"} onChange={set("openToCollab")}>
            <option value="yes">Yes — accepting requests</option>
            <option value="no">No — just sharing</option>
          </select>
        </div>

        {error && <div className="form-error">{error}</div>}

        <button className="btn-primary" onClick={submit} disabled={loading}>
          {loading ? "Posting..." : "Post idea"}
        </button>
      </div>
    </div>
  );
}