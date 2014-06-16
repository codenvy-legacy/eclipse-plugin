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
package com.codenvy.eclipse.core.client;

import static com.google.common.base.Preconditions.checkNotNull;
import static javax.ws.rs.client.Entity.json;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import com.codenvy.eclipse.core.client.model.Credentials;
import com.codenvy.eclipse.core.client.model.Token;
import com.codenvy.eclipse.core.client.store.StoredCredentials;

/**
 * This class provides the user credentials to the API.
 * 
 * @author Kevin Pollet
 */
public class CredentialsProvider {
    private final Context   context;
    private final WebTarget webTarget;

    /**
     * Constructs an instance of {@link CredentialsProvider}.
     * 
     * @param context the API call {@link Context}.
     * @throws NullPointerException if context parameter is {@code null}.
     */
    public CredentialsProvider(Context context) {
        checkNotNull(context);

        this.context = context;

        final UriBuilder uriBuilder = UriBuilder.fromUri(context.getUrl())
                                                .path("api")
                                                .path("auth")
                                                .path("login");

        this.webTarget = ClientBuilder.newClient()
                                      .target(uriBuilder);
    }

    /**
     * Authorises the user with the following {@link Credentials} on Codenvy.
     * 
     * @param credentials the user {@link Credentials}.
     * @return the authentication {@link Token}.
     * @throws NullPointerException if credentials parameter is {@code null}.
     */
    public Token authorize(Credentials credentials) {
        final Response response = webTarget.request()
                                           .accept(MediaType.APPLICATION_JSON_TYPE)
                                           .post(json(credentials));

        return response.getStatus() == Status.OK.getStatusCode() ? response.readEntity(Token.class) : null;
    }

    /**
     * Retrieves the Codenvy API {@link Token} for the given user.
     * 
     * @param username the user name.
     * @return the {@link Token}.
     */
    public Token getToken(String username) {
        final StoredCredentials credentials = context.loadStoredCredentials(username);
        return credentials == null ? null : credentials.token;
    }

    /**
     * Refresh the the Codenvy API {@link Token} for the given user.
     * 
     * @param username the user name.
     * @return the {@link Token}.
     */
    public Token refreshToken(String username) {
        // TODO
        return null;
    }
}
