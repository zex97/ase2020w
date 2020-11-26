package com.studyboard.security.dto.authentication;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "AuthenticationToken", description = "Data Transfer Objects for AuthenticationTokens")
public class AuthenticationToken {

    @ApiModelProperty(required = true, readOnly = true, name = "Current authentication token")
    private String currentToken;

    @ApiModelProperty(required = true, readOnly = true, name = "Future authentication token")
    private String futureToken;

    public String getCurrentToken() {
        return currentToken;
    }

    public void setCurrentToken(String currentToken) {
        this.currentToken = currentToken;
    }

    public String getFutureToken() {
        return futureToken;
    }

    public void setFutureToken(String futureToken) {
        this.futureToken = futureToken;
    }

    @Override
    public String toString() {
        return "AuthenticationToken{" +
            "currentToken='" + currentToken + '\'' +
            ", futureToken='" + futureToken + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuthenticationToken that = (AuthenticationToken) o;

        if (currentToken != null ? !currentToken.equals(that.currentToken) : that.currentToken != null) return false;
        return futureToken != null ? futureToken.equals(that.futureToken) : that.futureToken == null;

    }

    @Override
    public int hashCode() {
        int result = currentToken != null ? currentToken.hashCode() : 0;
        result = 31 * result + (futureToken != null ? futureToken.hashCode() : 0);
        return result;
    }

    public static AuthenticationTokenBuilder builder() {
        return new AuthenticationTokenBuilder();
    }

    public static final class AuthenticationTokenBuilder {

        private String currentToken;
        private String futureToken;

        public AuthenticationTokenBuilder currentToken(String currentToken) {
            this.currentToken = currentToken;
            return this;
        }

        public AuthenticationTokenBuilder futureToken(String futureToken) {
            this.futureToken = futureToken;
            return this;
        }

        public AuthenticationToken build() {
            AuthenticationToken authenticationToken = new AuthenticationToken();
            authenticationToken.setCurrentToken(currentToken);
            authenticationToken.setFutureToken(futureToken);
            return authenticationToken;
        }
    }
}
