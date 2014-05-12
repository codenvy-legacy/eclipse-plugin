package com.codenvy.eclipse.core.impl;

import java.net.URI;

import org.eclipse.equinox.security.storage.EncodingUtils;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;

import com.codenvy.eclipse.core.CodenvySecureStorageService;
import com.codenvy.eclipse.core.model.CodenvyCredentials;
import com.codenvy.eclipse.core.model.CodenvyToken;

public class DefaultCodenvySecureStorageService implements CodenvySecureStorageService {
    private static final String CODENVY_PASSWORD_KEY_NAME            = "password";
    private static final String CODENVY_PASSWORD_TOKEN_NAME          = "token";
    private static final String CODENVY_PREFERENCE_STORAGE_NODE_NAME = "Codenvy";

    @Override
    public void storeCredentials(URI url, CodenvyCredentials credentials, CodenvyToken token) throws StorageException {
        final ISecurePreferences node = getNode(url, credentials.username);
        node.put(CODENVY_PASSWORD_KEY_NAME, credentials.password, true);
        node.put(CODENVY_PASSWORD_TOKEN_NAME, token.value, true);
    }

    @Override
    public String getPassword(URI url, String username) throws StorageException {
        final ISecurePreferences node = getNode(url, username);
        return node.get(CODENVY_PASSWORD_KEY_NAME, null);
    }

    @Override
    public String getToken(URI url, String username) throws StorageException {
        final ISecurePreferences node = getNode(url, username);
        return node.get(CODENVY_PASSWORD_TOKEN_NAME, null);
    }

    @Override
    public void deleteCredentials(URI url, String username) throws StorageException {
        getNode(url, username).removeNode();
    }

    static ISecurePreferences getNode(URI url, String username) {
        final ISecurePreferences root = SecurePreferencesFactory.getDefault();
        if (root == null) {
            // TODO Stéphane Daviet - 2014/05/12: Throw an exception either.
        }
        return root.node(calcNodeName(url, username));
    }

    static String calcNodeName(URI url, String username) {
        return CODENVY_PREFERENCE_STORAGE_NODE_NAME + '/' + EncodingUtils.encodeSlashes(url.toString()) + '/' + username;
    }
}
