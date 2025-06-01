package com.example.forumapp.service;

import com.example.forumapp.entity.Answer;
import com.example.forumapp.entity.Question;
import com.example.forumapp.entity.User;
import com.example.forumapp.entity.Vote;
import com.example.forumapp.enums.VoteType;
import com.example.forumapp.repository.AnswerRepository;
import com.example.forumapp.repository.QuestionRepository;
import com.example.forumapp.repository.UserRepository;
import com.example.forumapp.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VoteService {

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private QuestionRepository questionRepository;


    //adaugare vot la un raspuns
    public String voteAnswer(Long answerId, Long userId, VoteType voteType) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new IllegalArgumentException("Answer not found"));

        if (answer.getUser().getId().equals(userId)) {
            return "You cannot vote your own answer.";
        }

        Optional<Vote> existingVote = voteRepository.findByUserIdAndAnswerId(userId, answerId);
        if (existingVote.isPresent()) {
            return "User has already voted for this answer.";
        }

        User author = answer.getUser();
        User voter = userRepository.findById(userId).orElseThrow();

        Vote vote = new Vote();
        vote.setAnswer(answer);
        vote.setUser(voter);
        vote.setType(voteType);
        voteRepository.save(vote);

        int newScore = calculateVotes(answer.getId());
        answer.setVoteScore(newScore);
        answerRepository.save(answer);

        if (voteType == VoteType.LIKE) {
            author.setScore(author.getScore()+5);
        } else {
            author.setScore(author.getScore()-2.5);
            voter.setScore(voter.getScore()-1.5);
            userRepository.save(voter);
        }

        userRepository.save(author);
        return "Vote added successfully.";
    }


    public String voteQuestion(Long questionId, Long userId, VoteType voteType) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found"));

        if (question.getUser().getId().equals(userId)) {
            return "You cannot vote your own question.";
        }

        Optional<Vote> existingVote = voteRepository.findByUserIdAndQuestionId(userId, questionId);
        if (existingVote.isPresent()) {
            return "User has already voted for this question.";
        }

        User author = question.getUser();
        User voter = userRepository.findById(userId).orElseThrow();

        Vote vote = new Vote();
        vote.setQuestion(question);
        vote.setUser(voter);
        vote.setType(voteType);
        voteRepository.save(vote);

        int newScore = calculateQuestionVotes(question.getId());
        question.setVoteScore(newScore);
        questionRepository.save(question);

        // scor autor
        if (voteType == VoteType.LIKE) {
            author.setScore(author.getScore()+2.5);
        } else {
            author.setScore(author.getScore()-1.5);
        }

        userRepository.save(author);
        return "Vote added successfully.";
    }

    public boolean checkIfUserVotedAnswer(Long answerId, Long userId) {
        Optional<Vote> existingVote = voteRepository.findByUserIdAndAnswerId(userId, answerId);
        return existingVote.isPresent();
    }


    public boolean checkIfUserVotedQuestion(Long questionId, Long userId) {
        Optional<Vote> existingVote = voteRepository.findByUserIdAndQuestionId(userId, questionId);
        return existingVote.isPresent();
    }

    public int calculateVotes(Long answerId) {
        int likes = voteRepository.countByAnswerIdAndType(answerId, VoteType.LIKE);
        int dislikes = voteRepository.countByAnswerIdAndType(answerId, VoteType.DISLIKE);
        return likes - dislikes;
    }

    public int calculateQuestionVotes(Long questionId) {
        int likes = voteRepository.countByQuestionIdAndType(questionId, VoteType.LIKE);
        int dislikes = voteRepository.countByQuestionIdAndType(questionId, VoteType.DISLIKE);
        return likes - dislikes;
    }



    public String deleteVote(Long userId, Long answerId, Long questionId) {
        Optional<Vote> vote = voteRepository.findByUserIdAndAnswerId(userId, answerId);
        if (vote.isPresent()) {
            voteRepository.delete(vote.get());
            return "Vote deleted successfully.";
        }

        vote = voteRepository.findByUserIdAndQuestionId(userId, questionId);
        if (vote.isPresent()) {
            voteRepository.delete(vote.get());
            return "Vote deleted successfully.";
        }
        return "Vote not found";
    }
}
