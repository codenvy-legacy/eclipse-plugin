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

import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipInputStream;

import org.eclipse.core.resources.IResource;

import com.codenvy.eclipse.core.model.CodenvyProject;

/**
 * Codenvy project service contract.
 * 
 * @author Kevin Pollet
 */
public interface ProjectService extends RestServiceWithAuth {
    /**
     * Retrieves all workspace projects.
     * 
     * @param workspaceId the workspace id.
     * @return the workspace project list never {@code null}.
     * @throws NullPointerException if workspaceId parameter is {@code null}.
     * @throws IllegalArgumentException if workspaceId parameter is an empty {@linkplain String}.
     */
    List<CodenvyProject> getWorkspaceProjects(String workspaceId);

    /**
     * Creates a project in the given workspace.
     * 
     * @param project the project to create.
     * @param workspaceId the workspace id.
     * @return the new project, never {@code null}.
     * @throws NullPointerException if project or workspaceId parameter is {@code null}.
     * @throws IllegalArgumentException if workspaceId parameter is an empty {@linkplain String}.
     */
    CodenvyProject newProject(CodenvyProject project, String workspaceId);

    /**
     * Exports a resource in the given project.
     * 
     * @param project the project.
     * @param workspaceId the workspace id.
     * @param resourcePath the path of the resource to export, must be a folder.
     * @return the resource {@link ZipInputStream} or {@code null} if the resource is not found.
     * @throws NullPointerException if project, workspaceId parameter is {@code null}.
     * @throws IllegalArgumentException if workspaceId parameter is an empty {@linkplain String}.
     */
    ZipInputStream exportResources(CodenvyProject project, String workspaceId, String resourcePath);

    /**
     * Updates a resource in the given project.
     * 
     * @param project the project.
     * @param workspaceId the workspace id.
     * @param filePath the path to the file to update.
     * @param fileInputStream the file {@link InputStream}.
     * @throws NullPointerException if project, workspaceId, filePath or fileInputStream parameter is {@code null}.
     * @throws IllegalArgumentException if workspaceId or filePath parameter is an empty {@linkplain String}.
     */
    void updateFile(CodenvyProject project, String workspaceId, String filePath, InputStream fileInputStream);

    /**
     * Gets file content in the given project.
     * 
     * @param project the project.
     * @param workspaceId the workspace id.
     * @param filePath the file path.
     * @return the file {@link InputStream} or {@code null} if not found.
     */
    InputStream getFile(CodenvyProject project, String workspaceId, String filePath);

    /**
     * Returns if the given resource exists in the given codenvy project.
     * 
     * @param project the Codenvy project.
     * @param workspaceId the workspace id.
     * @param resource the resource to update.
     * @return {@code true} if the given resource exists in the codenvy project, {@code false} otherwise.
     * @throws NullPointerException if project, workspaceId or resource parameter is {@code null}.
     * @throws IllegalArgumentException if workspaceId parameter is an empty {@linkplain String}.
     */
    // TODO workaround to check if a resource exists
    boolean isResourceInProject(CodenvyProject project, String workspaceId, IResource resource);
}
