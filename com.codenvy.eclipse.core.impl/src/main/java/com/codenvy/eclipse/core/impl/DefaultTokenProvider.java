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

import static com.codenvy.eclipse.core.utils.StringHelper.isEmpty;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.codenvy.eclipse.core.exceptions.ServiceUnavailableException;
import com.codenvy.eclipse.core.model.Token;
import com.codenvy.eclipse.core.services.AuthenticationService;
import com.codenvy.eclipse.core.services.RestServiceFactory;
import com.codenvy.eclipse.core.services.SecureStorageService;
import com.codenvy.eclipse.core.services.TokenProvider;
import com.codenvy.eclipse.core.utils.ServiceHelper;
import com.codenvy.eclipse.core.utils.ServiceHelper.ServiceInvoker;

/**
 * @author Stéphane Daviet
 */
public class DefaultTokenProvider implements TokenProvider {
    @Override
    public Token getToken(final String url, final String username) {
        checkNotNull(url);
        checkArgument(!isEmpty(url));
        checkNotNull(username);
        checkArgument(!isEmpty(username));

        try {

            return ServiceHelper.forService(SecureStorageService.class)
                                .invoke(new ServiceInvoker<SecureStorageService, Token>() {
                                    @Override
                                    public Token run(SecureStorageService service) {
                                        return service.getToken(url, username);
                                    }
                                });

        } catch (ServiceUnavailableException e) {
            // TODO do something if service is unavailable
            throw new RuntimeException(e);
        }
    }

    @Override
    public Token renewToken(final String url, final String username) {
        checkNotNull(url);
        checkArgument(!isEmpty(url));
        checkNotNull(username);
        checkArgument(!isEmpty(username));

        try {

            return ServiceHelper.forService(SecureStorageService.class)
                                .invoke(new ServiceInvoker<SecureStorageService, Token>() {
                                    @Override
                                    public Token run(final SecureStorageService secureStorageService) {
                                        try {

                                            return ServiceHelper.forService(RestServiceFactory.class)
                                                                .invoke(new ServiceInvoker<RestServiceFactory, Token>() {
                                                                    @Override
                                                                    public Token run(RestServiceFactory factory) {
                                                                        final AuthenticationService authenticationService =
                                                                                                                            factory.newRestService(AuthenticationService.class,
                                                                                                                                                   url);
                                                                        return authenticationService.login(secureStorageService.getCredentials(url,
                                                                                                                                               username));
                                                                    }
                                                                });

                                        } catch (ServiceUnavailableException e) {
                                            // TODO do something if service is unavailable
                                            throw new RuntimeException(e);
                                        }
                                    }
                                });

        } catch (ServiceUnavailableException e) {
            // TODO do something if service is unavailable
            throw new RuntimeException(e);
        }
    }
}
