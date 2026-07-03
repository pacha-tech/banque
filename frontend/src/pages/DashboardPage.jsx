import { useEffect, useState } from 'react';
import AppLayout from '../components/AppLayout';
import AccountCard from '../components/AccountCard';
import AccountNumberField from '../components/AccountNumberField';
import { AccountApi } from '../network';
import { formatXAF } from '../utils/currency';
import { useAuth } from '../context/AuthContext';
import { addStoredAccountNumber, getStoredAccountNumbers } from '../utils/localAccounts';

export default function DashboardPage() {
  const { customerId } = useAuth();
  const [accounts, setAccounts] = useState([]);
  const [newNumber, setNewNumber] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const loadFromCustomer = async () => {
    setLoading(true);
    setError('');
    try {
      const list = await AccountApi.getAccountsByCustomer(customerId);
      list.forEach((a) => addStoredAccountNumber(a.accountNumber));
      setAccounts(list);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  // Repli : si le customerId n'est pas encore disponible (backend de login
  // pas encore mis à jour), on retombe sur les numéros suivis manuellement.
  const loadFromTrackedNumbers = async () => {
    setLoading(true);
    const numbers = getStoredAccountNumbers();
    const results = await Promise.all(
      numbers.map(async (accountNumber) => {
        try {
          const balance = await AccountApi.getBalance(accountNumber);
          return { accountNumber, balance, type: null };
        } catch {
          return null;
        }
      })
    );
    setAccounts(results.filter(Boolean));
    setLoading(false);
  };

  useEffect(() => {
    if (customerId) loadFromCustomer();
    else loadFromTrackedNumbers();
  }, [customerId]);

  const totalBalance = accounts.reduce((sum, a) => sum + Number(a.balance || 0), 0);

  const handleTrack = (e) => {
    e.preventDefault();
    if (!newNumber.trim()) return;
    addStoredAccountNumber(newNumber.trim());
    setNewNumber('');
    loadFromTrackedNumbers();
  };

  return (
    <AppLayout title="Tableau de bord" subtitle="Vue d'ensemble de vos comptes Vaultly">
      <div className="card hero-balance">
        <p className="hero-label">Solde total</p>
        <p className="mono hero-amount">{formatXAF(totalBalance)}</p>
      </div>

      {error && <p className="error-text" style={{ marginBottom: 16 }}>{error}</p>}

      {loading ? (
        <p className="muted-text">Chargement des soldes…</p>
      ) : accounts.length === 0 ? (
        <p className="muted-text">Aucun compte pour le moment.</p>
      ) : (
        <div className="grid-3" style={{ marginBottom: 24 }}>
          {accounts.map((a) => (
            <AccountCard key={a.accountNumber} accountNumber={a.accountNumber} type={a.type} balance={a.balance} />
          ))}
        </div>
      )}

      {!customerId && (
        <div className="card" style={{ maxWidth: 420 }}>
          <p className="panel-title">Suivre un compte existant</p>
          <form onSubmit={handleTrack} className="inline-row">
            <div style={{ flex: 1 }}>
              <AccountNumberField label="" value={newNumber} onChange={setNewNumber} required={false} />
            </div>
            <button className="btn btn-primary">Ajouter</button>
          </form>
        </div>
      )}
    </AppLayout>
  );
}
