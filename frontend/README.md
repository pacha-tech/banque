# Vaultly — Front-end React

App bancaire React (Vite) consommant le backend Spring Boot (`com.example._4`).

## Démarrer

```bash
npm install
cp .env.example .env   # ajuste VITE_API_BASE_URL si besoin
npm run dev
```

## Structure clé

```
src/
  network/          <- toute la logique réseau
    ApiConfig.js       config (base URL)
    HttpMethod.js       "enum" des verbes HTTP + ApiError
    HttpClient.js       seul point de contact avec fetch
    ApiEndpoints.js      classe regroupant TOUS les endpoints (+ méthodes dérivées paramétrées)
    AuthApi.js           service Auth (register/login)
    AccountApi.js        service Compte (open/balance/deposit/withdraw/transfer/history)
  enums/            <- miroirs JS des enums Java (AccountType, TransactionType, TransactionStatus, UserStatus)
  context/          <- AuthContext (session)
  components/       <- Sidebar, AppLayout, AccountCard, TransactionRow...
  pages/            <- Login, Register, Dashboard, Accounts, Transfer, History
  styles/           <- variables.css (design tokens), base.css, components.css, layout.css
```

## Design

Thème clair, pro et coloré : navy `#0F2544` (marque), émeraude `#0FA968` (dépôts/épargne),
ambre `#FFB020` (en attente), rouge `#E14B4B` (retraits/échecs). Titres en **Space Grotesk**,
texte en **Inter**, montants et numéros de compte en **IBM Plex Mono** pour un effet "registre
comptable" — c'est la signature visuelle des cartes de compte (`.ledger-card`).

Le backend n'exposant pas de "liste des comptes d'un client", le dashboard mémorise
localement les numéros de compte consultés/créés (`src/utils/localAccounts.js`).
