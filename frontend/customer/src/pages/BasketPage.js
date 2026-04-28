import '../App.css';
import Navbar from '../components/Navbar';

function BasketPage() {
  return (
    <main className="basket-page">
      <Navbar />

      <section className="basket-content">
        <h1>Checkout</h1>

        <div className="basket-item">
          <img src="/images/hot-chocolate.jpg" alt="Hot Chocolate" />
          <div>
            <h2>Hot Chocolate</h2>
            <p>x2 regular</p>
          </div>
          <span>£5.00</span>
        </div>

        <div className="basket-item">
          <img src="/images/latte.jpg" alt="Latte" />
          <div>
            <h2>Latte</h2>
            <p>x1 large</p>
          </div>
          <span>£2.50</span>
        </div>

        <div className="basket-item">
          <img src="/images/cappuccino.jpg" alt="Cappuccino" />
          <div>
            <h2>Cappuccino</h2>
            <p>x1 regular</p>
          </div>
          <span>£3.00</span>
        </div>

        <div className="basket-item">
          <img src="/images/water.avif" alt="Mineral Water" />
          <div>
            <h2>Mineral Water</h2>
            <p>x1 regular</p>
          </div>
          <span>£1.00</span>
        </div>

        <div className="basket-total">
          <strong>Total:</strong>
          <strong>£11.50</strong>
        </div>

        <button className="payment-button">Proceed to Payment</button>
      </section>
    </main>
  );
}

export default BasketPage;