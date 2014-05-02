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
package com.codenvy.eclipse.core.impl;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.net.URI;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;

import com.codenvy.eclipse.core.AbstractRestServiceWithAuth;
import com.codenvy.eclipse.core.UserService;
import com.codenvy.eclipse.core.model.CodenvyToken;
import com.codenvy.eclipse.core.model.User;

/**
 * The Codenvy user client service.
 * 
 * @author Kevin Pollet
 */
public class DefaultUserService extends AbstractRestServiceWithAuth implements UserService {
    private final WebTarget userWebTarget;

    /**
     * Constructs an instance of {@linkplain DefaultUserService}.
     * 
     * @param url the Codenvy platform url.
     * @param codenvyToken the Codenvy authentication token.
     * @throws NullPointerException if url or codenvyToken is {@code null}.
     * @throws IllegalArgumentException if url parameter is an empty {@linkplain String}.
     */
    public DefaultUserService(String url, CodenvyToken codenvyToken) {
        super(url, codenvyToken);

        final URI uri = UriBuilder.fromUri(url)
                                  .path("api/user")
                                  .build();

        this.userWebTarget = ClientBuilder.newClient()
                                          .target(uri);
    }

    @Override
    public User getCurrentUser() {
        return userWebTarget.queryParam("token", getCodenvyToken().value)
                            .request()
                            .accept(APPLICATION_JSON)
                            .get(User.class);
    }
}
