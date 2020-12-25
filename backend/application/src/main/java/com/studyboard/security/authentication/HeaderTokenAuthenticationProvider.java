package com.studyboard.security.authentication;

import com.studyboard.security.service.HeaderTokenAuthenticationService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class HeaderTokenAuthenticationProvider implements AuthenticationProvider {

    private final HeaderTokenAuthenticationService headerTokenAuthenticationService;

    public HeaderTokenAuthenticationProvider(HeaderTokenAuthenticationService headerTokenAuthenticationService) {
        Assert.notNull(headerTokenAuthenticationService, "headerTokenAuthenticationService cannot be null");
        this.headerTokenAuthenticationService = headerTokenAuthenticationService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        String headerToken = (String) authentication.getCredentials();
        User user = headerTokenAuthenticationService.authenticate(headerToken);
        AuthenticationHeaderToken authenticationResult = new AuthenticationHeaderToken(user, headerToken, user.getAuthorities());
        authenticationResult.setDetails(authentication.getDetails());
        return authenticationResult;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return AuthenticationHeaderToken.class.isAssignableFrom(authentication);
    }
}
