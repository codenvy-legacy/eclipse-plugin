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

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.StorageException;

import com.codenvy.eclipse.client.auth.Credentials;
import com.codenvy.eclipse.client.auth.Token;
import com.codenvy.eclipse.client.store.DataStore;

/**
 * Stores user credentials in Eclipse secure storage.
 * 
 * @author Kevin Pollet
 */
public class SecureStorageDataStore implements DataStore<String, Credentials> {
    public static final String       CODENVY_PASSWORD_KEY_NAME = "password";
    public static final String       CODENVY_TOKEN_KEY_NAME    = "token";

    private final ISecurePreferences urlNode;

    /**
     * Constructs an instance of {@link SecureStorageDataStore}.
     * 
     * @param urlNode the root secure storage node for one Codenvy URL.
     * @throws NullPointerException if urlNode is {@code null}.
     */
    SecureStorageDataStore(ISecurePreferences urlNode) {
        checkNotNull(urlNode);

        this.urlNode = urlNode;
    }

    @Override
    public Credentials get(String username) {
        checkNotNull(username);

        try {

            if (!urlNode.nodeExists(username)) {
                return null;
            }


            final ISecurePreferences node = urlNode.node(username);
            final String password = node.get(CODENVY_PASSWORD_KEY_NAME, null);
            final String token = node.get(CODENVY_TOKEN_KEY_NAME, null);

            return new Credentials.Builder().withUsername(username)
                                            .withPassword(password)
                                            .withToken(new Token(token))
                                            .build();

        } catch (StorageException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Credentials put(String username, Credentials credentials) {
        checkNotNull(username);
        checkNotNull(credentials);

        try {

            final Credentials previousCredentials = get(username);
            final ISecurePreferences node = urlNode.node(username);

            // a put replace all values
            node.remove(CODENVY_PASSWORD_KEY_NAME);
            node.remove(CODENVY_TOKEN_KEY_NAME);

            if (!credentials.storeOnlyToken) {
                node.put(CODENVY_PASSWORD_KEY_NAME, credentials.password, true);
            }
            node.put(CODENVY_TOKEN_KEY_NAME, credentials.token.value, true);

            return previousCredentials;

        } catch (StorageException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Credentials delete(String username) {
        checkNotNull(username);

        final Credentials previousCredentials = get(username);
        final ISecurePreferences node = urlNode.node(username);
        if (node != null) {
            node.removeNode();
        }

        return previousCredentials;
    }
}
