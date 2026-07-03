import Sidebar from './Sidebar';

export default function AppLayout({ title, subtitle, children }) {
  return (
    <div className="app-shell">
      <Sidebar />
      <main className="main-area">
        {title && (
          <div className="page-header">
            <h1>{title}</h1>
            {subtitle && <p>{subtitle}</p>}
          </div>
        )}
        {children}
      </main>
    </div>
  );
}
