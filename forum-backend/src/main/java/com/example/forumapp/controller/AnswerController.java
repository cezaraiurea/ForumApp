
package com.example.forumapp.controller;

import com.example.forumapp.entity.Answer;
import com.example.forumapp.service.AnswerService;
import com.example.forumapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/answers")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AnswerController {

    @Autowired
    private AnswerService answerService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/question/{questionId}")
    public ResponseEntity<List<Answer>> getAnswersByQuestionId(@PathVariable Long questionId) {
        List<Answer> answers = answerService.getAnswersByQuestionId(questionId);
        return ResponseEntity.ok(answers);
    }

    @PostMapping
    public ResponseEntity<Answer> createAnswer(
            @RequestParam("questionId") Long questionId,
            @RequestParam("userId") Long userId,
            @RequestParam("text") String text,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        Answer answer = answerService.createAnswer(questionId, userId, text, image);
        return ResponseEntity.ok(answer);
    }

    @PutMapping(value = "/{answerId}", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, String>> updateAnswer(
            @PathVariable Long answerId,
            @RequestParam("userId") Long userId,
            @RequestParam("text") String text,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) {
        try {
            answerService.updateAnswerWithImage(answerId, userId, text, image);
            return ResponseEntity.ok(Map.of("message", "Answer updated successfully."));

        }
        catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }

    }

    @DeleteMapping("/{answerId}")
    public ResponseEntity<String> deleteAnswer(
            @PathVariable Long answerId,
            @RequestParam Long userId) {
        answerService.deleteAnswer(answerId, userId);
        return ResponseEntity.ok("Answer deleted successfully");
    }

    @GetMapping("/question/{questionId}/by-score")
    public List<Answer> getAnswersByQuestionSorted(@PathVariable Long questionId) {
        return answerService.getAnswersSortedByVotes(questionId);
    }


    @PutMapping("/{answerId}/accept")
    public ResponseEntity<String> acceptAnswer(@PathVariable Long answerId, @RequestParam Long userId) {
        try {
            String result = answerService.acceptAnswer(answerId, userId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

}
