import { httpClient } from './HttpClient';
import { ApiEndpoints } from './ApiEndpoints';

// Miroir de AccountController.java
export class AccountApi {
  static openAccount(openAccountRequest) {
    // { customerId, type: AccountType, initialBalance }
    // Renvoie désormais un AccountResponse { idAccount, accountNumber, type, balance }
    return httpClient.post(ApiEndpoints.ACCOUNTS_OPEN(), openAccountRequest);
  }

  static getAccountsByCustomer(customerId) {
    // Renvoie AccountResponse[] { idAccount, accountNumber, type, balance }
    return httpClient.get(ApiEndpoints.ACCOUNTS_BY_CUSTOMER(customerId));
  }

  static getBalance(accountNumber) {
    return httpClient.get(ApiEndpoints.ACCOUNTS_BALANCE(accountNumber));
  }

  static deposit(transactionRequest) {
    // { accountNumber, amount }
    return httpClient.post(ApiEndpoints.ACCOUNTS_DEPOSIT(), transactionRequest);
  }

  static withdraw(transactionRequest) {
    return httpClient.post(ApiEndpoints.ACCOUNTS_WITHDRAW(), transactionRequest);
  }

  static transfer(transfertRequest) {
    // { sourceAccountNumber, destAccountNumber, amount }
    return httpClient.post(ApiEndpoints.ACCOUNTS_TRANSFER(), transfertRequest);
  }

  static getHistory(accountNumber) {
    return httpClient.get(ApiEndpoints.ACCOUNTS_HISTORY(accountNumber));
  }
}
