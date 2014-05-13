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

import java.util.List;

import org.eclipse.core.resources.IProject;
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
     * Imports given project from the given workspace into Eclipse.
     * 
     * @param project the project to export.
     * @param workspaceId the workspace id.
     * @return the imported project as an instance of {@linkplain IProject}.
     * @throws NullPointerException if project or workspaceId parameter is {@code null}.
     * @throws IllegalArgumentException if workspaceId parameter is an empty {@linkplain String}.
     */
    IProject importCodenvyProject(CodenvyProject project, String workspaceId);

    /**
     * Updates the given resource in the given project of the given workspace.
     * 
     * @param project the Codenvy project.
     * @param workspaceId the workspace id.
     * @param resource the resource to update.
     * @throws NullPointerException if project, workspaceId or resource parameter is {@code null}.
     * @throws IllegalArgumentException if workspaceId parameter is an empty {@linkplain String}.
     */
    void updateCodenvyResource(CodenvyProject project, String workspaceId, IResource resource);

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
    // TODO workaround to check if a resource exists in codenvy
    boolean isCodenvyResource(CodenvyProject project, String workspaceId, IResource resource);
}
