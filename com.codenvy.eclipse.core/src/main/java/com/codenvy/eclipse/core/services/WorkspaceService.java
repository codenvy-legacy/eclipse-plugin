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

import java.util.List;

import com.codenvy.eclipse.core.model.Workspace;
import com.codenvy.eclipse.core.model.Workspace.WorkspaceRef;

/**
 * Codenvy workspace service contract.
 * 
 * @author Kevin Pollet
 * @author Stéphane Daviet
 */
public interface WorkspaceService extends RestServiceWithAuth {
    /**
     * Retrieves all Codenvy workspaces of the user identified by the authentication token.
     * 
     * @return all Codenvy workspaces never {@code null}.
     */
    List<Workspace> getAllWorkspaces();

    /**
     * Retrieves a Codenvy workspace by it's name.
     * 
     * @param name the workspace name.
     * @return the Codenvy workspace or {@code null} if none.
     * @throws NullPointerException if name parameter is {@code null}.
     * @throws IllegalArgumentException if name parameter is an empty {@link String}.
     */
    WorkspaceRef getWorkspaceByName(String name);

    /**
     * Creates the given workspace.
     * 
     * @param workspaceRef the workspace to create.
     * @return the created workspace.
     * @throws NullPointerException if {@link WorkspaceRef} parameter is {@code null}.
     */
    WorkspaceRef newWorkspace(WorkspaceRef workspaceRef);
}
