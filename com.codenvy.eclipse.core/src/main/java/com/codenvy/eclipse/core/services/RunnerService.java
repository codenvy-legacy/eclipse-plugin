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
import com.codenvy.eclipse.core.model.Project;
import com.codenvy.eclipse.core.model.RunnerStatus;

/**
 * Codenvy runner service contract.
 * 
 * @author Kevin Pollet
 */
public interface RunnerService extends RestServiceWithAuth {
    /**
     * Runs the given project with a codenvy runner.
     * 
     * @param project the project to run.
     * @return the {@link RunnerStatus}.
     * @throws NullPointerException if project parameter is {@code null}.
     * @throws APIException if something goes wrong with the API call.
     */
    RunnerStatus run(Project project) throws APIException;

    /**
     * Stops the project runner with the given process id.
     * 
     * @param project the project.
     * @param processId the runner process id.
     * @return the {@link RunnerStatus}.
     * @throws NullPointerException if project parameter is {@code null}.
     * @throws APIException if something goes wrong with the API call.
     */
    RunnerStatus stop(Project project, long processId) throws APIException;

    /**
     * Gets the project runner status with the given process id.
     * 
     * @param project the project.
     * @param processId the runner process id.
     * @return the {@link RunnerStatus}.
     * @throws NullPointerException if project parameter is {@code null}.
     * @throws APIException if something goes wrong with the API call.
     */
    RunnerStatus status(Project project, long processId) throws APIException;

    /**
     * Gets the project runner logs with the given process id.
     * 
     * @param project the project.
     * @param processId the runner process id.
     * @return the runner logs.
     * @throws NullPointerException if project parameter is {@code null}.
     * @throws APIException if something goes wrong with the API call.
     */
    String logs(Project project, long processId) throws APIException;
}
