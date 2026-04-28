import '../App.css';
import Navbar from '../components/Navbar';

function OrderStatusPage() {
  return (
    <main className="order-status-page">
      <Navbar />

      <section className="status-content">
        <h1>Order Progress</h1>

        <div className="status-card">
          <p className="order-number">Order #1042</p>
          <h2>In Progress</h2>
          <p>Your order is being prepared.</p>

          <div className="progress-track">
            <div className="progress-fill"></div>
            <span className="progress-step step-one">✓</span>
            <span className="progress-step step-two">☕</span>
            <span className="progress-step step-three">🥤</span>
          </div>

          <div className="status-labels">
            <span>Accepted</span>
            <span>Preparing</span>
            <span>Ready</span>
          </div>

          <div className="pickup-box">
            <strong>Pickup time</strong>
            <span>09:20</span>
          </div>

          <div className="order-summary-box">
            <h3>Your Order</h3>
            <p>Latte x1 large</p>
            <p>Cappuccino x1 regular</p>
          </div>
        </div>
      </section>
    </main>
  );
}

export default OrderStatusPage;