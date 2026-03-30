import { useState } from "react";
import { loginApi } from "../services/auth";

export default function LoginPage({ onLogin, onNavigate }) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const submit = async () => {
    if (!username || !password) { setError("Both fields are required."); return; }
    setLoading(true); setError("");
    try {
      const user = await loginApi(username, password);
      onLogin(user);
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-outer">
      <div className="auth-card">
        <div className="auth-title">Sign in</div>
        <div className="auth-sub">Welcome back to NeuroCrew</div>

        <div className="field">
          <label>Username</label>
          <input
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            placeholder="your username"
          />
        </div>
        <div className="field">
          <label>Password</label>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="password"
            onKeyDown={(e) => e.key === "Enter" && submit()}
          />
        </div>

        {error && <div className="form-error">{error}</div>}

        <button className="btn-primary" onClick={submit} disabled={loading}>
          {loading ? "Signing in..." : "Sign in"}
        </button>

        <div className="switch-link">
          New here? <a onClick={() => onNavigate("register")}>Create an account</a>
        </div>
      </div>
    </div>
  );
}