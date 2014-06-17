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
package com.codenvy.eclipse.client.exceptions;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.ws.rs.core.Response;

import com.codenvy.eclipse.client.model.Error;

/**
 * Exception thrown when something is wrong with the REST API.
 * 
 * @author Kevin Pollet
 */
public class APIException extends RuntimeException {
    private static final long serialVersionUID = 7031838814322889179L;

    /**
     * Reads the {@code Response} body and constructs an instance of {@link APIException}.
     * 
     * @param response the rest API {@link Response}.
     * @throws NullPointerException if response parameter is {@code null}.
     */
    public static APIException from(Response response) {
        checkNotNull(response);

        final Error codenvyError = response.readEntity(Error.class);
        return new APIException(response.getStatus(), codenvyError.message);
    }

    private final int status;

    /**
     * Constructs an instance of {@link APIException}.
     * 
     * @param status the HTTP status code.
     * @param message the error message.
     */
    private APIException(int status, String message) {
        super(message);

        this.status = status;
    }

    /**
     * Returns the HTTP status code.
     * 
     * @return the HTTP status code.
     */
    public int getStatus() {
        return status;
    }
}
