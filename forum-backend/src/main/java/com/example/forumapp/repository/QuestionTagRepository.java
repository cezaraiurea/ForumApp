package com.example.forumapp.repository;


import com.example.forumapp.entity.QuestionTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionTagRepository extends JpaRepository<QuestionTag, Long> {
    List<QuestionTag> findByTagId(Long tagId);
}
