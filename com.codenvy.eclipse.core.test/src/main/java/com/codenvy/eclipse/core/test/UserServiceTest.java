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
package com.codenvy.eclipse.core.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import com.codenvy.eclipse.core.service.api.RestServiceFactory;
import com.codenvy.eclipse.core.service.api.UserService;
import com.codenvy.eclipse.core.service.api.model.User;

/**
 * Test the user service.
 * 
 * @author Kevin Pollet
 */
public class UserServiceTest extends RestApiBaseTest {
    private UserService userService;

    @Before
    public void initialize() {
        final BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();
        final ServiceReference<RestServiceFactory> restServiceFactoryRef = context.getServiceReference(RestServiceFactory.class);
        Assert.assertNotNull(restServiceFactoryRef);

        final RestServiceFactory restServiceFactory = context.getService(restServiceFactoryRef);
        Assert.assertNotNull(restServiceFactory);

        userService = restServiceFactory.newRestService(UserService.class, REST_API_URL);
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
