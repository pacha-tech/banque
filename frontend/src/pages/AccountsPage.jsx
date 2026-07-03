import { useState } from 'react';
import AppLayout from '../components/AppLayout';
import Tabs from '../components/Tabs';
import AccountNumberField from '../components/AccountNumberField';
import { AccountApi } from '../network';
import { AccountType, AccountTypeMeta } from '../enums/enums';
import { useAuth } from '../context/AuthContext';
import { addStoredAccountNumber } from '../utils/localAccounts';

const TABS = [
  { value: 'open', label: 'Nouveau compte', icon: '＋' },
  { value: 'deposit', label: 'Dépôt', icon: '↓' },
  { value: 'withdraw', label: 'Retrait', icon: '↑' },
];

function Feedback({ message, tone = 'ok' }) {
  if (!message) return null;
  return (
    <p className={tone === 'error' ? 'error-text' : 'success-text'} style={{ marginBottom: 14 }}>
      {message}
    </p>
  );
}

function OpenAccountPanel() {
  const { customerId } = useAuth();
  const [manualId, setManualId] = useState('');
  const [type, setType] = useState(AccountType.CHECKING);
  const [initialBalance, setInitialBalance] = useState('');
  const [feedback, setFeedback] = useState(null);
  const [loading, setLoading] = useState(false);

  const effectiveCustomerId = customerId || manualId;

  const submit = async (e) => {
    e.preventDefault();
    setFeedback(null);
    setLoading(true);
    try {
      const created = await AccountApi.openAccount({
        customerId: effectiveCustomerId,
        type,
        initialBalance: Number(initialBalance || 0),
      });
      if (created?.accountNumber) addStoredAccountNumber(created.accountNumber);
      setFeedback({
        tone: 'ok',
        text: created?.accountNumber
          ? `Compte ouvert avec succès — n° ${created.accountNumber}.`
          : 'Compte ouvert avec succès.',
      });
      setInitialBalance('');
    } catch (err) {
      setFeedback({ tone: 'error', text: err.message });
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={submit} className="panel-form">
      {!customerId && (
        <div className="field">
          <label>Identifiant client</label>
          <input required value={manualId} onChange={(e) => setManualId(e.target.value)} />
          <p className="hint-text">
            Non détecté automatiquement — connecte-toi à nouveau une fois le backend mis à jour pour ne plus voir ce champ.
          </p>
        </div>
      )}

      <div className="tile-select">
        {Object.values(AccountType).map((t) => (
          <button
            type="button"
            key={t}
            className={`tile ${type === t ? 'active' : ''}`}
            onClick={() => setType(t)}
          >
            {AccountTypeMeta[t].label}
          </button>
        ))}
      </div>

      <div className="field">
        <label>Solde initial (FCFA)</label>
        <input type="number" min="0" step="1" value={initialBalance}
          onChange={(e) => setInitialBalance(e.target.value)} placeholder="0" />
      </div>

      <Feedback message={feedback?.text} tone={feedback?.tone} />
      <button className="btn btn-primary btn-block" disabled={loading}>
        {loading ? 'Ouverture…' : 'Ouvrir le compte'}
      </button>
    </form>
  );
}

function MoveFundsPanel({ action, verb }) {
  const [accountNumber, setAccountNumber] = useState('');
  const [amount, setAmount] = useState('');
  const [feedback, setFeedback] = useState(null);
  const [loading, setLoading] = useState(false);

  const submit = async (e) => {
    e.preventDefault();
    setFeedback(null);
    setLoading(true);
    try {
      const result = await action({ accountNumber, amount: Number(amount) });
      addStoredAccountNumber(accountNumber);
      setFeedback({ tone: 'ok', text: typeof result === 'string' ? result : 'Opération effectuée.' });
      setAmount('');
    } catch (err) {
      setFeedback({ tone: 'error', text: err.message });
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={submit} className="panel-form">
      <AccountNumberField label="N° de compte" value={accountNumber} onChange={setAccountNumber} />
      <div className="field">
        <label>Montant (FCFA)</label>
        <input required type="number" min="1" step="1" value={amount}
          onChange={(e) => setAmount(e.target.value)} placeholder="0" />
      </div>
      <Feedback message={feedback?.text} tone={feedback?.tone} />
      <button className="btn btn-primary btn-block" disabled={loading}>
        {loading ? 'Traitement…' : verb}
      </button>
    </form>
  );
}

export default function AccountsPage() {
  const [tab, setTab] = useState('open');

  return (
    <AppLayout title="Mes comptes" subtitle="Ouvrez un compte ou déplacez des fonds">
      <div className="card panel-card">
        <Tabs tabs={TABS} active={tab} onChange={setTab} />
        <div className="panel-body">
          {tab === 'open' && <OpenAccountPanel />}
          {tab === 'deposit' && <MoveFundsPanel action={AccountApi.deposit} verb="Déposer" />}
          {tab === 'withdraw' && <MoveFundsPanel action={AccountApi.withdraw} verb="Retirer" />}
        </div>
      </div>
    </AppLayout>
  );
}
