import { useState } from 'react';
import AppLayout from '../components/AppLayout';
import TransactionRow from '../components/TransactionRow';
import AccountNumberField from '../components/AccountNumberField';
import { AccountApi } from '../network';

export default function HistoryPage() {
  const [accountNumber, setAccountNumber] = useState('');
  const [history, setHistory] = useState([]);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const search = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const data = await AccountApi.getHistory(accountNumber);
      setHistory(data);
    } catch (err) {
      setError(err.message);
      setHistory([]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <AppLayout title="Historique" subtitle="Consultez toutes les écritures d'un compte">
      <form className="card search-bar" onSubmit={search} style={{ display: 'flex', gap: 10, alignItems: 'center' }}>
        <div style={{ flex: 1 }}>
          <AccountNumberField label="" value={accountNumber} onChange={setAccountNumber} />
        </div>
        <button className="btn btn-primary" disabled={loading}>{loading ? '…' : 'Rechercher'}</button>
      </form>

      {error && <p className="error-text" style={{ marginBottom: 16 }}>{error}</p>}

      <div className="card">
        {history.length === 0 ? (
          <p className="muted-text">Aucune transaction à afficher pour l'instant.</p>
        ) : (
          history.map((tx) => <TransactionRow key={tx.idTransaction} tx={tx} />)
        )}
      </div>
    </AppLayout>
  );
}
