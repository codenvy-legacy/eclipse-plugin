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

import com.codenvy.eclipse.core.model.CodenvyCredentials;
import com.codenvy.eclipse.core.model.CodenvyToken;
import com.codenvy.eclipse.core.services.AuthenticationService;
import com.codenvy.eclipse.core.services.RestServiceFactory;

/**
 * {@link AuthenticationService} test.
 * 
 * @author Kevin Pollet
 */
public class AuthenticationServiceTest extends RestApiBaseTest {
    private static final String          USERNAME  = "codenvy@codenvy.com";
    private static final String          PASSWORD  = "password";
    private static final CodenvyToken    SDK_TOKEN = new CodenvyToken("123123");

    private static AuthenticationService authenticationService;

    @BeforeClass
    public static void initialize() {
        final BundleContext context = FrameworkUtil.getBundle(AuthenticationServiceTest.class).getBundleContext();
        final ServiceReference<RestServiceFactory> restServiceFactoryRef = context.getServiceReference(RestServiceFactory.class);
        Assert.assertNotNull(restServiceFactoryRef);

        final RestServiceFactory restServiceFactory = context.getService(restServiceFactoryRef);
        Assert.assertNotNull(restServiceFactory);

        authenticationService = restServiceFactory.newRestService(AuthenticationService.class, REST_API_URL);
        Assert.assertNotNull(authenticationService);
    }

    @Test(expected = NullPointerException.class)
    public void testLoginWithNullCredentials() {
        authenticationService.login(null);
    }

    @Test
    public void testLogin() {
        final CodenvyToken token = authenticationService.login(new CodenvyCredentials(USERNAME, PASSWORD));

        Assert.assertNotNull(token);
        Assert.assertEquals(SDK_TOKEN, token);
    }
}
