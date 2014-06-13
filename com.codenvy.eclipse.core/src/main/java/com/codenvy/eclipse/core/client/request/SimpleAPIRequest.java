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
package com.codenvy.eclipse.core.client.request;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.codenvy.eclipse.core.client.exceptions.APIException;

/**
 * {@link APIRequest} implementation reading the body of the {@link Response}.
 * 
 * @author Kevin Pollet
 * @param <T> the {@linkplain java.lang.reflect.Type Type} of the {@link Response} body.
 */
public class SimpleAPIRequest<T> implements APIRequest<T> {
    private final Class<T>       entityType;
    private final GenericType<T> genericEntityType;
    private final Invocation     request;

    /**
     * Constructs an instance of {@link SimpleAPIRequest}.
     * 
     * @param request the request to invoke.
     * @param entityType the request response entity {@linkplain java.lang.reflect.Type Type}.
     */
    public SimpleAPIRequest(Invocation request, Class<T> entityType) {
        this(request, entityType, null);
    }

    /**
     * Constructs an instance of {@link SimpleAPIRequest}.
     * 
     * @param request the request to invoke.
     * @param genericEntityType the request response entity {@link GenericType}.
     */
    public SimpleAPIRequest(Invocation request, GenericType<T> genericEntityType) {
        this(request, null, genericEntityType);
    }

    /**
     * Constructs an instance of {@link SimpleAPIRequest}.
     * 
     * @param request the request to invoke.
     * @param entityType the request response entity {@linkplain java.lang.reflect.Type Type}.
     * @param genericEntityType the request response entity {@link GenericType}.
     * @throws NullPointerException if request parameter, entityType or genericEntityType is {@code null}.
     */
    private SimpleAPIRequest(Invocation request, Class<T> entityType, GenericType<T> genericEntityType) {
        checkNotNull(request);
        checkNotNull(entityType != null || genericEntityType != null);

        this.request = request;
        this.entityType = entityType;
        this.genericEntityType = genericEntityType;
    }

    @Override
    public T execute() throws APIException {
        if (genericEntityType != null) {
            return readEntity(request.invoke(), genericEntityType);
        }

        if (entityType.equals(Response.class)) {
            return entityType.cast(request.invoke());
        }

        return readEntity(request.invoke(), entityType);
    }

    /**
     * Reads the API {@link Response} body entity.
     * 
     * @param response the API {@link Response}.
     * @param entityType the entity type to read in {@link Response} body.
     * @return the entity type instance.
     * @throws APIException if something goes wrong with the API call.
     */
    private T readEntity(Response response, Class<T> entityType) throws APIException {
        if (Status.OK.getStatusCode() == response.getStatus()) {
            return response.readEntity(entityType);
        }

        throw APIException.from(response);
    }

    /**
     * Reads the API {@link Response} body entity.
     * 
     * @param response the API {@link Response}.
     * @param genericEntityType the entity type to read in {@link Response} body.
     * @return the entity type instance.
     * @throws APIException if something goes wrong with the API call.
     */
    private T readEntity(Response response, GenericType<T> genericEntityType) throws APIException {
        if (Status.OK.getStatusCode() == response.getStatus()) {
            return response.readEntity(genericEntityType);
        }

        throw APIException.from(response);
    }
}
