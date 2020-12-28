package com.studyboard.dto;

import com.studyboard.model.Document;

import java.util.List;

public class DocumentDTO {
    private long id;
    private boolean needsTranscription;
    private String transcription;
    private String name;
    private SpaceDTO space;
    private String filePath;
    private List<FlashcardDTO> flashcards;

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

    public String getTranscription() {
        return transcription;
    }

    public void setTranscription(String transcription) {
        this.transcription = transcription;
    }

    public SpaceDTO getSpace() {
        return space;
    }

    public void setSpace(SpaceDTO space) {
        this.space = space;
    }

    public boolean isNeedsTranscription() {
        return needsTranscription;
    }

    public void setNeedsTranscription(boolean needsTranscription) {
        this.needsTranscription = needsTranscription;
    }

    public List<FlashcardDTO> getFlashcards() {
        return flashcards;
    }

    public void setFlashcards(List<FlashcardDTO> flashcards) {
        this.flashcards = flashcards;
    }

    public Document DocumentFromDocumentDTO() {
        Document document = new Document();
        document.setId(this.id);
        document.setFilePath(this.filePath);
        document.setName(this.name);
        document.setTranscription(this.transcription);
        document.setSpace(this.space.toSpace());
        return document;
    }

    public static DocumentDTO DocumentDTOFromDocument(Document document) {
        DocumentDTO documentDTO = new DocumentDTO();
        documentDTO.setId(document.getId());
        documentDTO.setFilePath(document.getFilePath());
        documentDTO.setName(document.getName());
        documentDTO.setTranscription(document.getTranscription());
        documentDTO.setSpace(SpaceDTO.of(document.getSpace()));
        return documentDTO;
    }

    @Override
    public String toString() {
        return "documentDTO{" +
                "id=" + id +
                ", filePath='" + filePath + '\'' +
                ", name='" + name + '\'' +
                ", transcription=" + transcription +
                ", space=" + space.toString() +
                '}';
    }
}
