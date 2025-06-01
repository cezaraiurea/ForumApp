package com.example.forumapp.repository;

import com.example.forumapp.entity.Vote;
import com.example.forumapp.enums.VoteType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByUserIdAndAnswerId(Long userId, Long answerId);
    Optional<Vote> findByUserIdAndQuestionId(Long userId, Long questionId);
    int countByAnswerIdAndType(Long answerId, VoteType type);
    int countByQuestionIdAndType(Long questionId, VoteType type);
    void deleteAllByAnswer_Question_Id(Long questionId);
    void deleteAllByQuestionId(Long id);
    void deleteAllByAnswerId(Long answerId);
}
