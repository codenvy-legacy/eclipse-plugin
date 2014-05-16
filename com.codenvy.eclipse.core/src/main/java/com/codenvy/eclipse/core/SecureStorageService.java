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
package com.codenvy.eclipse.core;

import java.util.List;

import com.codenvy.eclipse.core.model.CodenvyCredentials;
import com.codenvy.eclipse.core.model.CodenvyToken;

/**
 * Convenient OSGI service that provides all the operations related to secure storage for Codenvy credentials.
 * 
 * @author Stéphane Daviet
 * @author Kevin Pollet
 */
public interface SecureStorageService {
    /**
     * Stores codenvy url, {@link CodenvyCredentials} and {@link CodenvyToken} in Eclipse secure storage.
     * 
     * @param url the codenvy instance url.
     * @param credentials the {@link CodenvyCredentials}
     * @param token the {@link CodenvyToken}.
     * @throws NullPointerException if url, credentials or token parameter is {@code null}.
     * @throws IllegalArgumentException if url parameter is an empty {@code String}.
     */
    void storeCredentials(String url, CodenvyCredentials credentials, CodenvyToken token);

    /**
     * Gets the codenvy password corresponding to the given url and username.
     * 
     * @param url the codenvy url.
     * @param username the codenvy username.
     * @return the codenvy password or {@code null} if not found.
     * @throws NullPointerException if url or username parameter is {@code null}.
     * @throws IllegalArgumentException if url or username is an empty {@code String}.
     */
    String getPassword(String url, String username);

    /**
     * Gets the {@link CodenvyToken} corresponding to the given codenvy url and username.
     * 
     * @param url the codenvy url.
     * @param username the codenvy username.
     * @return the {@link CodenvyToken} or {@code null} if not found.
     * @throws NullPointerException if url or username parameter is {@code null}.
     * @throws IllegalArgumentException if url or username is an empty {@code String}.
     */
    CodenvyToken getToken(String url, String username);

    /**
     * Deletes the codenvy credentials corresponding to the given codenvy url and username.
     * 
     * @param url the codenvy url.
     * @param username the codenvy username.
     * @throws NullPointerException if url or username parameter is {@code null}.
     * @throws IllegalArgumentException if url or username is an empty {@code String}.
     */
    void deleteCredentials(String url, String username);

    /**
     * Returns all url stored in the Eclipse secure storage.
     * 
     * @return the urls never {@code null}.
     */
    List<String> getURLs();

    /**
     * Gets all codenvy usernames associated with the given codenvy url.
     * 
     * @param url the codenvy url.
     * @return the usernames never {@code null}.
     * @throws NullPointerException if url parameter is {@code null}.
     * @throws IllegalArgumentException if url is an empty {@code String}.
     */
    List<String> getUsernamesForURL(String url);
}
