package com.studyboard.space.transcriber.service;

import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import com.studyboard.space.transcriber.service.interafaces.SpeechRecognitionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Service
public class GoogleSpeechRecognitionService implements SpeechRecognitionService {
    private final Logger logger = LoggerFactory.getLogger(GoogleSpeechRecognitionService.class);

    private final RecognitionConfig config =
            RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                    .setLanguageCode("en-US")
                    .build();

    /**
     * This method is used to retrieve the transcription from Google Speech-to-Text API.
     * The result is one combined transcription for all audio and video files in the directory.
     * @param directoryPath absolute path to the source directory
     * @return string with transcription
     */
    public String transcribeFilesInDirectory(String directoryPath){
        logger.info("Starting speech recognition");
        File dirWithChunks = new File(directoryPath);
        String[] fileNames = dirWithChunks.list();
        StringBuilder transcriptionBuilder = new StringBuilder();
        if (fileNames == null){
            return transcriptionBuilder.toString();
        }
        Arrays.sort(fileNames);
        for (String fileName : fileNames) {
            transcriptionBuilder.append(transcribeFile(directoryPath + fileName));
        }
        logger.info("Speech recognition complete");
        return transcriptionBuilder.toString();
    }

    /**
     * This method is used to retrieve the transcription from Google Speech-to-Text API.
     * The result is a transcription for the given audio or video file.
     * @param filePath absolute path to the source file
     * @return string with transcription
     */
    public String transcribeFile(String filePath){
        logger.info("Processing next file.");
        String transcription = "";
        try (SpeechClient speech = SpeechClient.create()) {
            RecognitionAudio requestBody = RecognitionAudio.newBuilder().setContent(getBytes(filePath)).build();
            RecognizeResponse response = speech.recognize(config, requestBody);
            transcription = extractResult(response.getResultsList());
        } catch (IOException e){
            logger.error("Could not get transcription for the file {}", filePath, e);
        }
        return transcription;
    }

    private String extractResult(List<SpeechRecognitionResult> results){
        if (results.size() != 1) {
            logger.warn("Size of the result list of chunks is not one");
        }
        StringBuilder transcript = new StringBuilder();
        for (SpeechRecognitionResult result : results) {
            transcript.append(result.getAlternativesList().get(0).getTranscript());
        }
        return transcript.toString();
    }

    private ByteString getBytes(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        byte[] content = Files.readAllBytes(path);
        ByteString bytes = ByteString.copyFrom(content);
        return bytes;
    }
}
