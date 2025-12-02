import { NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../../context/useAuth";

export default function Navbar() {
  const { user, isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login", { replace: true });
  };

  if (!isAuthenticated) {
    return null; // Keine Navbar auf Login-Seite
  }

  return (
    <nav
      style={{
        padding: "0.5rem 1rem",
        borderBottom: "1px solid #ddd",
        marginBottom: "1rem",
        display: "flex",
        justifyContent: "space-between",
        alignItems: "center",
      }}
    >
      <div style={{ display: "flex", gap: "1.5rem" }}>
        <NavLink
          to="/"
          style={({ isActive }) => ({
            textDecoration: isActive ? "underline" : "none",
            fontWeight: isActive ? "bold" : "normal",
            color: "#333",
          })}
        >
          Dashboard
        </NavLink>

        <NavLink
          to="/checklists"
          style={({ isActive }) => ({
            textDecoration: isActive ? "underline" : "none",
            fontWeight: isActive ? "bold" : "normal",
            color: "#333",
          })}
        >
          Checklists
        </NavLink>

        {/* Nur ADMIN sieht Inspections */}
        {user?.role === "ADMIN" && (
          <NavLink
            to="/inspections"
            style={({ isActive }) => ({
              textDecoration: isActive ? "underline" : "none",
              fontWeight: isActive ? "bold" : "normal",
              color: "#333",
            })}
          >
            Inspections
          </NavLink>
        )}
      </div>

      <div style={{ display: "flex", alignItems: "center", gap: "1rem" }}>
        <span style={{ fontSize: "0.9rem", color: "#666" }}>
          {user?.displayName} <strong>({user?.role})</strong>
        </span>
        <button
          onClick={handleLogout}
          style={{
            padding: "0.5rem 1rem",
            backgroundColor: "#dc3545",
            color: "white",
            border: "none",
            borderRadius: "4px",
            cursor: "pointer",
            fontSize: "0.9rem",
          }}
        >
          Logout
        </button>
      </div>
    </nav>
  );
}
