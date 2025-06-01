const AUTH_BASE_URL = 'http://localhost:8081/auth';
const API_BASE_URL = 'http://localhost:8080'; // asta e pt intrebari, raspunsuri, scor, voturi etc

async function request(path, options = {}) {
  
   let baseUrl;

if (path.startsWith('/auth') || path === '/login' || path === '/register') {
  baseUrl = AUTH_BASE_URL;
} else {
  baseUrl = API_BASE_URL;
}

let headers = {
  'Content-Type': 'application/json',
  ...options.headers,
};


   
  if (baseUrl === API_BASE_URL && !path.startsWith('/answers/question/')) {
      const token = localStorage.getItem('token');
      if (token) {
          headers = { ...headers, Authorization: `Bearer ${token}` };
      }
  } else if (baseUrl === AUTH_BASE_URL) {
        //
  }


  const response = await fetch(`${baseUrl}${path}`, {
    credentials: 'include',
    headers,
    ...options,
  });

  if (!response.ok) {
  const contentType = response.headers.get('content-type') || '';
  let errMsg = `Request failed: ${response.status}`;

  try {
    if (contentType.includes('application/json')) {
      const errJson = await response.clone().json();  
      errMsg = errJson.message || errJson.token || errMsg;
    } else {
      const errText = await response.clone().text();  
      errMsg = errText || errMsg;
    }
  } catch (e) {
    console.error("Eroare la parsarea răspunsului:", e);
  }

  throw new Error(errMsg);
}


  const contentType = response.headers.get('content-type') || '';
  if (contentType.includes('application/json')) {
    if (options.method === 'DELETE') {
        return { message: await response.text() };
    }
    return response.json();
  } else {
    return { message: await response.text() };
  }
}

export function login(credentials) {
  return request('/login', {
    method: 'POST',
    body: JSON.stringify(credentials),
  });
}

export function register(data) {
  return request('/register', {
    method: 'POST',
    body: JSON.stringify(data),
  });
}


export function getQuestions() {
  return request('/questions');
}

export function getQuestionById(id) {
  return request(`/questions/${id}`);
}


export function postQuestion(formData) {
  const token = localStorage.getItem('token');

  return fetch('http://localhost:8080/questions', {
    method: 'POST',
    body: formData,
    headers: {
      Authorization: `Bearer ${token}`,   
    },
  }).then(async res => {
    if (!res.ok) throw new Error(await res.text());
    return res.json();
  });
}

export function getAnswersByQuestionId(questionId) {
  return request(`/answers/question/${questionId}`);
}
 

export function postAnswer(formData) {
  const token = localStorage.getItem('token');

  return fetch('http://localhost:8080/answers', {
    method: 'POST',
    body: formData,
    headers: {
      Authorization: `Bearer ${token}`,  
    },
  }).then(async res => {
    if (!res.ok) throw new Error(await res.text());
    return res.json();
  });
}
 
export function deleteAnswer(answerId, userId) {
  return request(`/answers/${answerId}?userId=${userId}`, {
    method: 'DELETE',
  });
}



export function deleteQuestion(questionId, userId) {
  return request(`/questions/${questionId}`, {
    method: 'DELETE',
    body: JSON.stringify({ id: userId }), 
  });
}


export function voteQuestion(questionId, voteType) {
  const token = localStorage.getItem('token');
  return fetch(`http://localhost:8080/votes/question/${questionId}?voteType=${voteType}`, {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`
    }
  }).then(async res => {
    if (!res.ok) throw new Error(await res.text());
    return res.text();
  });
}

export function voteAnswer(answerId, voteType) {
  const token = localStorage.getItem('token');
  return fetch(`http://localhost:8080/votes/answer/${answerId}?voteType=${voteType}`, {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
    },
  }).then(async res => {
    if (!res.ok) throw new Error(await res.text());
    return res.text();
  });
}

export function updateQuestion(id, formData) {
  const token = localStorage.getItem('token');

  return fetch(`http://localhost:8080/questions/${id}`, {
    method: 'PUT',
    headers: {
      Authorization: `Bearer ${token}`,
    },
    body: formData,
  }).then(async res => {
    if (!res.ok) throw new Error(await res.text());
    return res.text();
  });
}

export function updateAnswer(answerId, formData) {
  const token = localStorage.getItem('token');

  return fetch(`http://localhost:8080/answers/${answerId}`, {
    method: 'PUT',
    headers: {
      Authorization: `Bearer ${token}`,
    },
    body: formData,
  }).then(async res => {
    if (!res.ok) throw new Error(await res.text());
    return res.json();  
  });
}

export const getMyQuestions = async () => {
  const userId = localStorage.getItem('userId');
  const token = localStorage.getItem('token');

  const res = await fetch(`http://localhost:8080/questions/user/${userId}`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  if (!res.ok) throw new Error("Eroare la afisarea intrebarilor utilizatorului");

  return await res.json();
};

export function getAllUsers() {
  return request('/users');
}

export function getBlockedUsers() {
  return request('/users/blocked');
}

export function blockUser(userId) {
  return request(`/users/block/${userId}`, { method: 'PUT' });
}

export function unblockUser(userId) {
  return request(`/users/unblock/${userId}`, { method: 'PUT' });
}


