const BASE_URL = "http://localhost:8080/api";

export const getToken = () => localStorage.getItem("nc_token");
export const setToken = (t) => localStorage.setItem("nc_token", t);
export const removeToken = () => localStorage.removeItem("nc_token");

export const authHeaders = () => ({
  "Content-Type": "application/json",
  Authorization: `Bearer ${getToken()}`,
});

export async function loginApi(username, password) {
  const res = await fetch(`${BASE_URL}/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username, password }),
  });
  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    throw new Error(err.message || "Invalid credentials");
  }
  const data = await res.json();
  const payload = data.data ?? data;              // ← unwrap ApiResponse
  setToken(payload.token);                        // ← fixed: was data.token
  return payload.user;                            // ← fixed: was data.user
}

export async function registerApi(username, password, role) {
  const res = await fetch(`${BASE_URL}/auth/register`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username, password, role }),
  });
  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    throw new Error(err.message || "Registration failed");
  }
  const data = await res.json();
  const payload = data.data ?? data;              // ← unwrap ApiResponse
  setToken(payload.token);                        // ← fixed: was data.token
  return payload.user;                            // ← fixed: was data.user
}