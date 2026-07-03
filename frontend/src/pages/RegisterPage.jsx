import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const EMPTY = { email: '', password: '', firstName: '', lastName: '', phone: '' };

export default function RegisterPage() {
  const { register } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState(EMPTY);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const update = (key) => (e) => setForm({ ...form, [key]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      await register(form);
      navigate('/login');
    } catch (err) {
      setError(err.message || "Impossible de créer le compte");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-shell">
      <div className="auth-side">
        <div className="brand"><span className="mark">V</span> Vaultly</div>
        <p className="pitch">Ouvrez un compte courant en une minute — 5 000 FCFA de bonus offerts.</p>
      </div>
      <div className="auth-form-wrap">
        <form className="auth-form" onSubmit={handleSubmit}>
          <h2 style={{ marginBottom: 6 }}>Créer un compte</h2>
          <p style={{ color: 'var(--color-text-muted)', marginBottom: 24 }}>
            Quelques informations pour démarrer.
          </p>

          <div className="grid-2" style={{ gap: 12 }}>
            <div className="field">
              <label>Prénom</label>
              <input required value={form.firstName} onChange={update('firstName')} />
            </div>
            <div className="field">
              <label>Nom</label>
              <input required value={form.lastName} onChange={update('lastName')} />
            </div>
          </div>
          <div className="field">
            <label>Adresse e-mail</label>
            <input type="email" required value={form.email} onChange={update('email')} />
          </div>
          <div className="field">
            <label>Téléphone</label>
            <input required value={form.phone} onChange={update('phone')} />
          </div>
          <div className="field">
            <label>Mot de passe</label>
            <input type="password" required value={form.password} onChange={update('password')} />
          </div>

          {error && <p className="error-text" style={{ marginBottom: 12 }}>{error}</p>}

          <button className="btn btn-accent btn-block" disabled={loading}>
            {loading ? 'Création…' : 'Créer mon compte'}
          </button>
          <p className="auth-switch">Déjà client ? <Link to="/login">Se connecter</Link></p>
        </form>
      </div>
    </div>
  );
}
