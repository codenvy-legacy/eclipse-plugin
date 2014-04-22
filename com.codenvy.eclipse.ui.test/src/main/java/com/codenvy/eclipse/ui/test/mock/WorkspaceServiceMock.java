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
package com.codenvy.eclipse.ui.test.mock;

import static com.codenvy.eclipse.ui.test.mock.UserServiceMock.MOCK_USER_ID;

import java.util.ArrayList;
import java.util.List;

import com.codenvy.eclipse.core.WorkspaceService;
import com.codenvy.eclipse.core.model.CodenvyToken;
import com.codenvy.eclipse.core.model.Workspace;
import com.codenvy.eclipse.core.model.Workspace.WorkspaceRef;

/**
 * The Codenvy workspace client service mock.
 * 
 * @author Kevin Pollet
 */
public class WorkspaceServiceMock implements WorkspaceService {
    public static final String       MOCK_WORKSPACE_ID   = "ws1-id";
    public static final String       MOCK_WORKSPACE_NAME = "ws1";

    private final List<WorkspaceRef> workspaces;

    public WorkspaceServiceMock(String url, CodenvyToken codenvyToken) {
        this.workspaces = new ArrayList<>();
        this.workspaces.add(new WorkspaceRef(MOCK_WORKSPACE_ID, MOCK_WORKSPACE_NAME, "codenvy-organization"));
        this.workspaces.add(new WorkspaceRef("ws2-id", "ws2", "codenvy-organization"));
        this.workspaces.add(new WorkspaceRef("ws3-id", "ws3", "codenvy-organization"));
        this.workspaces.add(new WorkspaceRef("ws4-id", "ws4", "codenvy-organization"));
    }

    @Override
    public List<Workspace> getAllWorkspaces() {
        throw new UnsupportedOperationException();
    }

    @Override
    public WorkspaceRef getWorkspaceByName(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<WorkspaceRef> findWorkspacesByAccount(String accountId) {
        if (MOCK_USER_ID.equals(accountId)) {
            return workspaces;
        }
        return new ArrayList<>();
    }

    @Override
    public WorkspaceRef createWorkspace(WorkspaceRef workspaceRef) {
        throw new UnsupportedOperationException();
    }
}
