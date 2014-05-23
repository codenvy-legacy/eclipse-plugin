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
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN_TYPE;

import javax.ws.rs.core.Response;

import com.codenvy.eclipse.core.exceptions.APIException;
import com.codenvy.eclipse.core.model.CodenvyProject;
import com.codenvy.eclipse.core.model.CodenvyRunnerStatus;
import com.codenvy.eclipse.core.services.AbstractRestServiceWithAuth;
import com.codenvy.eclipse.core.services.RunnerService;

/**
 * {@link RunnerService} implementation.
 * 
 * @author Kevin Pollet
 */
public class DefaultRunnerService extends AbstractRestServiceWithAuth implements RunnerService {
    /**
     * Constructs an instance of {@linkplain DefaultRunnerService}.
     * 
     * @param url the Codenvy platform url.
     * @param username the username.
     * @throws NullPointerException if url or username is {@code null}.
     * @throws IllegalArgumentException if url or username are empty {@linkplain String}.
     */
    public DefaultRunnerService(String url, String username) {
        super(url, username, "api/runner");
    }

    @Override
    public CodenvyRunnerStatus run(CodenvyProject project) throws APIException {
        checkNotNull(project);

        final Response response = getWebTarget().path(project.workspaceId)
                                                .path("run")
                                                .queryParam("project", project.name)
                                                .request()
                                                .accept(APPLICATION_JSON_TYPE)
                                                .post(null);

        return readBody(response, CodenvyRunnerStatus.class);
    }

    @Override
    public CodenvyRunnerStatus stop(CodenvyProject project, long processId) throws APIException {
        checkNotNull(project);

        final Response response = getWebTarget().path(project.workspaceId)
                                                .path("stop")
                                                .path(String.valueOf(processId))
                                                .request()
                                                .accept(APPLICATION_JSON_TYPE)
                                                .post(null);

        return readBody(response, CodenvyRunnerStatus.class);
    }

    @Override
    public CodenvyRunnerStatus status(CodenvyProject project, long processId) throws APIException {
        checkNotNull(project);

        final Response response = getWebTarget().path(project.workspaceId)
                                                .path("status")
                                                .path(String.valueOf(processId))
                                                .request()
                                                .accept(APPLICATION_JSON_TYPE)
                                                .get();

        return readBody(response, CodenvyRunnerStatus.class);
    }

    @Override
    public String logs(CodenvyProject project, long processId) throws APIException {
        checkNotNull(project);

        final Response response = getWebTarget().path(project.workspaceId)
                                                .path("logs")
                                                .path(String.valueOf(processId))
                                                .request()
                                                .accept(TEXT_PLAIN_TYPE)
                                                .get();

        return readBody(response, String.class);
    }
}
