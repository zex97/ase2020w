package com.studyboard.security.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "AuthenticationRequest", description = "Data Transfer Objects for Authentication Requests via REST")
public class AuthenticationRequest {

    @ApiModelProperty(required = true, name = "The unique name of the user", example = "admin")
    private String username;

    @ApiModelProperty(required = true, name = "The password of the user", example = "password")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public CharSequence getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "AuthenticationRequest{" +
            "username='" + username + '\'' +
            ", password='" + password + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuthenticationRequest that = (AuthenticationRequest) o;

        if (username != null ? !username.equals(that.username) : that.username != null) return false;
        return password != null ? password.equals(that.password) : that.password == null;

    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }

    public static AuthenticationRequestBuilder builder() {
        return new AuthenticationRequestBuilder();
    }

    public static final class AuthenticationRequestBuilder {

        private String username;
        private String password;

        public AuthenticationRequestBuilder username(String username) {
            this.username = username;
            return this;
        }

        public AuthenticationRequestBuilder password(String password) {
            this.password = password;
            return this;
        }

        public AuthenticationRequest build() {
            AuthenticationRequest authenticationRequest = new AuthenticationRequest();
            authenticationRequest.setUsername(username);
            authenticationRequest.setPassword(password);
            return authenticationRequest;
        }
    }
}
