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

import com.codenvy.eclipse.core.service.api.AuthenticationService;
import com.codenvy.eclipse.core.service.api.RestServiceFactory;
import com.codenvy.eclipse.core.service.api.model.CodenvyToken;

/**
 * Test the authentication service.
 * 
 * @author Kevin Pollet
 */
public class AuthenticationServiceTest extends RestApiBaseTest {
    private AuthenticationService authenticationService;

    @Before
    public void initialize() {
        final BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();
        final ServiceReference<RestServiceFactory> restServiceFactoryRef = context.getServiceReference(RestServiceFactory.class);
        Assert.assertNotNull(restServiceFactoryRef);

        final RestServiceFactory restServiceFactory = context.getService(restServiceFactoryRef);
        Assert.assertNotNull(restServiceFactory);

        authenticationService = restServiceFactory.newRestService(AuthenticationService.class, REST_API_URL);
    }

    @Test(expected = NullPointerException.class)
    public void testLoginWithNullUsername() {
        authenticationService.login(null, "codenvy");
    }

    @Test(expected = NullPointerException.class)
    public void testLoginWithNullPassword() {
        authenticationService.login("codenvy@codenvy.com", null);
    }

    @Test(expected = NullPointerException.class)
    public void testLoginWithNullUsernameAndPassword() {
        authenticationService.login(null, null);
    }

    @Test
    public void testLogin() {
        final CodenvyToken token = authenticationService.login("codenvy@codenvy.com", "codenvy");

        Assert.assertNotNull(token);
        Assert.assertNotNull(token.value);
    }
}
