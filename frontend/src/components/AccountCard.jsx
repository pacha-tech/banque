import { AccountTypeMeta } from '../enums/enums';
import { formatXAF } from '../utils/currency';

export default function AccountCard({ accountNumber, type, balance }) {
  const meta = AccountTypeMeta[type] ?? { label: type, color: 'var(--color-primary)' };
  return (
    <div className="card ledger-card" style={{ '--rail-color': meta.color }}>
      <div className="ledger-type">{meta.label}</div>
      <div className="ledger-number">N° {accountNumber}</div>
      <div className="ledger-amount">{formatXAF(balance)}</div>
    </div>
  );
}
