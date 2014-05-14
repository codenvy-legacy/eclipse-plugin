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
package com.codenvy.eclipse.ui.test.mock;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipInputStream;

import org.eclipse.core.resources.IResource;

import com.codenvy.eclipse.core.ProjectService;
import com.codenvy.eclipse.core.model.CodenvyProject;
import com.codenvy.eclipse.core.model.CodenvyToken;

/**
 * The Codenvy project client service mock.
 * 
 * @author Kevin Pollet
 */
public class ProjectServiceMock implements ProjectService {
    public static final String         MOCK_PROJECT_NAME        = "prj1";
    public static final String         MOCK_PROJECT_TYPE_NAME   = "maven";
    public static final String         MOCK_PROJECT_DESCRIPTION = "prj1-description";

    private final List<CodenvyProject> projects;

    public ProjectServiceMock(String url, CodenvyToken codenvyToken) {
        this.projects = new ArrayList<>();

        final CodenvyProject prj1 = new CodenvyProject.Builder().withName(MOCK_PROJECT_NAME)
                                                                .withProjectTypeName(MOCK_PROJECT_TYPE_NAME)
                                                                .withDescription(MOCK_PROJECT_DESCRIPTION)
                                                                .build();

        final CodenvyProject prj2 = new CodenvyProject.Builder().withName("prj2")
                                                                .withProjectTypeName("maven")
                                                                .withDescription("prj2-description")
                                                                .build();

        final CodenvyProject prj3 = new CodenvyProject.Builder().withName("prj3")
                                                                .withProjectTypeName("maven")
                                                                .withDescription("prj3-description")
                                                                .build();

        final CodenvyProject prj4 = new CodenvyProject.Builder().withName("prj4")
                                                                .withProjectTypeName("maven")
                                                                .withDescription("prj4-description")
                                                                .build();

        this.projects.add(prj1);
        this.projects.add(prj2);
        this.projects.add(prj3);
        this.projects.add(prj4);
    }

    @Override
    public List<CodenvyProject> getWorkspaceProjects(String workspaceId) {
        if (WorkspaceServiceMock.MOCK_WORKSPACE_ID.equals(workspaceId)) {
            return projects;
        }
        return new ArrayList<>();
    }

    @Override
    public CodenvyProject newProject(CodenvyProject project) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ZipInputStream exportResources(CodenvyProject project, String resourcePath) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateFile(CodenvyProject project, String filePath, InputStream fileInputStream) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isResourceInProject(CodenvyProject project, IResource resource) {
        throw new UnsupportedOperationException();
    }

    @Override
    public InputStream getFile(CodenvyProject project, String filePath) {
        throw new UnsupportedOperationException();
    }
}
