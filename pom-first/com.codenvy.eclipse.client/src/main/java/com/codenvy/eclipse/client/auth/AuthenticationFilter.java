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
import java.net.URI;

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
    private final AuthenticationManager authenticationManager;

    /**
     * Constructs an instance of {@link AuthenticationFilter}.
     * 
     * @param authenticationManager the {@link AuthenticationManager}.
     * @throws NullPointerException if authenticationManager parameter is {@code null}.
     */
    public AuthenticationFilter(AuthenticationManager authenticationManager) {
        checkNotNull(authenticationManager);

        this.authenticationManager = authenticationManager;
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException, AuthenticationException {
        Token token = authenticationManager.getToken();
        if (token == null) {
            token = authenticationManager.authorize();
        }

        final URI uriWithToken = UriBuilder.fromUri(requestContext.getUri())
                                           .queryParam("token", token.value)
                                           .build();

        requestContext.setUri(uriWithToken);
    }
}
