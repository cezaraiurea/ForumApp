 import React, { useState, useEffect } from 'react';
import './AnswerForm.css';
import { postAnswer, updateAnswer } from '../../services/api';

function AnswerForm({ questionId, onAnswerSubmitted, existingAnswer = null, onCancelEdit }) {
  const [text, setText] = useState('');
  const [image, setImage] = useState(null);
  const userId = localStorage.getItem('userId');

  useEffect(() => {
    if (existingAnswer) {
      setText(existingAnswer.text || '');
    }
  }, [existingAnswer]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    const formData = new FormData();
    formData.append('text', text);
    formData.append('questionId', questionId);
    formData.append('userId', userId);
    if (image) formData.append('image', image);

    try {
      const result = existingAnswer
        ? await updateAnswer(existingAnswer.id, formData)
        : await postAnswer(formData);

      onAnswerSubmitted(result);
      if (!existingAnswer) {
        setText('');
        setImage(null);
      }
    } catch (error) {
      alert('Eroare la trimiterea răspunsului.');
      console.error('Eroare submit:', error);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="answer-form" encType="multipart/form-data">
      <textarea
        value={text}
        onChange={(e) => setText(e.target.value)}
        placeholder="Scrie un răspuns..."
      />
      <input
        type="file"
        accept="image/*"
        onChange={(e) => setImage(e.target.files[0])}
      />
      <button type="submit">{existingAnswer ? 'Salvează modificările' : 'Trimite răspuns'}</button>
      {existingAnswer && <button type="button" onClick={onCancelEdit}>Anulează</button>}
    </form>
  );
}

export default AnswerForm;
