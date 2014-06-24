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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
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

import com.codenvy.eclipse.client.model.Project;
import com.codenvy.eclipse.client.model.Workspace.WorkspaceRef;

/**
 * {@linkplain com.codenvy.eclipse.client.ProjectClient ProjectService} tests.
 *
 * @author Kevin Pollet
 * @author Stéphane Daviet
 */
public class ProjectClientIT extends AbstractIT {
    private static WorkspaceRef defaultWorkspace;
    private static Project      projectPrj1;

    @BeforeClass
    public static void initialize() {
        defaultWorkspace = codenvy.workspace()
                                  .withName(SDK_WORKSPACE_NAME)
                                  .execute();

        Assert.assertNotNull(defaultWorkspace);

        projectPrj1 = new Project.Builder().withProjectTypeId("maven")
                                           .withName("prj1")
                                           .withDescription("description")
                                           .withWorkspaceId(defaultWorkspace.id)
                                           .withWorkspaceName(defaultWorkspace.name)
                                           .build();

        codenvy.project()
               .create(projectPrj1)
               .execute();

        Assert.assertNotNull(projectPrj1);


        final URI uri = UriBuilder.fromUri(REST_API_URL).path("api/project").build();
        final WebTarget webTarget = ClientBuilder.newClient().target(uri);
        webTarget.path(projectPrj1.workspaceId)
                 .path("folder")
                 .path(projectPrj1.name)
                 .path("src")
                 .request()
                 .post(null);

        webTarget.path(projectPrj1.workspaceId)
                 .path("file")
                 .path(projectPrj1.name)
                 .queryParam("name", "src/file.txt")
                 .request()
                 .post(Entity.text(ProjectClientIT.class.getResourceAsStream("/file.txt")));

    }

    @Test(expected = NullPointerException.class)
    public void testGetWorkspaceProjectsWithNullWorkspaceId() {
        codenvy.project()
               .getWorkspaceProjects(null)
               .execute();
    }

    @Test
    public void testGetWorkspaceProjects() {
        final List<Project> projects = codenvy.project()
                                              .getWorkspaceProjects(defaultWorkspace.id)
                                              .execute();

        Assert.assertNotNull(projects);
        Assert.assertFalse(projects.isEmpty());
        Assert.assertTrue(projects.size() == 1);
    }

    @Test(expected = NullPointerException.class)
    public void testCreateWithNullProject() {
        codenvy.project()
               .create(null)
               .execute();
    }

    @Test(expected = NullPointerException.class)
    public void testExportResourcesWithNullProject() {
        codenvy.project()
               .exportResources(null, null)
               .execute();
    }

    @Test
    public void testExportResources() {
        final ZipInputStream zipInputStream = codenvy.project()
                                                     .exportResources(projectPrj1, null)
                                                     .execute();

        Assert.assertNotNull(zipInputStream);
    }

    @Test(expected = NullPointerException.class)
    public void testImportArchiveWithNullWorkspace() {
        codenvy.project()
               .importArchive(null,
                              projectPrj1,
                              ProjectClientIT.class.getResourceAsStream("/archiveToImport.zip"))
               .execute();
    }

    @Test(expected = NullPointerException.class)
    public void testImportArchiveWithNullProject() {
        codenvy.project()
               .importArchive(defaultWorkspace,
                              null,
                              ProjectClientIT.class.getResourceAsStream("/archiveToImport.zip"))
               .execute();
    }

    @Test(expected = NullPointerException.class)
    public void testImportArchiveWithNullInputStream() {
        codenvy.project()
               .importArchive(defaultWorkspace,
                              projectPrj1,
                              null)
               .execute();
    }

    @Test
    public void testImportArchive() throws FileNotFoundException, IOException {
        codenvy.project()
               .importArchive(defaultWorkspace,
                              projectPrj1,
                              ProjectClientIT.class.getResourceAsStream("/archiveToImport.zip"))
               .execute();

        Assert.assertTrue(codenvy.project().isResource(projectPrj1, "/fileToImport.txt").execute());
    }


    @Test(expected = NullPointerException.class)
    public void testUpdateFileWithNullProject() {
        codenvy.project()
               .updateFile(null, "dummyPath", new ByteArrayInputStream(new byte[0]))
               .execute();
    }

    @Test(expected = NullPointerException.class)
    public void testUpdateFileWithNullFilePath() {
        codenvy.project()
               .updateFile(projectPrj1, null, new ByteArrayInputStream(new byte[0]))
               .execute();
    }

    @Test(expected = NullPointerException.class)
    public void testUpdateFileWithNullInputStream() {
        codenvy.project()
               .updateFile(projectPrj1, "dummyPath", null)
               .execute();
    }

    @Test(expected = NullPointerException.class)
    public void testGetFileWithNullProject() {
        codenvy.project()
               .getFile(null, "dummyPath")
               .execute();
    }

    @Test(expected = NullPointerException.class)
    public void testGetFileWithNullFilePath() {
        codenvy.project()
               .getFile(projectPrj1, null)
               .execute();
    }

    @Test
    public void testUpdateAndGetFile() throws IOException {
        codenvy.project()
               .updateFile(projectPrj1, "src/file.txt", new ByteArrayInputStream("content2".getBytes()))
               .execute();

        final InputStream stream = codenvy.project()
                                          .getFile(projectPrj1, "src/file.txt")
                                          .execute();

        final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        Assert.assertEquals("content2", reader.readLine());
    }

    @Test(expected = NullPointerException.class)
    public void testIsResourceWithNullProject() {
        codenvy.project()
               .isResource(null, "dummyPath")
               .execute();
    }

    @Test(expected = NullPointerException.class)
    public void testIsResourceWithNullResourcePath() {
        codenvy.project()
               .isResource(projectPrj1, null)
               .execute();
    }

    @Test
    public void testIsResourceWithFile() {
        final boolean result = codenvy.project()
                                      .isResource(projectPrj1, "src/file.txt")
                                      .execute();

        Assert.assertTrue(result);
    }

    @Test
    public void testIsResourceWithFolder() {
        final boolean result = codenvy.project()
                                      .isResource(projectPrj1, "src")
                                      .execute();

        Assert.assertTrue(result);
    }
}
