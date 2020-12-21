package com.studyboard.space.transcriber.service.interafaces;

public interface SpeechRecognitionService {

    /**
     * This method is used to retrieve the transcription from Google Speech-to-Text API.
     * The result is one combined transcription for all audio and video files in the directory.
     *
     * @param directoryPath absolute path to the source directory
     * @return string with transcription
     */
    String transcribeFilesInDirectory(String directoryPath);

    /**
     * This method is used to retrieve the transcription from Google Speech-to-Text API.
     * The result is a transcription for the given audio or video file.
     *
     * @param filePath absolute path to the source file
     * @return string with transcription
     */
    String transcribeFile(String filePath);
}
