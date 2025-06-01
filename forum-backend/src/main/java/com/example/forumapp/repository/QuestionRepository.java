package com.example.forumapp.repository;

import com.example.forumapp.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findAllByOrderByDateDesc();
    List<Question> findByTitleContainingIgnoreCaseOrderByDateDesc(String title);
    List<Question> findByQuestionTags_Tag_IdOrderByDateDesc(Long tagId);
    List<Question> findByUser_IdOrderByDateDesc(Long userId);
    List<Question> findByUser_UsernameOrderByDateDesc(String username);

}