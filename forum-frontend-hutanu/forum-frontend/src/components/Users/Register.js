import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import './Register.css';
import HomeIcon from '../Common/HomeIcon';
import { register, login } from '../../services/api';

function Register() {
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [submitted, setSubmitted] = useState(false);
  const [error, setError] = useState(null);

  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!username || !email || !password || !confirmPassword) {
      alert('Completează toate câmpurile!');
      return;
    }

    if (password !== confirmPassword) {
      alert('Parolele nu se potrivesc!');
      return;
    }

    try {
      const result = await register({ username, email, password });
      alert(result.message || 'Utilizator înregistrat cu succes!');
      setSubmitted(true);
      setError(null);
 
      try {
        const loginResult = await login({ email, password });

        if (loginResult.blocked) {
          alert('Contul tău este blocat. Nu poti accesa aplicatia.');
          navigate('/login');
          return;
        }

        localStorage.setItem('token', loginResult.token);
        localStorage.setItem('email', loginResult.email ?? email);
        localStorage.setItem('username', loginResult.username ?? username);
        localStorage.setItem('userId', loginResult.userId);
        localStorage.setItem('role', loginResult.role ?? 'USER');
        localStorage.setItem('blocked', loginResult.blocked ?? false);

        window.dispatchEvent(new Event('authChange'));
        navigate('/', { replace: true });
      } catch (loginErr) {
        console.error('Login automat eșuat:', loginErr);
        navigate('/login');
      }

    } catch (err) {
      setError(err.message);
      alert(err.message);
    }
  };

  return (
    <div className="register-container">
      <HomeIcon />
      <h1>Înregistrare</h1>

      <form onSubmit={handleSubmit}>
        <input
          type="text"
          placeholder="Username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
        />
        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />
        <input
          type="password"
          placeholder="Parolă"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
        <input
          type="password"
          placeholder="Confirmă parola"
          value={confirmPassword}
          onChange={(e) => setConfirmPassword(e.target.value)}
        />

        <button type="submit">Înregistrează-te</button>

        {error && <p style={{ color: 'red' }}>{error}</p>}
        <p>Ai deja un cont? <Link to="/login">Autentifică-te</Link></p>
      </form>

      {submitted && <p style={{ color: 'green' }}>Utilizator înregistrat cu succes!</p>}
    </div>
  );
}

export default Register;
