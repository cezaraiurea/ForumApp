 import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import './Login.css';
import HomeIcon from '../Common/HomeIcon';
import { login } from '../../services/api';

function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!email || !password) {
      alert('Completează toate câmpurile!');
      return;
    }

    try {
      const result = await login({ email, password });

      if (result && result.token && result.userId !== undefined && result.userId !== null) {
        if (result.blocked) {
          const msg = 'Contul tău este blocat. Nu poți accesa aplicația.';
          setError(msg);
          alert(msg);
          return;
        }

        localStorage.setItem('token', result.token);
        localStorage.setItem('userId', result.userId);
        localStorage.setItem('username', result.username ?? email.split('@')[0]);
        localStorage.setItem('email', result.email ?? email);
        localStorage.setItem('role', result.role ?? 'USER');
        localStorage.setItem('blocked', result.blocked ?? false);

        window.dispatchEvent(new Event('authChange'));
        navigate('/', { replace: true });
      } else {
        const errorMessage = result?.token || 'Login failed: Unexpected response from server.';
        setError(errorMessage);
        alert(errorMessage);
      }

    } catch (err) {
      if (err.response && err.response.status === 403) {
        const message = err.response.data?.token || 'Contul este blocat.';
        setError(message);
        alert(message);
      } else if (err.response && err.response.status === 401) {
        const message = 'Email sau parolă greșită.';
        setError(message);
        alert(message);
      } else {
        const message = err.message || 'A apărut o eroare la autentificare.';
        setError(message);
        alert(message);
      }
    }
  };

  return (
    <div className="login-container">
      <HomeIcon />
      <h1>Login</h1>

      <form onSubmit={handleSubmit}>
        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />
        <input
          type="password"
          placeholder="Parolă"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
        <button type="submit">Login</button>

        {error && <p style={{ color: 'red', marginTop: '1rem' }}>{error}</p>}

        <p style={{ textAlign: 'center', marginTop: '15px' }}>
          Nu ai un cont? <Link to="/register">Înregistrează-te</Link>
        </p>
      </form>
    </div>
  );
}

export default Login;
