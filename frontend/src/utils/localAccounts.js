// Le backend n'expose pas d'endpoint "liste des comptes d'un client" :
// on mémorise localement les numéros de compte connus (créés/consultés)
// pour pouvoir en afficher le solde et l'historique.
const KEY = 'vaultly_account_numbers';

export function getStoredAccountNumbers() {
  try {
    return JSON.parse(localStorage.getItem(KEY)) || [];
  } catch {
    return [];
  }
}

export function addStoredAccountNumber(accountNumber) {
  const current = getStoredAccountNumbers();
  if (!current.includes(accountNumber)) {
    localStorage.setItem(KEY, JSON.stringify([...current, accountNumber]));
  }
}
