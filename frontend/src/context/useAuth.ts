import { useContext } from "react";
import { AuthContext } from "./AuthContext";
import type { AuthContextType } from "./AuthContext";

export function useAuth(): AuthContextType {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth muss innerhalb von AuthProvider verwendet werden");
  }
  return context;
}
