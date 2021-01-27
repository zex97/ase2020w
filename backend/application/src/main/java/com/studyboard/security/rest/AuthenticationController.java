package com.studyboard.security.rest;

import com.studyboard.exception.UserDoesNotExist;
import com.studyboard.security.dto.AuthenticationRequest;
import com.studyboard.security.dto.AuthenticationToken;
import com.studyboard.security.dto.AuthenticationTokenInfo;
import com.studyboard.security.service.HeaderTokenAuthenticationService;
import com.studyboard.security.service.SimpleHeaderTokenAuthenticationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping(value = "/authentication")
@Api(value = "authentication")
public class AuthenticationController {

    private final HeaderTokenAuthenticationService authenticationService;
    private static final String TOKEN_PREFIX = "Bearer ";

    public AuthenticationController(SimpleHeaderTokenAuthenticationService simpleHeaderTokenAuthenticationService) {
        authenticationService = simpleHeaderTokenAuthenticationService;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = "Get an authentication token with your username and password")
    public ResponseEntity<?> authenticate(@RequestBody final AuthenticationRequest authenticationRequest) {
        AuthenticationToken authenticationToken;
        authenticationService.incrementLoginAttempts(authenticationRequest.getUsername());
        try {
            authenticationToken = authenticationService.authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        } catch (Exception e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
        authenticationService.resetLoginAttempts(authenticationRequest.getUsername());
        return ResponseEntity.ok(authenticationToken);
    }

    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(value = "Get some valid authentication tokens", authorizations = {@Authorization(value = "apiKey")})
    public AuthenticationToken authenticate(@ApiIgnore @RequestHeader(value = HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return authenticationService.renewAuthentication(authorizationHeader.substring(TOKEN_PREFIX.length()).trim());
    }

    @RequestMapping(value = "/info/{token}", method = RequestMethod.GET)
    @ApiOperation(value = "Get information about a specific authentication token", authorizations = {@Authorization(value = "apiKey")})
    public AuthenticationTokenInfo tokenInfoAny(@PathVariable String token) {
        return authenticationService.authenticationTokenInfo(token);
    }

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ApiOperation(value = "Get information about the current users authentication token", authorizations = {@Authorization(value = "apiKey")})
    public AuthenticationTokenInfo tokenInfoCurrent(@ApiIgnore @RequestHeader(value = HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return authenticationService.authenticationTokenInfo(authorizationHeader.substring(TOKEN_PREFIX.length()).trim());
    }
}
