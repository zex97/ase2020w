package com.studyboard.security.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class AuthenticationHeaderToken extends AbstractAuthenticationToken {

    private static final long serialVersionUID = 739L;

    private final String token;
    private final Object principal;

    public AuthenticationHeaderToken(String token) {
        super(null);
        this.token = token;
        principal = null;
        setAuthenticated(false);
    }

    public AuthenticationHeaderToken(Object principal, String token,
                                     Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.token = token;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
