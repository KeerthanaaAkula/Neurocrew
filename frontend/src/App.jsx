import { useState, useEffect } from "react";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import FeedPage from "./pages/FeedPage";
import PostIdeaPage from "./pages/PostIdeaPage";
import MyIdeasPage from "./pages/MyIdeasPage";
import RequestsPage from "./pages/RequestsPage";
import ProfilePage from "./pages/ProfilePage";
import Navbar from "./components/Navbar";
import { ToastContainer } from "./components/Toast";
import { useToast } from "./hooks/useToast";
import { getToken, removeToken } from "./services/auth";
import "./index.css";

export default function App() {
  const [page, setPage] = useState("login");
  const [user, setUser] = useState(null);
  const { toasts, showToast, removeToast } = useToast();

  useEffect(() => {
    const token = getToken();
    const savedUser = localStorage.getItem("nc_user");
    if (token && savedUser) {
      setUser(JSON.parse(savedUser));
      setPage("feed");
    }
  }, []);

  const handleLogin = (userData) => {
    setUser(userData);
    localStorage.setItem("nc_user", JSON.stringify(userData));
    setPage("feed");
    showToast(`Welcome back, ${userData.username}`, "success");
  };

  const handleLogout = () => {
    removeToken();
    localStorage.removeItem("nc_user");
    setUser(null);
    setPage("login");
    showToast("Signed out successfully", "info");
  };

  return (
    <div className="app">
      {user && (
        <Navbar
          user={user}
          currentPage={page}
          onNavigate={setPage}
          onLogout={handleLogout}
        />
      )}
      <main className="main-content" key={page}>
        {page === "login" && (
          <LoginPage onLogin={handleLogin} onNavigate={setPage} />
        )}
        {page === "register" && (
          <RegisterPage onRegister={handleLogin} onNavigate={setPage} />
        )}
        {page === "feed" && (
          <FeedPage user={user} showToast={showToast} />
        )}
        {page === "post" && (
          <PostIdeaPage
            user={user}
            onSuccess={() => {
              setPage("feed");
              showToast("Idea posted successfully", "success");
            }}
          />
        )}
        {page === "my-ideas" && (
          <MyIdeasPage user={user} showToast={showToast} />
        )}
        {page === "requests" && (
          <RequestsPage user={user} showToast={showToast} />
        )}
        {page === "profile" && (
          <ProfilePage user={user} showToast={showToast} />
        )}
      </main>
      <ToastContainer toasts={toasts} onClose={removeToast} />
    </div>
  );
}