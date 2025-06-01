 import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import './Home.css';
import { getQuestions } from '../../services/api';

function Home() {
  const [questions, setQuestions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const email = localStorage.getItem('email');
  const isLoggedIn = !!localStorage.getItem('token');

  useEffect(() => {
    getQuestions()
      .then(data => {
        setQuestions(data);
        setLoading(false);
      })
      .catch(err => {
        setError('Eroare la încărcarea întrebărilor');
        setLoading(false);
      });
  }, []);

  const tagCount = {};
  questions.forEach(q => {
    q.questionTags?.forEach(tagObj => {
      const tag = tagObj.tag.name;
      tagCount[tag] = (tagCount[tag] || 0) + 1;
    });
  });

  const popularTags = Object.entries(tagCount)
    .sort((a, b) => b[1] - a[1])
    .slice(0, 6)
    .map(([tag]) => tag);

  const latestQuestions = questions.slice(0, 3);

  return (
    <div className="home-forum-page">
      <main className="forum-main-content">
        <div className="welcome-card">
          <div className="avatar-section">
            <span className="avatar-icon">👤</span>
            <div>
              <h2>Bine ai venit{isLoggedIn && email ? `,` : ''} <span className="highlight">{isLoggedIn && email ? email : ''}</span>!</h2>
              <p className="welcome-text">Alătură-te comunității noastre și participă la discuții interesante.</p>
            </div>
          </div>
          {isLoggedIn && (
            <Link to="/questions/new" className="big-post-btn">✍️ Pune o întrebare</Link>
          )}
        </div>

        <div className="forum-cards-wrapper">
          <section className="forum-card">
            <h3>📝 Ultimele întrebări</h3>
            {loading ? (
              <p>Se încarcă...</p>
            ) : error ? (
              <p className="error-msg">{error}</p>
            ) : latestQuestions.length === 0 ? (
              <p>Nu există întrebări încă.</p>
            ) : (
              <ul className="latest-questions-list">
                {latestQuestions.map(q => (
                  <li key={q.id} className="latest-question-item">
                    <Link to={`/questions/${q.id}`} className="question-title-link">{q.title}</Link>
                    <div className="question-meta-preview">
                      <span className="author-preview">👤 {q.user?.username || 'Anonim'}</span>
                      <span className="tags-preview">
                        {q.questionTags?.map(tag => (
                          <span className="tag tag-preview" key={tag.id}>{tag.tag.name}</span>
                        ))}
                      </span>
                    </div>
                  </li>
                ))}
              </ul>
            )}
            <Link to="/questions" className="see-all-link">Vezi toate întrebările</Link>
          </section>

          <section className="forum-card">
            <h3>🏷️ Cele mai populare etichete</h3>
            {popularTags.length === 0 ? (
              <p className="no-tags">Nu există etichete încă.</p>
            ) : (
              <div className="tag-cloud">
                {popularTags.map(tag => (
                  <Link
                    to={`/questions?tag=${encodeURIComponent(tag)}`}
                    className="tag tag-cloud-item"
                    key={tag}
                    style={{ textDecoration: 'none' }}
                  >
                    {tag}
                  </Link>
                ))}
              </div>
            )}
          </section>

          <section className="forum-card">
            <h3>👥 Utilizatori activi</h3>
            <div className="active-users-demo">
              <span className="user-badge">utilizator1</span>
              <span className="user-badge">user2</span>
              <span className="user-badge">cont1234</span>
            </div>
          </section>
        </div>
      </main>
      <footer className="footer">
        <p>&copy; 2025 Forum App. Toate drepturile rezervate.</p>
      </footer>
    </div>
  );
}

export default Home;
