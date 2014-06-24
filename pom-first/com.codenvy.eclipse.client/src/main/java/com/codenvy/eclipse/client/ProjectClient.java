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
import static javax.ws.rs.client.Entity.entity;
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

import com.codenvy.eclipse.client.RequestResponseAdaptor.Adaptor;
import com.codenvy.eclipse.client.auth.AuthenticationManager;
import com.codenvy.eclipse.client.model.Project;
import com.codenvy.eclipse.client.model.Workspace.WorkspaceRef;

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
     * @param authenticationManager the {@link AuthenticationManager}.
     * @throws NullPointerException if url or authenticationManager parameter is {@code null}.
     */
    ProjectClient(String url, AuthenticationManager authenticationManager) {
        super(url, "project", authenticationManager);
    }

    /**
     * Retrieves all workspace projects.
     *
     * @param workspaceId the workspace id.
     * @return the workspace project list never {@code null}.
     * @throws NullPointerException if workspaceId parameter is {@code null}.
     * @throws CodenvyException if something goes wrong with the API call.
     */
    public Request<List<Project>> getWorkspaceProjects(String workspaceId) throws CodenvyException {
        checkNotNull(workspaceId);

        final Invocation request = getWebTarget().path(workspaceId)
                                                 .request()
                                                 .accept(APPLICATION_JSON)
                                                 .buildGet();

        return new SimpleRequest<>(request, new GenericType<List<Project>>() {
        }, getAuthenticationManager());
    }

    /**
     * Creates a project in the given workspace.
     *
     * @param project the project to create.
     * @return the new project, never {@code null}.
     * @throws NullPointerException if project parameter is {@code null}.
     * @throws CodenvyException if something goes wrong with the API call.
     */
    public Request<Project> create(Project project) throws CodenvyException {
        checkNotNull(project);

        final Invocation request = getWebTarget().path(project.workspaceId)
                                                 .queryParam("name", project.name)
                                                 .request()
                                                 .accept(APPLICATION_JSON)
                                                 .buildPost(json(project));

        return new SimpleRequest<>(request, Project.class, getAuthenticationManager());
    }

    /**
     * Exports a resource in the given project.
     *
     * @param project the project.
     * @param resourcePath the path of the resource to export, must be a folder.
     * @return the resource {@link ZipInputStream} or {@code null} if the resource is not found.
     * @throws NullPointerException if project parameter is {@code null}.
     * @throws CodenvyException if something goes wrong with the API call.
     */
    public Request<ZipInputStream> exportResources(Project project, String resourcePath) throws CodenvyException {
        checkNotNull(project);

        final Invocation request = getWebTarget().path(project.workspaceId)
                                                 .path("export")
                                                 .path(project.name)
                                                 .path(resourcePath == null ? "" : resourcePath)
                                                 .request()
                                                 .accept("application/zip")
                                                 .buildGet();

        return new RequestResponseAdaptor<>(new SimpleRequest<>(request, InputStream.class, getAuthenticationManager()),
                                            new Adaptor<ZipInputStream, InputStream>() {
                                                @Override
                                                public ZipInputStream adapt(InputStream response) {
                                                    return new ZipInputStream(response);
                                                }
                                            });
    }

    /**
     * Upload a local ZIP folder.
     *
     * @param workspace the {@link WorkspaceRef} in which the ZIP folder will be imported.
     * @param project the pre-exisiting {@link Project} in which the archive content should be imported.
     * @param archiveInputStream the archive {@link InputStream}.
     * @return the {@link Request} pointing to a {@link Void} result.
     * @throws NullPointerException if workspace, projectName or archiveInputStrem parameters are {@code null}.
     */
    public Request<Void> importArchive(WorkspaceRef workspace, Project project, InputStream archiveInputStream) {
        checkNotNull(workspace);
        checkNotNull(project);
        checkNotNull(archiveInputStream);

        final Invocation request = getWebTarget().path(workspace.id)
                                                 .path("import")
                                                 .path(project.name)
                                                 .request()
                                                 .buildPost(entity(archiveInputStream, "application/zip"));

        return new SimpleRequest<>(request, Void.class, getAuthenticationManager());
    }

    /**
     * Updates a resource in the given project.
     *
     * @param project the project.
     * @param filePath the path to the file to update.
     * @param fileInputStream the file {@link InputStream}.
     * @throws NullPointerException if project, filePath or fileInputStream parameter is {@code null}.
     * @throws CodenvyException if something goes wrong with the API call.
     */
    public Request<Void> updateFile(Project project, String filePath, InputStream fileInputStream) throws CodenvyException {
        checkNotNull(project);
        checkNotNull(filePath);
        checkNotNull(fileInputStream);

        final Invocation request = getWebTarget().path(project.workspaceId)
                                                 .path("file")
                                                 .path(project.name)
                                                 .path(filePath)
                                                 .request()
                                                 .buildPut(text(fileInputStream));

        return new SimpleRequest<>(request, Void.class, getAuthenticationManager());
    }

    /**
     * Gets file content in the given project.
     *
     * @param project the project.
     * @param filePath the file path.
     * @return the file {@link InputStream} or {@code null} if not found.
     * @throws CodenvyException if something goes wrong with the API call.
     */
    public Request<InputStream> getFile(Project project, String filePath) throws CodenvyException {
        checkNotNull(project);
        checkNotNull(filePath);

        final Invocation request = getWebTarget().path(project.workspaceId)
                                                 .path("file")
                                                 .path(project.name)
                                                 .path(filePath)
                                                 .request()
                                                 .accept(TEXT_PLAIN)
                                                 .buildGet();

        return new SimpleRequest<>(request, InputStream.class, getAuthenticationManager());
    }

    /**
     * Returns if the given resource exists in the given Codenvy project.
     *
     * @param project the Codenvy project.
     * @param resource the resource path.
     * @return {@code true} if the given resource exists in the Codenvy project, {@code false} otherwise.
     * @throws NullPointerException if project or resourcePath parameter is {@code null}.
     * @throws CodenvyException if something goes wrong with the API call.
     */
    public Request<Boolean> isResource(Project project, String resourcePath) throws CodenvyException {
        checkNotNull(project);
        checkNotNull(resourcePath);

        final Invocation request = getWebTarget().path(project.workspaceId)
                                                 .path("file")
                                                 .path(project.name)
                                                 .path(resourcePath)
                                                 .request()
                                                 .build("HEAD");

        return new RequestResponseAdaptor<>(new SimpleRequest<>(request, Response.class, getAuthenticationManager()),
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
