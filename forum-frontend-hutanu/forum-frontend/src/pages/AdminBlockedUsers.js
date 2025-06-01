import React, { useEffect, useState } from 'react';
import axios from 'axios';

function AdminBlockedUsers() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState('');

  const fetchUsers = async () => {
    try {
      const response = await axios.get('http://localhost:8080/users', {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`
        }
      });
      setUsers(response.data);
    } catch (err) {
      console.error('Eroare la obținerea utilizatorilor:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleBlock = async (userId) => {
    try {
      await axios.put(`http://localhost:8080/users/block/${userId}`, {}, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`
        }
      });
      setMessage(`Utilizatorul cu ID ${userId} a fost blocat.`);
      fetchUsers();
    } catch (err) {
      console.error('Eroare la blocare:', err);
    }
  };

  const handleUnblock = async (userId) => {
    try {
      await axios.put(`http://localhost:8080/users/unblock/${userId}`, {}, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`
        }
      });
      setMessage(`Utilizatorul cu ID ${userId} a fost deblocat.`);
      fetchUsers();
    } catch (err) {
      console.error('Eroare la deblocare:', err);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  return (
    <div style={{ padding: '2rem', fontFamily: 'Arial, sans-serif' }}>
      <h2 style={{ color: '#333', marginBottom: '1rem' }}>Panou Administrare Utilizatori</h2>

      {message && <p style={{ color: 'green', fontWeight: 'bold' }}>{message}</p>}

      {loading ? (
        <p>Se încarcă...</p>
      ) : users.length === 0 ? (
        <p>Nu există utilizatori înregistrați.</p>
      ) : (
        <table style={{
          width: '100%',
          borderCollapse: 'collapse',
          boxShadow: '0 0 10px rgba(0,0,0,0.1)',
          backgroundColor: '#fff',
          borderRadius: '8px',
          overflow: 'hidden'
        }}>
          <thead style={{ backgroundColor: '#4a5568', color: 'white' }}>
            <tr>
              <th style={thStyle}>ID</th>
              <th style={thStyle}>Username</th>
              <th style={thStyle}>Email</th>
              <th style={thStyle}>Status</th>
              <th style={thStyle}>Acțiune</th>
            </tr>
          </thead>
          <tbody>
            {users.map((user, idx) => (
              <tr key={user.id} style={{ backgroundColor: idx % 2 === 0 ? '#f7fafc' : 'white' }}>
                <td style={tdStyle}>{user.id}</td>
                <td style={tdStyle}>{user.username}</td>
                <td style={tdStyle}>{user.email}</td>
                <td style={tdStyle}>
                  <span style={{
                    padding: '4px 10px',
                    borderRadius: '20px',
                    color: 'white',
                    fontWeight: 'bold',
                    backgroundColor: user.blocked ? '#e53e3e' : '#38a169'
                  }}>
                    {user.blocked ? 'Blocat' : 'Activ'}
                  </span>
                </td>
                <td style={tdStyle}>
                  {user.blocked ? (
                    <button style={buttonStyle('#3182ce')} onClick={() => handleUnblock(user.id)}>
                      Deblochează
                    </button>
                  ) : (
                    <button style={buttonStyle('#e53e3e')} onClick={() => handleBlock(user.id)}>
                      Blochează
                    </button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
} 
const thStyle = {
  padding: '12px',
  textAlign: 'left',
  fontWeight: 'bold',
  fontSize: '14px'
};

const tdStyle = {
  padding: '12px',
  fontSize: '14px'
};

const buttonStyle = (bgColor) => ({
  padding: '6px 12px',
  backgroundColor: bgColor,
  color: '#fff',
  border: 'none',
  borderRadius: '4px',
  cursor: 'pointer',
  fontWeight: 'bold'
});

export default AdminBlockedUsers;
