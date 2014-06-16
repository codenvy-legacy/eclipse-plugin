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
package com.codenvy.eclipse.core.client;

import static com.google.common.base.Preconditions.checkNotNull;

import com.codenvy.eclipse.core.client.store.DataStore;
import com.codenvy.eclipse.core.client.store.StoredCredentials;

/**
 * Context class used to provide context to {@linkplain com.codenvy.eclipse.core.client.CredentialsProvider CredentialsProvider}
 * 
 * @author Kevin Pollet
 */
public class Context {
    private final String                               url;
    private final DataStore<String, StoredCredentials> dataStore;

    /**
     * Constructs an instance of {@link Context}.
     * 
     * @param url the Codenvy platform URL.
     * @param dataStore the {@link DataStore} used to store the credentials.
     * @throws NullPointerException if url parameter is {@code null}.
     */
    public Context(String url, DataStore<String, StoredCredentials> dataStore) {
        checkNotNull(url);

        this.url = url;
        this.dataStore = dataStore;
    }

    /**
     * Returns the Codenvy platform URL.
     * 
     * @return the Codenvy platform URL.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Loads the stored credentials for the given username.
     * 
     * @param username the username.
     * @return the {@link StoredCredentials} or {@code null} if none.
     * @throws NullPointerException if {@link DataStore} implementation doesn't support {@code null} keys.
     */
    public StoredCredentials loadStoredCredentials(String username) {
        if (dataStore == null) {
            return null;
        }
        return dataStore.get(username);
    }
}
