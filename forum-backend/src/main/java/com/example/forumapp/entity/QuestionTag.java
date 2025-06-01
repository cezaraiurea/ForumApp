package com.example.forumapp.entity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "question_tags")
public class QuestionTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "question_id")
    @JsonBackReference //partea copil
    private Question question;

    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Tag tag;


    public QuestionTag() {
    }

    public QuestionTag(Long id, Question question, Tag tag) {
        this.id = id;
        this.question = question;
        this.tag = tag;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }
}

