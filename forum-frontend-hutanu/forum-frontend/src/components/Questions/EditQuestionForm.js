 import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getQuestionById } from '../../services/api';
import './EditQuestionForm.css';

function EditQuestionForm() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [title, setTitle] = useState('');
  const [text, setText] = useState('');
  const [tags, setTags] = useState('');
  const [image, setImage] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getQuestionById(id).then(question => {
      setTitle(question.title);
      setText(question.text);
      if (question.questionTags) {
        setTags(question.questionTags.map(qt => qt.tag.name).join(', '));
      }
      setLoading(false);
    });
  }, [id]);

  const handleSubmit = async (e) => {
    e.preventDefault();

    const formData = new FormData();
    formData.append('title', title);
    formData.append('text', text);
    formData.append('userId', localStorage.getItem('userId'));

    if (tags.trim()) {
      tags.split(',').map(tag => tag.trim()).forEach(tag => {
        formData.append('tagNames', tag);
      });
    }

    if (image) {
      formData.append('image', image);
    }

    try {
      const res = await fetch(`http://localhost:8080/questions/${id}`, {
        method: 'PUT',
        headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`
        },
        body: formData
      });

      const result = await res.text();
      alert(result);
      navigate(`/questions/${id}`);
    } catch (err) {
      alert('Eroare la actualizare întrebare: ' + err.message);
    }
  };

  if (loading) return <p>Se încarcă formularul...</p>;

  return (
    <form onSubmit={handleSubmit} className="edit-question-form" encType="multipart/form-data">
      <h2>Editează întrebarea</h2>
      <input
        type="text"
        value={title}
        onChange={e => setTitle(e.target.value)}
        required
        placeholder="Titlul întrebării"
      />
      <textarea
        value={text}
        onChange={e => setText(e.target.value)}
        required
        placeholder="Conținutul întrebării"
      />
      <input
        type="text"
        value={tags}
        onChange={e => setTags(e.target.value)}
        placeholder="Etichete (ex: java, spring)"
      />
      <input
        type="file"
        accept="image/*"
        onChange={e => setImage(e.target.files[0])}
      />
      <button type="submit">Salvează modificările</button>
    </form>
  );
}

export default EditQuestionForm;
