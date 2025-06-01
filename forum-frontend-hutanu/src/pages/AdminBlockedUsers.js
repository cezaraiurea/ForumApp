import React, { useEffect, useState } from 'react';
import { getAllUsers, getBlockedUsers, blockUser, unblockUser } from '../services/api';

function AdminBlockedUsers() {
  const [blockedUsers, setBlockedUsers] = useState([]);
  const [allUsers, setAllUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState('');

  const fetchBlockedUsers = async () => {
    try {
      const data = await getBlockedUsers();
      setBlockedUsers(data);
    } catch (err) {
      setMessage(err.message || 'Eroare la obținerea utilizatorilor blocați.');
      setBlockedUsers([]);
    }
  };

  const fetchAllUsers = async () => {
    try {
      const data = await getAllUsers();
      setAllUsers(data);
    } catch (err) {
      setMessage(err.message || 'Eroare la obținerea tuturor utilizatorilor.');
      setAllUsers([]);
    } finally {
      setLoading(false);
    }
  };

  const handleBlock = async (userId) => {
    try {
      await blockUser(userId);
      setMessage(`Utilizatorul cu ID ${userId} a fost blocat.`);
      await fetchBlockedUsers();
      await fetchAllUsers();
    } catch (err) {
      setMessage(err.message || 'Eroare la blocarea utilizatorului.');
    }
  };

  const handleUnblock = async (userId) => {
    try {
      await unblockUser(userId);
      setMessage(`Utilizatorul cu ID ${userId} a fost deblocat.`);
      await fetchBlockedUsers();
      await fetchAllUsers();
    } catch (err) {
      setMessage(err.message || 'Eroare la deblocarea utilizatorului.');
    }
  };

  useEffect(() => {
    fetchBlockedUsers();
    fetchAllUsers();
  }, []);

  return (
    <div style={{ padding: '2rem' }}>
      <h2>Administrare Utilizatori</h2>
      
      <h3>Utilizatori Blocați</h3>
      {loading ? (
        <p>Se încarcă...</p>
      ) : blockedUsers.length === 0 ? (
        <p>Nu există utilizatori blocați.</p>
      ) : (
        <table style={{ width: '100%', marginBottom: '2rem' }}>
          <thead>
            <tr>
              <th>ID</th>
              <th>Username</th>
              <th>Email</th>
              <th>Acțiune</th>
            </tr>
          </thead>
          <tbody>
            {blockedUsers.map((user) => (
              <tr key={user.id}>
                <td>{user.id}</td>
                <td>{user.username}</td>
                <td>{user.email}</td>
                <td>
                  <button 
                    onClick={() => handleUnblock(user.id)}
                    style={{ backgroundColor: '#4CAF50', color: 'white', border: 'none', padding: '5px 10px', borderRadius: '3px', cursor: 'pointer' }}
                  >
                    Deblochează
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      <h3 style={{ marginTop: '2rem' }}>Toți utilizatorii</h3>
      {loading ? (
        <p>Se încarcă...</p>
      ) : (
        <div className="admin-table-container">
          <table className="admin-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Username</th>
                <th>Email</th>
                <th>Status</th>
                <th>Acțiune</th>
              </tr>
            </thead>
            <tbody>
              {allUsers.map((user, idx) => (
                <tr key={user.id}>
                  <td>{user.id}</td>
                  <td>{user.username}</td>
                  <td>{user.email}</td>
                  <td>
                    <span className={`status-badge ${user.blocked ? 'blocat' : 'activ'}`}>
                      {user.blocked ? 'Blocat' : 'Activ'}
                    </span>
                  </td>
                  <td>
                    {user.blocked ? (
                      <button
                        className="action-btn deblocheaza"
                        onClick={() => handleUnblock(user.id)}
                        title="Deblochează"
                      >
                        <span role="img" aria-label="unlock">🔓</span> Deblochează
                      </button>
                    ) : (
                      <button
                        className="action-btn blocheaza"
                        onClick={() => handleBlock(user.id)}
                        title="Blochează"
                      >
                        <span role="img" aria-label="lock">🔒</span> Blochează
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
      
      {message && (
        <p style={{ 
          color: message.includes('Eroare') ? 'red' : 'green',
          marginTop: '1rem',
          padding: '10px',
          backgroundColor: message.includes('Eroare') ? '#ffebee' : '#e8f5e9',
          borderRadius: '4px'
        }}>
          {message}
        </p>
      )}
    </div>
  );
}

export default AdminBlockedUsers; 