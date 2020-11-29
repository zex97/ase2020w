package com.studyboard.security.dto;

import io.swagger.annotations.ApiModel;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@ApiModel(value = "AuthenticationTokenInfo", description = "Informations about the current authentication token")
public class AuthenticationTokenInfo {

    private String username;

    private List<String> roles;

    private LocalDateTime issuedAt;

    private LocalDateTime notBefore;

    private LocalDateTime expireAt;

    private Duration validityDuration;

    private Duration overlapDuration;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }

    public LocalDateTime getNotBefore() {
        return notBefore;
    }

    public void setNotBefore(LocalDateTime notBefore) {
        this.notBefore = notBefore;
    }

    public LocalDateTime getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(LocalDateTime expireAt) {
        this.expireAt = expireAt;
    }

    public Duration getValidityDuration() {
        return validityDuration;
    }

    public void setValidityDuration(Duration validityDuration) {
        this.validityDuration = validityDuration;
    }

    public Duration getOverlapDuration() {
        return overlapDuration;
    }

    public void setOverlapDuration(Duration overlapDuration) {
        this.overlapDuration = overlapDuration;
    }

    @Override
    public String toString() {
        return "AuthenticationTokenInfo{" +
            "username='" + username + '\'' +
            ", roles=" + roles +
            ", issuedAt=" + issuedAt +
            ", notBefore=" + notBefore +
            ", expireAt=" + expireAt +
            ", validityDuration=" + validityDuration +
            ", overlapDuration=" + overlapDuration +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuthenticationTokenInfo that = (AuthenticationTokenInfo) o;

        if (username != null ? !username.equals(that.username) : that.username != null) return false;
        if (roles != null ? !roles.equals(that.roles) : that.roles != null) return false;
        if (issuedAt != null ? !issuedAt.equals(that.issuedAt) : that.issuedAt != null) return false;
        if (notBefore != null ? !notBefore.equals(that.notBefore) : that.notBefore != null) return false;
        if (expireAt != null ? !expireAt.equals(that.expireAt) : that.expireAt != null) return false;
        if (validityDuration != null ? !validityDuration.equals(that.validityDuration) : that.validityDuration != null)
            return false;
        return overlapDuration != null ? overlapDuration.equals(that.overlapDuration) : that.overlapDuration == null;

    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (roles != null ? roles.hashCode() : 0);
        result = 31 * result + (issuedAt != null ? issuedAt.hashCode() : 0);
        result = 31 * result + (notBefore != null ? notBefore.hashCode() : 0);
        result = 31 * result + (expireAt != null ? expireAt.hashCode() : 0);
        result = 31 * result + (validityDuration != null ? validityDuration.hashCode() : 0);
        result = 31 * result + (overlapDuration != null ? overlapDuration.hashCode() : 0);
        return result;
    }

    public static AuthenticationTokenInfoBuilder builder() {
        return new AuthenticationTokenInfoBuilder();
    }

    public static final class AuthenticationTokenInfoBuilder {

        private String username;
        private List<String> roles;
        private LocalDateTime issuedAt;
        private LocalDateTime notBefore;
        private LocalDateTime expireAt;
        private Duration validityDuration;
        private Duration overlapDuration;

        public AuthenticationTokenInfoBuilder username(String username) {
            this.username = username;
            return this;
        }

        public AuthenticationTokenInfoBuilder roles(List<String> roles) {
            this.roles = roles;
            return this;
        }

        public AuthenticationTokenInfoBuilder issuedAt(LocalDateTime issuedAt) {
            this.issuedAt = issuedAt;
            return this;
        }

        public AuthenticationTokenInfoBuilder notBefore(LocalDateTime notBefore) {
            this.notBefore = notBefore;
            return this;
        }

        public AuthenticationTokenInfoBuilder expireAt(LocalDateTime expireAt) {
            this.expireAt = expireAt;
            return this;
        }

        public AuthenticationTokenInfoBuilder validityDuration(Duration validityDuration) {
            this.validityDuration = validityDuration;
            return this;
        }

        public AuthenticationTokenInfoBuilder overlapDuration(Duration overlapDuration) {
            this.overlapDuration = overlapDuration;
            return this;
        }

        public AuthenticationTokenInfo build() {
            AuthenticationTokenInfo authenticationTokenInfo = new AuthenticationTokenInfo();
            authenticationTokenInfo.setUsername(username);
            authenticationTokenInfo.setRoles(roles);
            authenticationTokenInfo.setIssuedAt(issuedAt);
            authenticationTokenInfo.setNotBefore(notBefore);
            authenticationTokenInfo.setExpireAt(expireAt);
            authenticationTokenInfo.setValidityDuration(validityDuration);
            authenticationTokenInfo.setOverlapDuration(overlapDuration);
            return authenticationTokenInfo;
        }
    }
}
