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

import com.codenvy.eclipse.core.model.CodenvyToken;
import com.codenvy.eclipse.core.model.CodenvyUser;
import com.codenvy.eclipse.core.services.RestServiceFactory;
import com.codenvy.eclipse.core.services.UserService;

/**
 * {@link UserService} test.
 * 
 * @author Kevin Pollet
 */
public class UserServiceTest extends RestApiBaseTest {
    private static final String SDK_TOKEN_VALUE = "123123";

    private static UserService  userService;

    @BeforeClass
    public static void initialize() {
        final BundleContext context = FrameworkUtil.getBundle(UserServiceTest.class).getBundleContext();
        final ServiceReference<RestServiceFactory> restServiceFactoryRef = context.getServiceReference(RestServiceFactory.class);
        Assert.assertNotNull(restServiceFactoryRef);

        final RestServiceFactory restServiceFactory = context.getService(restServiceFactoryRef);
        Assert.assertNotNull(restServiceFactory);

        userService = restServiceFactory.newRestServiceWithAuth(UserService.class, REST_API_URL, new CodenvyToken(SDK_TOKEN_VALUE));
        Assert.assertNotNull(userService);
    }

    @Test
    public void testGetCurrentUser() {
        final CodenvyUser currentUser = userService.getCurrentUser();

        Assert.assertNotNull(currentUser);
        Assert.assertNotNull(currentUser.id);
        Assert.assertNotNull(currentUser.password);
        Assert.assertNotNull(currentUser.email);
    }
}
