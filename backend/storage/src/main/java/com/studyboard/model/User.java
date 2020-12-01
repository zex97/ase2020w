package com.studyboard.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sb_user")
public class User {

    @Column(name="sb_user_id")
    private long id;
    private String username;
    private String password;
    private String email;
    private Integer loginAttempts;
    private List<String> filePaths;
    private String role;
    private Boolean enabled;
    private List<Deck> decks;
    private List<Space> spaces;

    @Column(nullable = false, name = "role")
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Column(nullable = false, name = "enabled")
    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "userId")
  public List<Space> getSpaces() {
    if (spaces == null) {
      spaces = new ArrayList<>();
    }
    return spaces;
  }

  @ElementCollection
  public List<String> getFilePaths() {
    if(filePaths == null) {
      filePaths = new ArrayList<>();
    }
    return filePaths;
  }

  public void setFilePaths(List<String> filePaths) {
    this.filePaths = filePaths;
  }

  public void setSpaces(List<Space> spaces) {
    this.spaces = spaces;
  }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<Deck> getDecks() {
        if (decks == null) {
            decks = new ArrayList<>();
        }
        return decks;
    }

    public void setDecks(List<Deck> decks) {
        this.decks = decks;
    }

    @Column(nullable = false, name = "username", unique = true)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(nullable = false, name = "password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(nullable = false, name = "email", unique = true)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(nullable = false, name = "loginAttempts")
    public Integer getLoginAttempts() {
        return loginAttempts;
    }

    public void setLoginAttempts(Integer loginAttempts) {
        this.loginAttempts = loginAttempts;
    }
}
