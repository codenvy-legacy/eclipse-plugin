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

import static com.codenvy.eclipse.core.utils.StringHelper.isEmpty;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static javax.ws.rs.HttpMethod.POST;
import static javax.ws.rs.client.Entity.json;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

import java.util.List;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.GenericType;

import com.codenvy.eclipse.core.client.exceptions.APIException;
import com.codenvy.eclipse.core.client.model.Credentials;
import com.codenvy.eclipse.core.client.model.Workspace;
import com.codenvy.eclipse.core.client.model.Workspace.WorkspaceRef;
import com.codenvy.eclipse.core.client.request.APIRequest;
import com.codenvy.eclipse.core.client.request.SimpleAPIRequest;
import com.codenvy.eclipse.core.client.store.DataStoreFactory;
import com.codenvy.eclipse.core.client.store.StoredCredentials;

/**
 * The Codenvy workspace API client.
 * 
 * @author Kevin Pollet
 * @author Stéphane Daviet
 */
public class WorkspaceClient extends AbstractClient {
    /**
     * Constructs an instance of {@link WorkspaceClient}.
     * 
     * @param url the Codenvy platform URL.
     * @param apiName the API name.
     * @param username the username.
     * @param credentials the provided user {@link Credentials} might be {@code null}.
     * @param credentialsProvider the {@link CredentialsProvider}.
     * @param credentialsStoreFactory the {@link DataStoreFactory}.
     * @throws NullPointerException if url, username or credentialsProvider parameter is {@code null}.
     */
    WorkspaceClient(String url,
                    String username,
                    Credentials credentials,
                    CredentialsProvider credentialsProvider,
                    DataStoreFactory<String, StoredCredentials> credentialsStoreFactory) {

        super(url, "workspace", username, credentials, credentialsProvider, credentialsStoreFactory);
    }

    /**
     * Retrieves all Codenvy workspaces of the user identified by the authentication token.
     * 
     * @return all Codenvy workspaces never {@code null}.
     * @throws APIException if something goes wrong with the API call.
     */
    public APIRequest<List<Workspace>> all() throws APIException {
        final Invocation request = getWebTarget().path("all")
                                                 .request()
                                                 .accept(APPLICATION_JSON)
                                                 .buildGet();

        return new SimpleAPIRequest<>(request, new GenericType<List<Workspace>>() {
        });
    }

    /**
     * Retrieves a Codenvy workspace by it's name.
     * 
     * @param name the workspace name.
     * @return the Codenvy workspace or {@code null} if none.
     * @throws NullPointerException if name parameter is {@code null}.
     * @throws IllegalArgumentException if name parameter is an empty {@link String}.
     * @throws APIException if something goes wrong with the API call.
     */
    public APIRequest<WorkspaceRef> withName(String name) throws APIException {
        checkNotNull(name);
        checkArgument(!isEmpty(name));

        final Invocation request = getWebTarget().queryParam("name", name)
                                                 .request()
                                                 .accept(APPLICATION_JSON)
                                                 .buildGet();

        return new SimpleAPIRequest<>(request, WorkspaceRef.class);
    }

    /**
     * Creates the given workspace.
     * 
     * @param workspaceRef the workspace to create.
     * @return the created workspace.
     * @throws NullPointerException if {@link WorkspaceRef} parameter is {@code null}.
     * @throws APIException if something goes wrong with the API call.
     */
    public APIRequest<WorkspaceRef> create(WorkspaceRef workspaceRef) throws APIException {
        checkNotNull(workspaceRef);

        final Invocation request = getWebTarget().request()
                                                 .accept(APPLICATION_JSON_TYPE)
                                                 .build(POST, json(workspaceRef));

        return new SimpleAPIRequest<>(request, WorkspaceRef.class);

    }
}
