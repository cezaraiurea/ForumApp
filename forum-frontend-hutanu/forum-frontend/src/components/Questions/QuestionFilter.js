import React from 'react';
import './QuestionFilter.css';

function QuestionFilter({ filterText, onFilterChange }) {
  return (
    <div className="question-filter">
      <input
        type="text"
        placeholder="Caută întrebare, autor sau etichetă..."
        value={filterText}
        onChange={(e) => onFilterChange(e.target.value)}
      />
    </div>
  );
}

export default QuestionFilter;
