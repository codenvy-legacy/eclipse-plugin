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
import com.codenvy.eclipse.core.client.model.Credentials;
import com.codenvy.eclipse.core.client.model.Project;
import com.codenvy.eclipse.core.client.model.RunnerStatus;
import com.codenvy.eclipse.core.client.request.APIRequest;
import com.codenvy.eclipse.core.client.request.SimpleAPIRequest;

/**
 * The Codenvy runner API client.
 * 
 * @author Kevin Pollet
 */
public class RunnerClient extends AbstractClient {
    /**
     * Constructs an instance of {@link RunnerClient}.
     * 
     * @param url the Codenvy platform URL.
     * @param apiName the API name.
     * @param username the username.
     * @param credentials the provided user {@link Credentials} might be {@code null}.
     * @param credentialsProvider the {@link CredentialsProvider}.
     * @throws NullPointerException if url, username or credentialsProvider parameter is {@code null}.
     */
    RunnerClient(String url,
                 String username,
                 Credentials credentials,
                 CredentialsProvider credentialsProvider) {

        super(url, "runner", username, credentials, credentialsProvider);
    }

    /**
     * Runs the given project with a codenvy runner.
     * 
     * @param project the project to run.
     * @return the {@link RunnerStatus}.
     * @throws NullPointerException if project parameter is {@code null}.
     * @throws APIException if something goes wrong with the API call.
     */
    public APIRequest<RunnerStatus> run(Project project) throws APIException {
        checkNotNull(project);

        final Invocation request = getWebTarget().path(project.workspaceId)
                                                 .path("run")
                                                 .queryParam("project", project.name)
                                                 .request()
                                                 .accept(APPLICATION_JSON_TYPE)
                                                 .buildPost(null);

        return new SimpleAPIRequest<>(request, RunnerStatus.class, getCredentialsProvider(), getUsername());
    }

    /**
     * Stops the project runner with the given process id.
     * 
     * @param project the project.
     * @param processId the runner process id.
     * @return the {@link RunnerStatus}.
     * @throws NullPointerException if project parameter is {@code null}.
     * @throws APIException if something goes wrong with the API call.
     */
    public APIRequest<RunnerStatus> stop(Project project, long processId) throws APIException {
        checkNotNull(project);

        final Invocation request = getWebTarget().path(project.workspaceId)
                                                 .path("stop")
                                                 .path(String.valueOf(processId))
                                                 .request()
                                                 .accept(APPLICATION_JSON_TYPE)
                                                 .buildPost(null);

        return new SimpleAPIRequest<>(request, RunnerStatus.class, getCredentialsProvider(), getUsername());
    }

    /**
     * Gets the project runner status with the given process id.
     * 
     * @param project the project.
     * @param processId the runner process id.
     * @return the {@link RunnerStatus}.
     * @throws NullPointerException if project parameter is {@code null}.
     * @throws APIException if something goes wrong with the API call.
     */
    public APIRequest<RunnerStatus> status(Project project, long processId) throws APIException {
        checkNotNull(project);

        final Invocation request = getWebTarget().path(project.workspaceId)
                                                 .path("status")
                                                 .path(String.valueOf(processId))
                                                 .request()
                                                 .accept(APPLICATION_JSON_TYPE)
                                                 .buildGet();

        return new SimpleAPIRequest<>(request, RunnerStatus.class, getCredentialsProvider(), getUsername());
    }

    /**
     * Gets the project runner logs with the given process id.
     * 
     * @param project the project.
     * @param processId the runner process id.
     * @return the runner logs.
     * @throws NullPointerException if project parameter is {@code null}.
     * @throws APIException if something goes wrong with the API call.
     */
    public APIRequest<String> logs(Project project, long processId) throws APIException {
        checkNotNull(project);

        final Invocation request = getWebTarget().path(project.workspaceId)
                                                 .path("logs")
                                                 .path(String.valueOf(processId))
                                                 .request()
                                                 .accept(TEXT_PLAIN_TYPE)
                                                 .buildGet();

        return new SimpleAPIRequest<>(request, String.class, getCredentialsProvider(), getUsername());
    }
}
