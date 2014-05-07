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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.codenvy.eclipse.core.AbstractRestServiceWithAuth;
import com.codenvy.eclipse.core.CodenvyNature;
import com.codenvy.eclipse.core.ProjectService;
import com.codenvy.eclipse.core.model.CodenvyToken;
import com.codenvy.eclipse.core.model.Project;

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
    public List<Project> getWorkspaceProjects(String workspaceId) {
        checkNotNull(workspaceId);
        checkArgument(!workspaceId.trim().isEmpty());

        return getWebTarget().path(workspaceId)
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

        return getWebTarget().path(workspaceId)
                             .queryParam("name", project.name)
                             .request()
                             .accept(APPLICATION_JSON)
                             .post(Entity.json(project), Project.class);
    }

    @Override
    public IProject importProject(Project project, String workspaceId) {
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

            final IProjectDescription importedProjectDescription = importedProject.getDescription();
            importedProjectDescription.setNatureIds(new String[]{CodenvyNature.NATURE_ID});
            importedProject.setDescription(importedProjectDescription, new NullProgressMonitor());

        } catch (CoreException e) {
            throw new RuntimeException(e);
        }

        return importedProject;
    }
}
