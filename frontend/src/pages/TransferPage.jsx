import { useState } from 'react';
import AppLayout from '../components/AppLayout';
import AccountNumberField from '../components/AccountNumberField';
import { AccountApi } from '../network';
import { TransactionStatusMeta } from '../enums/enums';
import { formatXAF } from '../utils/currency';
import { addStoredAccountNumber } from '../utils/localAccounts';

export default function TransferPage() {
  const [source, setSource] = useState('');
  const [dest, setDest] = useState('');
  const [amount, setAmount] = useState('');
  const [result, setResult] = useState(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const submit = async (e) => {
    e.preventDefault();
    setError('');
    setResult(null);
    setLoading(true);
    try {
      const tx = await AccountApi.transfer({
        sourceAccountNumber: source,
        destAccountNumber: dest,
        amount: Number(amount),
      });
      addStoredAccountNumber(source);
      addStoredAccountNumber(dest);
      setResult(tx);
      setAmount('');
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const statusMeta = result?.status ? TransactionStatusMeta[result.status] : null;

  return (
    <AppLayout title="Virement" subtitle="Transférez des fonds entre deux comptes Vaultly">
      <div className="grid-2">
        <form className="card" onSubmit={submit}>
          <AccountNumberField label="Compte émetteur" value={source} onChange={setSource} />
          <AccountNumberField label="Compte bénéficiaire" value={dest} onChange={setDest} />
          <div className="field">
            <label>Montant (FCFA)</label>
            <input required type="number" min="1" step="1" value={amount} onChange={(e) => setAmount(e.target.value)} placeholder="0" />
          </div>
          {error && <p className="error-text" style={{ marginBottom: 12 }}>{error}</p>}
          <button className="btn btn-primary btn-block" disabled={loading}>
            {loading ? 'Traitement…' : 'Envoyer le virement'}
          </button>
        </form>

        <div className="card result-panel">
          {result ? (
            <>
              <p className="hero-label">Virement traité</p>
              <p className="mono result-amount">{formatXAF(result.amount)}</p>
              {statusMeta && (
                <span className="badge" style={{ '--badge-color': statusMeta.color, width: 'fit-content' }}>
                  {statusMeta.label}
                </span>
              )}
              <p className="mono ellipsis" style={{ fontSize: '0.8rem', color: 'var(--color-text-muted)', marginTop: 12 }}>
                Réf. {result.reference || result.idTransaction}
              </p>
            </>
          ) : (
            <p className="muted-text">Le résultat du virement s'affichera ici.</p>
          )}
        </div>
      </div>
    </AppLayout>
  );
}
