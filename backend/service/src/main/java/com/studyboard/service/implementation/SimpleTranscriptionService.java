package com.studyboard.service.implementation;

import com.studyboard.model.Document;

import com.studyboard.repository.DocumentRepository;
import com.studyboard.service.FilePreprocessor;
import com.studyboard.service.SpeechRecognitionService;
import com.studyboard.service.TranscriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * This class implements TranscriptionService.
 * It can be used to transcribe long audion and video files.
 *
 */
@Service
@Profile("!local")
@EnableAsync
public class SimpleTranscriptionService implements TranscriptionService {
    private final Logger logger = LoggerFactory.getLogger(SimpleTranscriptionService.class);

    @Autowired
    private FilePreprocessor filePreprocessor;
    @Autowired
    private SpeechRecognitionService speechRecognitionService;
    @Autowired
    private DocumentRepository documentRepository;

    @Override
    @Async
    public void transcribe(Document document) {
        try{
            String outputDirectory = filePreprocessor.cutIntoChunks(document.getFilePath());
            String transcription = speechRecognitionService.transcribeFilesInDirectory(outputDirectory);
            document.setTranscription(transcription);
            if (documentRepository.existsById(document.getId())) {
                documentRepository.save(document);
            } else {
                logger.warn("Document deleted before it was transcribed");
            }
            logger.debug(transcription);
            cleanup(outputDirectory);
        } catch (Exception e){
            logger.error("Could not transcribe file {}", document.getFilePath(), e);
        }
    }

    private void cleanup(String tempDirectory){
        File path = new File(tempDirectory);
        if (!path.isDirectory()){
            return;
        }
        for (File file: path.listFiles()){
            file.delete();
        }
        path.delete();
    }
}
