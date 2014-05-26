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
package com.codenvy.eclipse.core.services;

import com.codenvy.eclipse.core.exceptions.APIException;
import com.codenvy.eclipse.core.model.CodenvyBuilderStatus;
import com.codenvy.eclipse.core.model.CodenvyProject;

/**
 * Codenvy builder service contract.
 * 
 * @author Kevin Pollet
 */
// TODO is CodenvyProject parameter always needed?
public interface BuilderService extends RestServiceWithAuth {
    /**
     * Builds the given {@link CodenvyProject} on codenvy.
     * 
     * @param project the project to build.
     * @return the {@link CodenvyBuilderStatus}.
     * @throws NullPointerException if project parameter is {@code null}.
     * @throws APIException if something goes wrong with the API call.
     */
    CodenvyBuilderStatus build(CodenvyProject project) throws APIException;

    /**
     * Gets the status of the builder with the given task id.
     * 
     * @param project the project.
     * @param taskId the builder task id.
     * @return the {@link CodenvyBuilderStatus}.
     * @throws NullPointerException if project parameter is {@code null}.
     * @throws APIException if something goes wrong with the API call.
     */
    CodenvyBuilderStatus status(CodenvyProject project, long taskId) throws APIException;

    /**
     * Gets the logs of the builder with the given task id.
     * 
     * @param project the project.
     * @param taskId the builder task id.
     * @return the builder logs.
     * @throws NullPointerException if project parameter is {@code null}.
     * @throws APIException if something goes wrong with the API call.
     */
    String logs(CodenvyProject project, long taskId) throws APIException;

    /**
     * Cancels the builder with the given task id.
     * 
     * @param project the project.
     * @param taskId the builder task id.
     * @return the {@link CodenvyBuilderStatus}.
     * @throws NullPointerException if project parameter is {@code null}.
     * @throws APIException if something goes wrong with the API call.
     */
    CodenvyBuilderStatus cancel(CodenvyProject project, long taskId) throws APIException;
}
