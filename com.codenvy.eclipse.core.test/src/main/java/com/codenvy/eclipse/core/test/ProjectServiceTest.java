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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import com.codenvy.eclipse.core.service.api.ProjectService;
import com.codenvy.eclipse.core.service.api.RestServiceFactory;
import com.codenvy.eclipse.core.service.api.WorkspaceService;
import com.codenvy.eclipse.core.service.api.model.CodenvyToken;
import com.codenvy.eclipse.core.service.api.model.Project;
import com.codenvy.eclipse.core.service.api.model.Workspace.WorkspaceRef;

/**
 * Test the project service.
 * 
 * @author Kevin Pollet
 */
public class ProjectServiceTest extends RestApiBaseTest {
    private ProjectService   projectService;
    private WorkspaceService workspaceService;

    @Before
    public void initialize() {
        final BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();
        final ServiceReference<RestServiceFactory> restServiceFactoryRef = context.getServiceReference(RestServiceFactory.class);
        Assert.assertNotNull(restServiceFactoryRef);

        final RestServiceFactory restServiceFactory = context.getService(restServiceFactoryRef);
        Assert.assertNotNull(restServiceFactory);

        projectService = restServiceFactory.newRestServiceWithAuth(ProjectService.class, REST_API_URL, new CodenvyToken("dummy"));
        workspaceService = restServiceFactory.newRestServiceWithAuth(WorkspaceService.class, REST_API_URL, new CodenvyToken("dummy"));
    }

    @Test(expected = NullPointerException.class)
    public void testGetWorkspaceProjectsWithNull() {
        projectService.getWorkspaceProjects(null);
    }

    @Test
    public void testGetWorkspaceProjects() {
        final WorkspaceRef defaultWorkspace = workspaceService.getWorkspaceByName("default");
        Assert.assertNotNull(defaultWorkspace);

        final Project project = new Project(null, null, "jar", null, null, "jar-project", "description", defaultWorkspace.name, null, null, null);
        final Project createdProject = projectService.newProject(project, defaultWorkspace.id);
        Assert.assertNotNull(createdProject);

        final List<Project> projects = projectService.getWorkspaceProjects(defaultWorkspace.id);

        Assert.assertNotNull(projects);
        Assert.assertFalse(projects.isEmpty());
        Assert.assertEquals("jar-project", projects.get(0).name);
        Assert.assertEquals("jar", projects.get(0).projectTypeId);
        Assert.assertEquals(defaultWorkspace.id, projects.get(0).workspaceId);
    }
}
