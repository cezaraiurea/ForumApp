 import React, { useState, useEffect } from 'react';
import { Link, useLocation } from 'react-router-dom';
import './QuestionList.css';
import HomeIcon from '../Common/HomeIcon';
import QuestionFilter from './QuestionFilter';
import { getQuestions, getMyQuestions } from '../../services/api';

function QuestionList() {
  const location = useLocation();
  const params = new URLSearchParams(location.search);
  const initialTag = params.get('tag') || '';

  const [questions, setQuestions] = useState([]);
  const [filterText, setFilterText] = useState(initialTag);
  const [isOwnFilter, setIsOwnFilter] = useState(false);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const data = isOwnFilter ? await getMyQuestions() : await getQuestions();
        setQuestions(data);
      } catch (err) {
        console.error('Eroare:', err);
      }
    };

    fetchData();
  }, [isOwnFilter]);

  useEffect(() => {
    setFilterText(initialTag);
  }, [initialTag]);

  const filteredQuestions = questions.filter(q =>
    q.title.toLowerCase().includes(filterText.toLowerCase()) ||
    (q.user?.username?.toLowerCase() || '').includes(filterText.toLowerCase()) ||
    q.questionTags.some(tag => tag.tag.name.toLowerCase().includes(filterText.toLowerCase()))
  );

  return (
    <div className="question-list-container">
      <HomeIcon />
      <div className="questions-header-bar">
        <h2>Întrebări</h2>
        <Link to="/questions/new" className="big-ask-btn">Pune o întrebare!</Link>
      </div>

      <div className="question-filters-bar">
        <button
          className={`filter-toggle-btn ${!isOwnFilter ? 'active' : ''}`}
          onClick={() => setIsOwnFilter(false)}
        >
          Toate
        </button>
        <button
          className={`filter-toggle-btn ${isOwnFilter ? 'active' : ''}`}
          onClick={() => setIsOwnFilter(true)}
        >
          Întrebările mele
        </button>
      </div>

      <QuestionFilter filterText={filterText} onFilterChange={setFilterText} />

      <div className="questions-cards-list">
        {filteredQuestions.length === 0 ? (
          <p className="no-questions-msg">Nu există întrebări care sa corespunda filtrului.</p>
        ) : (
          filteredQuestions.map(q => (
            <div key={q.id} className="question-card-forum">
              {q.image && (
                <img
                  src={`http://localhost:8080${q.image}`}
                  alt="Imagine întrebare"
                  className="question-preview-image"
                />
              )}
              <div className="question-card-header">
                <h3 className="question-title-forum">{q.title}</h3>
                <span className="author-forum">👤 {q.user?.username || 'Anonim'}</span>
              </div>
              <div className="question-tags-forum">
                {q.questionTags.length === 0 ? (
                  <span className="tag tag-empty">Fără etichete</span>
                ) : (
                  q.questionTags.map(tag => (
                    <span className="tag tag-forum" key={tag.id}>{tag.tag.name}</span>
                  ))
                )}
              </div>
              <Link to={`/questions/${q.id}`} className="see-details-btn">Vezi întrebarea și răspunsurile</Link>
            </div>
          ))
        )}
      </div>
    </div>
  );
}

export default QuestionList;
