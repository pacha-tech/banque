// Miroirs exacts des enums Java du backend (com.example._4.enums.*)
// Object.freeze pour un comportement immuable proche d'un enum réel.

export const AccountType = Object.freeze({
  CHECKING: 'CHECKING',
  SAVINGS: 'SAVINGS',
});

export const TransactionStatus = Object.freeze({
  COMPLETED: 'COMPLETED',
  PENDING: 'PENDING',
  FAILED: 'FAILED',
});

export const TransactionType = Object.freeze({
  DEPOSIT: 'DEPOSIT',
  WITHDRAWAL: 'WITHDRAWAL',
  TRANSFER: 'TRANSFER',
});

export const UserStatus = Object.freeze({
  ACTIVE: 'ACTIVE',
  INACTIVE: 'INACTIVE',
});

// Libellés FR + couleurs thématiques associées à chaque valeur,
// utilisés par les composants d'affichage (badges, rails colorés...).
export const AccountTypeMeta = {
  [AccountType.CHECKING]: { label: 'Compte courant', color: 'var(--color-primary)' },
  [AccountType.SAVINGS]: { label: 'Compte épargne', color: 'var(--color-accent)' },
};

export const TransactionStatusMeta = {
  [TransactionStatus.COMPLETED]: { label: 'Terminée', color: 'var(--color-accent)' },
  [TransactionStatus.PENDING]: { label: 'En attente', color: 'var(--color-amber)' },
  [TransactionStatus.FAILED]: { label: 'Échouée', color: 'var(--color-danger)' },
};

export const TransactionTypeMeta = {
  [TransactionType.DEPOSIT]: { label: 'Dépôt', sign: '+' },
  [TransactionType.WITHDRAWAL]: { label: 'Retrait', sign: '−' },
  [TransactionType.TRANSFER]: { label: 'Virement', sign: '⇄' },
};

export const UserStatusMeta = {
  [UserStatus.ACTIVE]: { label: 'Actif', color: 'var(--color-accent)' },
  [UserStatus.INACTIVE]: { label: 'Inactif', color: 'var(--color-text-muted)' },
};
