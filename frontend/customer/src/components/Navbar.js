import { Link } from 'react-router-dom';
import '../App.css';

function Navbar() {
  return (
    <nav className="navbar">
      <Link to="/home" className="nav-left">
        <img src="/images/logo.png" alt="logo" className="nav-logo" />
        <span className="nav-home-text">home</span>
      </Link>

      <div className="nav-actions">
        <Link to="/order" className="nav-item">
          <span className="nav-icon">☕</span>
          <span>order</span>
        </Link>

        <Link to="/status" className="nav-item">
          <span className="nav-icon">📍</span>
          <span>order progress</span>
        </Link>

        <Link to="/basket" className="nav-item basket-nav-item">
          <span className="basket-count">5</span>
          <span className="nav-icon">🛒</span>
          <span>basket</span>
        </Link>

        <Link to="/signin" className="nav-item">
          <span className="nav-icon">👤</span>
          <span>sign in</span>
        </Link>
      </div>
    </nav>
  );
}

export default Navbar;