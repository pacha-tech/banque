import { NavLink } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const LINKS = [
  { to: '/dashboard', label: 'Tableau de bord', icon: '◈' },
  { to: '/accounts', label: 'Mes comptes', icon: '▤' },
  { to: '/transfer', label: 'Virement', icon: '⇄' },
  { to: '/history', label: 'Historique', icon: '≡' },
];

export default function Sidebar() {
  const { logout } = useAuth();
  return (
    <aside className="sidebar">
      <div className="brand"><span className="mark">V</span> Vaultly</div>
      <nav>
        {LINKS.map((l) => (
          <NavLink key={l.to} to={l.to} className={({ isActive }) => (isActive ? 'active' : '')}>
            <span>{l.icon}</span> {l.label}
          </NavLink>
        ))}
      </nav>
      <button className="logout-btn" onClick={logout}>← Déconnexion</button>
    </aside>
  );
}
