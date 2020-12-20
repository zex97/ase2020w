package com.studyboard.space.transcriber.service.interafaces;

public interface FilePreprocessor {
    /**
     * cuts the file given file into chunks
     * @param sourceFilePath string path to the source file
     * @return string with a path to the target directory
     */
    String cutIntoChunks(String sourceFilePath);
}
