import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/useAuth";

export default function LoginPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  const navigate = useNavigate();
  const { login } = useAuth();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setIsLoading(true);

    try {
      await login(username, password);
      navigate("/", { replace: true });
    } catch (err) {
      setError(err instanceof Error ? err.message : "Login fehlgeschlagen");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: "400px", margin: "2rem auto" }}>
      <h1>Login</h1>

      {error && (
        <div
          style={{
            padding: "0.5rem",
            marginBottom: "1rem",
            backgroundColor: "#fee",
            color: "#c00",
            borderRadius: "4px",
          }}
        >
          {error}
        </div>
      )}

      <form onSubmit={handleSubmit}>
        <div style={{ marginBottom: "1rem" }}>
          <label htmlFor="username">Benutzername:</label>
          <br />
          <input
            id="username"
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            style={{ width: "100%", padding: "0.5rem", marginTop: "0.25rem" }}
            required
          />
        </div>

        <div style={{ marginBottom: "1rem" }}>
          <label htmlFor="password">Passwort:</label>
          <br />
          <input
            id="password"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            style={{ width: "100%", padding: "0.5rem", marginTop: "0.25rem" }}
            required
          />
        </div>

        <button
          type="submit"
          disabled={isLoading}
          style={{
            width: "100%",
            padding: "0.75rem",
            backgroundColor: "#007bff",
            color: "white",
            border: "none",
            borderRadius: "4px",
            cursor: isLoading ? "not-allowed" : "pointer",
            opacity: isLoading ? 0.6 : 1,
          }}
        >
          {isLoading ? "Wird angemeldet..." : "Anmelden"}
        </button>
      </form>

      <p style={{ marginTop: "1rem", fontSize: "0.9rem", color: "#666" }}>
        Demo: inspector1 / password123 (INSPECTOR)
        <br />
        Demo: admin1 / password123 (ADMIN)
      </p>
    </div>
  );
}
