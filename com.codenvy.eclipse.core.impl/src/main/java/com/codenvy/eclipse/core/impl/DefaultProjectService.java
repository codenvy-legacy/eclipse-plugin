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
package com.codenvy.eclipse.core.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.zip.ZipInputStream;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.UriBuilder;

import com.codenvy.eclipse.core.ProjectService;
import com.codenvy.eclipse.core.model.CodenvyToken;
import com.codenvy.eclipse.core.model.Project;

/**
 * The Codenvy project client service.
 * 
 * @author Kevin Pollet
 */
public class DefaultProjectService implements ProjectService {
    private final CodenvyToken codenvyToken;
    private final WebTarget    projectWebTarget;

    /**
     * Constructs an instance of {@linkplain DefaultProjectService}.
     * 
     * @param url the Codenvy platform url.
     * @param codenvyToken the Codenvy authentication token.
     * @throws NullPointerException if url or codenvyToken parameter is {@code null}.
     * @throws IllegalArgumentException if url parameter is an empty {@linkplain String}.
     */
    public DefaultProjectService(String url, CodenvyToken codenvyToken) {
        checkNotNull(codenvyToken);
        checkNotNull(url);
        checkArgument(!url.trim().isEmpty());

        final URI uri = UriBuilder.fromUri(url)
                                  .path("api/project")
                                  .build();

        this.codenvyToken = codenvyToken;
        this.projectWebTarget = ClientBuilder.newClient()
                                             .target(uri);
    }

    @Override
    public List<Project> getWorkspaceProjects(String workspaceId) {
        checkNotNull(workspaceId);
        checkArgument(!workspaceId.trim().isEmpty());

        return projectWebTarget.queryParam("token", codenvyToken.value)
                               .path(workspaceId)
                               .request()
                               .accept(APPLICATION_JSON)
                               .get(new GenericType<List<Project>>() {
                               });
    }

    @Override
    public Project newProject(Project project, String workspaceId) {
        checkNotNull(project);
        checkNotNull(workspaceId);
        checkArgument(!workspaceId.trim().isEmpty());

        return projectWebTarget.path(workspaceId)
                               .queryParam("token", codenvyToken.value)
                               .queryParam("name", project.name)
                               .request()
                               .accept(APPLICATION_JSON)
                               .post(Entity.json(project), Project.class);
    }

    @Override
    public ZipInputStream exportProject(Project project, String workspaceId) {
        checkNotNull(project);
        checkNotNull(workspaceId);
        checkArgument(!workspaceId.trim().isEmpty());

        final InputStream entityStream = projectWebTarget.path(workspaceId)
                                                         .path("export")
                                                         .path(project.name)
                                                         .queryParam("token", codenvyToken.value)
                                                         .request()
                                                         .get(InputStream.class);

        return new ZipInputStream(entityStream);
    }
}
