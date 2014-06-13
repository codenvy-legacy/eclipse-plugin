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
package com.codenvy.eclipse.core.client.security;

import static javax.ws.rs.client.Entity.json;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import com.codenvy.eclipse.core.client.Context;
import com.codenvy.eclipse.core.client.model.Credentials;
import com.codenvy.eclipse.core.client.model.Token;
import com.codenvy.eclipse.core.client.store.StoredCredentials;

public class RestCredentialsProvider implements CredentialsProvider {
    private Context   context;
    private WebTarget webTarget;

    @Override
    public void initialize(Context context) {
        this.context = context;

        final UriBuilder uriBuilder = UriBuilder.fromUri(context.getUrl())
                                                .path("api")
                                                .path("auth")
                                                .path("login");

        this.webTarget = ClientBuilder.newClient().target(uriBuilder);

    }

    @Override
    public Token authorize(Credentials credentials) {
        final Response response = webTarget.request()
                                           .accept(MediaType.APPLICATION_JSON_TYPE)
                                           .post(json(credentials));

        return response.getStatus() == Status.OK.getStatusCode() ? response.readEntity(Token.class) : null;
    }

    @Override
    public Token getToken(String username) {
        final StoredCredentials credentials = context.loadStoredCredentials(username);
        return credentials == null ? null : credentials.token;
    }

    @Override
    public Token refreshToken(String username) {
        // TODO
        return null;
    }
}
