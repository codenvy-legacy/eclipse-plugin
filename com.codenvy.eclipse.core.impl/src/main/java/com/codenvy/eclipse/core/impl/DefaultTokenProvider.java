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

import static com.codenvy.eclipse.core.utils.StringHelper.isNullOrEmpty;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import com.codenvy.eclipse.core.model.CodenvyToken;
import com.codenvy.eclipse.core.services.AuthenticationService;
import com.codenvy.eclipse.core.services.RestServiceFactory;
import com.codenvy.eclipse.core.services.SecureStorageService;
import com.codenvy.eclipse.core.services.TokenProvider;

/**
 * @author Stéphane Daviet
 */
public class DefaultTokenProvider implements TokenProvider {
    @Override
    public CodenvyToken getToken(String url, String username) {
        checkNotNull(url);
        checkArgument(!isNullOrEmpty(url));
        checkNotNull(username);
        checkArgument(!isNullOrEmpty(username));


        final BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();

        final ServiceReference<SecureStorageService> codenvySecureStorageServiceRef =
                                                                                      context.getServiceReference(SecureStorageService.class);
        if (codenvySecureStorageServiceRef != null) {
            final SecureStorageService codenvySecureStorageService =
                                                                     context.getService(codenvySecureStorageServiceRef);
            return codenvySecureStorageService.getToken(url, username);
        }
        return null;
    }

    @Override
    public CodenvyToken renewToken(String url, String username) {
        checkNotNull(url);
        checkArgument(!isNullOrEmpty(url));
        checkNotNull(username);
        checkArgument(!isNullOrEmpty(username));

        final BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();

        final ServiceReference<SecureStorageService> codenvySecureStorageServiceRef =
                                                                                      context.getServiceReference(SecureStorageService.class);
        if (codenvySecureStorageServiceRef != null) {
            final SecureStorageService codenvySecureStorageService =
                                                                     context.getService(codenvySecureStorageServiceRef);

            final ServiceReference<RestServiceFactory> restServiceFactoryRef =
                                                                               context.getServiceReference(RestServiceFactory.class);
            if (restServiceFactoryRef != null) {
                final RestServiceFactory restServiceFactory =
                                                              context.getService(restServiceFactoryRef);
                AuthenticationService authenticationService =
                                                              restServiceFactory.newRestService(AuthenticationService.class,
                                                                                                url);
                return authenticationService.login(codenvySecureStorageService.getCredentials(url, username));
            }
        }
        return null;
    }
}
