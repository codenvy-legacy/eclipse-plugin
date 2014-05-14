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
package com.codenvy.eclipse.core;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.team.core.RepositoryProvider;

import com.codenvy.eclipse.core.model.CodenvyProject;
import com.codenvy.eclipse.core.team.CodenvyMetaProject;
import com.codenvy.eclipse.core.team.CodenvyProvider;

/**
 * Helper providing methods to work with eclipse projects.
 * 
 * @author Kevin Pollet
 */
public final class EclipseProjectHelper {
    /**
     * Creates an {@link IProject} in the current Eclipse workspace.
     * 
     * @param stream the project {@link ZipInputStream}.
     * @param metaProject the {@link CodenvyMetaProject}.
     * @param monitor the {@link IProgressMonitor} or {@code null} if none.
     * @return the created {@link IProject}.
     * @throws NullPointerException if stream or metaProject parameter is {@code null}.
     */
    public static IProject createIProjectFromZipStream(ZipInputStream stream, CodenvyMetaProject metaProject, IProgressMonitor monitor) {
        checkNotNull(stream);
        checkNotNull(metaProject);

        final SubMonitor subMonitor = SubMonitor.convert(monitor);
        final IProject newProject = ResourcesPlugin.getWorkspace().getRoot().getProject(metaProject.projectName);

        subMonitor.setTaskName("Create project " + metaProject.projectName);

        try (ZipInputStream zipInputStream = stream) {

            if (!newProject.exists()) {
                newProject.create(subMonitor);
                newProject.open(subMonitor);

                ZipEntry entry;
                while ((entry = zipInputStream.getNextEntry()) != null) {
                    subMonitor.setWorkRemaining(1000);

                    final String entryName = entry.getName();

                    if (entry.isDirectory()) {
                        final IFolder folder = newProject.getFolder(entryName);
                        folder.create(true, true, subMonitor);

                    } else {
                        int b;
                        final IFile file = newProject.getFile(entryName);
                        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                        while ((b = zipInputStream.read()) != -1) {
                            byteArrayOutputStream.write(b);
                        }

                        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                        file.create(byteArrayInputStream, true, subMonitor);
                    }

                    subMonitor.worked(1);
                }

                try {

                    CodenvyMetaProject.create(newProject, metaProject);
                    RepositoryProvider.map(newProject, CodenvyProvider.PROVIDER_ID);

                    final IProjectDescription newProjectDescription = newProject.getDescription();
                    newProjectDescription.setNatureIds(new String[]{CodenvyNature.NATURE_ID});
                    newProject.setDescription(newProjectDescription, new NullProgressMonitor());

                } catch (CoreException e) {
                    throw new RuntimeException(e);
                }
            }

        } catch (CoreException | IOException e) {
            throw new RuntimeException(e);

        } finally {
            subMonitor.done();
        }

        return newProject;
    }

    /**
     * Updates the given resource in the given codenvy project.
     * 
     * @param codenvyProject the {@link CodenvyProject}.
     * @param resource the {@link IResource} to update in Codenvy.
     * @param projectService the {@link ProjectService} instance.
     * @param monitor the {@link IProgressMonitor} or {@code null} if none.
     * @throws NullPointerException if codenvyProject, resource or projectService is {@code null}.
     */
    public static void updateCodenvyProjectResource(CodenvyProject codenvyProject,
                                                    IResource resource,
                                                    ProjectService projectService,
                                                    IProgressMonitor monitor) {
        checkNotNull(codenvyProject);
        checkNotNull(resource);
        checkNotNull(projectService);

        final SubMonitor subMonitor = SubMonitor.convert(monitor);
        subMonitor.setWorkRemaining(1000);

        try {

            switch (resource.getType()) {
                case IResource.FILE: {
                    final IFile file = (IFile)resource;

                    try {

                        projectService.updateFile(codenvyProject, codenvyProject.workspaceId, file.getProjectRelativePath().toString(),
                                                  file.getContents());

                        subMonitor.worked(1);

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
                            updateCodenvyProjectResource(codenvyProject, oneResource, projectService, monitor);
                        }

                    } catch (CoreException e) {
                        throw new RuntimeException(e);
                    }
                }
                    break;
            }

        } finally {
            subMonitor.done();
        }
    }

    /**
     * Updates the given {@link IFile} with the given {@link InputStream} content.
     * 
     * @param stream the {@link InputStream}.
     * @param file the {@link IFile} to update.
     * @param monitor the {@link IProgressMonitor} or {@code null} if none.
     * @throws NullPointerException if stream, file or monitor parameter is {@code null}.
     */
    public static void updateIFile(InputStream stream, IFile file, IProgressMonitor monitor) {
        checkNotNull(stream);
        checkNotNull(file);

        final SubMonitor subMonitor = SubMonitor.convert(monitor, 1);

        try {

            file.setContents(stream, true, true, monitor);

            subMonitor.worked(1);

        } catch (CoreException e) {
            throw new RuntimeException(e);

        } finally {
            subMonitor.done();
        }
    }

    /**
     * Disable instantiation.
     */
    private EclipseProjectHelper() {
    }
}
