package com.studyboard.dto;


import com.studyboard.model.Document;

public class DocumentDTO {

    private long id;
    private boolean needsTranscription;
    private String transcription;
    private String name;
    private SpaceDTO spaceDTO;
    private String filePath;

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

    public Document DocumentFromDocumentDTO() {
        Document document = new Document();
        document.setId(this.id);
        document.setNeedsTranscription(this.needsTranscription);
        document.setName(this.name);
        document.setFilePath(this.filePath);
        document.setTranscription(this.transcription);
        document.setSpace(this.spaceDTO.toSpace());
        return document;
    }

    public DocumentDTO DocumentDTOFromDocument(Document document) {
        DocumentDTO documentDTO = new DocumentDTO();
        documentDTO.setId(document.getId());
        documentDTO.setNeedsTranscription(document.isNeedsTranscription());
        documentDTO.setName(document.getName());
        documentDTO.setFilePath(document.getFilePath());
        documentDTO.setTranscription(document.getTranscription());
        documentDTO.setSpaceDTO(SpaceDTO.of(document.getSpace()));
        return documentDTO;
    }

    @Override
    public String toString() {
        return "DocumentDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", needsTranscription='" + needsTranscription + '\'' +
                '}';
    }
}
