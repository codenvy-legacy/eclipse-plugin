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
package com.codenvy.eclipse.client;

import static com.google.common.base.Preconditions.checkNotNull;

import com.codenvy.eclipse.client.auth.AuthenticationManager;
import com.codenvy.eclipse.client.auth.Credentials;
import com.codenvy.eclipse.client.store.DataStoreFactory;
import com.codenvy.eclipse.client.store.InMemoryDataStoreFactory;

/**
 * The Codenvy client API entry point.
 * 
 * @author Kevin Pollet
 */
public class Codenvy {
    private final String              url;
    private final String              username;
    private final Credentials         credentials;
    private final AuthenticationManager authenticationManager;

    /**
     * Constructs an instance of {@link Codenvy} client API.
     * 
     * @param url the Codenvy platform URL.
     * @param username the username.
     * @param credentials the provided user {@link Credentials} might be {@code null}.
     * @param credentialsStoreFactory the {@link DataStoreFactory}.
     * @throws NullPointerException if url, username or credentialsProvider parameter is {@code null}.
     */
    private Codenvy(String url,
                    String username,
                    Credentials credentials,
                    DataStoreFactory<String, Credentials> credentialsStoreFactory) {

        checkNotNull(url);
        checkNotNull(username);

        this.url = url;
        this.username = username;
        this.credentials = credentials;
        this.authenticationManager =
                                   new AuthenticationManager(url, credentialsStoreFactory != null ? credentialsStoreFactory.getDataStore(url)
                                       : null);
    }

    /**
     * Returns the user API client.
     * 
     * @return the user API client.
     */
    public UserClient user() {
        return new UserClient(url, username, credentials, authenticationManager);
    }

    /**
     * Returns the builder API client.
     * 
     * @return the builder API client.
     */
    public BuilderClient builder() {
        return new BuilderClient(url, username, credentials, authenticationManager);
    }

    /**
     * Returns the runner API client.
     * 
     * @return the runner API client.
     */
    public RunnerClient runner() {
        return new RunnerClient(url, username, credentials, authenticationManager);
    }

    /**
     * Returns the project API client.
     * 
     * @return the project API client.
     */
    public ProjectClient project() {
        return new ProjectClient(url, username, credentials, authenticationManager);
    }

    /**
     * Returns the workspace API client.
     * 
     * @return the workspace API client.
     */
    public WorkspaceClient workspace() {
        return new WorkspaceClient(url, username, credentials, authenticationManager);
    }

    public static class Builder {
        private final String                          url;
        private final String                          username;
        private Credentials                           credentials;
        private DataStoreFactory<String, Credentials> credentialsStoreFactory;

        public Builder(String url, String username) {
            this.url = url;
            this.username = username;
            this.credentialsStoreFactory = new InMemoryDataStoreFactory();
        }

        /**
         * Provides the user {@link Credentials} used if they are not found in storage.
         * 
         * @param credentials the provided {@link Credentials}.
         * @return {@link Builder} instance.
         */
        public Builder withCredentials(Credentials credentials) {
            this.credentials = credentials;
            return this;
        }

        /**
         * Defines the {@link DataStoreFactory} used to store the user {@link Credentials}.
         * 
         * @param credentialsStoreFactory the {@link DataStoreFactory} to use.
         * @return {@link Builder} instance.
         */
        public Builder withCredentialsStoreFactory(DataStoreFactory<String, Credentials> credentialsStoreFactory) {
            this.credentialsStoreFactory = credentialsStoreFactory;
            return this;
        }

        public Codenvy build() {
            return new Codenvy(url, username, credentials, credentialsStoreFactory);
        }
    }
}
