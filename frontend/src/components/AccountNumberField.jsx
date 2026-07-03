import { useId } from 'react';
import { getStoredAccountNumbers } from '../utils/localAccounts';

export default function AccountNumberField({ label, value, onChange, required = true }) {
  const listId = useId();
  const known = getStoredAccountNumbers();

  return (
    <div className="field">
      <label>{label}</label>
      <input
        required={required}
        className="mono"
        list={listId}
        placeholder="Ex. FR76 3000…"
        value={value}
        onChange={(e) => onChange(e.target.value)}
        autoComplete="off"
      />
      <datalist id={listId}>
        {known.map((n) => (
          <option key={n} value={n} />
        ))}
      </datalist>
    </div>
  );
}
