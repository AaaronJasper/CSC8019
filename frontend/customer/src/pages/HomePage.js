import '../App.css';
import Navbar from '../components/Navbar';
import { Link } from 'react-router-dom';

function HomePage() {
  return (
    <main className="home-page">
      <Navbar />

      <section className="hero-section">
        <h1>Welcome to Whistlestop Coffee Hut</h1>
        <p>Order ahead and collect fresh coffee at Cramlington Station.</p>
        <Link to="/order" className="primary-button">
        Start Order
        </Link>
      </section>

      <section className="quick-order-section">
        <div>
          <h2>Quick Order</h2>
          <p>In a rush? Reorder your usual coffee in seconds.</p>
        </div>
        
        <button className="quick-order-button">Quick Order</button>
      </section>

      <section className="popular-section">
        <h2>Popular Drinks</h2>

        <div className="popular-grid">
          <div className="popular-card">
            <img src="/images/latte.jpg" alt="Latte" />
            <h3>Latte</h3>
          </div>

          <div className="popular-card">
            <img src="/images/cappuccino.jpg" alt="Cappuccino" />
            <h3>Cappuccino</h3>
          </div>

          <div className="popular-card">
            <img src="/images/hot-chocolate.jpg" alt="Hot Chocolate" />
            <h3>Hot Chocolate</h3>
          </div>
        </div>
      </section>

      <section className="offer-section">
        <h2>Today’s Offer</h2>
        <p>Free extra shot with any large coffee.</p>
      </section>

      <section className="story-section">
        <h2>Our Story</h2>
        <p>
          Whistlestop Coffee Hut serves quick, warm drinks for commuters,
          students and travellers passing through Cramlington Station.
        </p>
      </section>

    </main>
  );
}

export default HomePage;