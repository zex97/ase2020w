package com.studyboard.model;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@Entity
public class Flashcard {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "f_id")
    private long id;

    @Column(nullable = false, name = "question")
    private String question;

    @Column(nullable = false, name = "answer")
    private String answer;

    @Column(nullable = true, name = "confidenceLevel")
    @Min(0)
    @Max(5)
    private int confidenceLevel;

    @ManyToMany(mappedBy = "flashcards")
    private List<Deck> decks;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getConfidenceLevel() {
        return confidenceLevel;
    }

    public void setConfidenceLevel(int confidenceLevel) {
        this.confidenceLevel = confidenceLevel;
    }


    public List<Deck> getDecks() {
        return decks;
    }

    public void setDecks(List<Deck> decks) {
        this.decks = decks;
    }
}
