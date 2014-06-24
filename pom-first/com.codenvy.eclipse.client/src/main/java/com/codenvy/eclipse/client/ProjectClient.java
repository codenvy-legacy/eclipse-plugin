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
import static javax.ws.rs.client.Entity.json;
import static javax.ws.rs.client.Entity.text;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.Response.Status.fromStatusCode;

import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipInputStream;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.codenvy.eclipse.client.APIRequestAdaptor.Adaptor;
import com.codenvy.eclipse.client.auth.Credentials;
import com.codenvy.eclipse.client.auth.CredentialsProvider;
import com.codenvy.eclipse.client.exceptions.APIException;
import com.codenvy.eclipse.client.model.Project;

/**
 * The Codenvy project API client.
 * 
 * @author Kevin Pollet
 * @author Stéphane Daviet
 */
public class ProjectClient extends AbstractClient {
    /**
     * Constructs an instance of {@link ProjectClient}.
     * 
     * @param url the Codenvy platform URL.
     * @param apiName the API name.
     * @param username the username.
     * @param credentials the provided user {@link Credentials} might be {@code null}.
     * @param credentialsProvider the {@link CredentialsProvider}.
     * @throws NullPointerException if url, username or credentialsProvider parameter is {@code null}.
     */
    ProjectClient(String url,
                  String username,
                  Credentials credentials,
                  CredentialsProvider credentialsProvider) {

        super(url, "project", username, credentials, credentialsProvider);
    }

    /**
     * Retrieves all workspace projects.
     * 
     * @param workspaceId the workspace id.
     * @return the workspace project list never {@code null}.
     * @throws NullPointerException if workspaceId parameter is {@code null}.
     * @throws APIException if something goes wrong with the API call.
     */
    public APIRequest<List<Project>> getWorkspaceProjects(String workspaceId) throws APIException {
        checkNotNull(workspaceId);

        final Invocation request = getWebTarget().path(workspaceId)
                                                 .request()
                                                 .accept(APPLICATION_JSON)
                                                 .buildGet();

        return new SimpleAPIRequest<>(request, new GenericType<List<Project>>() {
        }, getCredentialsProvider(), getUsername());
    }

    /**
     * Creates a project in the given workspace.
     * 
     * @param project the project to create.
     * @return the new project, never {@code null}.
     * @throws NullPointerException if project parameter is {@code null}.
     * @throws APIException if something goes wrong with the API call.
     */
    public APIRequest<Project> create(Project project) throws APIException {
        checkNotNull(project);

        final Invocation request = getWebTarget().path(project.workspaceId)
                                                 .queryParam("name", project.name)
                                                 .request()
                                                 .accept(APPLICATION_JSON)
                                                 .buildPost(json(project));

        return new SimpleAPIRequest<>(request, Project.class, getCredentialsProvider(), getUsername());
    }

    /**
     * Exports a resource in the given project.
     * 
     * @param project the project.
     * @param resourcePath the path of the resource to export, must be a folder.
     * @return the resource {@link ZipInputStream} or {@code null} if the resource is not found.
     * @throws NullPointerException if project parameter is {@code null}.
     * @throws APIException if something goes wrong with the API call.
     */
    public APIRequest<ZipInputStream> exportResources(Project project, String resourcePath) throws APIException {
        checkNotNull(project);

        final Invocation request = getWebTarget().path(project.workspaceId)
                                                 .path("export")
                                                 .path(project.name)
                                                 .path(resourcePath == null ? "" : resourcePath)
                                                 .request()
                                                 .accept("application/zip")
                                                 .buildGet();

        return new APIRequestAdaptor<>(new SimpleAPIRequest<>(request, InputStream.class, getCredentialsProvider(), getUsername()),
                                       new Adaptor<ZipInputStream, InputStream>() {
                                           @Override
                                           public ZipInputStream adapt(InputStream response) {
                                               return new ZipInputStream(response);
                                           }
                                       });
    }

    /**
     * Updates a resource in the given project.
     * 
     * @param project the project.
     * @param filePath the path to the file to update.
     * @param fileInputStream the file {@link InputStream}.
     * @throws NullPointerException if project, filePath or fileInputStream parameter is {@code null}.
     * @throws APIException if something goes wrong with the API call.
     */
    public APIRequest<Void> updateFile(Project project, String filePath, InputStream fileInputStream) throws APIException {
        checkNotNull(project);
        checkNotNull(filePath);
        checkNotNull(fileInputStream);

        final Invocation request = getWebTarget().path(project.workspaceId)
                                                 .path("file")
                                                 .path(project.name)
                                                 .path(filePath)
                                                 .request()
                                                 .buildPut(text(fileInputStream));

        return new SimpleAPIRequest<>(request, Void.class, getCredentialsProvider(), getUsername());
    }

    /**
     * Gets file content in the given project.
     * 
     * @param project the project.
     * @param filePath the file path.
     * @return the file {@link InputStream} or {@code null} if not found.
     * @throws APIException if something goes wrong with the API call.
     */
    public APIRequest<InputStream> getFile(Project project, String filePath) throws APIException {
        checkNotNull(project);
        checkNotNull(filePath);

        final Invocation request = getWebTarget().path(project.workspaceId)
                                                 .path("file")
                                                 .path(project.name)
                                                 .path(filePath)
                                                 .request()
                                                 .accept(TEXT_PLAIN)
                                                 .buildGet();

        return new SimpleAPIRequest<>(request, InputStream.class, getCredentialsProvider(), getUsername());
    }

    /**
     * Returns if the given resource exists in the given Codenvy project.
     * 
     * @param project the Codenvy project.
     * @param resource the resource path.
     * @return {@code true} if the given resource exists in the Codenvy project, {@code false} otherwise.
     * @throws NullPointerException if project or resourcePath parameter is {@code null}.
     * @throws APIException if something goes wrong with the API call.
     */
    public APIRequest<Boolean> isResource(Project project, String resourcePath) throws APIException {
        checkNotNull(project);
        checkNotNull(resourcePath);

        final Invocation request = getWebTarget().path(project.workspaceId)
                                                 .path("file")
                                                 .path(project.name)
                                                 .path(resourcePath)
                                                 .request()
                                                 .build("HEAD");

        return new APIRequestAdaptor<>(new SimpleAPIRequest<>(request, Response.class, getCredentialsProvider(), getUsername()),
                                       new Adaptor<Boolean, Response>() {
                                           @Override
                                           public Boolean adapt(Response response) {
                                               // TODO check if better, bad request response is sent if resourcePath is a folder
                                               final Status status = fromStatusCode(response.getStatus());
                                               return status == Status.OK || status == Status.BAD_REQUEST;
                                           }
                                       });
    }
}
