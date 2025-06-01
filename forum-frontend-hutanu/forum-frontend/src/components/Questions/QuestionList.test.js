// src/components/Questions/QuestionList.test.js
import '@testing-library/jest-dom';
import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import QuestionList from './QuestionList';
import { MemoryRouter } from 'react-router-dom';

import * as api from '../../services/api';

jest.mock('../../services/api');

test('renders questions from API', async () => {
  api.getQuestions.mockResolvedValue([
    { id:1, title:'First', user:{username:'U1'}, questionTags:[] }
  ]);

  render(
     <MemoryRouter>
       <QuestionList />
      </MemoryRouter>
   );

  await waitFor(() => {
    expect(screen.getByText('First')).toBeInTheDocument();
    expect(screen.getByText('U1')).toBeInTheDocument();
  });
});
