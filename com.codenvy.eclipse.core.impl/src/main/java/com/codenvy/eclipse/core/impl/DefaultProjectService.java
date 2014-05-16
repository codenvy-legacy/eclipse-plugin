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
import static javax.ws.rs.client.Entity.json;
import static javax.ws.rs.client.Entity.text;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipInputStream;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.core.resources.IResource;

import com.codenvy.eclipse.core.model.CodenvyProject;
import com.codenvy.eclipse.core.model.CodenvyToken;
import com.codenvy.eclipse.core.services.AbstractRestServiceWithAuth;
import com.codenvy.eclipse.core.services.ProjectService;

/**
 * The Codenvy project client service.
 * 
 * @author Kevin Pollet
 */
public class DefaultProjectService extends AbstractRestServiceWithAuth implements ProjectService {
    /**
     * Constructs an instance of {@linkplain DefaultProjectService}.
     * 
     * @param url the Codenvy platform url.
     * @param codenvyToken the Codenvy authentication token.
     * @throws NullPointerException if url or codenvyToken parameter is {@code null}.
     * @throws IllegalArgumentException if url parameter is an empty {@linkplain String}.
     */
    public DefaultProjectService(String url, CodenvyToken codenvyToken) {
        super(url, "api/project", codenvyToken);
    }

    @Override
    public List<CodenvyProject> getWorkspaceProjects(String workspaceId) {
        checkNotNull(workspaceId);
        checkArgument(!workspaceId.trim().isEmpty());

        return getWebTarget().path(workspaceId)
                             .request()
                             .accept(APPLICATION_JSON)
                             .get(new GenericType<List<CodenvyProject>>() {
                             });
    }

    @Override
    public CodenvyProject newProject(CodenvyProject project) {
        checkNotNull(project);

        return getWebTarget().path(project.workspaceId)
                             .queryParam("name", project.name)
                             .request()
                             .accept(APPLICATION_JSON)
                             .post(json(project), CodenvyProject.class);
    }

    @Override
    public ZipInputStream exportResources(CodenvyProject project, String resourcePath) {
        checkNotNull(project);

        final InputStream stream = getWebTarget().path(project.workspaceId)
                                                 .path("export")
                                                 .path(project.name)
                                                 .path(resourcePath == null ? "" : resourcePath)
                                                 .request()
                                                 .get(InputStream.class);

        return stream == null ? null : new ZipInputStream(stream);
    }

    @Override
    public void updateFile(CodenvyProject project, String filePath, InputStream fileInputStream) {
        checkNotNull(project);
        checkNotNull(filePath);
        checkArgument(!filePath.trim().isEmpty());
        checkNotNull(fileInputStream);

        getWebTarget().path(project.workspaceId)
                      .path("file")
                      .path(project.name)
                      .path(filePath)
                      .request()
                      .put(text(fileInputStream));
    }

    @Override
    public InputStream getFile(CodenvyProject project, String filePath) {
        checkNotNull(project);
        checkNotNull(filePath);
        checkArgument(!filePath.trim().isEmpty());

        return getWebTarget().path(project.workspaceId)
                             .path("file")
                             .path(project.name)
                             .path(filePath)
                             .request()
                             .get(InputStream.class);
    }

    @Override
    public boolean isResourceInProject(CodenvyProject project, IResource resource) {
        checkNotNull(project);
        checkNotNull(resource);

        final Response response = getWebTarget().path(project.workspaceId)
                                                .path("file")
                                                .path(project.name)
                                                .path(resource.getProjectRelativePath().toString())
                                                .request()
                                                .head();

        return response.getStatus() != Status.NOT_FOUND.getStatusCode();
    }
}
