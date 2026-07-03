import { useState } from 'react';
import AppLayout from '../components/AppLayout';
import AccountNumberField from '../components/AccountNumberField';
import { AccountApi } from '../network';
import { TransactionStatusMeta } from '../enums/enums';
import { formatXAF } from '../utils/currency';
import { addStoredAccountNumber } from '../utils/localAccounts';

// Lecture tolérante : s'adapte si le champ `Transaction` du backend utilise
// des noms légèrement différents (ex: "id" au lieu de "idTransaction").
function pick(obj, keys) {
    for (const k of keys) {
        if (obj?.[k] !== undefined && obj[k] !== null) return obj[k];
    }
    return undefined;
}

export default function TransferPage() {
    const [source, setSource] = useState('');
    const [dest, setDest] = useState('');
    const [amount, setAmount] = useState('');
    const [result, setResult] = useState(null);
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    // Pas de <form onSubmit> : le clic déclenche directement cette fonction,
    // donc aucune soumission HTML native ne peut jamais recharger la page.
    const handleTransfer = async () => {
        if (!source || !dest || !amount || Number(amount) <= 0) {
            setError('Renseigne le compte émetteur, le bénéficiaire et un montant valide.');
            return;
        }
        setError('');
        setResult(null);
        setLoading(true);
        try {
            const tx = await AccountApi.transfer({
                sourceAccountNumber: source,
                destAccountNumber: dest,
                amount: Number(amount),
            });
            // eslint-disable-next-line no-console
            console.log('Réponse /api/accounts/transfer :', tx);
            addStoredAccountNumber(source);
            addStoredAccountNumber(dest);
            setResult(tx ?? {});
            setAmount('');
        } catch (err) {
            // eslint-disable-next-line no-console
            console.error('Échec du virement :', err);
            setError(err.message || 'Le virement a échoué.');
        } finally {
            setLoading(false);
        }
    };

    const txAmount = pick(result, ['amount', 'montant']);
    const txStatus = pick(result, ['status', 'statut']);
    const txRef = pick(result, ['reference', 'idTransaction', 'id', 'transactionId']);
    const statusMeta = txStatus ? TransactionStatusMeta[txStatus] : null;

    return (
        <AppLayout title="Virement" subtitle="Transférez des fonds entre deux comptes Vaultly">
            <div className="grid-2">
                <div className="card">
                    <AccountNumberField label="Compte émetteur" value={source} onChange={setSource} />
                    <AccountNumberField label="Compte bénéficiaire" value={dest} onChange={setDest} />
                    <div className="field">
                        <label>Montant (FCFA)</label>
                        <input type="number" min="1" step="1" value={amount} onChange={(e) => setAmount(e.target.value)} placeholder="0" />
                    </div>
                    <button type="button" className="btn btn-primary btn-block" disabled={loading} onClick={handleTransfer}>
                        {loading ? 'Traitement…' : 'Envoyer le virement'}
                    </button>
                </div>

                <div className="card result-panel">
                    {error ? (
                        <>
                            <p className="hero-label" style={{ color: 'var(--color-danger)' }}>Échec du virement</p>
                            <p className="error-text" style={{ marginTop: 6 }}>{error}</p>
                        </>
                    ) : result ? (
                        <>
                            <p className="hero-label">Virement traité</p>
                            <p className="mono result-amount">
                                {txAmount !== undefined ? formatXAF(txAmount) : '—'}
                            </p>
                            {statusMeta ? (
                                <span className="badge" style={{ '--badge-color': statusMeta.color, width: 'fit-content' }}>
                                    {statusMeta.label}
                                </span>
                            ) : txStatus ? (
                                <span className="badge" style={{ width: 'fit-content' }}>{txStatus}</span>
                            ) : null}
                            {txRef && (
                                <p className="mono ellipsis" style={{ fontSize: '0.8rem', color: 'var(--color-text-muted)', marginTop: 12 }}>
                                    Réf. {txRef}
                                </p>
                            )}
                            {txAmount === undefined && (
                                <p className="hint-text" style={{ marginTop: 10 }}>
                                    Réponse reçue mais champs inattendus — regarde la console (le JSON exact du
                                    backend y est affiché) pour ajuster les noms de champs.
                                </p>
                            )}
                        </>
                    ) : (
                        <p className="muted-text">Le résultat du virement s'affichera ici.</p>
                    )}
                </div>
            </div>
        </AppLayout>
    );
}
