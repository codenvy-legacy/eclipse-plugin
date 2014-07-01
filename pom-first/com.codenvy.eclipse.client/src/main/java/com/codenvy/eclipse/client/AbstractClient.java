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

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;

import com.codenvy.eclipse.client.auth.AuthenticationManager;
import com.codenvy.eclipse.client.auth.TokenInjectorFilter;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

/**
 * Abstract client class.
 * 
 * @author Kevin Pollet
 */
public abstract class AbstractClient {
    private final WebTarget             webTarget;
    private final AuthenticationManager authenticationManager;

    /**
     * Constructs an instance of {@link AbstractClient}.
     * 
     * @param url the Codenvy platform URL.
     * @param apiName the API name.
     * @param authenticationManager the {@link AuthenticationManager}.
     * @param credentialsStoreFactory the {@linkplain com.codenvy.eclipse.client.store.DataStoreFactory DataStoreFactory}.
     * @throws NullPointerException if url, apiName or authenticationManager parameter is {@code null}.
     */
    AbstractClient(String url,
                   String apiName,
                   AuthenticationManager authenticationManager) {

        checkNotNull(url);
        checkNotNull(apiName);
        checkNotNull(authenticationManager);

        this.authenticationManager = authenticationManager;

        final UriBuilder uriBuilder = UriBuilder.fromUri(url)
                                                .path("api")
                                                .path(apiName);

        this.webTarget = ClientBuilder.newClient()
                                      .target(uriBuilder)
                                      .register(JacksonJsonProvider.class)
                                      .register(TokenInjectorFilter.class);
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
     * Returns the {@link AuthenticationManager} used to authenticate.
     * 
     * @return the {@link AuthenticationManager} used for authentication.
     */
    public AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }
}
