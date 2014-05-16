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

import com.codenvy.eclipse.core.model.CodenvyWorkspace;
import com.codenvy.eclipse.core.model.CodenvyWorkspace.WorkspaceRef;

/**
 * Codenvy workspace service contract.
 * 
 * @author Kevin Pollet
 */
public interface WorkspaceService extends RestServiceWithAuth {
    /**
     * Retrieves all Codenvy workspaces of the user identified by the authentication token.
     * 
     * @return all Codenvy workspaces never {@code null}.
     */
    List<CodenvyWorkspace> getAllWorkspaces();

    /**
     * Retrieves a Codenvy workspace by it's name.
     * 
     * @param name the workspace name.
     * @return the Codenvy workspace or {@code null} if none.
     * @throws NullPointerException if name parameter is {@code null}.
     * @throws IllegalArgumentException if name parameter is an empty {@linkplain String}.
     */
    WorkspaceRef getWorkspaceByName(String name);

    /**
     * Finds Codenvy workspaces of the given account.
     * 
     * @param accountId the account id.
     * @return the Codenvy workspace list never {@code null}.
     * @throws NullPointerException if accountId parameter is {@code null}.
     * @throws IllegalArgumentException if accountId parameter is an empty {@linkplain String}.
     */
    List<WorkspaceRef> findWorkspacesByAccount(String accountId);

    /**
     * Creates the given workspace.
     * 
     * @param workspaceRef the workspace to create.
     * @return the created workspace.
     */
    WorkspaceRef newWorkspace(WorkspaceRef workspaceRef);
}
