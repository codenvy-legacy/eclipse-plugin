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


import static com.codenvy.eclipse.ui.test.mocks.WorkspaceServiceMock.MOCK_WORKSPACE_ID;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.codenvy.eclipse.core.model.Project;
import com.codenvy.eclipse.core.services.ProjectService;

/**
 * {@link ProjectService} mock.
 * 
 * @author Kevin Pollet
 */
public class ProjectServiceMock implements ProjectService {
    public static final String  MOCK_PROJECT_NAME        = "prj1";
    public static final String  MOCK_PROJECT_TYPE_NAME   = "maven";
    public static final String  MOCK_PROJECT_DESCRIPTION = "prj1-description";

    private final List<Project> projects;

    public ProjectServiceMock(String url, String username) {
        this.projects = new ArrayList<>();

        final Project prj1 = new Project.Builder().withName(MOCK_PROJECT_NAME)
                                                  .withWorkspaceId(MOCK_WORKSPACE_ID)
                                                  .withProjectTypeName(MOCK_PROJECT_TYPE_NAME)
                                                  .withDescription(MOCK_PROJECT_DESCRIPTION)
                                                  .build();

        final Project prj2 = new Project.Builder().withName("prj2")
                                                  .withWorkspaceId(MOCK_WORKSPACE_ID)
                                                  .withProjectTypeName("maven")
                                                  .withDescription("prj2-description")
                                                  .build();

        final Project prj3 = new Project.Builder().withName("prj3")
                                                  .withWorkspaceId(MOCK_WORKSPACE_ID)
                                                  .withProjectTypeName("maven")
                                                  .withDescription("prj3-description")
                                                  .build();

        final Project prj4 = new Project.Builder().withName("prj4")
                                                  .withWorkspaceId(MOCK_WORKSPACE_ID)
                                                  .withProjectTypeName("maven")
                                                  .withDescription("prj4-description")
                                                  .build();

        this.projects.add(prj1);
        this.projects.add(prj2);
        this.projects.add(prj3);
        this.projects.add(prj4);
    }

    @Override
    public List<Project> getWorkspaceProjects(String workspaceId) {
        if (MOCK_WORKSPACE_ID.equals(workspaceId)) {
            return projects;
        }
        return new ArrayList<>();
    }

    @Override
    public Project newProject(Project project) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ZipInputStream exportResources(Project project, String resourcePath) {
        if (MOCK_WORKSPACE_ID.equals(project.workspaceId) && MOCK_PROJECT_NAME.equals(project.name)) {
            return new ZipInputStream(getClass().getResourceAsStream("/prj1.zip"));
        }
        return null;
    }

    @Override
    public void updateFile(Project project, String filePath, InputStream fileInputStream) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isResourceInProject(Project project, String resourcePath) {
        if (MOCK_WORKSPACE_ID.equals(project.workspaceId) && MOCK_PROJECT_NAME.equals(project.name)) {

            final InputStream in = getClass().getResourceAsStream("/prj1.zip");
            try (ZipInputStream zipIn = new ZipInputStream(in)) {

                ZipEntry entry;
                while ((entry = zipIn.getNextEntry()) != null) {
                    String entryName = entry.getName();
                    entryName = entry.isDirectory() ? entryName.substring(0, entryName.length() - 1) : entryName;

                    if (entryName.equals(resourcePath)) {
                        return true;
                    }
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        return false;
    }

    @Override
    public InputStream getFile(Project project, String filePath) {
        throw new UnsupportedOperationException();
    }
}
