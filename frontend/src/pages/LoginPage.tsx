import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/useAuth";
import "./LoginPage.css";

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
    <div className="login-container">
      <div className="login-box">
        <div className="login-header">
          <h1>Inspektionsverwaltung</h1>
          <p>Anmeldung erforderlich</p>
        </div>

        {error && <div className="alert alert-danger">{error}</div>}

        <form onSubmit={handleSubmit} className="login-form">
          <div className="form-group">
            <label htmlFor="username">Benutzername:</label>
            <input
              id="username"
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="z.B. inspector"
              required
              autoFocus
            />
          </div>

          <div className="form-group">
            <label htmlFor="password">Passwort:</label>
            <input
              id="password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Passwort eingeben"
              required
            />
          </div>

          <button
            type="submit"
            disabled={isLoading}
            className="btn-primary btn-lg w-full"
          >
            {isLoading ? "Wird angemeldet..." : "Anmelden"}
          </button>
        </form>

        <div className="login-demo">
          <p className="demo-title">Demo-Zugänge:</p>
          <div className="demo-credentials">
            <div className="credential">
              <strong>Inspector:</strong>
              <span>inspector / inspector123</span>
            </div>
            <div className="credential">
              <strong>Administrator:</strong>
              <span>admin / admin123</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
