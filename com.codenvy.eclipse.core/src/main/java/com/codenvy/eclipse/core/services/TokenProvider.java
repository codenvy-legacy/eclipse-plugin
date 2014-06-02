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

import com.codenvy.eclipse.core.model.Token;

/**
 * Service to operate on the Codenvy token. This service is able to get the token (presumably from the {@link SecureStorageService}) and to
 * renew it (thanks to {@link AuthenticationService}).
 * 
 * @author Stéphane Daviet
 * @see SecureStorageService
 * @see AuthenticationService
 */
public interface TokenProvider {
    /**
     * Get a {@link Token} presumably from {@link SecureStorageService} for the given URL and username.
     * 
     * @param url the Codenvy platform URL.
     * @param username the username.
     * @return the {@link Token} associated to these URL and username or {@code null} if none can be found.
     * @throws NullPointerException if URL, rootPath or codenvyToken parameter is {@code null}.
     * @throws IllegalArgumentException if URL parameter is an empty {@linkplain String}.
     * @see SecureStorageService#getToken(String, String)
     */
    Token getToken(String url, String username);

    /**
     * Renew the {@link Token} through a new authentication towards the given Codenvy platform URL and username. This will be
     * realized with a call to {@link AuthenticationService}. The password used for authenticate is retrieved with
     * {@link SecureStorageService#getPassword(String, String)}.
     * 
     * @param url the Codenvy platform URL.
     * @param username the username.
     * @return the {@link Token} associated to these URL and username or {@code null} if no password can be found for this
     *         URL/username couple or if renewal (authentication) fails.
     * @throws NullPointerException if URL, rootPath or codenvyToken parameter is {@code null}.
     * @throws IllegalArgumentException if URL parameter is an empty {@linkplain String}.
     * @see SecureStorageService#getPassword(String, String)
     * @see AuthenticationService#login(com.codenvy.eclipse.core.model.CodenvyCredentials)
     */
    Token renewToken(String url, String username);
}
