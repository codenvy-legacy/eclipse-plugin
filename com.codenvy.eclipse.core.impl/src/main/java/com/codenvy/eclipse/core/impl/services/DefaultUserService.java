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
package com.codenvy.eclipse.core.impl.services;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import com.codenvy.eclipse.core.model.CodenvyUser;
import com.codenvy.eclipse.core.services.AbstractRestServiceWithAuth;
import com.codenvy.eclipse.core.services.UserService;

/**
 * The Codenvy user client service.
 * 
 * @author Kevin Pollet
 * @author Stéphane Daviet
 */
public class DefaultUserService extends AbstractRestServiceWithAuth implements UserService {
    /**
     * Constructs an instance of {@linkplain DefaultUserService}.
     * 
     * @param url the Codenvy platform url.
     * @param username the username.
     * @throws NullPointerException if url or username is {@code null}.
     * @throws IllegalArgumentException if url parameter is an empty {@linkplain String}.
     */
    public DefaultUserService(String url, String username) {
        super(url, username, "api/user");
    }

    @Override
    public CodenvyUser getCurrentUser() {
        return getWebTarget().request()
                             .accept(APPLICATION_JSON)
                             .get(CodenvyUser.class);
    }
}
