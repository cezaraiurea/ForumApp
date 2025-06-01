package com.example.forumapp.controller;

import com.example.forumapp.entity.Question;
import com.example.forumapp.entity.User;
import com.example.forumapp.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.forumapp.service.TagService;
import com.example.forumapp.entity.Tag;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/questions")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private TagService tagService;

    @GetMapping
    public List<Question> getAllQuestions() {
        return questionService.getAllQuestions();
    }

    @GetMapping("/{questionId}")
    public ResponseEntity<Question> getQuestionById(@PathVariable Long questionId) {
        Question question = questionService.getQuestionById(questionId);
        if (question != null) {
            return ResponseEntity.ok(question);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/user/{userId}")
    public List<Question> getQuestionsByUserId(@PathVariable Long userId) {
        return questionService.getQuestionsByUserId(userId);
    }

    @GetMapping("/title")
    public List<Question> searchByTitle(@RequestParam("text") String title) {
        return questionService.getQuestionsByTitle(title);
    }

    @GetMapping("/tag")
    public List<Question> getQuestionsByTagName(@RequestParam("name") String tagName) {
        Optional<Tag> tag = tagService.findByNameIgnoreCase(tagName);
        if (tag.isPresent()) {
            return questionService.getQuestionsByTagName(tagName);
        }
        return new ArrayList<>();
    }

    @GetMapping("/user/by-username")
    public List<Question> getQuestionsByUsername(@RequestParam String username) {
        return questionService.getQuestionsByUsername(username);
    }


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Question> createQuestion(
            @RequestParam("title") String title,
            @RequestParam("text") String text,
            @RequestParam(value = "tagNames", required = false) List<String> tagNames,
            @RequestParam(value = "image", required = false) MultipartFile image // <-- AICI e diferența!
    )
    {
        Question question = new Question();
        question.setTitle(title);
        question.setText(text);
        Question createdQuestion = questionService.insertQuestion(question, tagNames, image);

        return ResponseEntity.status(201).body(createdQuestion);
    }

    @PutMapping(value = "/{questionId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateQuestionWithImageAndTags(
            @PathVariable Long questionId,
            @RequestParam("title") String title,
            @RequestParam("text") String text,
            @RequestParam("userId") Long userId,
            @RequestParam(value = "tagNames", required = false) List<String> tagNames,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        Question question = new Question();
        question.setId(questionId);
        question.setTitle(title);
        question.setText(text);

        User user = new User();
        user.setId(userId);
        question.setUser(user);

        String result = questionService.updateQuestion(question, userId, tagNames, image);

        if (result.equals("Question updated successfully.")) {
            return ResponseEntity.ok(result);
        } else if (result.equals("Question not found.")) {
            return ResponseEntity.status(404).body(result);
        } else {
            return ResponseEntity.status(403).body(result);
        }
    }


    @DeleteMapping("/{questionId}")
    public ResponseEntity<String> deleteQuestion(@PathVariable Long questionId, @RequestBody User user) {

        Long userId = user.getId();
        String result = questionService.deleteQuestionById(questionId, userId);

        if (result.equals("Question deleted successfully.")) {
            return ResponseEntity.ok(result);
        } else if (result.equals("Question not found.")) {
            return ResponseEntity.status(404).body(result);
        } else {
            return ResponseEntity.status(403).body(result);
        }
    }
}