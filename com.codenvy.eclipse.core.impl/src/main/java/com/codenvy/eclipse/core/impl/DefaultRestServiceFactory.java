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
package com.codenvy.eclipse.core.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import com.codenvy.eclipse.core.model.CodenvyToken;
import com.codenvy.eclipse.core.services.AuthenticationService;
import com.codenvy.eclipse.core.services.ProjectService;
import com.codenvy.eclipse.core.services.RestService;
import com.codenvy.eclipse.core.services.RestServiceFactory;
import com.codenvy.eclipse.core.services.RestServiceWithAuth;
import com.codenvy.eclipse.core.services.UserService;
import com.codenvy.eclipse.core.services.WorkspaceService;

/**
 * The default {@linkplain RestServiceFactory} implementation.
 * 
 * @author Kevin Pollet
 */
public class DefaultRestServiceFactory implements RestServiceFactory {
    private final Map<Class< ? extends RestService>, Class< ? >>         restServiceBindings;
    private final Map<Class< ? extends RestServiceWithAuth>, Class< ? >> restServiceWithAuthBindings;

    public DefaultRestServiceFactory() {
        this.restServiceBindings = new HashMap<>();
        this.restServiceBindings.put(AuthenticationService.class, DefaultAuthenticationService.class);

        this.restServiceWithAuthBindings = new HashMap<>();
        this.restServiceWithAuthBindings.put(WorkspaceService.class, DefaultWorkspaceService.class);
        this.restServiceWithAuthBindings.put(UserService.class, DefaultUserService.class);
        this.restServiceWithAuthBindings.put(ProjectService.class, DefaultProjectService.class);
    }

    @Override
    public <T extends RestService, S extends T> T newRestService(Class<T> clazz, String url) {
        checkNotNull(clazz);

        try {

            @SuppressWarnings("unchecked")
            final Class<S> impl = (Class<S>)restServiceBindings.get(clazz);
            final Constructor<S> constructor = impl.getConstructor(String.class);
            return constructor.newInstance(url);

        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException
            | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T extends RestServiceWithAuth, S extends T> T newRestServiceWithAuth(Class<T> clazz, String url, CodenvyToken token) {
        checkNotNull(clazz);

        try {

            @SuppressWarnings("unchecked")
            final Class<S> impl = (Class<S>)restServiceWithAuthBindings.get(clazz);
            final Constructor<S> constructor = impl.getConstructor(String.class, CodenvyToken.class);
            return constructor.newInstance(url, token);

        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException
            | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
