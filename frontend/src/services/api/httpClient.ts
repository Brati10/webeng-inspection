import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080/api",
  withCredentials: true, // Wichtig: Sendet Cookies mit
});

// Request-Interceptor: Basic Auth Header hinzufügen und Content-Type setzen
api.interceptors.request.use((config) => {
  const credentials = localStorage.getItem("credentials");

  // Falls wir Credentials gespeichert haben, schicke sie als Basic Auth
  if (credentials) {
    config.headers.Authorization = `Basic ${credentials}`;
  }

  // Content-Type setzen, ABER nicht für FormData (Browser muss das auto-generieren)
  if (config.data instanceof FormData) {
    // FormData: Nicht Content-Type setzen, Browser generiert mit Boundary
    delete config.headers["Content-Type"];
  } else if (config.data && !config.headers["Content-Type"]) {
    // Für andere Requests: Default auf application/json
    config.headers["Content-Type"] = "application/json";
  }

  return config;
});

// Response-Interceptor für Fehlerbehandlung
api.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error(
      "API Error:",
      error.response?.status,
      error.response?.data || error.message
    );

    // 401: Unauthorized -> Logout
    if (error.response?.status === 401) {
      localStorage.removeItem("user");
      localStorage.removeItem("credentials");
      window.location.href = "/login";
    }

    return Promise.reject(error);
  }
);

export default api;
