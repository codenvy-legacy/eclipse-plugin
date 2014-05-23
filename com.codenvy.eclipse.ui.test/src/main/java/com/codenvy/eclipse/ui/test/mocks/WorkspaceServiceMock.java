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
package com.codenvy.eclipse.ui.test.mocks;

import static com.codenvy.eclipse.ui.test.mocks.AccountServiceMock.MOCK_ACCOUNT_ID;

import java.util.ArrayList;
import java.util.List;

import com.codenvy.eclipse.core.model.CodenvyWorkspace;
import com.codenvy.eclipse.core.model.CodenvyWorkspace.WorkspaceRef;
import com.codenvy.eclipse.core.services.WorkspaceService;

/**
 * {@link WorkspaceService} mock.
 * 
 * @author Kevin Pollet
 */
public class WorkspaceServiceMock implements WorkspaceService {
    public static final String       MOCK_WORKSPACE_ID   = "ws1-id";
    public static final String       MOCK_WORKSPACE_NAME = "ws1";

    private final List<WorkspaceRef> workspaces;

    public WorkspaceServiceMock(String url, String username) {
        this.workspaces = new ArrayList<>();
        this.workspaces.add(new WorkspaceRef(MOCK_WORKSPACE_ID, MOCK_WORKSPACE_NAME, "codenvy-organization"));
        this.workspaces.add(new WorkspaceRef("ws2-id", "ws2", "codenvy-organization"));
        this.workspaces.add(new WorkspaceRef("ws3-id", "ws3", "codenvy-organization"));
        this.workspaces.add(new WorkspaceRef("ws4-id", "ws4", "codenvy-organization"));
    }

    @Override
    public List<CodenvyWorkspace> getAllWorkspaces() {
        throw new UnsupportedOperationException();
    }

    @Override
    public WorkspaceRef getWorkspaceByName(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<WorkspaceRef> findWorkspacesByAccount(String accountId) {
        if (MOCK_ACCOUNT_ID.equals(accountId)) {
            return workspaces;
        }
        return new ArrayList<>();
    }

    @Override
    public WorkspaceRef newWorkspace(WorkspaceRef workspaceRef) {
        throw new UnsupportedOperationException();
    }
}
