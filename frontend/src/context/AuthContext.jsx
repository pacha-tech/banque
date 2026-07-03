import { createContext, useContext, useState, useCallback } from 'react';
import { AuthApi } from '../network';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [customerId, setCustomerId] = useState(localStorage.getItem('vaultly_customer_id'));
  const [email, setEmail] = useState(localStorage.getItem('vaultly_email'));

  const login = useCallback(async (loginRequest) => {
    const data = await AuthApi.login(loginRequest);
    // Repli : si le backend ne renvoie encore qu'un token brut (String),
    // on ne peut pas connaître le customerId — voir note dans AuthApi.js.
    const token = typeof data === 'string' ? data : data.token;
    const id = typeof data === 'object' && data ? data.customerId : null;

    localStorage.setItem('vaultly_token', token);
    localStorage.setItem('vaultly_email', loginRequest.email);
    if (id) localStorage.setItem('vaultly_customer_id', id);

    setEmail(loginRequest.email);
    setCustomerId(id);
    return token;
  }, []);

  const register = useCallback((registerRequest) => AuthApi.register(registerRequest), []);

  const logout = useCallback(() => {
    localStorage.removeItem('vaultly_token');
    localStorage.removeItem('vaultly_email');
    localStorage.removeItem('vaultly_customer_id');
    setEmail(null);
    setCustomerId(null);
  }, []);

  const isAuthenticated = Boolean(localStorage.getItem('vaultly_token'));

  return (
    <AuthContext.Provider value={{ email, customerId, isAuthenticated, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);
