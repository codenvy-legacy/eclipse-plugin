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

import static javax.ws.rs.client.Entity.json;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import com.codenvy.eclipse.client.store.DataStore;

/**
 * Authentication manager used to authenticate an user with the Codenvy platform.
 * 
 * @author Kevin Pollet
 */
public class AuthenticationManager {
    private final WebTarget                      webTarget;
    private final DataStore<String, Credentials> dataStore;

    /**
     * Constructs an instance of {@link AuthenticationManager}.
     * 
     * @param url the Codenvy platform url.
     * @param dataStore the {@link DataStore} used to store the user credentials, might be {@code null}.
     * @throws NullPointerException if url parameter is {@code null}.
     * @throws IllegalArgumentException if url parameter is an empty {@link String}.
     */
    public AuthenticationManager(String url, DataStore<String, Credentials> dataStore) {
        this.dataStore = dataStore;

        final UriBuilder uriBuilder = UriBuilder.fromUri(url)
                                                .path("api")
                                                .path("auth")
                                                .path("login");

        this.webTarget = ClientBuilder.newClient()
                                      .target(uriBuilder);
    }

    /**
     * Authorises the user with the following {@link Credentials} on Codenvy platform.
     * 
     * @param credentials the user {@link Credentials}.
     * @return the authentication {@link Token}.
     * @throws NullPointerException if credentials parameter is {@code null}.
     */
    public Token authorize(Credentials credentials) {
        Token token = null;

        if (credentials != null) {
            final Response response = webTarget.request()
                                               .accept(APPLICATION_JSON)
                                               .post(json(credentials));

            if (response.getStatus() == Status.OK.getStatusCode()) {
                token = response.readEntity(Token.class);

                if (dataStore != null) {
                    dataStore.put(credentials.username, new Credentials(credentials.password, token));
                }
            }
        }
        return token;
    }

    /**
     * Retrieves the stored Codenvy API {@link Token} for the given user.
     * 
     * @param username the user name.
     * @return the {@link Token} or {@code null} if none.
     */
    public Token getToken(String username) {
        if (dataStore == null) {
            return null;
        }

        final Credentials credentials = dataStore.get(username);
        return credentials == null ? null : credentials.token;
    }

    /**
     * Refresh the the Codenvy API {@link Token} for the given user.
     * 
     * @param username the user name.
     * @return the {@link Token}.
     */
    public Token refreshToken(String username) {
        if (dataStore == null) {
            return null;
        }

        final Credentials storedCredentials = dataStore.get(username);
        return storedCredentials != null ? authorize(new Credentials(username, storedCredentials.password)) : null;
    }
}
