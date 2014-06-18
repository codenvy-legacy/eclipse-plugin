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
package com.codenvy.eclipse.client;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.codenvy.eclipse.client.model.Workspace;
import com.codenvy.eclipse.client.model.Workspace.WorkspaceRef;

/**
 * {@linkplain com.codenvy.eclipse.client.WorkspaceClient WorkspaceService} tests.
 * 
 * @author Kevin Pollet
 * @author Stéphane Daviet
 */
public class WorkspaceClientIT extends RestClientBaseIT {
    @Test
    public void testGetAllWorkspaces() {
        final List<Workspace> workspaces = codenvy.workspace()
                                                  .all()
                                                  .execute();

        Assert.assertNotNull(workspaces);
        Assert.assertTrue(workspaces.size() > 0);
        Assert.assertNotNull(workspaces.get(0).workspaceRef);
        Assert.assertNull(workspaces.get(0).workspaceRef.id);
        Assert.assertNotNull(workspaces.get(0).workspaceRef.name);
    }

    @Test(expected = NullPointerException.class)
    public void testGetWorkspaceByNameWithNullName() {
        codenvy.workspace()
               .withName(null)
               .execute();
    }

    @Test
    public void testGetWorkspaceByName() {
        final WorkspaceRef workspaceRef = codenvy.workspace()
                                                 .withName(SDK_WORKSPACE_NAME)
                                                 .execute();

        Assert.assertNotNull(workspaceRef);
        Assert.assertNotNull(workspaceRef.id);
        Assert.assertNotNull(workspaceRef.name);
    }

    @Test(expected = NullPointerException.class)
    public void testNewWorkspaceWithNullWorkspaceRef() {
        codenvy.workspace()
               .create(null)
               .execute();
    }
}
