// Classe unique qui recense TOUS les endpoints exposés par le backend
// (com.example._4.controller.*). Chaque contrôleur Java a son groupe de
// méthodes statiques ; les méthodes qui prennent un paramètre (ex: un
// numéro de compte) sont des méthodes "dérivées" qui construisent le path
// final à partir d'un endpoint de base.
export class ApiEndpoints {
  // --- AuthController -> /api/auth ---
  static AUTH_BASE = '/api/auth';
  static AUTH_REGISTER = () => `${ApiEndpoints.AUTH_BASE}/register`;
  static AUTH_LOGIN = () => `${ApiEndpoints.AUTH_BASE}/login`;

  // --- AccountController -> /api/accounts ---
  static ACCOUNTS_BASE = '/api/accounts';
  static ACCOUNTS_OPEN = () => `${ApiEndpoints.ACCOUNTS_BASE}/open`;
  static ACCOUNTS_DEPOSIT = () => `${ApiEndpoints.ACCOUNTS_BASE}/deposit`;
  static ACCOUNTS_WITHDRAW = () => `${ApiEndpoints.ACCOUNTS_BASE}/withdraw`;
  static ACCOUNTS_TRANSFER = () => `${ApiEndpoints.ACCOUNTS_BASE}/transfer`;

  // Méthodes dérivées : construites dynamiquement à partir d'un paramètre.
  static ACCOUNTS_BALANCE = (accountNumber) =>
    `${ApiEndpoints.ACCOUNTS_BASE}/${accountNumber}/balance`;
  static ACCOUNTS_HISTORY = (accountNumber) =>
    `${ApiEndpoints.ACCOUNTS_BASE}/${accountNumber}/history`;
  static ACCOUNTS_BY_CUSTOMER = (customerId) =>
    `${ApiEndpoints.ACCOUNTS_BASE}/customer/${customerId}`;
}
