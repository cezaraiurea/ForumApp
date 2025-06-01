package com.example.forumapp.service;

import com.example.forumapp.entity.Question;
import com.example.forumapp.entity.QuestionTag;
import com.example.forumapp.entity.Tag;
import com.example.forumapp.entity.User;
import com.example.forumapp.repository.*;
import com.example.forumapp.enums.QuestionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuestionService {
    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionTagRepository questionTagRepository;

    @Autowired
    private TagService tagService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Transactional
    public Question insertQuestion(Question question, List<String> tagNames, MultipartFile image) {
        question.setDate(LocalDateTime.now());
        question.setStatus(QuestionStatus.RECEIVED);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        question.setUser(user);

        if (image != null && !image.isEmpty()) {
            try {
                Path uploadPath = Paths.get("uploads");
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String originalFilename = image.getOriginalFilename();
                String extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
                String uniqueFilename = UUID.randomUUID().toString() + extension;

                Path filePath = uploadPath.resolve(uniqueFilename);
                Files.copy(image.getInputStream(), filePath);

                question.setImage("/uploads/" + uniqueFilename);

            } catch (IOException e) {
                throw new RuntimeException("Eroare la salvarea imaginii", e);
            }
        }


        Question savedQuestion = questionRepository.save(question);

        if (tagNames != null && !tagNames.isEmpty()) {
            for (String tagName : tagNames) {
                Tag tag = tagService.getOrCreateTag(tagName);
                QuestionTag newQuestionTag = new QuestionTag();
                newQuestionTag.setQuestion(savedQuestion);
                newQuestionTag.setTag(tag);
                questionTagRepository.save(newQuestionTag);
            }
        }

        return savedQuestion;
    }

    public Question getQuestionById(Long id) {
        return questionRepository.findById(id).orElse(null);
    }

    public List<Question> getAllQuestions() {
        return questionRepository.findAllByOrderByDateDesc();
    }

    public List<Question> getQuestionsByUserId(Long userId) {
        return this.questionRepository.findByUser_IdOrderByDateDesc(userId);
    }

    public List<Question> getQuestionsByTitle(String title) {
        return this.questionRepository.findByTitleContainingIgnoreCaseOrderByDateDesc(title);
    }

    public List<Question> getQuestionsByTagName(String tagName) {
        Optional<Tag> tag = tagService.findByNameIgnoreCase(tagName);
        return tag.map(value -> questionRepository.findByQuestionTags_Tag_IdOrderByDateDesc(value.getId())).orElseGet(ArrayList::new);
    }

    public List<Question> getQuestionsByUsername(String username) {
        return questionRepository.findByUser_UsernameOrderByDateDesc(username);
    }


    @Transactional
    public String updateQuestion(Question updatedQuestion, Long userId, List<String> tagNames, MultipartFile image) {
        Optional<Question> existingQuestionOpt = this.questionRepository.findById(updatedQuestion.getId());
        if (existingQuestionOpt.isEmpty()) {
            return "Question not found.";
        }

        Question question = existingQuestionOpt.get();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));

        boolean isAuthor = question.getUser().getId().equals(userId);
        boolean isAdmin = "ADMIN".equalsIgnoreCase(user.getRole());

        if (!isAuthor && !isAdmin) {
            return "You are not allowed to update this question.";
        }

        question.setTitle(updatedQuestion.getTitle());
        question.setText(updatedQuestion.getText());
        question.setStatus(QuestionStatus.RECEIVED);


        if (image != null && !image.isEmpty()) {
            try {
                Path uploadPath = Paths.get("uploads");
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String originalFilename = image.getOriginalFilename();
                String extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
                String uniqueFilename = UUID.randomUUID().toString() + extension;

                Path filePath = uploadPath.resolve(uniqueFilename);
                Files.copy(image.getInputStream(), filePath);

                question.setImage("/uploads/" + uniqueFilename);

            } catch (IOException e) {
                throw new RuntimeException("Eroare la salvarea imaginii", e);
            }
        }

        questionTagRepository.deleteAll(question.getQuestionTags());

        if (tagNames != null) {
            List<QuestionTag> newTags = new ArrayList<>();
            for (String tagName : tagNames) {
                Tag tag = tagService.getOrCreateTag(tagName);
                QuestionTag qt = new QuestionTag();
                qt.setQuestion(question);
                qt.setTag(tag);
                newTags.add(questionTagRepository.save(qt));
            }
            question.setQuestionTags(newTags);
        }

        questionRepository.save(question);
        return "Question updated successfully.";
    }



    @Transactional
    public String deleteQuestionById(Long id, Long userId) {
        Optional<Question> optionalQuestion = questionRepository.findById(id);
        if (optionalQuestion.isEmpty()) {
            return "Question not found.";
        }

        Question question = optionalQuestion.get();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));

        boolean isAuthor = question.getUser().getId().equals(userId);
        boolean isAdmin = "ADMIN".equalsIgnoreCase(user.getRole());

        if (!isAuthor && !isAdmin) {
            return "You are not allowed to delete this question.";
        }

        voteRepository.deleteAllByQuestionId(question.getId());
        voteRepository.deleteAllByAnswer_Question_Id(question.getId());
        answerRepository.deleteAllByQuestionId(question.getId());
        questionTagRepository.deleteAll(question.getQuestionTags());
        questionRepository.deleteById(id);

        return "Question deleted successfully.";
    }

}
