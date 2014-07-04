/*******************************************************************************
 * Copyright (c) 2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.eclipse.core.utils;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

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

import com.codenvy.eclipse.client.Codenvy;
import com.codenvy.eclipse.client.model.Project;
import com.codenvy.eclipse.core.CodenvyNature;
import com.codenvy.eclipse.core.team.CodenvyMetaProject;
import com.codenvy.eclipse.core.team.CodenvyProvider;
import com.google.common.io.ByteStreams;

/**
 * Helper providing methods to work with eclipse projects.
 *
 * @author Kevin Pollet
 * @author Stéphane Daviet
 */
public final class EclipseProjectHelper {
    private static final String       CODENVY_PROJECT_LAYOUT_MARKER_ID = "com.codenvy.eclipse.core.codenvyProjectLayoutMarker";

    private static final List<String> excludedResources                = Arrays.asList(new String[]{".project",
                                                                       ".classpath",
                                                                       ".settings",
                                                                       "target",
                                                                       "bin",
                                                                       ".git"});

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
     * <p>
     * Get an {@link InputStream} from a {@link IProject}. Used to get an archive of an Eclipse project in ZIP format.
     * </p>
     * <p>
     * <strong>Don't forget to close the stream when done.</strong>
     * </p>
     * <p>
     * To extract this {@link InputStream}, the best is to promote it to a {@link ZipInputStream} through some code like
     * {@code new ZipInputStream(EclipseProjectHelper.exportIProjectToZipStream(project, monitor))}. <strong>If used as a transmitting
     * stream (other socket for instance), it should not been promoted to {@link ZipInputStream} before transmitting as the
     * {@link ZipInputStream#read()} is different from a classic {@link InputStream#read()} method in that it works only on the current
     * entry, positioned thanks to {@link ZipInputStream#getNextEntry()}. Most consumers cannot know this specificity if expecting to work
     * with classic {@link InputStream} and will fail to read the stream (for instance Jersey
     * {@code org.glassfish.jersey.message.internal.InputStreamProvider}).</strong>
     * </p>
     *
     * @param project the project to archive.
     * @param monitor the monitor for reporting the archiving progress.
     * @return a {@link ZipInputStream} corresponding to the archive of the {@link IProject}.
     * @throws NullPointerException if project is {@code null}.
     */
    public static InputStream exportIProjectToZipStream(final IProject project, final IProgressMonitor monitor) {
        checkNotNull(project);

        final SubMonitor subMonitor = SubMonitor.convert(monitor, "Export project " + project.getName(), 1);
        final AtomicBoolean writeDoneSignal = new AtomicBoolean(false);
        final CountDownLatch writeStartLock = new CountDownLatch(1);

        final PipedInputStream pipedInputStream = new PipedInputStream() {
            @Override
            public int read(byte[] b, int off, int len) throws IOException {
                try {
                    // Wait until writer get created and connected
                    writeStartLock.await();

                    // Hack to avoid premature closing of this PipedInputStream by a reader whose basing closing action on assumption that
                    // if read() send -1 the stream is to be closed (for instance InputStreamProvider of Jersey). The stream can be only
                    // closed when the thread where PipedOutputStream write is ended. Otherwise, if PipedOutputStream write ’slower’ than
                    // PipedInputStream is read, there could be underflow and read could send -1 whereas there's still some data to be
                    // written.
                    int result = super.read(b, off, len);
                    if (result == -1 && !writeDoneSignal.get()) {
                        return 0;
                    }
                    return result;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void close() throws IOException {
                super.close();
            }
        };

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final PipedOutputStream pipedOutputStream = new PipedOutputStream(pipedInputStream);
                    // Writer is on, unlock the reader
                    writeStartLock.countDown();
                    final ZipOutputStream outputStream = new ZipOutputStream(pipedOutputStream);

                    final List<IResource> files = getFiles(project);

                    for (int i = 0; i < files.size(); i++) {
                        final IResource resource = files.get(i);
                        if (excludedResources.contains(resource.getName())) {
                            continue;
                        }

                        final ZipEntry entry =
                                               new ZipEntry(resource.getProjectRelativePath().toString()
                                                            + (resource instanceof IContainer ? '/' : ""));
                        outputStream.putNextEntry(entry);

                        if (resource instanceof IFile) {
                            try (InputStream inputStream = ((IFile)resource).getContents()) {
                                ByteStreams.copy(inputStream, outputStream);
                            }
                        }
                        outputStream.flush();

                        subMonitor.worked((int)Math.floor(i / files.size()));
                    }
                    outputStream.closeEntry();
                    outputStream.flush();
                    // Flag for writing end, see hack above for PipedInputStream#read(…).
                    writeDoneSignal.set(true);
                    outputStream.close();
                } catch (CoreException | IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    subMonitor.done();
                }
            }
        });

        return pipedInputStream;
    }

    private static List<IResource> getFiles(IContainer root) throws CoreException {
        checkNotNull(root);

        List<IResource> files = new ArrayList<>();
        files.add(root);
        for (IResource file : root.members(IContainer.EXCLUDE_DERIVED)) {
            if (file instanceof IContainer) {
                files.addAll(getFiles((IContainer)file));
            } else {
                files.add(file);
            }
        }
        return files;
    }

    /**
     * Updates the given resource in the given Codenvy project.
     *
     * @param codenvyProject the {@link Project}.
     * @param resource the {@link IResource} to update in Codenvy.
     * @param codenvy the {@link Codenvy} client API.
     * @param monitor the {@link IProgressMonitor} or {@code null} if none.
     * @throws NullPointerException if codenvyProject, resource or codenvy parameter is {@code null}.
     */
    public static void updateCodenvyProjectResource(Project codenvyProject,
                                                    IResource resource,
                                                    Codenvy codenvy,
                                                    IProgressMonitor monitor) {
        checkNotNull(codenvyProject);
        checkNotNull(resource);
        checkNotNull(codenvy);

        final SubMonitor subMonitor = SubMonitor.convert(monitor);
        subMonitor.setWorkRemaining(1000);

        try {

            switch (resource.getType()) {
                case IResource.FILE: {
                    final IFile file = (IFile)resource;
                    try {

                        codenvy.project()
                               .updateFile(codenvyProject, file.getProjectRelativePath().toString(), file.getContents())
                               .execute();

                    } catch (CoreException e) {
                        throw new RuntimeException(e);
                    }

                    subMonitor.worked(1);

                }
                    break;

                case IResource.PROJECT:
                case IResource.FOLDER: {
                    final IContainer container = (IContainer)resource;
                    try {

                        for (IResource oneResource : container.members()) {
                            updateCodenvyProjectResource(codenvyProject, oneResource, codenvy, monitor);
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
     * Updates the given {@link IResource} from Codenvy (no resource are deleted).
     *
     * @param codenvyProject the {@link Project}.
     * @param resource the {@link IResource} to update in Codenvy.
     * @param codeny the {@link Codenvy} client API.
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
        final IFile codenvyProjectFile = codenvyFolder.getFile("project.json");
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

    /**
     * Create or update an {@link IContainer} content with the provided {@link ZipInputStream}.
     * 
     * @param stream the {@link ZipInputStream} containing the resource to create or update.
     * @param container the {@link IContainer} where the resources will be unzipped.
     * @param monitor the {@link IProgressMonitor} to follow work progression.
     */
    public static void createOrUpdateResourcesFromZip(ZipInputStream stream, IContainer container, IProgressMonitor monitor) {
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
