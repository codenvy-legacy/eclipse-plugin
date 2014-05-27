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
package com.codenvy.eclipse.core.utils;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.codenvy.eclipse.core.exceptions.APIException;


/**
 * Helper providing utility methods to work with API {@code Response}.
 * 
 * @author Kevin Pollet
 */
public final class APIResponseHelper {
    /**
     * Reads the API {@link Response} body.
     * 
     * @param response the API {@link Response}.
     * @param entityClass the entity to read in {@link Response} body.
     * @return the entityClass instance.
     * @throws APIException if something goes wrong with the API call.
     */
    public static <T> T readBody(Response response, Class<T> entityClass) throws APIException {
        if (Status.OK.getStatusCode() == response.getStatus()) {
            return response.readEntity(entityClass);
        }

        throw APIException.from(response);
    }

    /**
     * Disable instantiation.
     */
    private APIResponseHelper() {
    }
}
