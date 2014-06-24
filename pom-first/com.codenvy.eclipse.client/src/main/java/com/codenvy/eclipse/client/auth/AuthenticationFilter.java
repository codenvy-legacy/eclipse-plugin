/*
 * CODENVY CONFIDENTIAL
 * ________________
 * 
 * [2012] - [2014] Codenvy, S.A.
 * All Rights Reserved.
 * NOTICE: All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any. The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.eclipse.client.auth;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.Provider;

/**
 * Filter used to inject {@link Token} in client request.
 * 
 * @author Kevin Pollet
 */
@Provider
public class AuthenticationFilter implements ClientRequestFilter {
    private final String                username;
    private final Credentials           credentials;
    private final AuthenticationManager authenticationManager;

    /**
     * Constructs an instance of {@link AuthenticationFilter}.
     * 
     * @param username the user name concerned by the authentication.
     * @param credentials the {@link Credentials} used for authentication.
     * @param authenticationManager the {@link AuthenticationManager}.
     * @throws NullPointerException if authenticationManager or username parameter is {@code null}.
     */
    public AuthenticationFilter(String username, Credentials credentials, AuthenticationManager authenticationManager) {
        checkNotNull(username);
        checkNotNull(authenticationManager);

        this.username = username;
        this.credentials = credentials;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        Token token = authenticationManager.getToken(username);

        if (token == null) {
            if (credentials == null) {
                throw new AuthenticationException("No credentials provided for authentication");
            }

            token = authenticationManager.authorize(credentials);
            if (token == null) {
                throw new AuthenticationException("Unable to negociate a token for authentication");
            }
        }

        requestContext.setUri(UriBuilder.fromUri(requestContext.getUri())
                                        .queryParam("token", token.value)
                                        .build());
    }
}
