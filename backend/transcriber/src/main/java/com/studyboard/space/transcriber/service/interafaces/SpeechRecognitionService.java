package com.studyboard.space.transcriber.service.interafaces;

public interface SpeechRecognitionService {
    String transcribeFilesInDirectory(String directoryPath);
    String transcribeFile(String filePath);
}
