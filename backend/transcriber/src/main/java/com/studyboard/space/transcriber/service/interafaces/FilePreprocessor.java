package com.studyboard.space.transcriber.service.interafaces;

public interface FilePreprocessor {

    /**
     * Cuts the given audio or video file into chunks with length CHUNK_LENGTH seconds
     *
     * @param sourceFilePath absolute path to the source file
     * @return new directory with chunks
     * Attention! creates an additional folder in system path.
     * */
    String cutIntoChunks(String sourceFilePath);
}
