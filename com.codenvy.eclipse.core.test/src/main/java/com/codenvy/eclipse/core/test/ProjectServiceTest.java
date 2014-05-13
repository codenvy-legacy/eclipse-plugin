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

import org.eclipse.core.resources.IProject;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import com.codenvy.eclipse.core.ProjectService;
import com.codenvy.eclipse.core.RestServiceFactory;
import com.codenvy.eclipse.core.WorkspaceService;
import com.codenvy.eclipse.core.model.CodenvyToken;
import com.codenvy.eclipse.core.model.CodenvyProject;
import com.codenvy.eclipse.core.model.CodenvyWorkspace.WorkspaceRef;

/**
 * Test the project service.
 * 
 * @author Kevin Pollet
 */
public class ProjectServiceTest extends RestApiBaseTest {
    private static ProjectService   projectService;
    private static WorkspaceService workspaceService;
    private static WorkspaceRef     defaultWorkspace;
    private static CodenvyProject          projectPrj1;

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

        projectPrj1 = new CodenvyProject(null, null, "jar", null, null, "prj1", "description", defaultWorkspace.name, null, null, null);
        Assert.assertNotNull(projectService.newProject(projectPrj1, defaultWorkspace.id));
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
    @Ignore
    public void testImportProject() {
        final IProject project = projectService.importProject(projectPrj1, defaultWorkspace.id);

        Assert.assertNotNull(project);
    }
}
