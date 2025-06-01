// src/services/api.test.js
import { getQuestions, login, postQuestion } from './api';

describe('api service', () => {
  beforeEach(() => {
    global.fetch = jest.fn();
  });

  it('getQuestions() fetches /questions and returns JSON', async () => {
    const fake = [{ id:1, title:'Q1' }];
    fetch.mockResolvedValueOnce({
      ok: true,
      headers: { get: () => 'application/json' },
      json: () => Promise.resolve(fake)
    });

    const data = await getQuestions();
    expect(fetch).toHaveBeenCalledWith('/questions', expect.any(Object));
    expect(data).toEqual(fake);
  });

  it('login() wraps plain-text into {message}', async () => {
    const txt = 'Login OK';
    fetch.mockResolvedValueOnce({
      ok: true,
      headers: { get: () => 'text/plain' },
      text: () => Promise.resolve(txt)
    });

    const result = await login({ email:'a', password:'b' });
    expect(fetch).toHaveBeenCalledWith('/users/login', expect.objectContaining({
      method: 'POST',
      body: JSON.stringify({ email:'a', password:'b' })
    }));
    expect(result).toEqual({ message: txt });
  });

  it('postQuestion() throws on HTTP error with JSON message', async () => {
    fetch.mockResolvedValueOnce({
      ok: false,
      status: 400,
      headers: { get: () => 'application/json' },
      json: () => Promise.resolve({ message:'Bad!' })
    });

    await expect(postQuestion({})).rejects.toThrow('Bad!');
  });
});
