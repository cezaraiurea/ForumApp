package com.example.forumapp.entity;

import com.example.forumapp.enums.QuestionStatus;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "text")
    private String text;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "image")
    private String image;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private QuestionStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "vote_score")
    private Integer voteScore = 0;


    @OneToMany(mappedBy = "question")
    @JsonManagedReference //partea parinte
    private List<QuestionTag> questionTags;

    public Question() {
    }

    public Question(Long id, String title, String text, LocalDateTime date, QuestionStatus status, User user) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.date = date;
        this.status = status;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public int getVoteScore() {
        return voteScore;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setVoteScore(int voteScore) {
        this.voteScore = voteScore;
    }

    public QuestionStatus getStatus() {
        return status;
    }

    public void setStatus(QuestionStatus status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<QuestionTag> getQuestionTags() {
        return questionTags;
    }

    public void setQuestionTags(List<QuestionTag> questionTags) {
        this.questionTags = questionTags;
    }
}
