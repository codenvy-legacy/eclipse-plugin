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
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN_TYPE;

import javax.ws.rs.client.Invocation;

import com.codenvy.eclipse.core.client.exceptions.APIException;
import com.codenvy.eclipse.core.client.model.BuilderStatus;
import com.codenvy.eclipse.core.client.model.Credentials;
import com.codenvy.eclipse.core.client.model.Project;
import com.codenvy.eclipse.core.client.request.APIRequest;
import com.codenvy.eclipse.core.client.request.SimpleAPIRequest;
import com.codenvy.eclipse.core.client.store.DataStoreFactory;
import com.codenvy.eclipse.core.client.store.StoredCredentials;

/**
 * The Codenvy builder API client.
 * 
 * @author Kevin Pollet
 */
public class BuilderClient extends AbstractClient {
    /**
     * Constructs an instance of {@link BuilderClient}.
     * 
     * @param url the Codenvy platform URL.
     * @param apiName the API name.
     * @param username the username.
     * @param credentials the provided user {@link Credentials} might be {@code null}.
     * @param credentialsProvider the {@link CredentialsProvider}.
     * @param credentialsStoreFactory the {@link DataStoreFactory}.
     * @throws NullPointerException if url, username or credentialsProvider parameter is {@code null}.
     */
    BuilderClient(String url,
                  String username,
                  Credentials credentials,
                  CredentialsProvider credentialsProvider,
                  DataStoreFactory<String, StoredCredentials> credentialsStoreFactory) {

        super(url, "builder", username, credentials, credentialsProvider, credentialsStoreFactory);
    }

    /**
     * Builds the given {@link Project} on codenvy.
     * 
     * @param project the project to build.
     * @return the {@link BuilderStatus}.
     * @throws NullPointerException if project parameter is {@code null}.
     * @throws APIException if something goes wrong with the API call.
     */
    public APIRequest<BuilderStatus> build(Project project) throws APIException {
        checkNotNull(project);

        final Invocation request = getWebTarget().path(project.workspaceId)
                                                 .path("build")
                                                 .queryParam("project", project.name)
                                                 .request()
                                                 .accept(APPLICATION_JSON_TYPE)
                                                 .buildPost(null);

        return new SimpleAPIRequest<>(request, BuilderStatus.class);
    }

    /**
     * Gets the status of the builder with the given task id.
     * 
     * @param project the project.
     * @param taskId the builder task id.
     * @return the {@link BuilderStatus}.
     * @throws NullPointerException if project parameter is {@code null}.
     * @throws APIException if something goes wrong with the API call.
     */
    public APIRequest<BuilderStatus> status(Project project, long taskId) throws APIException {
        checkNotNull(project);

        final Invocation request = getWebTarget().path(project.workspaceId)
                                                 .path("status")
                                                 .path(String.valueOf(taskId))
                                                 .request()
                                                 .accept(APPLICATION_JSON_TYPE)
                                                 .buildGet();

        return new SimpleAPIRequest<>(request, BuilderStatus.class);
    }

    /**
     * Gets the logs of the builder with the given task id.
     * 
     * @param project the project.
     * @param taskId the builder task id.
     * @return the builder logs.
     * @throws NullPointerException if project parameter is {@code null}.
     * @throws APIException if something goes wrong with the API call.
     */
    public APIRequest<String> logs(Project project, long taskId) throws APIException {
        checkNotNull(project);

        final Invocation request = getWebTarget().path(project.workspaceId)
                                                 .path("logs")
                                                 .path(String.valueOf(taskId))
                                                 .request()
                                                 .accept(TEXT_PLAIN_TYPE)
                                                 .buildGet();

        return new SimpleAPIRequest<>(request, String.class);
    }

    /**
     * Cancels the builder with the given task id.
     * 
     * @param project the project.
     * @param taskId the builder task id.
     * @return the {@link BuilderStatus}.
     * @throws NullPointerException if project parameter is {@code null}.
     * @throws APIException if something goes wrong with the API call.
     */
    public APIRequest<BuilderStatus> cancel(Project project, long taskId) throws APIException {
        checkNotNull(project);

        final Invocation request = getWebTarget().path(project.workspaceId)
                                                 .path("cancel")
                                                 .path(String.valueOf(taskId))
                                                 .request()
                                                 .accept(APPLICATION_JSON_TYPE)
                                                 .buildGet();

        return new SimpleAPIRequest<>(request, BuilderStatus.class);
    }
}
