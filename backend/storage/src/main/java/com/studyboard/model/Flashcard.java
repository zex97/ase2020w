package com.studyboard.model;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
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

    @Column(nullable = true, name = "easiness")
    private double easiness;

    @Column(nullable = true, name = "interval")
    private int interval;

    @Column(nullable = true, name = "correctness_streak")
    private int correctnessStreak;

    @Column(nullable = true, name = "next_due_date")
    private LocalDateTime nextDueDate;

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

    public List<Deck> getDecks() {
        return decks;
    }

    public void setDecks(List<Deck> decks) {
        this.decks = decks;
    }

    public double getEasiness() {
        return easiness;
    }

    public void setEasiness(double easiness) {
        this.easiness = easiness;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getCorrectnessStreak() {
        return correctnessStreak;
    }

    public void setCorrectnessStreak(int correctnessStreak) {
        this.correctnessStreak = correctnessStreak;
    }

    public LocalDateTime getNextDueDate() {
        return nextDueDate;
    }

    public void setNextDueDate(LocalDateTime nextDueDate) {
        this.nextDueDate = nextDueDate;
    }
}
