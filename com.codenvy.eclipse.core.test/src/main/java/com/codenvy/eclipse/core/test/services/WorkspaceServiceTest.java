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
package com.codenvy.eclipse.core.test.services;

import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import com.codenvy.eclipse.core.model.CodenvyToken;
import com.codenvy.eclipse.core.model.CodenvyUser;
import com.codenvy.eclipse.core.model.CodenvyWorkspace;
import com.codenvy.eclipse.core.model.CodenvyWorkspace.WorkspaceRef;
import com.codenvy.eclipse.core.services.RestServiceFactory;
import com.codenvy.eclipse.core.services.UserService;
import com.codenvy.eclipse.core.services.WorkspaceService;

/**
 * {@link WorkspaceService} test.
 * 
 * @author Kevin Pollet
 */
public class WorkspaceServiceTest extends RestApiBaseTest {
    private static final String     SDK_TOKEN_VALUE    = "123123";
    private static final String     SDK_WORKSPACE_NAME = "default";

    private static WorkspaceService workspaceService;
    private static UserService      userService;

    @BeforeClass
    public static void initialize() {
        final BundleContext context = FrameworkUtil.getBundle(WorkspaceServiceTest.class).getBundleContext();
        final ServiceReference<RestServiceFactory> restServiceFactoryRef = context.getServiceReference(RestServiceFactory.class);
        Assert.assertNotNull(restServiceFactoryRef);

        final RestServiceFactory restServiceFactory = context.getService(restServiceFactoryRef);
        Assert.assertNotNull(restServiceFactory);

        workspaceService =
                           restServiceFactory.newRestServiceWithAuth(WorkspaceService.class, REST_API_URL,
                                                                     new CodenvyToken(SDK_TOKEN_VALUE));
        Assert.assertNotNull(workspaceService);

        userService = restServiceFactory.newRestServiceWithAuth(UserService.class, REST_API_URL, new CodenvyToken(SDK_TOKEN_VALUE));
        Assert.assertNotNull(userService);
    }

    @Test
    public void testGetAllWorkspaces() {
        final List<CodenvyWorkspace> workspaces = workspaceService.getAllWorkspaces();

        Assert.assertNotNull(workspaces);
        Assert.assertTrue(workspaces.size() > 0);
        Assert.assertNotNull(workspaces.get(0).workspaceRef);
        Assert.assertNull(workspaces.get(0).workspaceRef.id);
        Assert.assertNotNull(workspaces.get(0).workspaceRef.name);
    }

    @Test(expected = NullPointerException.class)
    public void testGetWorkspaceByNameWithNull() {
        workspaceService.getWorkspaceByName(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetWorkspaceByNameWithEmptyName() {
        workspaceService.getWorkspaceByName("");
    }

    @Test
    public void testGetWorkspaceByName() {
        final WorkspaceRef workspaceRef = workspaceService.getWorkspaceByName(SDK_WORKSPACE_NAME);

        Assert.assertNotNull(workspaceRef);
        Assert.assertNotNull(workspaceRef.id);
        Assert.assertNotNull(workspaceRef.name);
    }

    @Test(expected = NullPointerException.class)
    public void testFindWorkspacesByAccountWithNullAccountId() {
        workspaceService.findWorkspacesByAccount(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindWorkspacesByAccountWithEmptyAccountId() {
        workspaceService.findWorkspacesByAccount("");
    }

    @Test
    public void testFindWorkspacesByAccount() {
        final CodenvyUser currentUser = userService.getCurrentUser();
        Assert.assertNotNull(currentUser);

        final List<WorkspaceRef> workspaces = workspaceService.findWorkspacesByAccount(currentUser.id);

        Assert.assertNotNull(workspaces);
        Assert.assertTrue(workspaces.size() > 0);
        Assert.assertNotNull(workspaces.get(0).id);
        Assert.assertNotNull(workspaces.get(0).name);
    }

    @Test(expected = NullPointerException.class)
    public void testNewWorkspaceWithNullWorkspaceRef() {
        workspaceService.newWorkspace(null);
    }
}
