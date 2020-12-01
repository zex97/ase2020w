package com.studyboard.security.configuration;

import com.studyboard.StudyboardApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

@Configuration
@EntityScan(basePackageClasses = {StudyboardApplication.class, Jsr310JpaConverters.class})
public class JpaJsr310Configuration {
}
