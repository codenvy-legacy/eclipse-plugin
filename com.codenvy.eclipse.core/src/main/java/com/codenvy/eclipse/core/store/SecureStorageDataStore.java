/*******************************************************************************
 * Copyright (c) 2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.eclipse.core.store;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.StorageException;

import com.codenvy.client.CodenvyAPI;
import com.codenvy.client.auth.Credentials;
import com.codenvy.client.store.DataStore;

/**
 * Implementation of {@link SecureStorageDataStore} backed by Eclipse secure storage.
 * 
 * @author Kevin Pollet
 */
public final class SecureStorageDataStore implements DataStore<String, Credentials> {
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
        this.urlNode = checkNotNull(urlNode);
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

            return CodenvyAPI.getClient().newCredentialsBuilder().withUsername(username)
                             .withPassword(password)
                             .withToken(CodenvyAPI.getClient().newTokenBuilder(token).build())
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

            if (!credentials.isStoreOnlyToken()) {
                node.put(CODENVY_PASSWORD_KEY_NAME, credentials.password(), true);
            }
            node.put(CODENVY_TOKEN_KEY_NAME, credentials.token().value(), true);

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
