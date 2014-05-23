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

import com.codenvy.eclipse.core.model.Credentials;
import com.codenvy.eclipse.core.model.Token;
import com.codenvy.eclipse.core.model.Workspace;
import com.codenvy.eclipse.core.model.Workspace.WorkspaceRef;
import com.codenvy.eclipse.core.services.RestServiceFactory;
import com.codenvy.eclipse.core.services.SecureStorageService;
import com.codenvy.eclipse.core.services.UserService;
import com.codenvy.eclipse.core.services.WorkspaceService;

/**
 * {@link WorkspaceService} tests.
 * 
 * @author Kevin Pollet
 * @author Stéphane Daviet
 */
public class WorkspaceServiceIT extends RestApiBaseIT {
    private static final String     DUMMY_USERNAME     = "dummyUsername";
    private static final String     DUMMY_PASSWORD     = "dummyPassword";
    private static final String     SDK_TOKEN_VALUE    = "123123";
    private static final String     SDK_WORKSPACE_NAME = "default";

    private static WorkspaceService workspaceService;
    private static UserService      userService;

    @BeforeClass
    public static void initialize() {
        final BundleContext context = FrameworkUtil.getBundle(WorkspaceServiceIT.class).getBundleContext();
        final ServiceReference<RestServiceFactory> restServiceFactoryRef = context.getServiceReference(RestServiceFactory.class);
        Assert.assertNotNull(restServiceFactoryRef);

        final RestServiceFactory restServiceFactory = context.getService(restServiceFactoryRef);
        Assert.assertNotNull(restServiceFactory);

        workspaceService =
                           restServiceFactory.newRestServiceWithAuth(WorkspaceService.class, REST_API_URL,
                                                                     DUMMY_USERNAME);
        Assert.assertNotNull(workspaceService);

        userService = restServiceFactory.newRestServiceWithAuth(UserService.class, REST_API_URL, DUMMY_USERNAME);
        Assert.assertNotNull(userService);

        final ServiceReference<SecureStorageService> secureStorageServiceRef = context.getServiceReference(SecureStorageService.class);
        Assert.assertNotNull(secureStorageServiceRef);

        final SecureStorageService secureStorageService = context.getService(secureStorageServiceRef);
        Assert.assertNotNull(secureStorageService);

        secureStorageService.storeCredentials(REST_API_URL, new Credentials(DUMMY_USERNAME, DUMMY_PASSWORD),
                                              new Token(SDK_TOKEN_VALUE));
    }

    @Test
    public void testGetAllWorkspaces() {
        final List<Workspace> workspaces = workspaceService.getAllWorkspaces();

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
    public void testNewWorkspaceWithNullCodenvyWorkspaceRef() {
        workspaceService.newWorkspace(null);
    }
}
