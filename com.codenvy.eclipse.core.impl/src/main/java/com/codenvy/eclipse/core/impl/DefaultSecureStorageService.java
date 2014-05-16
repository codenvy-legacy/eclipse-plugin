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

import static com.codenvy.eclipse.core.utils.StringHelper.isNullOrEmpty;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Arrays.asList;
import static org.eclipse.equinox.security.storage.EncodingUtils.decodeSlashes;
import static org.eclipse.equinox.security.storage.EncodingUtils.encodeSlashes;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;

import com.codenvy.eclipse.core.model.CodenvyCredentials;
import com.codenvy.eclipse.core.model.CodenvyToken;
import com.codenvy.eclipse.core.services.SecureStorageService;

/**
 * Convenient OSGI service that provides all the operations related to secure storage for Codenvy credentials. This is the default
 * implementation.
 * 
 * @author Stéphane Daviet
 * @author Kevin Pollet
 */
public class DefaultSecureStorageService implements SecureStorageService {
    private static final String CODENVY_PASSWORD_KEY_NAME            = "password";
    private static final String CODENVY_TOKEN_KEY_NAME               = "token";
    private static final String CODENVY_PREFERENCE_STORAGE_NODE_NAME = "Codenvy";

    @Override
    public void storeCredentials(String url, CodenvyCredentials credentials, CodenvyToken token) {
        checkNotNull(url);
        checkArgument(!isNullOrEmpty(url));
        checkNotNull(credentials);
        checkNotNull(token);

        try {

            final ISecurePreferences node = getNode(url, credentials.username, true);
            node.put(CODENVY_PASSWORD_KEY_NAME, credentials.password, true);
            node.put(CODENVY_TOKEN_KEY_NAME, token.value, true);

        } catch (StorageException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getPassword(String url, String username) {
        checkNotNull(url);
        checkArgument(!isNullOrEmpty(url));
        checkNotNull(username);
        checkArgument(!isNullOrEmpty(username));

        try {

            final ISecurePreferences node = getNode(url, username, false);
            return node == null ? null : node.get(CODENVY_PASSWORD_KEY_NAME, null);

        } catch (StorageException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CodenvyToken getToken(String url, String username) {
        checkNotNull(url);
        checkArgument(!isNullOrEmpty(url));
        checkNotNull(username);
        checkArgument(!isNullOrEmpty(username));

        try {

            final ISecurePreferences node = getNode(url, username, false);
            if (node == null) {
                return null;
            }

            final String nodeValue = node.get(CODENVY_TOKEN_KEY_NAME, null);
            return nodeValue == null ? null : new CodenvyToken(nodeValue);

        } catch (StorageException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteCredentials(String url, String username) {
        checkNotNull(url);
        checkArgument(!isNullOrEmpty(url));
        checkNotNull(username);
        checkArgument(!isNullOrEmpty(username));

        final ISecurePreferences node = getNode(url, username, true);
        if (node != null) {
            node.removeNode();
        }
    }

    @Override
    public List<String> getURLs() {
        final ISecurePreferences root = SecurePreferencesFactory.getDefault();
        checkNotNull(root);

        final List<String> unescapedUrls = new ArrayList<>();
        if (root.nodeExists(CODENVY_PREFERENCE_STORAGE_NODE_NAME)) {
            final String[] urls = root.node(CODENVY_PREFERENCE_STORAGE_NODE_NAME).childrenNames();

            for (String oneUrl : urls) {
                unescapedUrls.add(decodeSlashes(oneUrl));
            }
        }

        return unescapedUrls;
    }

    @Override
    public List<String> getUsernamesForURL(String url) {
        final ISecurePreferences root = SecurePreferencesFactory.getDefault();
        checkNotNull(root);

        final List<String> usernames = new ArrayList<>();
        if (root.nodeExists(CODENVY_PREFERENCE_STORAGE_NODE_NAME)
            && root.node(CODENVY_PREFERENCE_STORAGE_NODE_NAME).nodeExists(encodeSlashes(url))) {

            final String[] usernamesArray = root.node(CODENVY_PREFERENCE_STORAGE_NODE_NAME).node(encodeSlashes(url)).childrenNames();
            usernames.addAll(asList(usernamesArray));
        }

        return usernames;
    }

    /**
     * Gets the secure storage node corresponding to the given url and username.
     * 
     * @param url the codenvy url.
     * @param username the codenvy username.
     * @param createIfNotExist {@code true} if the node must be created if not found.
     * @return the {@link ISecurePreferences} node.
     */
    private ISecurePreferences getNode(String url, String username, boolean createIfNotExist) {
        final ISecurePreferences root = SecurePreferencesFactory.getDefault();
        checkNotNull(root);

        if (!createIfNotExist && !root.nodeExists(nodeName(url, username))) {
            return null;
        }
        return root.node(nodeName(url, username));
    }

    /**
     * Returns the node name corresponding to the given url and username.
     * 
     * @param url the url.
     * @param username the username.
     * @return the node name.
     */
    private String nodeName(String url, String username) {
        return CODENVY_PREFERENCE_STORAGE_NODE_NAME + '/' + encodeSlashes(url) + '/' + username;
    }
}
