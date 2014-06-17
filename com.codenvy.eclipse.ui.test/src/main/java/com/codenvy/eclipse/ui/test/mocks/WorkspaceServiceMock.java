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

import java.util.ArrayList;
import java.util.List;

import com.codenvy.eclipse.client.WorkspaceClient;
import com.codenvy.eclipse.client.model.Workspace;
import com.codenvy.eclipse.client.model.Workspace.WorkspaceRef;

/**
 * {@link WorkspaceClient} mock.
 * 
 * @author Kevin Pollet
 * @author Stéphane Daviet
 */
public class WorkspaceServiceMock implements WorkspaceClient {
    public static final String    MOCK_WORKSPACE_ID   = "ws1-id";
    public static final String    MOCK_WORKSPACE_NAME = "ws1";

    private final List<Workspace> workspaces;

    public WorkspaceServiceMock(String url, String username) {
        this.workspaces = new ArrayList<>();
        this.workspaces.add(new Workspace(new WorkspaceRef(MOCK_WORKSPACE_ID, MOCK_WORKSPACE_NAME, "codenvy-organization")));
        this.workspaces.add(new Workspace(new WorkspaceRef("ws2-id", "ws2", "codenvy-organization")));
        this.workspaces.add(new Workspace(new WorkspaceRef("ws3-id", "ws3", "codenvy-organization")));
        this.workspaces.add(new Workspace(new WorkspaceRef("ws4-id", "ws4", "codenvy-organization")));
    }

    @Override
    public List<Workspace> all() {
        return workspaces;
    }

    @Override
    public WorkspaceRef withName(String name) {
        for (Workspace workspace : workspaces) {
            if (workspace.workspaceRef.name.equals(name)) {
                return workspace.workspaceRef;
            }
        }
        return null;
    }

    @Override
    public WorkspaceRef create(WorkspaceRef workspaceRef) {
        throw new UnsupportedOperationException();
    }
}
