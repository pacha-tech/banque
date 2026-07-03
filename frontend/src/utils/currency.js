// Le Franc CFA (XAF) ne comporte pas de décimales — on arrondit à l'unité.
const formatter = new Intl.NumberFormat('fr-FR', {
  style: 'currency',
  currency: 'XAF',
  maximumFractionDigits: 0,
});

export function formatXAF(value) {
  return formatter.format(Number(value) || 0);
}
