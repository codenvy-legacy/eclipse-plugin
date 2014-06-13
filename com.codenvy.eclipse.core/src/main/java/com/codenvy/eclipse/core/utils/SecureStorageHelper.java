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
package com.codenvy.eclipse.core.utils;

import static com.codenvy.eclipse.core.utils.StringHelper.isEmpty;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Arrays.asList;
import static org.eclipse.equinox.security.storage.EncodingUtils.encodeSlashes;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;

/**
 * Helper providing helper methods to work with Eclipse secure storage.
 * 
 * @author Stéphane Daviet
 * @author Kevin Pollet
 */
public final class SecureStorageHelper {
    public static final String CODENVY_PREFERENCE_STORAGE_NODE_NAME = "Codenvy";
    public static final String CODENVY_PASSWORD_KEY_NAME            = "password";
    public static final String CODENVY_TOKEN_KEY_NAME               = "token";

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
        if (root.nodeExists(CODENVY_PREFERENCE_STORAGE_NODE_NAME)
            && root.node(CODENVY_PREFERENCE_STORAGE_NODE_NAME).nodeExists(encodeSlashes(url))) {

            final String[] usernamesArray = root.node(CODENVY_PREFERENCE_STORAGE_NODE_NAME).node(encodeSlashes(url)).childrenNames();
            usernames.addAll(asList(usernamesArray));
        }

        return usernames;
    }

    /**
     * Disable instantiation.
     */
    private SecureStorageHelper() {
    }
}
