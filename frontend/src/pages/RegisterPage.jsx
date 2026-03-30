import { useState } from "react";
import { registerApi } from "../services/auth";

export default function RegisterPage({ onRegister, onNavigate }) {
  const [form, setForm] = useState({ username: "", password: "", role: "" });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const set = (k) => (e) => setForm((f) => ({ ...f, [k]: e.target.value }));

  const submit = async () => {
    if (!form.username || !form.password || !form.role) {
      setError("All fields are required."); return;
    }
    setLoading(true); setError("");
    try {
      const user = await registerApi(form.username, form.password, form.role);
      onRegister(user);
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-outer">
      <div className="auth-card">

        {/* ── NeuroCrew Branding ── */}
        <div className="brand-header">
           
          <div className="brand-name">NeuroCrew</div>
        </div>

        <div className="auth-title">Create account</div>
        <div className="auth-sub">Join NeuroCrew and start collaborating</div>

        <div className="field">
          <label>Username</label>
          <input value={form.username} onChange={set("username")} placeholder="choose a username" />
        </div>
        <div className="field">
          <label>Password</label>
          <input type="password" value={form.password} onChange={set("password")} placeholder="min 8 characters" />
        </div>
        <div className="field">
          <label>Your role</label>
          <select value={form.role} onChange={set("role")}>
            <option value="">Select role</option>
            <option>Founder</option>
            <option>Developer</option>
            <option>Designer</option>
            <option>Investor</option>
          </select>
        </div>

        {/* ── Role descriptions ── */}
        {form.role && (
          <div className="role-hint">
            {form.role === "Founder" && "🚀 You can post ideas and manage collaboration requests"}
            {form.role === "Developer" && "💻 You can join ideas as a developer or collaborator"}
            {form.role === "Designer" && "🎨 You can join ideas as a designer or collaborator"}
            {form.role === "Investor" && "💰 You can browse ideas and request collaboration"}
          </div>
        )}

        {error && <div className="form-error">{error}</div>}

        <button className="btn-primary" onClick={submit} disabled={loading}>
          {loading ? "Creating account..." : "Create account"}
        </button>

        <div className="switch-link">
          Already have an account? <a onClick={() => onNavigate("login")}>Sign in</a>
        </div>
      </div>
    </div>
  );
}