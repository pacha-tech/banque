// "Enum" des verbes HTTP utilisés par la couche network.
export const HttpMethod = Object.freeze({
  GET: 'GET',
  POST: 'POST',
  PUT: 'PUT',
  DELETE: 'DELETE',
});

// Erreur typée renvoyée par le HttpClient, homogène pour toute l'appli.
export class ApiError extends Error {
  constructor(message, status, payload) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.payload = payload;
  }
}
