import '../App.css';
import { Link } from 'react-router-dom';
import Navbar from '../components/Navbar';

function CustomerMenu() {
  return (
    <main className="customer-menu-page">
      <Navbar />

      <section className="menu-header">
        <h1>Order Menu</h1>
        <p>Choose your coffee and collection time</p>
      </section>

      <section className="menu-list">
        <div className="drink-card">
          <div className="drink-info">
            <h2>Americano</h2>
            <div className="drink-prices">
              <span>Regular: £1.50</span>
              <span>Large: £2.00</span>
            </div>
            <Link to="/drink-order/americano" className="drink-add-button">Add</Link>
          </div>

          <div className="drink-image-wrapper">
            <img src="/images/americano.avif" alt="Americano" />
            <div className="rating-badge"><span>⭐</span><span>4.1</span></div>
          </div>
        </div>

        <div className="drink-card">
          <div className="drink-info">
            <h2>Latte</h2>
            <div className="drink-prices">
              <span>Regular: £2.50</span>
              <span>Large: £3.00</span>
            </div>
            <Link to="/drink-order/latte" className="drink-add-button">Add</Link>
          </div>

          <div className="drink-image-wrapper">
            <img src="/images/latte.jpg" alt="Latte" />
            <div className="rating-badge"><span>⭐</span><span>4.3</span></div>
          </div>
        </div>

        <div className="drink-card">
          <div className="drink-info">
            <h2>Cappuccino</h2>
            <div className="drink-prices">
              <span>Regular: £2.50</span>
              <span>Large: £3.00</span>
            </div>
            <Link to="/drink-order/cappuccino" className="drink-add-button">Add</Link>
          </div>

          <div className="drink-image-wrapper">
            <img src="/images/cappuccino.jpg" alt="Cappuccino" />
            <div className="rating-badge"><span>⭐</span><span>4.1</span></div>
          </div>
        </div>

        <div className="drink-card">
          <div className="drink-info">
            <h2>Americano with milk</h2>
            <div className="drink-prices">
              <span>Regular: £2.00</span>
              <span>Large: £2.50</span>
            </div>
            <Link to="/drink-order/americano-milk" className="drink-add-button">Add</Link>
          </div>

          <div className="drink-image-wrapper">
            <img src="/images/americano-milk.jpg" alt="Americano with milk" />
            <div className="rating-badge"><span>⭐</span><span>4.0</span></div>
          </div>
        </div>

        <div className="drink-card">
          <div className="drink-info">
            <h2>Mocha</h2>
            <div className="drink-prices">
              <span>Regular: £2.50</span>
              <span>Large: £3.00</span>
            </div>
            <Link to="/drink-order/mocha" className="drink-add-button">Add</Link>
          </div>

          <div className="drink-image-wrapper">
            <img src="/images/mocha.jpg" alt="Mocha" />
            <div className="rating-badge"><span>⭐</span><span>4.4</span></div>
          </div>
        </div>

        <div className="drink-card">
          <div className="drink-info">
            <h2>Hot Chocolate</h2>
            <div className="drink-prices">
              <span>Regular: £2.00</span>
              <span>Large: £2.50</span>
            </div>
            <Link to="/drink-order/hot-chocolate" className="drink-add-button">Add</Link>
          </div>

          <div className="drink-image-wrapper">
            <img src="/images/hot-chocolate.jpg" alt="Hot Chocolate" />
            <div className="rating-badge"><span>⭐</span><span>4.5</span></div>
          </div>
        </div>

        <div className="drink-card">
          <div className="drink-info">
            <h2>Mineral Water</h2>
            <div className="drink-prices">
              <span>Regular: £1.00</span>
            </div>
            <Link to="/drink-order/mineral-water" className="drink-add-button">Add</Link>
          </div>

          <div className="drink-image-wrapper">
            <img src="/images/water.avif" alt="Mineral Water" />
            <div className="rating-badge"><span>⭐</span><span>4.2</span></div>
          </div>
        </div>
      </section>
    </main>
  );
}

export default CustomerMenu;