package com.studyboard.service;

import com.studyboard.model.Document;
import com.studyboard.space.transcriber.exception.FfmpegException;
import com.studyboard.space.transcriber.service.SimpleTranscriptionService;
import com.studyboard.space.transcriber.service.interafaces.FilePreprocessor;
import com.studyboard.space.transcriber.service.interafaces.SpeechRecognitionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TranscriptionServiceTest {
    private static final String FILE_PATH = "/some/path/file.mp3";
    private static final String TRANSCRIPTION_TEXT = "Transcription found";

    @Mock
    private FilePreprocessor filePreprocessor;
    @Mock
    private SpeechRecognitionService speechRecognitionService;

    @InjectMocks
    private SimpleTranscriptionService transcriptionService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void transcribeForAnUnknownFileReturnsEmptyTranscription(){
        Document document = new Document();
        document.setFilePath(FILE_PATH);

        Mockito.when(filePreprocessor.cutIntoChunks(FILE_PATH)).thenThrow(FfmpegException.class);

        transcriptionService.transcribe(document);

        Assertions.assertNull(document.getTranscription());
    }

    @Test
    public void transcribeForAnExistingFileReturnsTranscription(){
        Document document = new Document();
        document.setFilePath(FILE_PATH);

        Mockito.when(filePreprocessor.cutIntoChunks(FILE_PATH)).thenReturn(FILE_PATH);
        Mockito.when(speechRecognitionService.transcribeFilesInDirectory(FILE_PATH)).thenReturn(TRANSCRIPTION_TEXT);

        transcriptionService.transcribe(document);

        Assertions.assertEquals(document.getTranscription(), TRANSCRIPTION_TEXT);
    }

}
