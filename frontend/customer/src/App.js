import './App.css';
import { BrowserRouter, Routes, Route } from 'react-router-dom';

import HomePage from './pages/HomePage';
import CustomerMenu from './pages/CustomerMenu';
import DrinkOrderPage from './pages/DrinkOrderPage';
import BasketPage from './pages/BasketPage';
import OrderStatusPage from './pages/OrderStatusPage';
import AccountPage from './pages/AccountPage';
import SignInPage from './pages/SignInPage';

function App() {
  return (
    <BrowserRouter>
      <div className="app-container">
        <Routes>
          <Route path="/" element={<SignInPage />} />
          <Route path="/home" element={<HomePage />} />
          <Route path="/order" element={<CustomerMenu />} />
          <Route path="/drink-order/:drinkId" element={<DrinkOrderPage />} />
          <Route path="/basket" element={<BasketPage />} />
          <Route path="/status" element={<OrderStatusPage />} />
          <Route path="/account" element={<AccountPage />} />
          <Route path="/signin" element={<SignInPage />} />
        </Routes>
      </div>
    </BrowserRouter>
  );
}

export default App;


 