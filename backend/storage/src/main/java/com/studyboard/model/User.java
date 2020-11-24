package com.studyboard.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sb_user")
public class User {
    private long id;
    private List<Space> spaces;
    private List<Deck> decks;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "userId")
    public List<Space> getSpaces() {
        if (spaces == null) {
            spaces = new ArrayList<>();
        }
        return spaces;
    }

    public void setSpaces(List<Space> spaces) {
        this.spaces = spaces;
    }

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "userId")
    public List<Deck> getDecks() {
        if (decks == null) {
            decks = new ArrayList<>();
        }
        return decks;
    }

    public void setDecks(List<Deck> decks) {
        this.decks = decks;
    }
}
