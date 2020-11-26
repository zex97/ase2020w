package com.studyboard.model;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Deck {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, name = "size")
    private Integer size;

    @Column(nullable = false, name = "creationDate")
    private LocalDate creationDate;

    @Column(nullable = true, name = "lastTimeUsed")
    private LocalDateTime lastTimeUsed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sb_user_id")
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getLastTimeUsed() {
        return lastTimeUsed;
    }

    public void setLastTimeUsed(LocalDateTime lastTimeUsed) {
        this.lastTimeUsed = lastTimeUsed;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}