package com.studyboard.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
// @Inheritance(strategy = InheritanceType.JOINED)
public class Document {
    private long id;
    private boolean needsTranscription;
    private String transcription;
    private String name;
    private Space space;
    private String filePath;

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
}
