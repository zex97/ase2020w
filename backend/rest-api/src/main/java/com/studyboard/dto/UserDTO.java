package com.studyboard.dto;

import com.studyboard.model.User;

import java.util.Objects;

public class UserDTO {

    private Long id;
    private String username;
    private String password;
    private String email;
    private Integer loginAttempts;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getLoginAttempts() {
        return loginAttempts;
    }

    public void setLoginAttempts(Integer loginAttempts) {
        this.loginAttempts = loginAttempts;
    }

    public User toUser () {
        User user = new User();
        if (this.id != null) {
            user.setId(this.id);
        }
        user.setUsername(this.username);
        user.setPassword(this.password);
        user.setEmail(this.email);
        user.setLoginAttempts(this.loginAttempts);
        return user;
    }

    public static UserDTO of(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setPassword(user.getPassword());
        userDTO.setEmail(user.getEmail());
        userDTO.setLoginAttempts(user.getLoginAttempts());
        return userDTO;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", username=" + username +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", loginAttempts='" + loginAttempts + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserDTO that = (UserDTO) o;

        if (!Objects.equals(id, that.id)) return false;
        if (!Objects.equals(username, that.username)) return false;
        if (!Objects.equals(password, that.password)) return false;
        if (!Objects.equals(email, that.email)) return false;
        return Objects.equals(loginAttempts, that.loginAttempts);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (loginAttempts != null ? loginAttempts.hashCode() : 0);
        return result;
    }
}
