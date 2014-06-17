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
package com.codenvy.eclipse.core.store;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.StorageException;

import com.codenvy.eclipse.client.model.Token;
import com.codenvy.eclipse.client.store.DataStore;
import com.codenvy.eclipse.client.store.StoredCredentials;

/**
 * Stores user credentials in Eclipse secure storage.
 * 
 * @author Kevin Pollet
 */
public class SecureStorageDataStore implements DataStore<String, StoredCredentials> {
    public static final String       CODENVY_PASSWORD_KEY_NAME = "password";
    public static final String       CODENVY_TOKEN_KEY_NAME    = "token";

    private final ISecurePreferences urlNode;

    /**
     * Constructs an instance of {@link SecureStorageDataStore}.
     * 
     * @param urlNode the root secure storage node.
     * @throws NullPointerException if urlNode is {@code null}.
     */
    public SecureStorageDataStore(ISecurePreferences urlNode) {
        checkNotNull(urlNode);

        this.urlNode = urlNode;
    }

    @Override
    public StoredCredentials get(String username) {
        checkNotNull(username);

        try {

            if (!urlNode.nodeExists(username)) {
                return null;
            }


            final ISecurePreferences node = urlNode.node(username);
            final String password = node.get(CODENVY_PASSWORD_KEY_NAME, null);
            final String token = node.get(CODENVY_TOKEN_KEY_NAME, null);

            return new StoredCredentials(password, new Token(token));

        } catch (StorageException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public StoredCredentials put(String username, StoredCredentials credentials) {
        checkNotNull(username);
        checkNotNull(credentials);

        try {

            final StoredCredentials previousCredentials = get(username);
            final ISecurePreferences node = urlNode.node(username);

            node.put(CODENVY_PASSWORD_KEY_NAME, credentials.password, true);
            node.put(CODENVY_TOKEN_KEY_NAME, credentials.token.value, true);

            return previousCredentials;

        } catch (StorageException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public StoredCredentials delete(String username) {
        checkNotNull(username);

        final StoredCredentials previousCredentials = get(username);
        final ISecurePreferences node = urlNode.node(username);
        if (node != null) {
            node.removeNode();
        }

        return previousCredentials;
    }
}
