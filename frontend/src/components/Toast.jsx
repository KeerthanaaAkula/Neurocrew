import { useEffect, useState } from "react";

export function Toast({ message, type = "success", onClose }) {
  const [hiding, setHiding] = useState(false);

  useEffect(() => {
    const timer = setTimeout(() => {
      setHiding(true);
      setTimeout(onClose, 250);
    }, 3000);
    return () => clearTimeout(timer);
  }, [onClose]);

  const icons = {
    success: (
      <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
        <circle cx="8" cy="8" r="7" stroke="currentColor" strokeWidth="1.5"/>
        <path d="M5 8l2 2 4-4" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
      </svg>
    ),
    error: (
      <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
        <circle cx="8" cy="8" r="7" stroke="currentColor" strokeWidth="1.5"/>
        <path d="M6 6l4 4M10 6l-4 4" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round"/>
      </svg>
    ),
    info: (
      <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
        <circle cx="8" cy="8" r="7" stroke="currentColor" strokeWidth="1.5"/>
        <path d="M8 7v4M8 5.5v.5" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round"/>
      </svg>
    ),
  };

  return (
    <div
      className={`toast toast-${type} ${hiding ? "hiding" : ""}`}
      onClick={() => { setHiding(true); setTimeout(onClose, 250); }}
      style={{ position: "relative", overflow: "hidden" }}
    >
      {icons[type]}
      <span style={{ flex: 1 }}>{message}</span>
      <div className="toast-progress" />
    </div>
  );
}

export function ToastContainer({ toasts, onClose }) {
  return (
    <div className="toast-container">
      {toasts.map((t) => (
        <Toast key={t.id} message={t.message} type={t.type} onClose={() => onClose(t.id)} />
      ))}
    </div>
  );
}