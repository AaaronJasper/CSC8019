import '../App.css';
import { useParams } from 'react-router-dom';

function DrinkOrderPage() {

const { drinkId } = useParams();

  // TEMPORARY DATA 
  const drinks = {
    americano: {
      name: 'Americano',
      image: '/images/americano.avif',
      description: 'A bold black coffee made with espresso and hot water.',
      regularPrice: '£1.50',
      largePrice: '£2.00',
    },
    latte: {
      name: 'Latte',
      image: '/images/latte.jpg',
      description: 'Smooth espresso with steamed milk.',
      regularPrice: '£2.50',
      largePrice: '£3.00',
    },
    cappuccino: {
      name: 'Cappuccino',
      image: '/images/cappuccino.jpg',
      description: 'Rich espresso topped with steamed milk and foam.',
      regularPrice: '£2.50',
      largePrice: '£3.00',
    },
    'americano-milk': {
    name: 'Americano with milk',
    image: '/images/americano-milk.jpg',
    description: 'Americano with added milk.',
    regularPrice: '£2.00',
    largePrice: '£2.50',
  },
    'hot-chocolate': {
      name: 'Hot Chocolate',
      image: '/images/hot-chocolate.jpg',
      description: 'Creamy hot chocolate.',
      regularPrice: '£2.00',
      largePrice: '£2.50',
    },
    mocha: {
      name: 'Mocha',
      image: '/images/mocha.jpg',
      description: 'Espresso with chocolate and milk.',
      regularPrice: '£2.50',
      largePrice: '£3.00',
    },
    'mineral-water': {
      name: 'Mineral Water',
      image: '/images/water.avif',
      description: 'Still bottled water.',
      regularPrice: '£1.00',
      largePrice: null,
    },
  };

  const drink = drinks[drinkId];

  return (
    <main className="drink-order-page">
      <nav className="navbar">
        <div className="nav-left">
          <img src="/images/logo.png" alt="logo" className="nav-logo" />
        </div>

        <div className="nav-actions">
          <div className="nav-item">
            <span className="nav-icon">👤</span>
            <span>sign in</span>
          </div>

          <div className="nav-item">
            <span className="nav-icon">←</span>
            <span>back</span>
          </div>
        </div>
      </nav>

      <section className="drink-order-card">
        <img src={drink.image} alt={drink.name} className="drink-order-image" />

        <h1>{drink.name}</h1>
        <p className="drink-description">{drink.description}</p>

        <div className="option-section">
          <h2>Choose size</h2>

          <div className="option-buttons">
            <button>Regular {drink.regularPrice}</button>
            {drink.largePrice && (
              <button>Large {drink.largePrice}</button>)}
          </div>
        </div>

        <div className="option-section">
          <h2>Quantity</h2>

          <div className="quantity-row">
            <button>-</button>
            <span>1</span>
            <button>+</button>
          </div>
        </div>

        <div className="option-section">
          <h2>Extras</h2>

          <label>
            <input type="checkbox" /> Extra shot
          </label>

          <label>
            <input type="checkbox" /> Oat milk
          </label>

          <label>
            <input type="checkbox" /> No sugar
          </label>
        </div>

        <div className="order-summary">
          <span>Total</span>
          <strong>£2.50</strong>
        </div>

        <button className="confirm-button">Confirm</button>
      </section>
    </main>
  );
}

export default DrinkOrderPage;