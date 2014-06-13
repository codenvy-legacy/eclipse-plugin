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
package com.codenvy.eclipse.core.utils;

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
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.team.core.RepositoryProvider;

import com.codenvy.eclipse.core.CodenvyNature;
import com.codenvy.eclipse.core.client.Codenvy;
import com.codenvy.eclipse.core.client.ProjectClient;
import com.codenvy.eclipse.core.client.model.Project;
import com.codenvy.eclipse.core.team.CodenvyMetaProject;
import com.codenvy.eclipse.core.team.CodenvyProvider;

/**
 * Helper providing methods to work with eclipse projects.
 * 
 * @author Kevin Pollet
 */
public final class EclipseProjectHelper {
    private static final String CODENVY_PROJECT_LAYOUT_MARKER_ID = "com.codenvy.eclipse.core.codenvyProjectLayoutMarker";

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

        final SubMonitor subMonitor = SubMonitor.convert(monitor, "Create project " + metaProject.projectName, 1);
        final IProject newProject = ResourcesPlugin.getWorkspace().getRoot().getProject(metaProject.projectName);

        try (ZipInputStream zipInputStream = stream) {

            if (!newProject.exists()) {
                newProject.create(subMonitor);
                newProject.open(subMonitor);

                createOrUpdateResourcesFromZip(stream, newProject, subMonitor);

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

            subMonitor.worked(1);

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
     * @param codenvyProject the {@link Project}.
     * @param resource the {@link IResource} to update in Codenvy.
     * @param projectService the {@link ProjectClient} instance.
     * @param monitor the {@link IProgressMonitor} or {@code null} if none.
     * @throws NullPointerException if codenvyProject, resource or projectService is {@code null}.
     */
    public static void updateCodenvyProjectResource(Project codenvyProject,
                                                    IResource resource,
                                                    ProjectClient projectService,
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

                        projectService.updateFile(codenvyProject, file.getProjectRelativePath().toString(), file.getContents());

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
     * Updates the given {@link IResource} from codenvy (no resource are deleted).
     * 
     * @param codenvyProject the {@link Project}.
     * @param resource the {@link IResource} to update in Codenvy.
     * @param codeny the {@link Codenvy} client instance.
     * @param monitor the {@link IProgressMonitor} or {@code null} if none.
     * @throws NullPointerException if codenvyProject, resource or codenvy parameter is {@code null}.
     */
    public static void updateIResource(Project codenvyProject,
                                       IResource resource,
                                       Codenvy codenvy,
                                       IProgressMonitor monitor) {
        checkNotNull(codenvyProject);
        checkNotNull(resource);
        checkNotNull(codenvy);

        final SubMonitor subMonitor = SubMonitor.convert(monitor, "Update " + resource.getName(), 1);

        try {

            final String resourceRelativePath = resource.getProjectRelativePath().toString();

            switch (resource.getType()) {
                case IResource.FILE: {
                    final InputStream stream = codenvy.project()
                                                      .getFile(codenvyProject, resourceRelativePath)
                                                      .execute();

                    ((IFile)resource).setContents(stream, true, true, monitor);
                }
                    break;

                case IResource.FOLDER:
                case IResource.PROJECT: {
                    final ZipInputStream stream = codenvy.project()
                                                         .exportResources(codenvyProject, resourceRelativePath)
                                                         .execute();

                    createOrUpdateResourcesFromZip(stream, (IContainer)resource, subMonitor);
                }
                    break;
            }

            subMonitor.worked(1);

        } catch (CoreException e) {
            throw new RuntimeException(e);

        } finally {
            subMonitor.done();
        }
    }

    /**
     * Checks that the codenvy {@link IProject} layout is valid.
     * 
     * @param project the codenvy project.
     */
    public static void checkCodenvyProjectLayout(IProject project) {
        final IFolder codenvyFolder = project.getFolder(".codenvy");
        final IFile codenvyProjectFile = codenvyFolder.getFile("project");
        final IFile codenvyTeamFile = codenvyFolder.getFile("team");

        try {
            project.deleteMarkers(CODENVY_PROJECT_LAYOUT_MARKER_ID, true, IResource.DEPTH_INFINITE);

            if (!codenvyFolder.exists()) {
                final IMarker marker = project.createMarker(CODENVY_PROJECT_LAYOUT_MARKER_ID);
                marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
                marker.setAttribute(IMarker.MESSAGE, ".codenvy folder is missing");
            }

            if (!codenvyProjectFile.exists()) {
                final IMarker marker = codenvyFolder.createMarker(CODENVY_PROJECT_LAYOUT_MARKER_ID);
                marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
                marker.setAttribute(IMarker.MESSAGE, "project file is missing");
            }

            if (!codenvyTeamFile.exists()) {
                final IMarker marker = codenvyFolder.createMarker(CODENVY_PROJECT_LAYOUT_MARKER_ID);
                marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
                marker.setAttribute(IMarker.MESSAGE, "team file is missing");
            }
        } catch (CoreException e) {
            throw new RuntimeException(e);
        }
    }

    private static void createOrUpdateResourcesFromZip(ZipInputStream stream, IContainer container, IProgressMonitor monitor) {
        final SubMonitor subMonitor = SubMonitor.convert(monitor);
        subMonitor.setTaskName("Create resources");

        try (ZipInputStream zipInputStream = stream) {

            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                subMonitor.setWorkRemaining(1000);

                final String entryName = entry.getName();

                if (entry.isDirectory()) {
                    final IFolder folder = container.getFolder(new Path(entryName));
                    if (!folder.exists()) {
                        folder.create(true, true, subMonitor);
                    }

                } else {
                    int b;
                    final IFile file = container.getFile(new Path(entryName));
                    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                    while ((b = zipInputStream.read()) != -1) {
                        byteArrayOutputStream.write(b);
                    }

                    final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                    if (file.exists()) {
                        file.setContents(byteArrayInputStream, true, true, subMonitor);
                    } else {
                        file.create(byteArrayInputStream, true, subMonitor);
                    }
                }

                subMonitor.worked(1);
            }

        } catch (CoreException | IOException e) {
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
