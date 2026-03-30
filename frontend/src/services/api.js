import { authHeaders } from "./auth";

const BASE_URL = "http://localhost:8080/api";

async function handle(res) {
  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    throw new Error(err.message || "Request failed");
  }
  const data = await res.json();
  return data.data ?? data;
}

// ── Ideas ─────────────────────────────────────────────────────
export const getIdeas = () =>
  fetch(`${BASE_URL}/ideas`, { headers: authHeaders() }).then(handle);

export const getMyIdeas = () =>
  fetch(`${BASE_URL}/ideas/mine`, { headers: authHeaders() }).then(handle);

export const getIdeaById = (id) =>
  fetch(`${BASE_URL}/ideas/${id}`, { headers: authHeaders() }).then(handle);

export const postIdea = (title, description, openToCollab) =>
  fetch(`${BASE_URL}/ideas`, {
    method: "POST",
    headers: authHeaders(),
    body: JSON.stringify({ title, description, openToCollab }),
  }).then(handle);

export const updateIdea = (id, data) =>
  fetch(`${BASE_URL}/ideas/${id}`, {
    method: "PUT",
    headers: authHeaders(),
    body: JSON.stringify(data),
  }).then(handle);

export const deleteIdea = (id) =>
  fetch(`${BASE_URL}/ideas/${id}`, {
    method: "DELETE",
    headers: authHeaders(),
  }).then(handle);

// ── Requests ──────────────────────────────────────────────────
export const getIncomingRequests = () =>
  fetch(`${BASE_URL}/requests/incoming`, { headers: authHeaders() }).then(handle);

export const getOutgoingRequests = () =>
  fetch(`${BASE_URL}/requests/outgoing`, { headers: authHeaders() }).then(handle);

export const sendRequest = (ideaId, type) =>
  fetch(`${BASE_URL}/requests`, {
    method: "POST",
    headers: authHeaders(),
    body: JSON.stringify({ ideaId, type }),
  }).then(handle);

export const respondToRequest = (requestId, status) =>
  fetch(`${BASE_URL}/requests/${requestId}`, {
    method: "PUT",
    headers: authHeaders(),
    body: JSON.stringify({ status }),
  }).then(handle);

// ── Comments ──────────────────────────────────────────────────
export const getComments = (ideaId) =>
  fetch(`${BASE_URL}/ideas/${ideaId}/comments`, {
    headers: authHeaders(),
  }).then(handle);

export const addComment = (ideaId, content) =>
  fetch(`${BASE_URL}/ideas/${ideaId}/comments`, {
    method: "POST",
    headers: authHeaders(),
    body: JSON.stringify({ content }),
  }).then(handle);

export const updateComment = (ideaId, commentId, content) =>
  fetch(`${BASE_URL}/ideas/${ideaId}/comments/${commentId}`, {
    method: "PUT",
    headers: authHeaders(),
    body: JSON.stringify({ content }),
  }).then(handle);

export const deleteComment = (ideaId, commentId) =>
  fetch(`${BASE_URL}/ideas/${ideaId}/comments/${commentId}`, {
    method: "DELETE",
    headers: authHeaders(),
  }).then(handle);

// ── Request Resume (attached to COLLAB/DEV request) ─────────
export const uploadRequestResume = (requestId, file) => {
  const formData = new FormData();
  formData.append("file", file);
  const { Authorization } = authHeaders();
  return fetch(`${BASE_URL}/requests/${requestId}/resume`, {
    method: "POST",
    headers: { Authorization },
    body: formData,
  }).then(handle);
};

// ── Profile ───────────────────────────────────────────────────
export const getMyProfile = () =>
  fetch(`${BASE_URL}/profile/me`, {
    headers: authHeaders(),
  }).then(handle);

export const getProfileByUserId = (userId) =>
  fetch(`${BASE_URL}/profile/${userId}`, {
    headers: authHeaders(),
  }).then(handle);

export const updateProfile = (data) =>
  fetch(`${BASE_URL}/profile/me`, {
    method: "PUT",
    headers: authHeaders(),
    body: JSON.stringify(data),
  }).then(handle);

export const uploadResume = (file) => {
  const formData = new FormData();
  formData.append("file", file);
  const { Authorization } = authHeaders();                     // ← only pass auth header
  return fetch(`${BASE_URL}/profile/me/resume`, {
    method: "POST",
    headers: { Authorization },                                // ← no Content-Type for multipart
    body: formData,
  }).then(handle);
};

export const deleteResume = () =>
  fetch(`${BASE_URL}/profile/me/resume`, {
    method: "DELETE",
    headers: authHeaders(),
  }).then(handle);

export const downloadResume = (userId) =>
  fetch(`${BASE_URL}/profile/${userId}/resume`, {
    headers: authHeaders(),
  });                                                          // ← no handle, returns raw response