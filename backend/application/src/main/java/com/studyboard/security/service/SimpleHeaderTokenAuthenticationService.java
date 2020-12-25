package com.studyboard.security.service;

import com.studyboard.security.dto.AuthenticationToken;
import com.studyboard.security.dto.AuthenticationTokenInfo;
import com.studyboard.security.configuration.properties.AuthenticationConfigurationProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/** Service used to manage token authentication. */
@Service
public class SimpleHeaderTokenAuthenticationService implements HeaderTokenAuthenticationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleHeaderTokenAuthenticationService.class);

    private final AuthenticationManager authenticationManager;
    private final ObjectMapper objectMapper;
    private final SecretKeySpec signingKey;
    private final SignatureAlgorithm signatureAlgorithm;
    private final Duration validityDuration;
    private final Duration overlapDuration;
    private static final String ROLE_PREFIX = "ROLE_";
    private static final String JWT_CLAIM_AUTHORITY = "aut";
    private static final String JWT_CLAIM_PRINCIPAL = "pri";
    private static final String JWT_CLAIM_PRINCIPAL_ID = "pid";

    public SimpleHeaderTokenAuthenticationService(
        @Lazy AuthenticationManager authenticationManager,
        AuthenticationConfigurationProperties authenticationConfigurationProperties,
        ObjectMapper objectMapper
    ) {
        this.authenticationManager = authenticationManager;
        this.objectMapper = objectMapper;
        byte[] apiKeySecretBytes = Base64.getEncoder().encode(
            authenticationConfigurationProperties.getSecret().getBytes());
        signatureAlgorithm = authenticationConfigurationProperties.getSignatureAlgorithm();
        signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        validityDuration = authenticationConfigurationProperties.getValidityDuration();
        overlapDuration = authenticationConfigurationProperties.getOverlapDuration();
    }

    @Override
    public AuthenticationToken authenticate(String username, CharSequence password) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, password));
        Instant now = Instant.now();
        String authorities = "";
        try {
            authorities = objectMapper.writeValueAsString(authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        } catch (JsonProcessingException e) {
            LOGGER.error("Failed to wrap authorities", e);
        }
        String currentToken = Jwts.builder()
            .claim(JWT_CLAIM_PRINCIPAL_ID, null)
            .claim(JWT_CLAIM_PRINCIPAL, authentication.getName())
            .claim(JWT_CLAIM_AUTHORITY, authorities)
            .setIssuedAt(Date.from(now))
            .setNotBefore(Date.from(now))
            .setExpiration(Date.from(now.plus(validityDuration)))
            .signWith(signatureAlgorithm, signingKey)
            .compact();
        String futureToken = Jwts.builder()
            .claim(JWT_CLAIM_PRINCIPAL_ID, null)
            .claim(JWT_CLAIM_PRINCIPAL, authentication.getName())
            .claim(JWT_CLAIM_AUTHORITY, authorities)
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now
                .plus(validityDuration
                    .minus(overlapDuration)
                    .plus(validityDuration))))
            .setNotBefore(Date.from(now
                .plus(validityDuration
                    .minus(overlapDuration))))
            .signWith(signatureAlgorithm, signingKey)
            .compact();
        return AuthenticationToken.builder()
            .currentToken(currentToken)
            .futureToken(futureToken)
            .build();
    }

    @Override
    public AuthenticationTokenInfo authenticationTokenInfo(String headerToken) {
        final Claims claims = Jwts.parser()
            .setSigningKey(signingKey)
            .parseClaimsJws(headerToken)
            .getBody();
        List<String> roles = readJwtAuthorityClaims(claims);
        return AuthenticationTokenInfo.builder()
            .username((String) claims.get(JWT_CLAIM_PRINCIPAL))
            .roles(roles)
            .issuedAt(LocalDateTime.ofInstant(claims.getIssuedAt().toInstant(), ZoneId.systemDefault()))
            .notBefore(LocalDateTime.ofInstant(claims.getNotBefore().toInstant(), ZoneId.systemDefault()))
            .expireAt(LocalDateTime.ofInstant(claims.getExpiration().toInstant(), ZoneId.systemDefault()))
            .validityDuration(validityDuration)
            .overlapDuration(overlapDuration)
            .build();
    }

    @Override
    public AuthenticationToken renewAuthentication(String headerToken) {
        final Claims claims = Jwts.parser()
            .setSigningKey(signingKey)
            .parseClaimsJws(headerToken)
            .getBody();
        String futureToken = Jwts.builder()
            .claim(JWT_CLAIM_PRINCIPAL_ID, claims.get(JWT_CLAIM_PRINCIPAL_ID))
            .claim(JWT_CLAIM_PRINCIPAL, claims.get(JWT_CLAIM_PRINCIPAL))
            .claim(JWT_CLAIM_AUTHORITY, claims.get(JWT_CLAIM_AUTHORITY))
            .setIssuedAt(Date.from(Instant.now()))
            .setExpiration(Date.from(claims.getExpiration().toInstant()
                .plus(validityDuration
                    .minus(overlapDuration))))
            .setNotBefore(Date.from(claims.getExpiration().toInstant().minus(overlapDuration)))
            .signWith(signatureAlgorithm, signingKey)
            .compact();
        return AuthenticationToken.builder()
            .currentToken(headerToken)
            .futureToken(futureToken)
            .build();
    }

    @Override
    public User authenticate(String headerToken) {
        try {
            final Claims claims = Jwts.parser()
                .setSigningKey(signingKey)
                .parseClaimsJws(headerToken)
                .getBody();
            List<String> authoritiesWrapper = readJwtAuthorityClaims(claims);
            List<SimpleGrantedAuthority> authorities = authoritiesWrapper.stream()
                .map(roleName -> roleName.startsWith(ROLE_PREFIX) ?
                    roleName : (ROLE_PREFIX + roleName))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
            return new User(
                (String) claims.get(JWT_CLAIM_PRINCIPAL),
                headerToken,
                authorities);
        } catch (ExpiredJwtException e) {
            throw new CredentialsExpiredException(e.getMessage(), e);
        } catch (JwtException e) {
            throw new BadCredentialsException(e.getMessage(), e);
        }
    }

    private List<String> readJwtAuthorityClaims(Claims claims) {
        ArrayList<String> authoritiesWrapper = new ArrayList<>();
        try {
            authoritiesWrapper = (ArrayList<String>) objectMapper.readValue(claims.get(
                JWT_CLAIM_AUTHORITY, String.class),
                new TypeReference<List<String>>() {
                });
        } catch (IOException e) {
            LOGGER.error("Failed to unwrap roles", e);
        }
        return authoritiesWrapper;
    }
}
