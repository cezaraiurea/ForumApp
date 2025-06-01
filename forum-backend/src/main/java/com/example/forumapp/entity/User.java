package com.example.forumapp.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    private String role;

    @Column(name = "score")
    private Double score = 0.0;

    @Column(name = "blocked")
    private Boolean blocked = false;


    public User() {
    }

    public User(Long id, String username, String email, String password, String role, Double score) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.score = score;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Double getScore() {
        if (score != null) {
            return score;
        } else {
            return 0.0;
        }
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Boolean getBlocked() {
        return blocked != null && blocked;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }
}
