import { NavLink } from "react-router-dom";

export default function Navbar() {
  return (
    <nav
      style={{
        padding: "0.5rem 1rem",
        borderBottom: "1px solid #ddd",
        marginBottom: "1rem",
      }}
    >
      <NavLink
        to="/checklists"
        style={({ isActive }) => ({
          marginRight: "1rem",
          textDecoration: isActive ? "underline" : "none",
          fontWeight: isActive ? "bold" : "normal",
        })}
      >
        Checklists
      </NavLink>
      {/* Weitere Links (z. B. Startseite, About, etc.) können später dazu */}
    </nav>
  );
}
