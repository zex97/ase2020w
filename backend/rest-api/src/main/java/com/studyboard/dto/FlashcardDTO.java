package com.studyboard.dto;


import com.studyboard.model.Flashcard;

import java.time.LocalDateTime;
import java.util.List;

public class FlashcardDTO {

    private Long id;
    private String question;
    private String answer;
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
                '}';
    }

}
