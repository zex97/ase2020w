package com.studyboard.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "user_roles", uniqueConstraints = {@UniqueConstraint(columnNames = {"username"})})
public class Authorities {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_authorities_id")
    @SequenceGenerator(name = "seq_authorities_id", sequenceName = "seq_authorities_id")
    private Long id;

    @Column(nullable = false, name = "username")
    private String username;

    @Column(nullable = false, name = "role")
    private String authority;

    public Authorities(String username, String authority) {
        this.username = username;
        this.authority = authority;
    }

    public Authorities() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return authority;
    }

    public static AuthoritiesBuilder builder() {
        return new AuthoritiesBuilder();
    }

    @Override
    public String toString() {
        return "authorities{" +
            "id=" + id +
            ", username=" + username +
            ", authority='" + authority + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Authorities authorities = (Authorities) o;

        if (!Objects.equals(id, authorities.id)) return false;
        if (!Objects.equals(username, authorities.username)) return false;
        return Objects.equals(authority, authorities.authority);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (authority != null ? authority.hashCode() : 0);
        return result;
    }


    public static final class AuthoritiesBuilder {
        private Long id;
        private String username;
        private String authority;

        private AuthoritiesBuilder() {
        }

        public AuthoritiesBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public AuthoritiesBuilder username(String username) {
            this.username = username;
            return this;
        }

        public AuthoritiesBuilder password(String authority) {
            this.authority = authority;
            return this;
        }


        public Authorities build() {
            Authorities authorities = new Authorities();
            authorities.setId(id);
            authorities.setUsername(username);
            authorities.setAuthority(authority);
            return authorities;
        }
    }
}
