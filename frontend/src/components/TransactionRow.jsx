import { TransactionTypeMeta, TransactionStatusMeta } from '../enums/enums';
import { formatXAF } from '../utils/currency';

const TX_COLOR = {
  DEPOSIT: 'var(--color-accent)',
  WITHDRAWAL: 'var(--color-danger)',
  TRANSFER: 'var(--color-primary)',
};

export default function TransactionRow({ tx }) {
  const typeMeta = TransactionTypeMeta[tx.type] ?? { label: tx.type, sign: '' };
  const statusMeta = TransactionStatusMeta[tx.status] ?? { label: tx.status, color: 'var(--color-text-muted)' };
  const color = TX_COLOR[tx.type] ?? 'var(--color-primary)';

  return (
    <div className="tx-row">
      <div style={{ display: 'flex', alignItems: 'center', gap: 14, minWidth: 0 }}>
        <div className="tx-icon" style={{ '--tx-color': color }}>{typeMeta.sign}</div>
        <div style={{ minWidth: 0 }}>
          <p style={{ fontWeight: 600 }}>{typeMeta.label}</p>
          <p className="mono ellipsis" style={{ fontSize: '0.78rem', color: 'var(--color-text-muted)' }}>
            {tx.reference}
          </p>
        </div>
      </div>
      <div style={{ textAlign: 'right', flexShrink: 0 }}>
        <p className="tx-amount" style={{ color }}>
          {typeMeta.sign} {formatXAF(tx.amount)}
        </p>
        <span className="badge" style={{ '--badge-color': statusMeta.color, marginTop: 4 }}>
          {statusMeta.label}
        </span>
      </div>
    </div>
  );
}
