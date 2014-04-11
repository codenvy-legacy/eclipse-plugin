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
package com.codenvy.eclipse.core.service.api;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static javax.ws.rs.client.Entity.json;
import static javax.ws.rs.core.Response.Status.OK;

import java.net.URI;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import com.codenvy.eclipse.core.service.api.exception.AuthenticationException;
import com.codenvy.eclipse.core.service.api.model.CodenvyToken;
import com.codenvy.eclipse.core.service.api.model.Credentials;

/**
 * The Codenvy authentication client service.
 * 
 * @author Kevin Pollet
 */
public class AuthenticationService implements RestService {
    private final WebTarget authWebTarget;

    /**
     * Constructs an instance of {@linkplain AuthenticationService}.
     * 
     * @param url the Codenvy platform url.
     * @throws NullPointerException if url parameter is {@code null}.
     * @throws IllegalArgumentException if url parameter is an empty {@linkplain String}.
     */
    public AuthenticationService(String url) {
        checkNotNull(url);
        checkArgument(!url.trim().isEmpty());

        final URI uri = UriBuilder.fromUri(url)
                                  .path("api/auth")
                                  .build();

        this.authWebTarget = ClientBuilder.newClient()
                                          .target(uri);
    }

    /**
     * Authenticates the user on the Codenvy platform.
     * 
     * @param username the user username.
     * @param password the user password.
     * @return the authentication token.
     * @throws NullPointerException if username or password parameter is {@code null}.
     */
    public CodenvyToken login(String username, String password) {
        checkNotNull(username);
        checkNotNull(password);

        final Response response = authWebTarget.path("login")
                                               .request(MediaType.APPLICATION_JSON)
                                               .post(json(new Credentials(username, password)));

        if (OK.getStatusCode() != response.getStatus()) {
            throw new AuthenticationException("Authentication failed : Wrong username or password");
        }

        return response.readEntity(CodenvyToken.class);
    }
}
