package com.studyboard.service;

import com.studyboard.model.Document;

public interface TranscriptionService {

    /**
     * This method is used to transcribe given file and store information in the DB.
     * If the service could not transcribe the file empty transcription will be saved.
     *
     * @param document document representation of the file to be transcribed
     */
    void transcribe(Document document);
}
