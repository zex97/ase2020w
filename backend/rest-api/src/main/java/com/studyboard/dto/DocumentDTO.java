package com.studyboard.dto;

import com.studyboard.model.Document;

import java.util.List;

public class DocumentDTO {

    private long id;
    private boolean needsTranscription;
    private String transcription;
    private String name;
    private SpaceDTO spaceDTO;
    private String filePath;
    private List<FlashcardDTO> flashcards;
    private List<String> tags;

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

    public SpaceDTO getSpaceDTO() {
        return spaceDTO;
    }

    public void setSpaceDTO(SpaceDTO spaceDTO) {
        this.spaceDTO = spaceDTO;
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

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Document DocumentFromDocumentDTO() {
        Document document = new Document();
        document.setId(this.id);
        document.setFilePath(this.filePath);
        document.setName(this.name);
        document.setTranscription(this.transcription);
        document.setSpace(this.spaceDTO.toSpace());
        document.setTags(this.tags);
        return document;
    }

    public static DocumentDTO DocumentDTOFromDocument(Document document) {
        DocumentDTO documentDTO = new DocumentDTO();
        documentDTO.setId(document.getId());
        documentDTO.setFilePath(document.getFilePath());
        documentDTO.setName(document.getName());
        documentDTO.setTranscription(document.getTranscription());
        documentDTO.setSpaceDTO(SpaceDTO.of(document.getSpace()));
        documentDTO.setTags(document.getTags());
        return documentDTO;
    }

    @Override
    public String toString() {
        return "DocumentDTO{" +
                "id=" + id +
                ", needsTranscription=" + needsTranscription +
                ", transcription='" + transcription + '\'' +
                ", name='" + name + '\'' +
                ", spaceDTO=" + spaceDTO +
                ", filePath='" + filePath + '\'' +
                ", flashcards=" + flashcards +
                ", tags=" + tags +
                '}';
    }
}
