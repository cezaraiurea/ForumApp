import React, { useState } from 'react';
import { postAnswer } from '../services/api';
import { useAuth } from '../contexts/AuthContext';

const AddAnswer = ({ questionId, onAnswerAdded }) => {
    const [text, setText] = useState('');
    const [image, setImage] = useState(null);
    const [error, setError] = useState('');
    const { user } = useAuth();

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!text.trim()) {
            setError('Please enter an answer');
            return;
        }

        try {
            const formData = new FormData();
            formData.append('text', text);
            formData.append('questionId', questionId);
            formData.append('userId', user.id);
            if (image) {
                formData.append('image', image);
            }

            await postAnswer(formData);
            setText('');
            setImage(null);
            setError('');
            if (onAnswerAdded) {
                onAnswerAdded();
            }
        } catch (err) {
            setError(err.message || 'Failed to post answer');
        }
    };

    const handleImageChange = (e) => {
        if (e.target.files && e.target.files[0]) {
            setImage(e.target.files[0]);
        }
    };

    return (
        <div className="add-answer">
            <h3>Add Your Answer</h3>
            {error && <div className="error">{error}</div>}
            <form onSubmit={handleSubmit}>
                <div className="form-group">
                    <textarea
                        value={text}
                        onChange={(e) => setText(e.target.value)}
                        placeholder="Write your answer here..."
                        rows="4"
                    />
                </div>
                <div className="form-group">
                    <label htmlFor="answer-image">Add Image (optional):</label>
                    <input
                        type="file"
                        id="answer-image"
                        accept="image/*"
                        onChange={handleImageChange}
                    />
                    {image && (
                        <div className="image-preview">
                            <p>Selected image: {image.name}</p>
                        </div>
                    )}
                </div>
                <button type="submit" className="btn btn-primary">Post Answer</button>
            </form>
        </div>
    );
};

export default AddAnswer; 