package com.example.forumapp.controller;

import com.example.forumapp.entity.Vote;
import com.example.forumapp.entity.User;
import com.example.forumapp.enums.VoteType;
import com.example.forumapp.repository.UserRepository;
import com.example.forumapp.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/votes")
public class VoteController {

    @Autowired
    private VoteService voteService;

    @Autowired
    private UserRepository userRepository;


    @PostMapping("/answer/{answerId}")
    public ResponseEntity<String> voteAnswer(@PathVariable Long answerId,
                                             @RequestParam VoteType voteType) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        String result = voteService.voteAnswer(answerId, user.getId(), voteType);
        if (result.equals("Vote added successfully.")) {
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.status(400).body(result);
    }


    @PostMapping("/question/{questionId}")
    public ResponseEntity<String> voteQuestion(@PathVariable Long questionId,
                                               @RequestParam VoteType voteType) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        String result = voteService.voteQuestion(questionId, user.getId(), voteType);
        if (result.equals("Vote added successfully.")) {
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.status(400).body(result);
    }


    @GetMapping("/answer/{answerId}/check")
    public ResponseEntity<Boolean> checkIfUserVotedAnswer(@PathVariable Long answerId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        boolean hasVoted = voteService.checkIfUserVotedAnswer(answerId, user.getId());
        return ResponseEntity.ok(hasVoted);
    }


    @GetMapping("/question/{questionId}/check")
    public ResponseEntity<Boolean> checkIfUserVotedQuestion(@PathVariable Long questionId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        boolean hasVoted = voteService.checkIfUserVotedQuestion(questionId, user.getId());
        return ResponseEntity.ok(hasVoted);
    }

}
