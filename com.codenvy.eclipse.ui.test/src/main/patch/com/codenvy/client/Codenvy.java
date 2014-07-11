/*******************************************************************************
 * Copyright (c) 2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.client;

import static com.codenvy.client.MockConstants.MOCK_PROJECT_DESCRIPTION;
import static com.codenvy.client.MockConstants.MOCK_PROJECT_NAME;
import static com.codenvy.client.MockConstants.MOCK_PROJECT_TYPE_NAME;
import static com.codenvy.client.MockConstants.MOCK_USERNAME;
import static com.codenvy.client.MockConstants.MOCK_USER_ID;
import static com.codenvy.client.MockConstants.MOCK_WORKSPACE_ID;
import static com.codenvy.client.MockConstants.MOCK_WORKSPACE_NAME;
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

import com.codenvy.client.BuilderClient;
import com.codenvy.client.Codenvy;
import com.codenvy.client.CodenvyErrorException;
import com.codenvy.client.ProjectClient;
import com.codenvy.client.Request;
import com.codenvy.client.RunnerClient;
import com.codenvy.client.UserClient;
import com.codenvy.client.WorkspaceClient;
import com.codenvy.client.auth.Credentials;
import com.codenvy.client.auth.CredentialsProvider;
import com.codenvy.client.model.Project;
import com.codenvy.client.model.User;
import com.codenvy.client.model.Workspace;
import com.codenvy.client.model.Workspace.WorkspaceRef;
import com.codenvy.client.store.DataStoreFactory;

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
        when(userClientMock.current()).thenReturn(new Request<User>() {
            @Override
            public User execute() throws CodenvyErrorException {
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
        when(projectClientMock.getWorkspaceProjects(anyString())).thenAnswer(new Answer<Request<List<Project>>>() {
            @Override
            public Request<List<Project>> answer(final InvocationOnMock invocation) throws Throwable {
                return new Request<List<Project>>() {
                    @Override
                    public List<Project> execute() throws CodenvyErrorException {
                        if (MOCK_WORKSPACE_ID.equals(invocation.getArguments()[0])) {
                            return projects;
                        }
                        return new ArrayList<>();
                    }
                };
            }
        });

        when(projectClientMock.exportResources(any(Project.class), anyString())).thenAnswer(new Answer<Request<ZipInputStream>>() {
            @Override
            public Request<ZipInputStream> answer(final InvocationOnMock invocation) throws Throwable {
                return new Request<ZipInputStream>() {
                    @Override
                    public ZipInputStream execute() throws CodenvyErrorException {
                        final Project project = (Project)invocation.getArguments()[0];
                        if (MOCK_WORKSPACE_ID.equals(project.workspaceId) && MOCK_PROJECT_NAME.equals(project.name)) {
                            return new ZipInputStream(getClass().getResourceAsStream("/prj1.zip"));
                        }
                        return null;
                    }
                };
            }
        });

        when(projectClientMock.isResource(any(Project.class), anyString())).thenAnswer(new Answer<Request<Boolean>>() {
            @Override
            public Request<Boolean> answer(final InvocationOnMock invocation) throws Throwable {
                return new Request<Boolean>() {
                    @Override
                    public Boolean execute() throws CodenvyErrorException {
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

        when(workspaceClientMock.all()).thenReturn(new Request<List<Workspace>>() {
            @Override
            public List<Workspace> execute() throws CodenvyErrorException {
                return workspaces;
            }
        });

        when(workspaceClientMock.withName(anyString())).thenAnswer(new Answer<Request<WorkspaceRef>>() {
            @Override
            public Request<WorkspaceRef> answer(final InvocationOnMock invocation) throws Throwable {
                return new Request<Workspace.WorkspaceRef>() {
                    @Override
                    public WorkspaceRef execute() throws CodenvyErrorException {
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
        /**
         * Constructs an instance of {@link Builder}.
         * 
         * @param url the Codenvy platform URL.
         * @param username the user name.
         * @throws NullPointerException if url or username parameter is {@code null}.
         */
        public Builder(String url, String username) {
        }

        /**
         * Provides the user {@link Credentials} used if they are not found in storage.
         * 
         * @param credentials the provided {@link Credentials}.
         * @return {@link Builder} instance.
         */
        public Builder withCredentials(Credentials credentials) {
            return this;
        }

        /**
         * Defines the {@link DataStoreFactory} used to store the user {@link Credentials}.
         * 
         * @param credentialsStoreFactory the {@link DataStoreFactory} to use.
         * @return {@link Builder} instance.
         */
        public Builder withCredentialsStoreFactory(DataStoreFactory<String, Credentials> credentialsStoreFactory) {
            return this;
        }

        /**
         * Defines the {@link CredentialsProvider} used to provide credentials if they are not stored or provided
         * 
         * @param credentialsProvider the credentials provider.
         * @return {@link Builder} instance.
         */
        public Builder withCredentialsProvider(CredentialsProvider credentialsProvider) {
            return this;
        }

        /**
         * Builds the {@link Codenvy} client.
         * 
         * @return the {@link Codenvy} client instance.
         */
        public Codenvy build() {
            return new Codenvy();
        }
    }
}
