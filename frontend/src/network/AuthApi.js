import { httpClient } from './HttpClient';
import { ApiEndpoints } from './ApiEndpoints';

// Miroir de AuthController.java
export class AuthApi {
  static register(registerRequest) {
    // registerRequest: { email, password, firstName, lastName, phone }
    return httpClient.post(ApiEndpoints.AUTH_REGISTER(), registerRequest);
  }

  static login(loginRequest) {
    // loginRequest: { email, password }
    // Format attendu côté backend : { token: string, customerId: string }.
    // (AuthController.login renvoie actuellement un simple String — il doit
    // être adapté pour renvoyer aussi le customerId, sinon l'app ne peut pas
    // savoir automatiquement quel client est connecté.)
    return httpClient.post(ApiEndpoints.AUTH_LOGIN(), loginRequest);
  }
}
