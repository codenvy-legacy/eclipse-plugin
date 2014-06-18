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
package com.codenvy.eclipse.client;

import static com.google.common.base.Preconditions.checkNotNull;
import static javax.ws.rs.Priorities.AUTHENTICATION;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.Provider;

import com.codenvy.eclipse.client.exceptions.AuthenticationException;
import com.codenvy.eclipse.client.model.Credentials;
import com.codenvy.eclipse.client.model.Token;

/**
 * Abstract client class.
 * 
 * @author Kevin Pollet
 */
public abstract class AbstractClient {
    private final WebTarget           webTarget;
    private final String              username;
    private final Credentials         credentials;
    private final CredentialsProvider credentialsProvider;

    /**
     * Constructs an instance of {@link AbstractClient}.
     * 
     * @param url the Codenvy platform URL.
     * @param apiName the API name.
     * @param username the username.
     * @param credentials the provided user {@link Credentials} might be {@code null}.
     * @param credentialsProvider the {@link CredentialsProvider}.
     * @param credentialsStoreFactory the {@linkplain com.codenvy.eclipse.client.store.DataStoreFactory DataStoreFactory}.
     * @throws NullPointerException if url, apiName, username or credentialsProvider parameter is {@code null}.
     */
    AbstractClient(String url,
                   String apiName,
                   String username,
                   Credentials credentials,
                   CredentialsProvider credentialsProvider) {

        checkNotNull(url);
        checkNotNull(apiName);
        checkNotNull(username);
        checkNotNull(credentialsProvider);

        this.username = username;
        this.credentials = credentials;
        this.credentialsProvider = credentialsProvider;

        final UriBuilder uriBuilder = UriBuilder.fromUri(url)
                                                .path("api")
                                                .path(apiName);

        this.webTarget = ClientBuilder.newClient()
                                      .target(uriBuilder)
                                      .register(new AuthenticationFilter());
    }

    /**
     * Returns the client {@link WebTarget} endpoint.
     * 
     * @return the client {@link WebTarget} endpoint.
     */
    public WebTarget getWebTarget() {
        return webTarget;
    }

    /**
     * Returns the {@link CredentialsProvider} used to authenticate.
     * 
     * @return the {@link CredentialsProvider} used for authentication.
     */
    public CredentialsProvider getCredentialsProvider() {
        return credentialsProvider;
    }

    /**
     * Returns the username.
     * 
     * @return the username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Filter used to inject {@link Token} in client request.
     * 
     * @author Kevin Pollet
     */
    @Provider
    @Priority(AUTHENTICATION)
    private class AuthenticationFilter implements ClientRequestFilter {
        @Override
        public void filter(ClientRequestContext requestContext) throws IOException {
            Token authToken = credentialsProvider.getToken(username);

            if (authToken == null) {
                if (credentials == null) {
                    throw new AuthenticationException("No credentials provided for authentication");
                }

                authToken = credentialsProvider.authorize(credentials);
                if (authToken == null) {
                    throw new AuthenticationException("Unable to negociate a token for authentication");
                }
            }

            requestContext.setUri(UriBuilder.fromUri(requestContext.getUri())
                                            .queryParam("token", authToken.value)
                                            .build());
        }
    }
}
