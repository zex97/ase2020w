package com.studyboard.security.configuration.properties;

import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Validated
@Configuration
public class AuthenticationConfigurationProperties {

    @NotNull
    private SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    @NotNull
    private String secret = "jwtSecret";
    @NotNull
    private Duration validityDuration = Duration.of(600L, ChronoUnit.SECONDS);
    @NotNull
    private Duration overlapDuration = Duration.of(300L, ChronoUnit.SECONDS);

    public SignatureAlgorithm getSignatureAlgorithm() {
        return signatureAlgorithm;
    }

    public void setSignatureAlgorithm(String signatureAlgorithm) {
        this.signatureAlgorithm = SignatureAlgorithm.forName(signatureAlgorithm);
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Duration getValidityDuration() {
        return validityDuration;
    }

    public void setValidityDuration(long validityDuration) {
        this.validityDuration = Duration.of(validityDuration, ChronoUnit.SECONDS);
    }

    public Duration getOverlapDuration() {
        return overlapDuration;
    }

    public void setOverlapDuration(long overlapDuration) {
        this.overlapDuration = Duration.of(overlapDuration, ChronoUnit.SECONDS);
    }
}
