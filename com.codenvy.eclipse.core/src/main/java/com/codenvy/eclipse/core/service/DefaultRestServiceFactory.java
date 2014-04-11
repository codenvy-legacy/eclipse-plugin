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
package com.codenvy.eclipse.core.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.codenvy.eclipse.core.service.api.RestService;
import com.codenvy.eclipse.core.service.api.RestServiceFactory;
import com.codenvy.eclipse.core.service.api.RestServiceWithAuth;
import com.codenvy.eclipse.core.service.api.model.CodenvyToken;

/**
 * The default {@linkplain RestServiceFactory} implementation.
 * 
 * @author Kevin Pollet
 */
public class DefaultRestServiceFactory implements RestServiceFactory {
    @Override
    public <T extends RestService> T newRestService(Class<T> clazz, String url) {
        checkNotNull(clazz);

        try {

            final Constructor<T> constructor = clazz.getConstructor(String.class);
            return constructor.newInstance(url);

        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException
            | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T extends RestServiceWithAuth> T newRestServiceWithAuth(Class<T> clazz, String url, CodenvyToken token) {
        checkNotNull(clazz);

        try {

            final Constructor<T> constructor = clazz.getConstructor(String.class, CodenvyToken.class);
            return constructor.newInstance(url, token);

        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException
            | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
