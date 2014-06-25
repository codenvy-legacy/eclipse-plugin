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
import static javax.ws.rs.client.Entity.json;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.net.URI;

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
    private final String                         username;
    private final Credentials                    credentials;
    private final WebTarget                      webTarget;
    private final DataStore<String, Credentials> dataStore;
    private final CredentialsProvider            credentialsProvider;

    /**
     * Constructs an instance of {@link AuthenticationManager}.
     * 
     * @param url the Codenvy platform URL.
     * @param username the user name.
     * @param credentials the provided {@link Credentials}.
     * @param credentialsProvider provider used to provide credentials if they are not stored or provided.
     * @param dataStore the {@link DataStore} used to store the user {@link Credentials}.
     * @throws NullPointerException if url or username parameter is {@code null}.
     */
    public AuthenticationManager(String url,
                                 String username,
                                 Credentials credentials,
                                 CredentialsProvider credentialsProvider,
                                 DataStore<String, Credentials> dataStore) {

        checkNotNull(url);
        checkNotNull(username);

        this.dataStore = dataStore;
        this.username = username;
        this.credentials = credentials;
        this.credentialsProvider = credentialsProvider;

        final URI loginURI = UriBuilder.fromUri(url)
                                       .path("api")
                                       .path("auth")
                                       .path("login")
                                       .build();

        this.webTarget = ClientBuilder.newClient().target(loginURI);
    }

    /**
     * Authorises the contextual user with the Codenvy platform.
     * 
     * @return the authentication {@link Token}.
     */
    public Token authorize() {
        return authorize(credentials);
    }

    /**
     * Authorises the user with the following {@link Credentials} on Codenvy platform.
     * 
     * @param credentials the user {@link Credentials}.
     * @return the authentication {@link Token}.
     * @throws AuthenticationException if credentials parameter is {@code null} or if there is a problem during the token negociation.
     */
    private Token authorize(Credentials credentials) throws AuthenticationException {
        if (credentials == null || credentials.password == null) {
            if (credentialsProvider != null) {
                credentials = credentialsProvider.load(username);
            }
            if (credentials == null || credentials.password == null) {
                throw new AuthenticationException("No credentials provided for authentication");
            }
        }

        final Response response = webTarget.request()
                                           .accept(APPLICATION_JSON)
                                           .post(json(credentials));

        Token token = null;

        if (Status.fromStatusCode(response.getStatus()) == Status.OK) {
            token = response.readEntity(Token.class);

            if (dataStore != null) {
                dataStore.put(credentials.username, new Credentials(credentials.password, token));
            }
        }

        if (token == null) {
            throw new AuthenticationException("Unable to negociate a token for authentication");
        }

        return token;
    }

    /**
     * Retrieves the stored Codenvy API {@link Token} for the given user.
     * 
     * @param username the user name.
     * @return the {@link Token} or {@code null} if none.
     */
    public Token getToken() {
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
    public Token refreshToken() {
        return authorize(dataStore == null ? null : dataStore.get(username));
    }
}
