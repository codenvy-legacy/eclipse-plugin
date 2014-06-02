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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.List;
import java.util.zip.ZipInputStream;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import com.codenvy.eclipse.core.model.CodenvyCredentials;
import com.codenvy.eclipse.core.model.CodenvyProject;
import com.codenvy.eclipse.core.model.CodenvyToken;
import com.codenvy.eclipse.core.model.CodenvyWorkspace.WorkspaceRef;
import com.codenvy.eclipse.core.services.ProjectService;
import com.codenvy.eclipse.core.services.RestServiceFactory;
import com.codenvy.eclipse.core.services.SecureStorageService;
import com.codenvy.eclipse.core.services.WorkspaceService;

/**
 * {@link ProjectService} tests.
 * 
 * @author Kevin Pollet
 * @author Stéphane Daviet
 */
public class ProjectServiceIT extends RestApiBaseIT {
    private static final String     DUMMY_USERNAME  = "dummyUsername";
    private static final String     DUMMY_PASSWORD  = "dummyPassword";
    private static final String     SDK_TOKEN_VALUE = "123123";

    private static ProjectService   projectService;
    private static WorkspaceService workspaceService;
    private static WorkspaceRef     defaultWorkspace;
    private static CodenvyProject   projectPrj1;

    @BeforeClass
    public static void initialize() {
        final BundleContext context = FrameworkUtil.getBundle(ProjectServiceIT.class).getBundleContext();
        final ServiceReference<RestServiceFactory> restServiceFactoryRef = context.getServiceReference(RestServiceFactory.class);
        Assert.assertNotNull(restServiceFactoryRef);

        final RestServiceFactory restServiceFactory = context.getService(restServiceFactoryRef);
        Assert.assertNotNull(restServiceFactory);

        final ServiceReference<SecureStorageService> secureStorageServiceRef = context.getServiceReference(SecureStorageService.class);
        Assert.assertNotNull(secureStorageServiceRef);

        final SecureStorageService secureStorageService = context.getService(secureStorageServiceRef);
        Assert.assertNotNull(secureStorageService);

        secureStorageService.storeCredentials(REST_API_URL, new CodenvyCredentials(DUMMY_USERNAME, DUMMY_PASSWORD),
                                              new CodenvyToken(SDK_TOKEN_VALUE));

        projectService = restServiceFactory.newRestServiceWithAuth(ProjectService.class, REST_API_URL, DUMMY_USERNAME);
        Assert.assertNotNull(projectService);

        workspaceService = restServiceFactory.newRestServiceWithAuth(WorkspaceService.class, REST_API_URL, DUMMY_USERNAME);
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

        // Add a file to the project
        final URI uri = UriBuilder.fromUri(REST_API_URL).path("api/project").build();
        final WebTarget webTarget = ClientBuilder.newClient().target(uri);
        webTarget.path(projectPrj1.workspaceId)
                 .path("file")
                 .path(projectPrj1.name)
                 .queryParam("name", "file.txt")
                 .request()
                 .post(Entity.text(ProjectServiceIT.class.getResourceAsStream("/file.txt")));

    }

    @Test(expected = NullPointerException.class)
    public void testGetWorkspaceProjectsWithNullWorkspaceId() {
        projectService.getWorkspaceProjects(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetWorkspaceProjectsWithEmptyWorkspaceId() {
        projectService.getWorkspaceProjects("");
    }

    @Test
    public void testGetWorkspaceProjects() {
        final List<CodenvyProject> projects = projectService.getWorkspaceProjects(defaultWorkspace.id);

        Assert.assertNotNull(projects);
        Assert.assertFalse(projects.isEmpty());
        Assert.assertTrue(projects.size() == 1);
    }

    @Test(expected = NullPointerException.class)
    public void testNewProjectWithNullProject() {
        projectService.newProject(null);
    }

    @Test(expected = NullPointerException.class)
    public void testExportResourcesWithNullProject() {
        projectService.exportResources(null, null);
    }

    @Test
    public void testExportResources() {
        final ZipInputStream zipInputStream = projectService.exportResources(projectPrj1, null);

        Assert.assertNotNull(zipInputStream);
    }

    @Test(expected = NullPointerException.class)
    public void testUpdateFileWithNullProject() {
        projectService.updateFile(null, "dummyPath", new ByteArrayInputStream(new byte[0]));
    }

    @Test(expected = NullPointerException.class)
    public void testUpdateFileWithNullFilePath() {
        projectService.updateFile(projectPrj1, null, new ByteArrayInputStream(new byte[0]));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateFileWithEmptyFilePath() {
        projectService.updateFile(projectPrj1, "", new ByteArrayInputStream(new byte[0]));
    }

    @Test(expected = NullPointerException.class)
    public void testUpdateFileWithNullInputStream() {
        projectService.updateFile(projectPrj1, "dummyPath", null);
    }

    @Test(expected = NullPointerException.class)
    public void testGetFileWithNullProject() {
        projectService.getFile(null, "dummyPath");
    }

    @Test(expected = NullPointerException.class)
    public void testGetFileWithNullFilePath() {
        projectService.getFile(projectPrj1, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetFileWithEmptyFilePath() {
        projectService.getFile(projectPrj1, "");
    }

    @Test
    public void testUpdateGetFile() throws IOException {
        projectService.updateFile(projectPrj1, "file.txt", new ByteArrayInputStream("content2".getBytes()));

        final InputStream stream = projectService.getFile(projectPrj1, "file.txt");
        final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        Assert.assertEquals("content2", reader.readLine());
    }

    @Test(expected = NullPointerException.class)
    public void testIsResourceInProjectWithNullProject() {
        projectService.isResourceInProject(null, "dummyPath");
    }

    @Test(expected = NullPointerException.class)
    public void testIsResourceInProjectWithNullResourcePath() {
        projectService.isResourceInProject(projectPrj1, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsResourceInProjectWithEmptyResourcePath() {
        projectService.isResourceInProject(projectPrj1, "");
    }

    @Test
    public void testIsResourceInProject() {
        final boolean result = projectService.isResourceInProject(projectPrj1, "file.txt");

        Assert.assertTrue(result);
    }
}
