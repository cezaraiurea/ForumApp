 import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Home from './components/Home/Home';
import Login from './components/Users/Login';
import Register from './components/Users/Register';
import QuestionList from './components/Questions/QuestionList';
import QuestionDetails from './components/Questions/QuestionDetails';
import AddQuestion from './components/Questions/AddQuestion';
import Header from './components/Common/Header';
import EditQuestionForm from './components/Questions/EditQuestionForm';
import AdminBlockedUsers from './pages/AdminBlockedUsers';


function App() {
  return (
    <Router>
       <Header key={localStorage.getItem('token') || 'guest'} />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/questions" element={<QuestionList />} />
        <Route path="/questions/:id" element={<QuestionDetails />} />
        <Route path="/questions/new" element={<AddQuestion />} />
        <Route path="/questions/edit/:id" element={<EditQuestionForm />} />
        <Route path="/admin/blocked-users" element={<AdminBlockedUsers />} />

      </Routes>
    </Router>
  );
}

export default App;
