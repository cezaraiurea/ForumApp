 import React, { useState } from 'react';
import './AddQuestion.css';
import { useNavigate } from 'react-router-dom';
import HomeIcon from '../Common/HomeIcon';
import { postQuestion } from '../../services/api';

function AddQuestion() {
  const [title, setTitle] = useState('');
  const [text, setText] = useState('');
  const [tags, setTags] = useState('');
  const [image, setImage] = useState(null);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!title || !text) {
      alert("Completează toate câmpurile!");
      return;
    }

    const formData = new FormData();
    formData.append("title", title);
    formData.append("text", text);

    if (tags.trim() !== "") {
      const tagList = tags.split(",").map(t => t.trim());
      tagList.forEach(tag => formData.append("tagNames", tag)); // trimite multiple tagNames[]
    }

    if (image) {
      formData.append("image", image);
    }

    try {
      await postQuestion(formData);
      alert("Întrebare adăugată!");
      navigate('/questions');
    } catch (err) {
      alert(err.message || 'Eroare la salvarea întrebării');
    }
  };

  return (
    <div className="add-question-container">
      <HomeIcon />
      <h2>Pune o întrebare</h2>
      <form onSubmit={handleSubmit} encType="multipart/form-data">
        <input
          type="text"
          placeholder="Titlu întrebare"
          value={title}
          onChange={e => setTitle(e.target.value)}
        />
        <textarea
          placeholder="Conținut întrebare"
          value={text}
          onChange={e => setText(e.target.value)}
        />
        <input
          type="text"
          placeholder="Etichete separate prin virgulă (ex: java, spring)"
          value={tags}
          onChange={e => setTags(e.target.value)}
        />
        <input
          type="file"
          accept="image/*"
          onChange={e => setImage(e.target.files[0])}
        />
        <button type="submit">Trimite întrebare</button>
      </form>
    </div>
  );
}

export default AddQuestion;
