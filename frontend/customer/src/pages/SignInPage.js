import '../App.css';
import { Link } from 'react-router-dom';

function SignInPage() {
  return (
    <main className="signin-page">
    
      <section className="signin-content">
        <form className="signin-card">
          <h1>Sign in</h1>
          <p>to get started</p>

          <label>Email</label>
          <input type="email" />

          <label>Password</label>
          <input type="password" />

          <button type="button" className="forgot-button">
            Forgot Password ?
          </button>

          <button type="submit" className="signin-button">
            Sign in
          </button>

          <Link to="/home" className="guest-button">
          Continue as guest
          </Link>
        
        </form>
      </section>
    </main>
  );
}

export default SignInPage;