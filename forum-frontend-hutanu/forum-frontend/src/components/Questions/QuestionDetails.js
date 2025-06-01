 import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import './QuestionDetails.css';
import HomeIcon from '../Common/HomeIcon';
import AnswerForm from '../Answers/AnswerForm';
import {
  getQuestionById,
  getAnswersByQuestionId,
  deleteAnswer,
  voteAnswer,
  voteQuestion,
  deleteQuestion
} from '../../services/api';

function QuestionDetails() {
  const { id } = useParams();
  const [question, setQuestion] = useState(null);
  const [answers, setAnswers] = useState([]);
  const [likes, setLikes] = useState(0);
  const [currentUserId, setCurrentUserId] = useState(null);
  const role = localStorage.getItem('role');
  const isAdmin = role === 'ADMIN';

  const [editingAnswerId, setEditingAnswerId] = useState(null);

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (token) {
      const userId = localStorage.getItem('userId');
      setCurrentUserId(userId ? Number(userId) : null);
    } else {
      setCurrentUserId(null);
    }

    getQuestionById(id)
      .then(data => {
        setQuestion(data);
        setLikes(data.voteScore || 0);
      })
      .catch(err => console.error('Eroare la încărcarea întrebării:', err));


    getAnswersByQuestionId(id)
      .then(data => setAnswers(data.sort((a, b) => (b.voteScore || 0) - (a.voteScore || 0))))
      .catch(err => console.error('Eroare la încărcarea răspunsurilor:', err));
  }, [id]);



  const handleVoteQuestion = async (type) => {
    try {
      const response = await voteQuestion(id, type.toUpperCase());
      if (response === "Vote added successfully.") {
        setLikes(prev => prev + (type === 'like' ? 1 : -1));
      } else {
        alert(response);
      }
    } catch (err) {
      alert("Eroare la votare: " + err.message);
    }
  };


  const handleVoteAnswer = async (answerId, type) => {
    try {
      const response = await voteAnswer(answerId, type);
      if (response === "Vote added successfully.") {
        setAnswers(prev =>
          [...prev].map(a =>
            a.id === answerId
              ? { ...a, voteScore: (a.voteScore || 0) + (type === "LIKE" ? 1 : -1) }
              : a
          ).sort((a, b) => (b.voteScore || 0) - (a.voteScore || 0))
        );
      } else {
        alert(response);
      }
    } catch (err) {
      alert("Eroare la vot: " + err.message);
    }
  };

 
  const handleDeleteAnswer = async (answerId) => {
  if (window.confirm('Sigur doriți să ștergeți acest răspuns?')) {
    try {
      const result = await deleteAnswer(answerId, currentUserId);
      console.log("Mesaj primit:", result.message);
      
      if (result.message?.toLowerCase().includes("deleted")) {
        setAnswers(prev => prev.filter(a => a.id !== answerId));
        alert('Răspuns șters cu succes!');
      } else {
        alert('Nu s-a putut șterge răspunsul: ' + result.message);
      }
    } catch (error) {
      alert('A apărut o eroare la ștergerea răspunsului.');
    }
  }
};

const handleAcceptAnswer = async (answerId) => {
  try {
    const response = await fetch(`http://localhost:8080/answers/${answerId}/accept?userId=${currentUserId}`, {
      method: 'PUT',
      headers: {
        Authorization: `Bearer ${localStorage.getItem('token')}`
      }
    });

    if (response.ok) {
      alert("Răspuns acceptat!");
      const updatedQuestion = await getQuestionById(id);
      setQuestion(updatedQuestion);

      const updatedAnswers = await getAnswersByQuestionId(id);
      setAnswers(updatedAnswers.sort((a, b) => (b.voteScore || 0) - (a.voteScore || 0)));
    } else {
      const msg = await response.text();
      alert("Eroare: " + msg);
    }
  } catch (err) {
    alert("Eroare la acceptare: " + err.message);
  }
};

 
  if (!question) return <p>Se încarcă...</p>;

  return (
    <div className="question-details-container">
      <div className="question-main-content">
        <HomeIcon />
        <h2 className="question-title">{question.title}</h2>
        <p className="question-content">{question.text}</p>
        {question.image && (
           <img
            src={`http://localhost:8080${question.image}`}
           alt="Imagine întrebare"
           className="question-preview-image"
           />

        )}

        <div className="question-tags">
          {(question.questionTags || []).map(qt => (
            <span className="tag" key={qt.id}>{qt.tag.name}</span>
          ))}
        </div>
        <div className="question-meta">
          <div className="meta-item">
             <strong>Autor:</strong> {question.user?.username || 'Anonim'} 
             {question.user && (
                <span style={{ marginLeft: '10px', color: '#666' }}>
                  ({question.user.score} pct)
             </span>
             )}
         </div> 
          <div className="meta-item"><strong>Data postării:</strong> {new Date(question.date).toLocaleString()}</div>
          <div className="meta-item"><strong>Număr răspunsuri:</strong> {answers.length}</div>
          <div className="meta-item"><strong>Voturi:</strong> {likes}</div>
          <div className="meta-item"><strong>Status:</strong>{" "}
          <span style={{ 
              color: question.status === "RESOLVED" ? "green" : question.status === "IN_PROGRESS" ? "orange" : "gray", 
               fontWeight: "bold" 
             }}>
           {question.status === "RESOLVED" ? "Rezolvată" : question.status === "IN_PROGRESS" ? "În curs de rezolvare" : "Primită"}
           </span>
          </div>
        </div>
      </div>

      {((currentUserId && question.user?.id === currentUserId) || isAdmin)
        && (
        <div style={{ marginTop: "20px" }}>
          <button
            className="delete-question-btn"
            onClick={async () => {
              if (window.confirm("Ești sigur că vrei să ștergi această întrebare?")) {
                try {
                  const res = await deleteQuestion(question.id, currentUserId);
                  alert(res.message);
                  window.location.href = "/questions";
                } catch (err) {
                  alert("Eroare la ștergere: " + err.message);
                }
              }
            }}
          >
            Șterge întrebarea
          </button>

          <button
            className="delete-question-btn"
            onClick={() => window.location.href = `/questions/edit/${question.id}`}
          >
            Editează întrebarea
          </button>
        </div>
      )}

      <div className="question-sidebar">
        <div className="like-buttons">
          <button onClick={() => handleVoteQuestion('like')}>👍</button>
          <button onClick={() => handleVoteQuestion('dislike')}>👎</button>
          <span>{likes} voturi</span>
        </div>

        <div className="answers-section">
          <h3>Răspunsuri ({answers.length})</h3>
          {answers.length === 0 ? (
            <p><i>Încă nu există răspunsuri pentru această întrebare.</i></p>
          ) : (
            <ul className="answer-list">
              {answers.map(answer => (
                <li key={answer.id} className="answer-item">
                  {question.user?.id === currentUserId && !answer.accepted && (
                    <button
                      className="accept-answer-btn"
                      onClick={() => handleAcceptAnswer(answer.id)}
                     >
                    Accepta raspunsul
                </button>
                )}
         {answer.accepted && (
            <span className="accepted-badge">Raspuns acceptat</span>
          )}

                  {editingAnswerId === answer.id ? (
                    <AnswerForm
                      questionId={id}
                      existingAnswer={answer}
                      onCancelEdit={() => setEditingAnswerId(null)}
                      onAnswerSubmitted={updated => {
                        setAnswers(prev => prev.map(a => a.id === updated.id ? updated : a));
                        setEditingAnswerId(null);
                      }}
                    />
                  ) : (
                    <>
                      <p className="answer-text">{answer.text}</p>
                      {answer.image && (
                        <img
                          src={`http://localhost:8080${answer.image}`}
                          alt="Imagine răspuns"
                          className="answer-image"
                        />
                      )}
                      <div className="answer-meta"> 
                        <span className="answer-author">
                          — {answer.user?.username || 'Utilizator necunoscut'}
                          {answer.user && (
                            <span style={{ marginLeft: '8px', color: '#666' }}>
                              ({answer.user.score} pct)
                            </span>
                         )}
                      </span>

                        {((currentUserId && question.user?.id === currentUserId) || isAdmin) && (
                          <>
                            <button
                              className="delete-answer-btn"
                              onClick={() => handleDeleteAnswer(answer.id)}
                            >
                              Șterge
                            </button>
                            <button
                              className="edit-answer-btn"
                              onClick={() => setEditingAnswerId(answer.id)}
                            >
                              Editează
                            </button>
                          </>
                        )}
                      </div>
                      <div className="answer-voting">
                        <button onClick={() => handleVoteAnswer(answer.id, 'LIKE')}>👍</button>
                        <button onClick={() => handleVoteAnswer(answer.id, 'DISLIKE')}>👎</button>
                        <span>{answer.voteScore || 0} voturi</span>
                      </div>
                    </>
                  )}
                </li>
              ))}
            </ul>
          )}

          {currentUserId && (
            <AnswerForm
              questionId={id}
              onAnswerSubmitted={newAnswer => setAnswers(prev => [...prev, newAnswer])}
            />
          )}
        </div>
      </div>
    </div>
  );
}

export default QuestionDetails;
