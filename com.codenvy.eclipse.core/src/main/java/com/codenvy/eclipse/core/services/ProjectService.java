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
package com.codenvy.eclipse.core.services;

import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipInputStream;

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
     * @return the new project, never {@code null}.
     * @throws NullPointerException if project parameter is {@code null}.
     */
    CodenvyProject newProject(CodenvyProject project);

    /**
     * Exports a resource in the given project.
     * 
     * @param project the project.
     * @param resourcePath the path of the resource to export, must be a folder.
     * @return the resource {@link ZipInputStream} or {@code null} if the resource is not found.
     * @throws NullPointerException if project parameter is {@code null}.
     */
    ZipInputStream exportResources(CodenvyProject project, String resourcePath);

    /**
     * Updates a resource in the given project.
     * 
     * @param project the project.
     * @param filePath the path to the file to update.
     * @param fileInputStream the file {@link InputStream}.
     * @throws NullPointerException if project, filePath or fileInputStream parameter is {@code null}.
     * @throws IllegalArgumentException if filePath parameter is an empty {@linkplain String}.
     */
    void updateFile(CodenvyProject project, String filePath, InputStream fileInputStream);

    /**
     * Gets file content in the given project.
     * 
     * @param project the project.
     * @param filePath the file path.
     * @return the file {@link InputStream} or {@code null} if not found.
     */
    InputStream getFile(CodenvyProject project, String filePath);

    /**
     * Returns if the given resource exists in the given codenvy project.
     * 
     * @param project the Codenvy project.
     * @param resource the resource path.
     * @return {@code true} if the given resource exists in the codenvy project, {@code false} otherwise.
     * @throws NullPointerException if project or resourcePath parameter is {@code null}.
     * @throws IllegalArgumentException if resourcePath parameter is an empty {@code String}.
     */
    // TODO workaround to check if a resource exists
    boolean isResourceInProject(CodenvyProject project, String resourcePath);
}
