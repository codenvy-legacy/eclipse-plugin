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
package com.codenvy.eclipse.client.fake;

import static com.codenvy.eclipse.client.fake.MockConstants.MOCK_PROJECT_DESCRIPTION;
import static com.codenvy.eclipse.client.fake.MockConstants.MOCK_PROJECT_NAME;
import static com.codenvy.eclipse.client.fake.MockConstants.MOCK_PROJECT_TYPE_NAME;
import static com.codenvy.eclipse.client.fake.MockConstants.MOCK_USERNAME;
import static com.codenvy.eclipse.client.fake.MockConstants.MOCK_USER_ID;
import static com.codenvy.eclipse.client.fake.MockConstants.MOCK_WORKSPACE_ID;
import static com.codenvy.eclipse.client.fake.MockConstants.MOCK_WORKSPACE_NAME;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
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
import com.codenvy.client.CodenvyErrorException;
import com.codenvy.client.ProjectClient;
import com.codenvy.client.Request;
import com.codenvy.client.RunnerClient;
import com.codenvy.client.UserClient;
import com.codenvy.client.WorkspaceClient;
import com.codenvy.client.model.Project;
import com.codenvy.client.model.User;
import com.codenvy.client.model.Workspace;
import com.codenvy.client.model.WorkspaceRef;

/**
 * Codenvy mock used for UI tests.
 * 
 * @author Kevin Pollet
 */
public class FakeCodenvy implements com.codenvy.client.Codenvy {
    private final List<Workspace> workspaces;
    private final List<Project>   projects;

    protected FakeCodenvy() {

    	// Mock workspaces
        this.workspaces = new ArrayList<>();
        this.workspaces.add(mockWorkspace(MOCK_WORKSPACE_ID, MOCK_WORKSPACE_NAME, "codenvy-organization"));
        this.workspaces.add(mockWorkspace("ws2-id", "ws2", "codenvy-organization"));
        this.workspaces.add(mockWorkspace("ws3-id", "ws3", "codenvy-organization"));
        this.workspaces.add(mockWorkspace("ws4-id", "ws4", "codenvy-organization"));

        // Mock projects
        this.projects = new ArrayList<>();
        this.projects.add(mockProject(MOCK_PROJECT_NAME, MOCK_WORKSPACE_ID, MOCK_PROJECT_TYPE_NAME, MOCK_PROJECT_DESCRIPTION));
        this.projects.add(mockProject("prj2", MOCK_WORKSPACE_ID, "maven", "prj2-description"));
        this.projects.add(mockProject("prj3", MOCK_WORKSPACE_ID, "maven", "prj3-description"));
        this.projects.add(mockProject("prj4", MOCK_WORKSPACE_ID, "maven", "prj4-description"));
    }

    public UserClient user() {
    	
    	// Mock user
    	User user = Mockito.mock(User.class);
    	doReturn(MOCK_USER_ID).when(user).id();
    	doReturn("<none>").when(user).password();
    	doReturn(MOCK_USERNAME).when(user).email();
    	
    	// mock User Client
        final UserClient userClientMock = Mockito.mock(UserClient.class);
        @SuppressWarnings("unchecked")
        Request<User> request = Mockito.mock(Request.class);
        doReturn(request).when(userClientMock).current();
        doReturn(user).when(request).execute();

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
                        if (MOCK_WORKSPACE_ID.equals(project.workspaceId()) && MOCK_PROJECT_NAME.equals(project.name())) {
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

                        if (MOCK_WORKSPACE_ID.equals(project.workspaceId()) && MOCK_PROJECT_NAME.equals(project.name())) {

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

        when(workspaceClientMock.all()).thenReturn(new Request<List<? extends Workspace>>() {
            @Override
            public List<Workspace> execute() throws CodenvyErrorException {
                return workspaces;
            }
        });

        when(workspaceClientMock.withName(anyString())).thenAnswer(new Answer<Request<WorkspaceRef>>() {
            @Override
            public Request<WorkspaceRef> answer(final InvocationOnMock invocation) throws Throwable {
                return new Request<WorkspaceRef>() {
                    @Override
                    public WorkspaceRef execute() throws CodenvyErrorException {
                        for (Workspace workspace : workspaces) {
                            if (workspace.workspaceRef().name().equals(invocation.getArguments()[0])) {
                                return workspace.workspaceRef();
                            }
                        }
                        return null;
                    }
                };

            }
        });

        return workspaceClientMock;
    }

    private Workspace mockWorkspace(String id, String name, String organization) {
    	Workspace workspace = mock(Workspace.class);

    	WorkspaceRef ref = mock(WorkspaceRef.class);
        doReturn(id).when(ref).id();
        doReturn(name).when(ref).name();
        doReturn(organization).when(ref).organizationId();

        doReturn(ref).when(workspace).workspaceRef();
        
        return workspace;
        
    }
    
    private Project mockProject(String name, String workspaceId, String projectTypeName, String description) {
    	Project project = mock(Project.class);

        doReturn(name).when(project).name();
        doReturn(workspaceId).when(project).workspaceId();
        doReturn(projectTypeName).when(project).projectTypeName();
        doReturn(description).when(project).description();
        
        return project;
        
    }
    
    
}
