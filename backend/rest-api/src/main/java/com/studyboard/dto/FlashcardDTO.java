package com.studyboard.dto;


import com.studyboard.model.Flashcard;

import java.time.LocalDateTime;
import java.util.List;

public class FlashcardDTO {

    private Long id;
    private String question;
    private String answer;
    private double easiness;
    private int interval;
    private int correctnessStreak;
    private LocalDateTime nextDueDate;
    private List<DeckDTO> deckDTOs;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public List<DeckDTO> getDeckDTOs() {
        return deckDTOs;
    }

    public void setDeckDTOs(List<DeckDTO> deckDTOs) {
        this.deckDTOs = deckDTOs;
    }

    public Flashcard FlashcardFromFlashcardDTO() {
        Flashcard flashcard = new Flashcard();
        flashcard.setId(this.id);
        flashcard.setQuestion(this.question);
        flashcard.setAnswer(this.answer);
        flashcard.setEasiness(this.easiness);
        flashcard.setInterval(this.interval);
        flashcard.setCorrectnessStreak(this.correctnessStreak);
        flashcard.setNextDueDate(this.nextDueDate);
        /*flashcard.setDecks(this.deckDTOs.stream()
                    .map(DeckDTO::toDeck)
                    .collect(Collectors.toList()));*/
        return flashcard;
    }

    public static FlashcardDTO FlashcardDTOFromFlashcard(Flashcard flashcard) {
        FlashcardDTO flashcardDTO = new FlashcardDTO();
        flashcardDTO.setId(flashcard.getId());
        flashcardDTO.setQuestion(flashcard.getQuestion());
        flashcardDTO.setAnswer(flashcard.getAnswer());
        flashcardDTO.setEasiness(flashcard.getEasiness());
        flashcardDTO.setInterval(flashcard.getInterval());
        flashcardDTO.setCorrectnessStreak(flashcard.getCorrectnessStreak());
        flashcardDTO.setNextDueDate(flashcard.getNextDueDate());
        /*flashcardDTO.setDeckDTOs(flashcard.getDecks().stream()
                .map(DeckDTO::of)
                .collect(Collectors.toList()));*/
        return flashcardDTO;
    }

    @Override
    public String toString() {
        return "FlashcardDTO{" +
                "id=" + id +
                ", question='" + question + '\'' +
                ", answer='" + answer + '\'' +
                ", current easiness factor=" + easiness +
                ", waiting interval=" + interval +
                ", current correct answers streak=" + correctnessStreak +
                ", due date for the next review=" + nextDueDate +
                '}';
    }

}
