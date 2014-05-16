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
package com.codenvy.eclipse.core.services;

import com.codenvy.eclipse.core.model.CodenvyToken;

/**
 * Codenvy authentication service contract.
 * 
 * @author Kevin Pollet
 */
public interface AuthenticationService extends RestService {
    /**
     * Authenticates the user on the Codenvy platform.
     * 
     * @param username the user username.
     * @param password the user password.
     * @return the authentication token.
     * @throws NullPointerException if username or password parameter is {@code null}.
     * @throws IllegalArgumentException if username or password parameter is an empty {@link String}.
     */
    CodenvyToken login(String username, String password);
}
