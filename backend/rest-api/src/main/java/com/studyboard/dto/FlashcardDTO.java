package com.studyboard.dto;


import com.studyboard.model.Document;
import com.studyboard.model.Flashcard;

import javax.persistence.ManyToMany;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class FlashcardDTO {

    private Long id;
    private String question;
    private String answer;
    private int confidenceLevel;
    private List<DeckDTO> deckDTOs;
    private List<DocumentDTO> documentReferences;

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

    public int getConfidenceLevel() {
        return confidenceLevel;
    }

    public void setConfidenceLevel(int confidenceLevel) {
        this.confidenceLevel = confidenceLevel;
    }

    public List<DeckDTO> getDeckDTOs() {
        return deckDTOs;
    }

    public void setDeckDTOs(List<DeckDTO> deckDTOs) {
        this.deckDTOs = deckDTOs;
    }

    public List<DocumentDTO> getDocumentReferences() {
        return documentReferences;
    }

    public void setDocumentReferences(List<DocumentDTO> documentReferences) {
        this.documentReferences = documentReferences;
    }

    public Flashcard FlashcardFromFlashcardDTO() {
        Flashcard flashcard = new Flashcard();
        flashcard.setId(this.id);
        flashcard.setQuestion(this.question);
        flashcard.setAnswer(this.answer);
        flashcard.setConfidenceLevel(this.confidenceLevel);
        /*flashcard.setDecks(this.deckDTOs.stream()
                    .map(DeckDTO::toDeck)
                    .collect(Collectors.toList()));*/
        flashcard.setDocumentReferences(this.documentReferences.stream()
                .map(DocumentDTO::DocumentFromDocumentDTO)
                .collect(Collectors.toList()));
        return flashcard;
    }

    public static FlashcardDTO FlashcardDTOFromFlashcard(Flashcard flashcard) {
        FlashcardDTO flashcardDTO = new FlashcardDTO();
        flashcardDTO.setId(flashcard.getId());
        flashcardDTO.setQuestion(flashcard.getQuestion());
        flashcardDTO.setAnswer(flashcard.getAnswer());
        flashcardDTO.setConfidenceLevel(flashcard.getConfidenceLevel());
        /*flashcardDTO.setDeckDTOs(flashcard.getDecks().stream()
                .map(DeckDTO::of)
                .collect(Collectors.toList()));*/
        flashcardDTO.setDocumentReferences(flashcard.getDocumentReferences().stream()
                .map(DocumentDTO::DocumentDTOFromDocument)
                .collect(Collectors.toList()));
        return flashcardDTO;
    }

    @Override
    public String toString() {
        return "FlashcardDTO{" +
                "id=" + id +
                ", question='" + question + '\'' +
                ", answer='" + answer + '\'' +
                ", confidenceLevel=" + confidenceLevel +
                ", documentReference size=" + documentReferences.size() +
                '}';
    }
}
