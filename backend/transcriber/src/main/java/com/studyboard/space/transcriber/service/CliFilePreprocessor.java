package com.studyboard.space.transcriber.service;

import com.studyboard.space.transcriber.config.TranscriberConfig;
import com.studyboard.space.transcriber.exception.FfmpegException;
import com.studyboard.space.transcriber.exception.FfmpegIllegalFormatException;
import com.studyboard.space.transcriber.service.interafaces.FilePreprocessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * This class implements File processor
 * using the command line tool FFMPEG [https://ffmpeg.org/]
 *
 */
@Service
@Profile("!local")
public class CliFilePreprocessor implements FilePreprocessor {
    private final Logger logger = LoggerFactory.getLogger(CliFilePreprocessor.class);

    private static final int CHUNK_LENGTH = 30;

    private final String ffmpegLocation;
    private final String videoCmd;
    private final String audioCmd;

    /**
     * The constructor is not intended to be used directly. Use @Autowired instead.
     * @param config TranscriberConfig including the path to the FFMPEG executable
     */
    @Autowired
    public CliFilePreprocessor(TranscriberConfig config) {
        this.ffmpegLocation = config.getFfmpegPath();
        this.videoCmd = ffmpegLocation + " -i %s -f segment -segment_time %s -ac 1 -vn -c:a pcm_s16le %s%%03d.wav";
        this.audioCmd = ffmpegLocation + " -i %s -f segment -segment_time %s -ac 1 %s%%03d.wav";
    }

    /**
     * Cuts the given audio or video file into chunks with length CHUNK_LENGTH seconds
     *
     * @param sourceFilePath absolute path to the source file
     * @return new directory with chunks
     * Attention! creates an additional folder in system path.
     * */
    @Override
    public String cutIntoChunks(String sourceFilePath) {
        logger.info("Request to FFMPEG: processing");
        String targetDirectory = "";
        try{
             targetDirectory = prepareTargetDirectory(sourceFilePath);
             exec(prepareCmdForFile(sourceFilePath, targetDirectory));
        } catch (IOException | InterruptedException e){
            throw new FfmpegException("Could not split the video file " + sourceFilePath, e);
        }

        logger.info("Request to FFMPEG: successful");
        return targetDirectory;
    }

    private String prepareTargetDirectory(String sourceFilePath) throws IOException {
        File sourceFile = new File(sourceFilePath);
        return Files.createTempDirectory(sourceFile.getParentFile().toPath(),sourceFile.getName()).toString();
    }

    private void exec(String command) throws IOException, InterruptedException {
        Process ffmpegProcess = Runtime.getRuntime().exec(command);
        ffmpegProcess.waitFor();
    }

    private String prepareCmdForFile(String sourceFile, String targetDirectory){

        String command = String.format(getCmdForFileType(sourceFile),
                sourceFile,
                CHUNK_LENGTH,
                targetDirectory + File.separator + getFilePrefix(sourceFile));
        return command;
    }

    private String getCmdForFileType(String path){
        String extension = getFileExtension(path);
        switch(extension){
            case ".mp3": return audioCmd;
            case ".mp4": return videoCmd;
            default: throw new FfmpegIllegalFormatException("Could not process file {}. Allowed extensions are [.mps, .mp4]");
        }
    }

    private String getFilePrefix(String path) {
        return path.substring(path.lastIndexOf(File.separatorChar),path.lastIndexOf('.'));
    }

    private String getFileExtension(String path) {
        return path.substring(path.length()-4);
    }
}

