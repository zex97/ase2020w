package com.studyboard.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;

@Entity
// @Inheritance(strategy = InheritanceType.JOINED)
public class Document {
    private long id;
    private boolean needsTranscription;
    private String transcription;
    private String name;
    private Space space;
    private String filePath;
    private List<Flashcard> flashcards;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    public String getTranscription() {
        return transcription;
    }

    public void setTranscription(String transcription) {
        this.transcription = transcription;
    }
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sb_space_id")
    public Space getSpace() {
        return space;
    }

    public void setSpace(Space space) {
        this.space = space;
    }

    public boolean isNeedsTranscription() {
        return needsTranscription;
    }

    public void setNeedsTranscription(boolean needsTranscription) {
        this.needsTranscription = needsTranscription;
    }

    @PreRemove
    private void removeDocumentFromSpaces() {
        this.space.removeDocument(this);
    }

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "flashcards_reference",
            joinColumns = @JoinColumn(name = "document_id"),
            inverseJoinColumns = @JoinColumn(name = "flashcard_id"))
    public List<Flashcard> getFlashcards() {
        return flashcards;
    }

    public void setFlashcards(List<Flashcard> flashcards) {
        this.flashcards = flashcards;
    }
}
