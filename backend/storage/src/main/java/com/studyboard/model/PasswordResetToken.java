package com.studyboard.model;

import javax.persistence.*;
import java.util.Calendar;

@Entity
public class PasswordResetToken {

    public PasswordResetToken () {

    }

    public PasswordResetToken(User user, String token) {
        this.user = user;
        this.token = token;
        Calendar expires =  Calendar.getInstance();
        expires.add(Calendar.HOUR, 24);
        this.expires = expires.getTimeInMillis();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="token_id")
    private long id;

    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "sb_user_id")
    private User user;

    private Long expires;

    public long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }

    public Long getExpires() {
        return expires;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setExpires(Long expires) {
        this.expires = expires;
    }

}
