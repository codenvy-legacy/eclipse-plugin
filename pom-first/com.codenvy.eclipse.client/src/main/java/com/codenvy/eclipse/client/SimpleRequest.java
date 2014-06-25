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

import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.codenvy.eclipse.client.auth.AuthenticationManager;

/**
 * {@link Request} implementation reading the body of the {@link Response}.
 * 
 * @author Kevin Pollet
 * @author Stéphane Daviet
 * @param <T> the {@linkplain java.lang.reflect.Type Type} of the {@link Response} body.
 */
public class SimpleRequest<T> implements Request<T> {
    private final Class<T>              entityType;
    private final GenericType<T>        genericEntityType;
    private final Invocation            request;
    private final AuthenticationManager authenticationManager;

    /**
     * Constructs an instance of {@link SimpleRequest}.
     * 
     * @param request the request to invoke.
     * @param entityType the request response entity {@linkplain java.lang.reflect.Type Type}.
     * @param authenticationManager the {@link AuthenticationManager} instance.
     * @throws NullPointerException if request, entityType or authenticationManager parameter is {@code null}.
     */
    SimpleRequest(Invocation request, Class<T> entityType, AuthenticationManager authenticationManager) {
        this(request, entityType, null, authenticationManager);
    }

    /**
     * Constructs an instance of {@link SimpleRequest}.
     * 
     * @param request the request to invoke.
     * @param genericEntityType the request response entity {@link GenericType}.
     * @param authenticationManager the {@link AuthenticationManager} instance.
     * @throws NullPointerException if request, genericEntityType or authenticationManager parameter is {@code null}.
     */
    SimpleRequest(Invocation request, GenericType<T> genericEntityType, AuthenticationManager authenticationManager) {
        this(request, null, genericEntityType, authenticationManager);
    }

    /**
     * Constructs an instance of {@link SimpleRequest}.
     * 
     * @param request the request to invoke.
     * @param entityType the request response entity {@linkplain java.lang.reflect.Type Type}.
     * @param genericEntityType the request response entity {@link GenericType}.
     * @param authenticationManager the {@link AuthenticationManager} instance.
     * @throws NullPointerException if request, entityType, genericEntityType, authenticationManager parameter is {@code null}.
     */
    private SimpleRequest(Invocation request,
                             Class<T> entityType,
                             GenericType<T> genericEntityType,
                             AuthenticationManager authenticationManager) {

        checkNotNull(request);
        checkNotNull(entityType != null || genericEntityType != null);
        checkNotNull(authenticationManager);

        this.request = request;
        this.entityType = entityType;
        this.genericEntityType = genericEntityType;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public T execute() throws CodenvyException {
        Response response = request.invoke();
        final Status responseStatus = Status.fromStatusCode(response.getStatus());

        if (responseStatus == Status.UNAUTHORIZED
            || responseStatus == Status.FORBIDDEN
            || responseStatus == Status.PAYMENT_REQUIRED) {

            authenticationManager.refreshToken();
            response = request.invoke();
        }

        // read response
        if (genericEntityType != null) {
            return readEntity(response, genericEntityType);
        }
        return entityType.equals(Response.class) ? entityType.cast(response) : readEntity(response, entityType);
    }

    /**
     * Reads the API {@link Response} body entity.
     * 
     * @param response the API {@link Response}.
     * @param entityType the entity type to read in {@link Response} body.
     * @return the entity type instance.
     * @throws CodenvyException if something goes wrong with the API call.
     */
    private T readEntity(Response response, Class<T> entityType) throws CodenvyException {
        if (Status.Family.SUCCESSFUL == response.getStatusInfo().getFamily()) {
            return response.readEntity(entityType);
        }

        throw CodenvyException.from(response);
    }

    /**
     * Reads the API {@link Response} body entity.
     * 
     * @param response the API {@link Response}.
     * @param genericEntityType the entity type to read in {@link Response} body.
     * @return the entity type instance.
     * @throws CodenvyException if something goes wrong with the API call.
     */
    private T readEntity(Response response, GenericType<T> genericEntityType) throws CodenvyException {
        if (Status.Family.SUCCESSFUL == response.getStatusInfo().getFamily()) {
            return response.readEntity(genericEntityType);
        }

        throw CodenvyException.from(response);
    }
}
