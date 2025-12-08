import { NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../../context/useAuth";
import "./Navbar.css";

export default function Navbar() {
  const { user, isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login", { replace: true });
  };

  if (!isAuthenticated) {
    return null;
  }

  return (
    <nav className="navbar">
      <div className="navbar-brand">
        <h2 style={{ margin: 0, color: "var(--primary-darker)" }}>
          Inspektionsverwaltung
        </h2>
      </div>

      <div className="navbar-links">
        <NavLink
          to="/"
          className={({ isActive }) =>
            isActive ? "nav-link nav-link-active" : "nav-link"
          }
        >
          Dashboard
        </NavLink>

        <NavLink
          to="/checklists"
          className={({ isActive }) =>
            isActive ? "nav-link nav-link-active" : "nav-link"
          }
        >
          Checklisten
        </NavLink>

        {user?.role === "ADMIN" && (
          <NavLink
            to="/inspections"
            className={({ isActive }) =>
              isActive ? "nav-link nav-link-active" : "nav-link"
            }
          >
            Inspektionen
          </NavLink>
        )}
      </div>

      <div className="navbar-user">
        <div className="user-info">
          <span className="user-name">{user?.displayName}</span>
          <span className="user-role">{user?.role}</span>
        </div>
        <button onClick={handleLogout} className="btn-danger btn-sm">
          Logout
        </button>
      </div>
    </nav>
  );
}
