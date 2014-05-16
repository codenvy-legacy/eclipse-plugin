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
package com.codenvy.eclipse.core.test;

import java.util.List;
import java.util.zip.ZipInputStream;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import com.codenvy.eclipse.core.model.CodenvyProject;
import com.codenvy.eclipse.core.model.CodenvyToken;
import com.codenvy.eclipse.core.model.CodenvyWorkspace.WorkspaceRef;
import com.codenvy.eclipse.core.services.ProjectService;
import com.codenvy.eclipse.core.services.RestServiceFactory;
import com.codenvy.eclipse.core.services.WorkspaceService;

/**
 * Test the project service.
 * 
 * @author Kevin Pollet
 */
public class ProjectServiceTest extends RestApiBaseTest {
    private static ProjectService   projectService;
    private static WorkspaceService workspaceService;
    private static WorkspaceRef     defaultWorkspace;
    private static CodenvyProject   projectPrj1;

    @BeforeClass
    public static void initialize() {
        final BundleContext context = FrameworkUtil.getBundle(ProjectServiceTest.class).getBundleContext();
        final ServiceReference<RestServiceFactory> restServiceFactoryRef = context.getServiceReference(RestServiceFactory.class);
        Assert.assertNotNull(restServiceFactoryRef);

        final RestServiceFactory restServiceFactory = context.getService(restServiceFactoryRef);
        Assert.assertNotNull(restServiceFactory);

        projectService = restServiceFactory.newRestServiceWithAuth(ProjectService.class, REST_API_URL, new CodenvyToken("dummy"));
        Assert.assertNotNull(projectService);

        workspaceService = restServiceFactory.newRestServiceWithAuth(WorkspaceService.class, REST_API_URL, new CodenvyToken("dummy"));
        Assert.assertNotNull(workspaceService);

        defaultWorkspace = workspaceService.getWorkspaceByName("default");
        Assert.assertNotNull(defaultWorkspace);

        projectPrj1 = new CodenvyProject.Builder().withProjectTypeId("maven")
                                                  .withName("prj1")
                                                  .withDescription("description")
                                                  .withWorkspaceId(defaultWorkspace.id)
                                                  .withWorkspaceName(defaultWorkspace.name)
                                                  .build();

        Assert.assertNotNull(projectService.newProject(projectPrj1));
    }

    @Test(expected = NullPointerException.class)
    public void testGetWorkspaceProjectsWithNull() {
        projectService.getWorkspaceProjects(null);
    }

    @Test
    public void testGetWorkspaceProjects() {
        final List<CodenvyProject> projects = projectService.getWorkspaceProjects(defaultWorkspace.id);

        Assert.assertNotNull(projects);
        Assert.assertFalse(projects.isEmpty());
        Assert.assertTrue(projects.size() == 1);
    }

    @Test
    public void testExportResources() {
        final ZipInputStream zipInputStream = projectService.exportResources(projectPrj1, null);

        Assert.assertNotNull(zipInputStream);
    }
}
