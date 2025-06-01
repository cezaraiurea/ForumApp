import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import './Header.css';
import HomeIcon from './HomeIcon';

function Header() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [email, setEmail] = useState('');
  const [isAdmin, setIsAdmin] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    // Check authentication status on component mount and when localStorage changes
    const checkAuth = () => {
      const token = localStorage.getItem('token');
      const storedEmail = localStorage.getItem('email');
      const role = localStorage.getItem('role');
      setIsLoggedIn(!!token);
      setEmail(storedEmail || '');
      setIsAdmin(role === 'ADMIN');
    };

    checkAuth();
    window.addEventListener('storage', checkAuth);
    window.addEventListener('authChange', checkAuth);
    return () => {
      window.removeEventListener('storage', checkAuth);
      window.removeEventListener('authChange', checkAuth);
    };
  }, []);

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    localStorage.removeItem('email');
    localStorage.removeItem('role');
    setIsLoggedIn(false);
    setEmail('');
    setIsAdmin(false);
    navigate('/login');
  };

  return (
    <header className="header">
      <div className="logo-row">
        <HomeIcon />
        <h1 className="logo">Forum App</h1>
      </div>
      <nav>
        {isLoggedIn && email ? (
          <div className="user-dropdown">
            <span className="user-icon" title="Contul meu">👤</span>
            <span className="user-email">{email}</span>
            {isAdmin && (
              <Link to="/admin/blocked-users" className="admin-btn">
                Panou Admin
              </Link>
            )}
            <button className="logout-btn" onClick={handleLogout}>Delogare</button>
          </div>
        ) : (
          <>
            <Link to="/login">Autentificare</Link>
            <Link to="/register">Înregistrare</Link>
          </>
        )}
      </nav>
    </header>
  );
}

export default Header;
