package com.example.forumapp.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="answers")
public class Answer {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name="text")
    private String text;

    @Column(name="date")
    private LocalDateTime date;

    @Column(name="image")
    private String image;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @Column(name = "vote_score")
    private Integer voteScore = 0;


    @Column(name = "accepted")
    private Boolean accepted = false;

    public Answer() {}

    public Answer(Long id, String text, LocalDateTime date, String image, User user, Question question) {
        this.id = id;
        this.text = text;
        this.date = date;
        this.image = image;
        this.user = user;
        this.question = question;
    }

    public Long getId() {

        return id;
    }

    public void setId(Long id) {

        this.id = id;
    }

    public String getText() {
        return text;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public Integer getVoteScore() {
        return voteScore != null ? voteScore : 0;
    }

    public void setVoteScore(Integer voteScore) {
        this.voteScore = voteScore;
    }


    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Boolean getAccepted() {
        return accepted != null ? accepted : false;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }


}
