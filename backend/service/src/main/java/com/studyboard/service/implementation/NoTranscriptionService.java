package com.studyboard.service.implementation;

import com.studyboard.model.Document;

import com.studyboard.service.TranscriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("local")
public class NoTranscriptionService implements TranscriptionService {
    private final Logger logger = LoggerFactory.getLogger(NoTranscriptionService.class);

    @Override
    public void transcribe(Document document) {
        logger.warn("No transcription will be performed in a local profile.");
        document.setTranscription("This is a dummy transcription.");
    }
}
