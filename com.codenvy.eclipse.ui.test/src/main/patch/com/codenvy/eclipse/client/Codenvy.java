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

import static com.codenvy.eclipse.client.MockConstants.MOCK_PROJECT_DESCRIPTION;
import static com.codenvy.eclipse.client.MockConstants.MOCK_PROJECT_NAME;
import static com.codenvy.eclipse.client.MockConstants.MOCK_PROJECT_TYPE_NAME;
import static com.codenvy.eclipse.client.MockConstants.MOCK_USERNAME;
import static com.codenvy.eclipse.client.MockConstants.MOCK_USER_ID;
import static com.codenvy.eclipse.client.MockConstants.MOCK_WORKSPACE_ID;
import static com.codenvy.eclipse.client.MockConstants.MOCK_WORKSPACE_NAME;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.codenvy.eclipse.client.exceptions.APIException;
import com.codenvy.eclipse.client.model.Credentials;
import com.codenvy.eclipse.client.model.Project;
import com.codenvy.eclipse.client.model.User;
import com.codenvy.eclipse.client.model.Workspace;
import com.codenvy.eclipse.client.model.Workspace.WorkspaceRef;
import com.codenvy.eclipse.client.store.DataStoreFactory;
import com.codenvy.eclipse.client.store.StoredCredentials;

/**
 * Codenvy mock used for UI tests.
 * 
 * @author Kevin Pollet
 */
public class Codenvy {
    private final List<Workspace> workspaces;
    private final List<Project>   projects;

    private Codenvy() {
        this.workspaces = new ArrayList<>();
        this.workspaces.add(new Workspace(new WorkspaceRef(MOCK_WORKSPACE_ID, MOCK_WORKSPACE_NAME, "codenvy-organization")));
        this.workspaces.add(new Workspace(new WorkspaceRef("ws2-id", "ws2", "codenvy-organization")));
        this.workspaces.add(new Workspace(new WorkspaceRef("ws3-id", "ws3", "codenvy-organization")));
        this.workspaces.add(new Workspace(new WorkspaceRef("ws4-id", "ws4", "codenvy-organization")));

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

    public UserClient user() {
        final UserClient userClientMock = Mockito.mock(UserClient.class);
        when(userClientMock.current()).thenReturn(new APIRequest<User>() {
            @Override
            public User execute() throws APIException {
                return new User(MOCK_USER_ID, "<none>", MOCK_USERNAME);
            }
        });

        return userClientMock;
    }

    public BuilderClient builder() {
        throw new UnsupportedOperationException();
    }

    public RunnerClient runner() {
        throw new UnsupportedOperationException();
    }

    public ProjectClient project() {
        final ProjectClient projectClientMock = mock(ProjectClient.class);
        when(projectClientMock.getWorkspaceProjects(anyString())).thenAnswer(new Answer<APIRequest<List<Project>>>() {
            @Override
            public APIRequest<List<Project>> answer(final InvocationOnMock invocation) throws Throwable {
                return new APIRequest<List<Project>>() {
                    @Override
                    public List<Project> execute() throws APIException {
                        if (MOCK_WORKSPACE_ID.equals(invocation.getArguments()[0])) {
                            return projects;
                        }
                        return new ArrayList<>();
                    }
                };
            }
        });

        when(projectClientMock.exportResources(any(Project.class), anyString())).thenAnswer(new Answer<APIRequest<ZipInputStream>>() {
            @Override
            public APIRequest<ZipInputStream> answer(final InvocationOnMock invocation) throws Throwable {
                return new APIRequest<ZipInputStream>() {
                    @Override
                    public ZipInputStream execute() throws APIException {
                        final Project project = (Project)invocation.getArguments()[0];
                        if (MOCK_WORKSPACE_ID.equals(project.workspaceId) && MOCK_PROJECT_NAME.equals(project.name)) {
                            return new ZipInputStream(getClass().getResourceAsStream("/prj1.zip"));
                        }
                        return null;
                    }
                };
            }
        });

        when(projectClientMock.isResource(any(Project.class), anyString())).thenAnswer(new Answer<APIRequest<Boolean>>() {
            @Override
            public APIRequest<Boolean> answer(final InvocationOnMock invocation) throws Throwable {
                return new APIRequest<Boolean>() {
                    @Override
                    public Boolean execute() throws APIException {
                        Boolean exists = Boolean.FALSE;
                        final Project project = (Project)invocation.getArguments()[0];
                        final String resourcePath = (String)invocation.getArguments()[1];

                        if (MOCK_WORKSPACE_ID.equals(project.workspaceId) && MOCK_PROJECT_NAME.equals(project.name)) {

                            final InputStream in = getClass().getResourceAsStream("/prj1.zip");
                            try (ZipInputStream zipIn = new ZipInputStream(in)) {

                                ZipEntry entry;
                                while ((entry = zipIn.getNextEntry()) != null) {
                                    String entryName = entry.getName();
                                    entryName = entry.isDirectory() ? entryName.substring(0, entryName.length() - 1) : entryName;

                                    if (entryName.equals(resourcePath)) {
                                        exists = Boolean.TRUE;
                                        break;
                                    }
                                }

                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        return exists;
                    }
                };
            }
        });

        return projectClientMock;
    }

    public WorkspaceClient workspace() {
        final WorkspaceClient workspaceClientMock = mock(WorkspaceClient.class);

        when(workspaceClientMock.all()).thenReturn(new APIRequest<List<Workspace>>() {
            @Override
            public List<Workspace> execute() throws APIException {
                return workspaces;
            }
        });

        when(workspaceClientMock.withName(anyString())).thenAnswer(new Answer<APIRequest<WorkspaceRef>>() {
            @Override
            public APIRequest<WorkspaceRef> answer(final InvocationOnMock invocation) throws Throwable {
                return new APIRequest<Workspace.WorkspaceRef>() {
                    @Override
                    public WorkspaceRef execute() throws APIException {
                        for (Workspace workspace : workspaces) {
                            if (workspace.workspaceRef.name.equals(invocation.getArguments()[0])) {
                                return workspace.workspaceRef;
                            }
                        }
                        return null;
                    }
                };

            }
        });

        return workspaceClientMock;
    }

    public static class Builder {

        public Builder(String url,
                       String username,
                       DataStoreFactory<String, StoredCredentials> credentialsStoreFactory) {
        }

        public Builder withCredentials(Credentials credentials) {
            return this;
        }

        public Codenvy build() {
            return new Codenvy();
        }
    }
}
