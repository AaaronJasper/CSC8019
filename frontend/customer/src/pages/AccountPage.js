import '../App.css';
import { Link } from 'react-router-dom';

function AccountPage() {
  return (
    <main className="account-page">
      <nav className="account-navbar">
        <img src="/images/logo.png" alt="Whistlestop Coffee" className="account-logo" />

        <div className="account-back">
          <span className="back-icon">←</span>
          <span>back</span>
        </div>
      </nav>

      <section className="account-content">
        <h1>Buy nine coffees, get your tenth for free!</h1>

        <div className="loyalty-card">
          <div className="stamp filled">☕</div>
          <div className="stamp filled">☕</div>
          <div className="stamp filled">☕</div>

          <div className="stamp filled">☕</div>
          <div className="stamp filled">☕</div>
          <div className="stamp empty">☕</div>

          <div className="stamp empty">☕</div>
          <div className="stamp empty">☕</div>
          <div className="stamp empty">☕</div>

          <div className="stamp reward">🥤</div>
        </div>

        <p className="loyalty-message">
          5 coffees collected. Buy 4 more to unlock your free drink.
        </p>
      </section>
    </main>
  );
}

export default AccountPage;