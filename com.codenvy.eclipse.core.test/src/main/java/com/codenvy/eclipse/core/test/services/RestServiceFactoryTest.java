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
import com.codenvy.eclipse.core.services.AuthenticationService;
import com.codenvy.eclipse.core.services.RestServiceFactory;
import com.codenvy.eclipse.core.services.WorkspaceService;

/**
 * {@link RestServiceFactory} test.
 * 
 * @author Kevin Pollet
 */
public class RestServiceFactoryTest {
    private static final String       CODENVY_URL     = "http://dummy.com";
    private static final String       SDK_TOKEN_VALUE = "123123";

    private static RestServiceFactory restServiceFactory;

    @BeforeClass
    public static void initialize() {
        final BundleContext context = FrameworkUtil.getBundle(RestServiceFactoryTest.class).getBundleContext();
        final ServiceReference<RestServiceFactory> restServiceFactoryRef = context.getServiceReference(RestServiceFactory.class);
        Assert.assertNotNull(restServiceFactoryRef);

        restServiceFactory = context.getService(restServiceFactoryRef);
        Assert.assertNotNull(restServiceFactory);
    }

    @Test(expected = NullPointerException.class)
    public void testNewRestServiceWithNullClass() {
        restServiceFactory.newRestService(null, null);
    }

    @Test
    public void testNewRestService() {
        final AuthenticationService authenticationService =
                                                            restServiceFactory.newRestService(AuthenticationService.class,
                                                                                              CODENVY_URL);

        Assert.assertNotNull(authenticationService);
    }

    @Test(expected = NullPointerException.class)
    public void testNewRestServiceWithAuthWithNullClass() {
        restServiceFactory.newRestServiceWithAuth(null, null, null);
    }

    @Test
    public void testNewRestServiceWithAuth() {
        final WorkspaceService workspaceService =
                                                  restServiceFactory.newRestServiceWithAuth(WorkspaceService.class, CODENVY_URL,
                                                                                            new CodenvyToken(SDK_TOKEN_VALUE));

        Assert.assertNotNull(workspaceService);
    }
}
