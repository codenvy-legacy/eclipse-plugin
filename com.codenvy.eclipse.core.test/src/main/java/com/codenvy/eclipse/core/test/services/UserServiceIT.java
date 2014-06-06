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
package com.codenvy.eclipse.core.test.services;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import com.codenvy.eclipse.core.model.Credentials;
import com.codenvy.eclipse.core.model.Token;
import com.codenvy.eclipse.core.model.User;
import com.codenvy.eclipse.core.services.RestServiceFactory;
import com.codenvy.eclipse.core.services.SecureStorageService;
import com.codenvy.eclipse.core.services.UserService;

/**
 * {@link UserService} tests.
 * 
 * @author Kevin Pollet
 * @author Stéphane Daviet
 */
public class UserServiceIT extends RestApiBaseIT {
    private static final String DUMMY_USERNAME  = "dummyUsername";
    private static final String DUMMY_PASSWORD  = "dummyPassword";
    private static final String SDK_TOKEN_VALUE = "123123";

    private static UserService  userService;

    @BeforeClass
    public static void initialize() {
        final BundleContext context = FrameworkUtil.getBundle(UserServiceIT.class).getBundleContext();
        final ServiceReference<RestServiceFactory> restServiceFactoryRef = context.getServiceReference(RestServiceFactory.class);
        Assert.assertNotNull(restServiceFactoryRef);

        final RestServiceFactory restServiceFactory = context.getService(restServiceFactoryRef);
        Assert.assertNotNull(restServiceFactory);

        userService = restServiceFactory.newRestServiceWithAuth(UserService.class, REST_API_URL, DUMMY_USERNAME);
        Assert.assertNotNull(userService);

        final ServiceReference<SecureStorageService> secureStorageServiceRef = context.getServiceReference(SecureStorageService.class);
        Assert.assertNotNull(secureStorageServiceRef);

        final SecureStorageService secureStorageService = context.getService(secureStorageServiceRef);
        Assert.assertNotNull(secureStorageService);

        secureStorageService.storeCredentials(REST_API_URL, new Credentials(DUMMY_USERNAME, DUMMY_PASSWORD),
                                              new Token(SDK_TOKEN_VALUE));
    }

    @Test
    public void testGetCurrentUser() {
        final User currentUser = userService.getCurrentUser();

        Assert.assertNotNull(currentUser);
        Assert.assertNotNull(currentUser.id);
        Assert.assertNotNull(currentUser.password);
        Assert.assertNotNull(currentUser.email);
    }
}