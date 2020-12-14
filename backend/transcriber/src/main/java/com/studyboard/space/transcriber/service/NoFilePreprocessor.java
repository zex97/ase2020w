package com.studyboard.space.transcriber.service;

import com.studyboard.space.transcriber.exception.TranscriberException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@Profile("local")
public class NoFilePreprocessor implements FilePreprocessor {
    private final Logger logger = LoggerFactory.getLogger(NoFilePreprocessor.class);

    public String cutIntoChunks(String sourceFilePath){
        try{
            logger.info("Local mode, no FFMPEG enabled. Copying original file into temp directory.");
            String targetDirectory = prepareTargetDirectory(sourceFilePath);
            String targetFile = getTargetFilePath(sourceFilePath, targetDirectory);
            Files.copy(Paths.get(sourceFilePath), Paths.get(targetFile), StandardCopyOption.REPLACE_EXISTING);
            return targetDirectory;
        } catch (IOException e){
            throw new TranscriberException("Could not move file " + sourceFilePath + "to target", e);
        }
    }

    private String getTargetFilePath(String sourcePath, String targetDirectory){
        return targetDirectory + File.separator + getFileName(sourcePath);
    }

    private String getFileName(String path) {
        return path.substring(path.lastIndexOf(File.separatorChar));
    }

    private String prepareTargetDirectory(String sourceFilePath) throws IOException {
        File sourceFile = new File(sourceFilePath);
        return Files.createTempDirectory(sourceFile.getParentFile().toPath(),sourceFile.getName()).toString();
    }
}
