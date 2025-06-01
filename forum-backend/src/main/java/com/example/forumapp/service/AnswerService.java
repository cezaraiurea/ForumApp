package com.example.forumapp.service;

import com.example.forumapp.entity.Answer;
import com.example.forumapp.entity.Question;
import com.example.forumapp.entity.User;
import com.example.forumapp.repository.AnswerRepository;
import com.example.forumapp.repository.QuestionRepository;
import com.example.forumapp.repository.UserRepository;
import com.example.forumapp.repository.VoteRepository;
import com.example.forumapp.enums.QuestionStatus;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AnswerService {

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private VoteService voteService;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private UserRepository userRepository;

    private final Path uploadDir = Paths.get("uploads/answers");

    public AnswerService() {
        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory!", e);
        }
    }

    @Transactional
    public Answer insertAnswer(Answer answer) {
        answer.setDate(LocalDateTime.now());
        Answer savedAnswer = answerRepository.save(answer);

        Question question = answer.getQuestion();

        if (question.getStatus() == QuestionStatus.RECEIVED) {
            question.setStatus(QuestionStatus.IN_PROGRESS);
            questionRepository.save(question);
        }

        return savedAnswer;
    }

    public List<Answer> getAnswersByQuestionId(Long questionId) {
        return answerRepository.findByQuestionId(questionId);
    }

    public Answer updateAnswer(Long answerId, Long userId, Answer updatedAnswer) {
        Optional<Answer> answer = this.answerRepository.findById(answerId);

        if (answer.isPresent() && answer.get().getUser().getId().equals(userId)) {
            Answer existingAnswer = answer.get();
            existingAnswer.setText(updatedAnswer.getText());
            existingAnswer.setImage(updatedAnswer.getImage());
            return this.answerRepository.save(existingAnswer);
        }
        return null;
    }

    public String deleteAnswerById(Long answerId, Long userId) {
        Optional<Answer> answer = this.answerRepository.findById(answerId);

        if (answer.isPresent() && answer.get().getUser().getId().equals(userId)) {
            this.answerRepository.deleteById(answerId);
            return "Answer deleted successfully.";
        }
        return "This answer can not be deleted because you are not the author.";
    }

    public List<Answer> getAnswersSortedByVotes(Long questionId) {
        return answerRepository.findByQuestionIdOrderByVoteScoreDesc(questionId);
    }

    @Transactional
    public String acceptAnswer(Long answerId, Long userId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("Answer not found"));
        Question question = answer.getQuestion();

        if (!question.getUser().getId().equals(userId)) {
            throw new RuntimeException("Only the question creator can accept an answer.");
        }

        List<Answer> allAnswers = answerRepository.findByQuestionId(question.getId());
        for (Answer a : allAnswers) {
            if (a.getAccepted()) {
                a.setAccepted(false);
                answerRepository.save(a);
            }
        }

        answer.setAccepted(true);
        answerRepository.save(answer);

        question.setStatus(QuestionStatus.RESOLVED);
        questionRepository.save(question);

        return "Answer accepted successfully.";
    }


    @Transactional
    public Answer createAnswer(Long questionId, Long userId, String text, MultipartFile image) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Answer answer = new Answer();
        answer.setQuestion(question);
        answer.setUser(user);
        answer.setText(text);
        answer.setDate(LocalDateTime.now());

        if (image != null && !image.isEmpty()) {
            try {
                String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
                Path filePath = uploadDir.resolve(fileName);
                Files.copy(image.getInputStream(), filePath);
                answer.setImage("/uploads/answers/" + fileName);
            } catch (IOException e) {
                throw new RuntimeException("Failed to store image", e);
            }
        }

        Answer savedAnswer = answerRepository.save(answer);

        if (question.getStatus() == QuestionStatus.RECEIVED) {
            question.setStatus(QuestionStatus.IN_PROGRESS);
            questionRepository.save(question);
        }

        return savedAnswer;
    }

    @Transactional
    public void deleteAnswer(Long answerId, Long userId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("Answer not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isAuthor = answer.getUser().getId().equals(userId);
        boolean isAdmin = "ADMIN".equalsIgnoreCase(user.getRole());

        if (!isAuthor && !isAdmin) {
            throw new RuntimeException("You are not allowed to delete this answer.");
        }

        voteRepository.deleteAllByAnswerId(answerId);

        if (answer.getImage() != null) {
            try {
                String fileName = answer.getImage().substring(answer.getImage().lastIndexOf("/") + 1);
                Path imagePath = uploadDir.resolve(fileName);
                Files.deleteIfExists(imagePath);
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete image file", e);
            }
        }

        answerRepository.delete(answer);
    }


    @Transactional
    public void updateAnswerWithImage(Long answerId, Long userId, String text, MultipartFile image) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("Answer not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isAuthor = answer.getUser().getId().equals(userId);
        boolean isAdmin = "ADMIN".equalsIgnoreCase(user.getRole());

        if (!isAuthor && !isAdmin) {
            throw new RuntimeException("You are not authorized to update this answer.");
        }

        answer.setText(text);

        if (image != null && !image.isEmpty()) {
            if (answer.getImage() != null) {
                try {
                    String oldFileName = answer.getImage().substring(answer.getImage().lastIndexOf("/") + 1);
                    Path oldPath = uploadDir.resolve(oldFileName);
                    Files.deleteIfExists(oldPath);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to delete old image", e);
                }
            }

            try {
                String newFileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
                Path newPath = uploadDir.resolve(newFileName);
                Files.copy(image.getInputStream(), newPath);
                answer.setImage("/uploads/answers/" + newFileName);
            } catch (IOException e) {
                throw new RuntimeException("Failed to store new image", e);
            }
        }

        answerRepository.save(answer);
    }





}
