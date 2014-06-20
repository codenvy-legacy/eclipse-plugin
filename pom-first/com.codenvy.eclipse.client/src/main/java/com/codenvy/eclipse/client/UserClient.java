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

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.client.Invocation;

import com.codenvy.eclipse.client.exceptions.APIException;
import com.codenvy.eclipse.client.model.Credentials;
import com.codenvy.eclipse.client.model.User;

/**
 * The Codenvy user API client.
 * 
 * @author Kevin Pollet
 * @author Stéphane Daviet
 */
public class UserClient extends AbstractClient {
    /**
     * Constructs an instance of {@link UserClient}.
     * 
     * @param url the Codenvy platform URL.
     * @param apiName the API name.
     * @param username the username.
     * @param credentials the provided user {@link Credentials} might be {@code null}.
     * @param credentialsProvider the {@link CredentialsProvider}.
     * @throws NullPointerException if url, username or credentialsProvider parameter is {@code null}.
     */
    UserClient(String url,
               String username,
               Credentials credentials,
               CredentialsProvider credentialsProvider) {

        super(url, "user", username, credentials, credentialsProvider);
    }

    /**
     * Returns the current user.
     * 
     * @return the current user.
     * @throws APIException if something goes wrong with the API call.
     */
    public APIRequest<User> current() throws APIException {
        final Invocation request = getWebTarget().request()
                                                 .accept(APPLICATION_JSON)
                                                 .buildGet();

        return new SimpleAPIRequest<User>(request, User.class, getCredentialsProvider(), getUsername());
    }
}
