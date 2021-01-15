package com.studyboard.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Deck {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, name = "size")
    private Integer size;

    @Column(nullable = false, name = "creationDate")
    private LocalDate creationDate;

    @Column(nullable = true, name = "lastTimeUsed")
    private LocalDateTime lastTimeUsed;

    @Column(nullable = true, name = "favorite")
    private boolean favorite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sb_user_id")
    private User user;

    @ManyToMany
    @JoinTable(name = "flashcards_assignment",
            joinColumns = @JoinColumn(name = "deck_id"),
            inverseJoinColumns = @JoinColumn(name = "flashcard_id"))
    private List<Flashcard> flashcards;

    public Deck() {
    }

    public Deck(Deck deck) {
        this.id = deck.getId();
        this.name = deck.getName();
        this.size = deck.getSize();
        this.creationDate = deck.getCreationDate();
        this.lastTimeUsed = deck.getLastTimeUsed();
        this.user = deck.getUser();
    }

    public Deck(String name, Integer size, LocalDate creationDate, LocalDateTime lastTimeUsed, User user) {
        this.name = name;
        this.size = size;
        this.creationDate = creationDate;
        this.lastTimeUsed = lastTimeUsed;
        this.user = user;
    }

    public List<Flashcard> getFlashcards() {
        if (flashcards == null) {
            flashcards = new ArrayList<>();
        }
        return flashcards;
    }

    public void setFlashcards(List<Flashcard> flashcards) {
        this.flashcards = flashcards;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
