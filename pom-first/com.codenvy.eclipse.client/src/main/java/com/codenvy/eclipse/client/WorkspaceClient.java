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

import static com.google.common.base.Preconditions.checkNotNull;
import static javax.ws.rs.client.Entity.json;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.List;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.GenericType;

import com.codenvy.eclipse.client.auth.AuthenticationManager;
import com.codenvy.eclipse.client.model.Workspace;
import com.codenvy.eclipse.client.model.Workspace.WorkspaceRef;

/**
 * The Codenvy workspace API client.
 * 
 * @author Kevin Pollet
 * @author Stéphane Daviet
 */
public class WorkspaceClient extends AbstractClient {
    /**
     * Constructs an instance of {@link WorkspaceClient}.
     * 
     * @param url the Codenvy platform URL.
     * @param authenticationManager the {@link AuthenticationManager}.
     * @throws NullPointerException if url or authenticationManager parameter is {@code null}.
     */
    WorkspaceClient(String url, AuthenticationManager authenticationManager) {
        super(url, "workspace", authenticationManager);
    }

    /**
     * Retrieves all Codenvy workspaces of the user identified by the authentication token.
     * 
     * @return all Codenvy workspaces never {@code null}.
     * @throws CodenvyException if something goes wrong with the API call.
     */
    public Request<List<Workspace>> all() throws CodenvyException {
        final Invocation request = getWebTarget().path("all")
                                                 .request()
                                                 .accept(APPLICATION_JSON)
                                                 .buildGet();

        return new SimpleRequest<>(request, new GenericType<List<Workspace>>() {
        }, getAuthenticationManager());
    }

    /**
     * Retrieves a Codenvy workspace by it's name.
     * 
     * @param name the workspace name.
     * @return the Codenvy workspace or {@code null} if none.
     * @throws NullPointerException if name parameter is {@code null}.
     * @throws CodenvyException if something goes wrong with the API call.
     */
    public Request<WorkspaceRef> withName(String name) throws CodenvyException {
        checkNotNull(name);

        final Invocation request = getWebTarget().queryParam("name", name)
                                                 .request()
                                                 .accept(APPLICATION_JSON)
                                                 .buildGet();

        return new SimpleRequest<>(request, WorkspaceRef.class, getAuthenticationManager());
    }

    /**
     * Creates the given workspace.
     * 
     * @param workspaceRef the workspace to create.
     * @return the created workspace.
     * @throws NullPointerException if {@link WorkspaceRef} parameter is {@code null}.
     * @throws CodenvyException if something goes wrong with the API call.
     */
    public Request<WorkspaceRef> create(WorkspaceRef workspaceRef) throws CodenvyException {
        checkNotNull(workspaceRef);

        final Invocation request = getWebTarget().request()
                                                 .accept(APPLICATION_JSON)
                                                 .buildPost(json(workspaceRef));

        return new SimpleRequest<>(request, WorkspaceRef.class, getAuthenticationManager());

    }
}
