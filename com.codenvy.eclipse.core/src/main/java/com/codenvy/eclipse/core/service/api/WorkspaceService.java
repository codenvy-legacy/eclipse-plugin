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
package com.codenvy.eclipse.core.service.api;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static javax.ws.rs.client.Entity.json;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.net.URI;
import java.util.List;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.UriBuilder;

import com.codenvy.eclipse.core.service.api.model.CodenvyToken;
import com.codenvy.eclipse.core.service.api.model.Workspace;
import com.codenvy.eclipse.core.service.api.model.Workspace.WorkspaceRef;

/**
 * The Codenvy workspace client service.
 * 
 * @author Kevin Pollet
 */
public class WorkspaceService implements RestServiceWithAuth {
    private final CodenvyToken codenvyToken;
    private final WebTarget    workspaceWebTarget;

    /**
     * Constructs an instance of {@linkplain WorkspaceService}.
     * 
     * @param url the Codenvy platform url.
     * @param codenvyToken the Codenvy authentication token.
     * @throws NullPointerException if url or codenvyToken parameter is {@code null}.
     * @throws IllegalArgumentException if url parameter is an empty {@linkplain String}.
     */
    public WorkspaceService(String url, CodenvyToken codenvyToken) {
        checkNotNull(codenvyToken);
        checkNotNull(url);
        checkArgument(!url.trim().isEmpty());

        final URI uri = UriBuilder.fromUri(url)
                                  .path("api/workspace")
                                  .build();

        this.codenvyToken = codenvyToken;
        this.workspaceWebTarget = ClientBuilder.newClient()
                                               .target(uri);
    }

    /**
     * Retrieves all Codenvy workspaces of the user identified by the authentication token.
     * 
     * @return all Codenvy workspaces never {@code null}.
     */
    public List<Workspace> getAllWorkspaces() {
        return workspaceWebTarget.path("all")
                                 .queryParam("token", codenvyToken.value)
                                 .request()
                                 .accept(APPLICATION_JSON)
                                 .get(new GenericType<List<Workspace>>() {
                                 });
    }

    /**
     * Retrieves a Codenvy workspace by it's name.
     * 
     * @param name the workspace name.
     * @return the Codenvy workspace or {@code null} if none.
     * @throws NullPointerException if name parameter is {@code null}.
     * @throws IllegalArgumentException if name parameter is an empty {@linkplain String}.
     */
    public WorkspaceRef getWorkspaceByName(String name) {
        checkNotNull(name);
        checkArgument(!name.trim().isEmpty());

        return workspaceWebTarget.queryParam("token", codenvyToken.value)
                                 .queryParam("name", name)
                                 .request()
                                 .accept(APPLICATION_JSON)
                                 .get(WorkspaceRef.class);
    }

    /**
     * Finds Codenvy workspaces of the given account.
     * 
     * @param accountId the account id.
     * @return the Codenvy workspace list never {@code null}.
     * @throws NullPointerException if accountId parameter is {@code null}.
     * @throws IllegalArgumentException if accountId parameter is an empty {@linkplain String}.
     */
    public List<WorkspaceRef> findWorkspacesByAccount(String accountId) {
        checkNotNull(accountId);
        checkArgument(!accountId.trim().isEmpty());

        return workspaceWebTarget.path("find/account")
                                 .queryParam("token", codenvyToken.value)
                                 .queryParam("id", accountId)
                                 .request()
                                 .accept(APPLICATION_JSON)
                                 .get(new GenericType<List<WorkspaceRef>>() {
                                 });
    }

    /**
     * Creates the given workspace.
     * 
     * @param workspaceRef the workspace to create.
     * @return the created workspace.
     */
    public WorkspaceRef createWorkspace(WorkspaceRef workspaceRef) {
        return workspaceWebTarget.queryParam("token", codenvyToken.value)
                                 .request()
                                 .post(json(workspaceRef), WorkspaceRef.class);

    }
}
