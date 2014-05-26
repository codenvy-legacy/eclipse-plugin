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

import static com.codenvy.eclipse.core.utils.APIResponseHelper.readBody;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.ws.rs.core.Response;

import com.codenvy.eclipse.core.exceptions.APIException;
import com.codenvy.eclipse.core.model.CodenvyBuilderStatus;
import com.codenvy.eclipse.core.model.CodenvyProject;
import com.codenvy.eclipse.core.services.AbstractRestServiceWithAuth;
import com.codenvy.eclipse.core.services.BuilderService;

/**
 * {@link BuilderService} implementation.
 * 
 * @author Kevin Pollet
 */
public class DefaultBuilderService extends AbstractRestServiceWithAuth implements BuilderService {
    /**
     * Constructs an instance of {@linkplain DefaultBuilderService}.
     * 
     * @param url the Codenvy platform url.
     * @param username the username.
     * @throws NullPointerException if url or username is {@code null}.
     * @throws IllegalArgumentException if url or username parameter is an empty {@linkplain String}.
     */
    public DefaultBuilderService(String url, String username) {
        super(url, username, "api/builder");
    }

    @Override
    public CodenvyBuilderStatus build(CodenvyProject project) throws APIException {
        checkNotNull(project);

        final Response response = getWebTarget().path(project.workspaceId)
                                                .path("build")
                                                .queryParam("project", project.name)
                                                .request()
                                                .post(null);

        return readBody(response, CodenvyBuilderStatus.class);
    }

    @Override
    public CodenvyBuilderStatus status(CodenvyProject project, long taskId) throws APIException {
        checkNotNull(project);

        final Response response = getWebTarget().path(project.workspaceId)
                                                .path("status")
                                                .path(String.valueOf(taskId))
                                                .request()
                                                .get();

        return readBody(response, CodenvyBuilderStatus.class);
    }

    @Override
    public String logs(CodenvyProject project, long taskId) throws APIException {
        checkNotNull(project);

        final Response response = getWebTarget().path(project.workspaceId)
                                                .path("logs")
                                                .path(String.valueOf(taskId))
                                                .request()
                                                .get();

        return readBody(response, String.class);
    }

    @Override
    public CodenvyBuilderStatus cancel(CodenvyProject project, long taskId) throws APIException {
        checkNotNull(project);

        final Response response = getWebTarget().path(project.workspaceId)
                                                .path("cancel")
                                                .path(String.valueOf(taskId))
                                                .request()
                                                .get();

        return readBody(response, CodenvyBuilderStatus.class);
    }
}
