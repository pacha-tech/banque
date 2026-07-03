// Configuration centrale de l'API. Modifie BASE_URL selon ton environnement
// (le backend Spring Boot du Projet 2 tourne par défaut sur le port 8080).
export const ApiConfig = Object.freeze({
  BASE_URL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  TIMEOUT_MS: 10000,
});
