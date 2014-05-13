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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.team.core.RepositoryProvider;

import com.codenvy.eclipse.core.AbstractRestServiceWithAuth;
import com.codenvy.eclipse.core.CodenvyNature;
import com.codenvy.eclipse.core.ProjectService;
import com.codenvy.eclipse.core.model.CodenvyProject;
import com.codenvy.eclipse.core.model.CodenvyToken;
import com.codenvy.eclipse.core.team.CodenvyProvider;
import com.codenvy.eclipse.core.team.CodenvyMetaProject;

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
    public CodenvyProject newProject(CodenvyProject project, String workspaceId) {
        checkNotNull(project);
        checkNotNull(workspaceId);
        checkArgument(!workspaceId.trim().isEmpty());

        return getWebTarget().path(workspaceId)
                             .queryParam("name", project.name)
                             .request()
                             .accept(APPLICATION_JSON)
                             .post(json(project), CodenvyProject.class);
    }

    @Override
    public IProject importProject(CodenvyProject project, String workspaceId) {
        checkNotNull(project);
        checkNotNull(workspaceId);
        checkArgument(!workspaceId.trim().isEmpty());

        final InputStream entityStream = getWebTarget().path(workspaceId)
                                                       .path("export")
                                                       .path(project.name)
                                                       .request()
                                                       .get(InputStream.class);

        final NullProgressMonitor nullProgressMonitor = new NullProgressMonitor();
        final IProject importedProject = ResourcesPlugin.getWorkspace().getRoot().getProject(project.name);

        try (ZipInputStream zipInputStream = new ZipInputStream(entityStream)) {

            if (!importedProject.exists()) {
                importedProject.create(nullProgressMonitor);
                importedProject.open(nullProgressMonitor);
            }

            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                final String entryName = entry.getName();

                if (entry.isDirectory()) {
                    final IFolder folder = importedProject.getFolder(entryName);
                    folder.create(true, true, nullProgressMonitor);

                } else {
                    int b;
                    final IFile file = importedProject.getFile(entryName);
                    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                    while ((b = zipInputStream.read()) != -1) {
                        byteArrayOutputStream.write(b);
                    }

                    final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                    file.create(byteArrayInputStream, true, nullProgressMonitor);
                }
            }

        } catch (CoreException | IOException e) {
            throw new RuntimeException(e);
        }

        try {

            CodenvyMetaProject.create(importedProject, new CodenvyMetaProject(getUrl(), project.name, project.workspaceId, getCodenvyToken().value));
            RepositoryProvider.map(importedProject, CodenvyProvider.PROVIDER_ID);

            final IProjectDescription importedProjectDescription = importedProject.getDescription();
            importedProjectDescription.setNatureIds(new String[]{CodenvyNature.NATURE_ID});
            importedProject.setDescription(importedProjectDescription, new NullProgressMonitor());

        } catch (CoreException e) {
            throw new RuntimeException(e);
        }

        return importedProject;
    }


    @Override
    public void updateProjectResource(CodenvyProject project, String workspaceId, IResource resource) {
        checkNotNull(project);
        checkNotNull(workspaceId);
        checkArgument(!workspaceId.trim().isEmpty());
        checkNotNull(resource);

        switch (resource.getType()) {
            case IResource.FILE: {
                final IFile file = (IFile)resource;

                try {

                    getWebTarget().path(workspaceId)
                                  .path("file")
                                  .path(project.name)
                                  .path(file.getProjectRelativePath().toString())
                                  .request()
                                  .put(text(file.getContents()));

                } catch (CoreException e) {
                    throw new RuntimeException(e);
                }
            }
                break;

            case IResource.PROJECT:
            case IResource.FOLDER: {
                final IContainer container = (IContainer)resource;

                try {

                    for (IResource oneResource : container.members()) {
                        updateProjectResource(project, workspaceId, oneResource);
                    }

                } catch (CoreException e) {
                    throw new RuntimeException(e);
                }
            }
                break;
        }
    }

    @Override
    public boolean isResourceInProject(CodenvyProject project, String workspaceId, IResource resource) {
        checkNotNull(project);
        checkNotNull(workspaceId);
        checkArgument(!workspaceId.trim().isEmpty());
        checkNotNull(resource);

        final Response response = getWebTarget().path(workspaceId)
                                                .path("file")
                                                .path(project.name)
                                                .path(resource.getProjectRelativePath().toString())
                                                .request()
                                                .head();

        return response.getStatus() != Status.NOT_FOUND.getStatusCode();
    }
}
