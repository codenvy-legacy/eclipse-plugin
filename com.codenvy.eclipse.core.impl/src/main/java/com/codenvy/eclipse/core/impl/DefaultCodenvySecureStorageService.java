package com.codenvy.eclipse.core.impl;

import org.eclipse.equinox.security.storage.EncodingUtils;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;

import com.codenvy.eclipse.core.CodenvySecureStorageService;
import com.codenvy.eclipse.core.model.CodenvyCredentials;
import com.codenvy.eclipse.core.model.CodenvyToken;

/**
 * Convenient OSGI service that provides all the operations related to secure storage for Codenvy credentials. This is the default
 * implementation.
 * 
 * @author Stéphane Daviet
 */
public class DefaultCodenvySecureStorageService implements CodenvySecureStorageService {
    private static final String CODENVY_PASSWORD_KEY_NAME            = "password";
    private static final String CODENVY_PASSWORD_TOKEN_NAME          = "token";
    private static final String CODENVY_PREFERENCE_STORAGE_NODE_NAME = "Codenvy";

    @Override
    public void storeCredentials(String url, CodenvyCredentials credentials, CodenvyToken token) throws StorageException {
        final ISecurePreferences node = getNode(url, credentials.username);
        node.put(CODENVY_PASSWORD_KEY_NAME, credentials.password, true);
        node.put(CODENVY_PASSWORD_TOKEN_NAME, token.value, true);
    }

    @Override
    public String getPassword(String url, String username) throws StorageException {
        final ISecurePreferences node = getNode(url, username);
        return node.get(CODENVY_PASSWORD_KEY_NAME, null);
    }

    @Override
    public String getToken(String url, String username) throws StorageException {
        final ISecurePreferences node = getNode(url, username);
        return node.get(CODENVY_PASSWORD_TOKEN_NAME, null);
    }

    @Override
    public void deleteCredentials(String url, String username) throws StorageException {
        getNode(url, username).removeNode();
    }

    @Override
    public String[] getURLs() throws StorageException {
        final ISecurePreferences root = SecurePreferencesFactory.getDefault();
        if (root == null) {
            // TODO Stéphane Daviet - 2014/05/12: Throw an exception either.
        }
        final String[] urls = root.node(CODENVY_PREFERENCE_STORAGE_NODE_NAME).childrenNames();
        final String[] unescapedUrls = new String[urls.length];
        for (int i = 0; i < urls.length; i++) {
            unescapedUrls[i] = EncodingUtils.decodeSlashes(urls[i]);
        }
        return unescapedUrls;
    }

    @Override
    public String[] getUsernamesForURL(String url) throws StorageException {
        final ISecurePreferences root = SecurePreferencesFactory.getDefault();
        if (root == null) {
            // TODO Stéphane Daviet - 2014/05/12: Throw an exception either.
        }
        if (!root.node(CODENVY_PREFERENCE_STORAGE_NODE_NAME).nodeExists(EncodingUtils.encodeSlashes(url))) {
            return new String[]{};
        }
        return root.node(CODENVY_PREFERENCE_STORAGE_NODE_NAME).node(EncodingUtils.encodeSlashes(url)).childrenNames();
    }

    static ISecurePreferences getNode(String url, String username) {
        final ISecurePreferences root = SecurePreferencesFactory.getDefault();
        if (root == null) {
            // TODO Stéphane Daviet - 2014/05/12: Throw an exception either.
        }
        return root.node(calcNodeName(url, username));
    }

    static String calcNodeName(String url, String username) {
        return CODENVY_PREFERENCE_STORAGE_NODE_NAME + '/' + EncodingUtils.encodeSlashes(url) + '/' + username;
    }
}
