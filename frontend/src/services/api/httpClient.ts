import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080/api",
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true, // Wichtig: Sendet Cookies mit
});

// Request-Interceptor: Basic Auth Header hinzufügen
api.interceptors.request.use((config) => {
  //const user = localStorage.getItem("user");
  const credentials = localStorage.getItem("credentials");

  // Falls wir Credentials gespeichert haben, schicke sie als Basic Auth
  if (credentials) {
    config.headers.Authorization = `Basic ${credentials}`;
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
