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

import java.util.List;

import com.codenvy.eclipse.core.model.Credentials;
import com.codenvy.eclipse.core.model.Token;

/**
 * Convenient OSGI service that provides all the operations related to secure storage for Codenvy credentials.
 * 
 * @author Stéphane Daviet
 * @author Kevin Pollet
 */
public interface SecureStorageService {
    /**
     * Stores Codenvy URL, {@link Credentials} and {@link Token} in Eclipse secure storage.
     * 
     * @param url the Codenvy instance URL.
     * @param credentials the {@link Credentials}
     * @param token the {@link Token}.
     * @throws NullPointerException if url, credentials or token parameter is {@code null}.
     * @throws IllegalArgumentException if url parameter is an empty {@code String}.
     */
    void storeCredentials(String url, Credentials credentials, Token token);

    /**
     * Gets the Codenvy password corresponding to the given URL and username.
     * 
     * @param url the Codenvy URL.
     * @param username the Codenvy username.
     * @return the Codenvy password or {@code null} if not found.
     * @throws NullPointerException if url or username parameter is {@code null}.
     * @throws IllegalArgumentException if url or username is an empty {@code String}.
     */
    String getPassword(String url, String username);

    /**
     * Gets the {@link Token} corresponding to the given Codenvy URL and username.
     * 
     * @param url the Codenvy URL.
     * @param username the Codenvy username.
     * @return the {@link Token} or {@code null} if not found.
     * @throws NullPointerException if url or username parameter is {@code null}.
     * @throws IllegalArgumentException if url or username is an empty {@code String}.
     */
    Token getToken(String url, String username);

    /**
     * Deletes the codenvy credentials corresponding to the given codenvy url and username.
     * 
     * @param url the Codenvy URL.
     * @param username the Codenvy username.
     * @throws NullPointerException if url or username parameter is {@code null}.
     * @throws IllegalArgumentException if url or username is an empty {@code String}.
     */
    void deleteCredentials(String url, String username);

    /**
     * Deletes the password corresponding to the given codenvy url and username.
     * 
     * @param url the Codenvy URL.
     * @param username the Codenvy username.
     * @throws NullPointerException if url or username parameter is {@code null}.
     * @throws IllegalArgumentException if url or username is an empty {@code String}.
     */
    void deletePassword(String url, String username);

    /**
     * Deletes the token corresponding to the given codenvy url and username.
     * 
     * @param url the Codenvy URL.
     * @param username the Codenvy username.
     * @throws NullPointerException if url or username parameter is {@code null}.
     * @throws IllegalArgumentException if url or username is an empty {@code String}.
     */
    void deleteToken(String url, String username);

    /**
     * Returns all URL stored in the Eclipse secure storage.
     * 
     * @return the URLs never {@code null}.
     */
    List<String> getURLs();

    /**
     * Gets all Codenvy usernames associated with the given Codenvy URL.
     * 
     * @param url the Codenvy URL.
     * @return the usernames never {@code null}.
     * @throws NullPointerException if url parameter is {@code null}.
     * @throws IllegalArgumentException if url is an empty {@code String}.
     */
    List<String> getUsernamesForURL(String url);

    /**
     * Get a plain {@link Credentials} object.
     * 
     * @param url the Codenvy URL.
     * @param username the Codenvy username.
     * @return the {@link Credentials} or {@code null} if not found.
     * @throws NullPointerException if url or username parameter is {@code null}.
     * @throws IllegalArgumentException if url or username is an empty {@code String}.
     */
    Credentials getCredentials(String url, String username);
}
