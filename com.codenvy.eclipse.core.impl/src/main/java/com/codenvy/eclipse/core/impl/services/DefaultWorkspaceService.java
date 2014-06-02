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
package com.codenvy.eclipse.core.impl.services;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static javax.ws.rs.client.Entity.json;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.List;

import javax.ws.rs.core.GenericType;

import com.codenvy.eclipse.core.model.Workspace;
import com.codenvy.eclipse.core.model.Workspace.WorkspaceRef;
import com.codenvy.eclipse.core.services.AbstractRestServiceWithAuth;
import com.codenvy.eclipse.core.services.WorkspaceService;

/**
 * The Codenvy workspace client service.
 * 
 * @author Kevin Pollet
 * @author Stéphane Daviet
 */
public class DefaultWorkspaceService extends AbstractRestServiceWithAuth implements WorkspaceService {
    /**
     * Constructs an instance of {@linkplain DefaultWorkspaceService}.
     * 
     * @param url the Codenvy platform url.
     * @param username the username.
     * @throws NullPointerException if url or username parameter is {@code null}.
     * @throws IllegalArgumentException if url parameter is an empty {@linkplain String}.
     */
    public DefaultWorkspaceService(String url, String username) {
        super(url, username, "api/workspace");
    }

    @Override
    public List<Workspace> getAllWorkspaces() {
        return getWebTarget().path("all")
                             .request()
                             .accept(APPLICATION_JSON)
                             .get(new GenericType<List<Workspace>>() {
                             });
    }

    @Override
    public WorkspaceRef getWorkspaceByName(String name) {
        checkNotNull(name);
        checkArgument(!name.trim().isEmpty());

        return getWebTarget().queryParam("name", name)
                             .request()
                             .accept(APPLICATION_JSON)
                             .get(WorkspaceRef.class);
    }

    @Override
    public WorkspaceRef newWorkspace(WorkspaceRef workspaceRef) {
        checkNotNull(workspaceRef);

        return getWebTarget().request()
                             .post(json(workspaceRef), WorkspaceRef.class);

    }
}
