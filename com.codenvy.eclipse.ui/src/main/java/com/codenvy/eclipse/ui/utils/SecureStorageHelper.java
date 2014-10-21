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
package com.codenvy.eclipse.ui.utils;

import static com.codenvy.eclipse.core.store.SecureStorageDataStore.CODENVY_PASSWORD_KEY_NAME;
import static com.codenvy.eclipse.core.store.SecureStorageDataStoreFactory.CODENVY_NODE_NAME;
import static com.codenvy.eclipse.core.utils.StringHelper.isEmpty;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Arrays.asList;
import static org.eclipse.equinox.security.storage.EncodingUtils.encodeSlashes;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;

/**
 * Helper providing methods to work with Eclipse secure storage.
 * 
 * @author Stéphane Daviet
 * @author Kevin Pollet
 */
public final class SecureStorageHelper {
    /**
     * Gets all Codenvy usernames associated with the given Codenvy URL.
     * 
     * @param url the Codenvy URL.
     * @return the usernames never {@code null}.
     * @throws NullPointerException if url parameter is {@code null}.
     * @throws IllegalArgumentException if url is an empty {@code String}.
     */
    public static List<String> getUsernamesForURL(String url) {
        checkNotNull(url);
        checkArgument(!isEmpty(url));

        final ISecurePreferences root = SecurePreferencesFactory.getDefault();
        checkNotNull(root);

        final List<String> usernames = new ArrayList<>();
        if (root.nodeExists(CODENVY_NODE_NAME)
            && root.node(CODENVY_NODE_NAME).nodeExists(encodeSlashes(url))) {

            final String[] usernamesArray = root.node(CODENVY_NODE_NAME).node(encodeSlashes(url)).childrenNames();
            usernames.addAll(asList(usernamesArray));
        }

        return usernames;
    }

    /**
     * Gets the Codenvy password corresponding to the given URL and username.
     * 
     * @param url the Codenvy URL.
     * @param username the Codenvy username.
     * @return the Codenvy password or {@code null} if not found.
     * @throws NullPointerException if url or username parameter is {@code null}.
     * @throws IllegalArgumentException if url or username is an empty {@code String}.
     */
    public static String getPassword(String url, String username) {
        checkNotNull(url);
        checkArgument(!isEmpty(url));
        checkNotNull(username);
        checkArgument(!isEmpty(username));

        try {

            final ISecurePreferences root = SecurePreferencesFactory.getDefault();
            checkNotNull(root);

            if (root.nodeExists(CODENVY_NODE_NAME)) {
                final ISecurePreferences codenvyNode = root.node(CODENVY_NODE_NAME);

                if (codenvyNode.nodeExists(encodeSlashes(url))) {
                    final ISecurePreferences urlNode = root.node(CODENVY_NODE_NAME).node(encodeSlashes(url));

                    if (urlNode.nodeExists(username)) {
                        return urlNode.node(username).get(CODENVY_PASSWORD_KEY_NAME, null);
                    }
                }
            }

            return null;

        } catch (StorageException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Disable instantiation.
     */
    private SecureStorageHelper() {
    }
}
