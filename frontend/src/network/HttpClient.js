import { ApiConfig } from './ApiConfig';
import { HttpMethod, ApiError } from './HttpMethod';

// Seule classe autorisée à parler à `fetch`. Toutes les requêtes réseau
// de l'application (via ApiEndpoints puis les *Api services) transitent ici.
class HttpClient {
  #baseUrl;

  constructor(baseUrl) {
    this.#baseUrl = baseUrl;
  }

  #buildHeaders() {
    const headers = { 'Content-Type': 'application/json' };
    const token = localStorage.getItem('vaultly_token');
    if (token) headers.Authorization = `Bearer ${token}`;
    return headers;
  }

  async #request(method, path, body) {
    const controller = new AbortController();
    const timer = setTimeout(() => controller.abort(), ApiConfig.TIMEOUT_MS);

    try {
      const response = await fetch(`${this.#baseUrl}${path}`, {
        method,
        headers: this.#buildHeaders(),
        body: body !== undefined ? JSON.stringify(body) : undefined,
        signal: controller.signal,
      });

      const contentType = response.headers.get('content-type') || '';
      const payload = contentType.includes('application/json')
        ? await response.json().catch(() => null)
        : await response.text();

      if (!response.ok) {
        const message = typeof payload === 'string' ? payload : payload?.message || 'Erreur serveur';
        throw new ApiError(message, response.status, payload);
      }
      return payload;
    } catch (err) {
      if (err.name === 'AbortError') {
        throw new ApiError('Le serveur ne répond pas (timeout)', 0, null);
      }
      if (err instanceof ApiError) throw err;
      throw new ApiError(err.message || 'Impossible de contacter le serveur', 0, null);
    } finally {
      clearTimeout(timer);
    }
  }

  get(path) {
    return this.#request(HttpMethod.GET, path);
  }
  post(path, body) {
    return this.#request(HttpMethod.POST, path, body);
  }
  put(path, body) {
    return this.#request(HttpMethod.PUT, path, body);
  }
  delete(path) {
    return this.#request(HttpMethod.DELETE, path);
  }
}

export const httpClient = new HttpClient(ApiConfig.BASE_URL);
