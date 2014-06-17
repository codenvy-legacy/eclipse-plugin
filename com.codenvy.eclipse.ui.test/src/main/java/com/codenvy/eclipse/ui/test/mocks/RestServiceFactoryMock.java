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
package com.codenvy.eclipse.ui.test.mocks;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import com.codenvy.eclipse.client.ProjectClient;
import com.codenvy.eclipse.client.UserClient;
import com.codenvy.eclipse.client.WorkspaceClient;
import com.codenvy.eclipse.core.services.RestService;
import com.codenvy.eclipse.core.services.RestServiceWithAuth;
import com.codenvy.eclipse.core.services.security.AuthenticationService;
import com.codenvy.eclipse.core.spi.RestServiceFactory;

/**
 * {@link RestServiceFactory} mock.
 * 
 * @author Kevin Pollet
 * @author Stéphane Daviet
 */
public class RestServiceFactoryMock implements RestServiceFactory {
    private final Map<Class< ? extends RestService>, Class< ? >>         restServiceBindings;
    private final Map<Class< ? extends RestServiceWithAuth>, Class< ? >> restServiceWithAuthBindings;

    public RestServiceFactoryMock() {
        this.restServiceBindings = new HashMap<>();
        this.restServiceBindings.put(AuthenticationService.class, AuthenticationServiceMock.class);

        this.restServiceWithAuthBindings = new HashMap<>();
        this.restServiceWithAuthBindings.put(WorkspaceClient.class, WorkspaceServiceMock.class);
        this.restServiceWithAuthBindings.put(UserClient.class, UserServiceMock.class);
        this.restServiceWithAuthBindings.put(ProjectClient.class, ProjectServiceMock.class);
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
    public <T extends RestServiceWithAuth, S extends T> T newRestServiceWithAuth(Class<T> clazz, String url, String username) {
        checkNotNull(clazz);

        try {

            @SuppressWarnings("unchecked")
            final Class<S> impl = (Class<S>)restServiceWithAuthBindings.get(clazz);
            final Constructor<S> constructor = impl.getConstructor(String.class, String.class);
            return constructor.newInstance(url, username);

        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException
            | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
