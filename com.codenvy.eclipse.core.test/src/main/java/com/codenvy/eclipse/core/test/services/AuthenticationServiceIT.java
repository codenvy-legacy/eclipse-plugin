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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import com.codenvy.eclipse.core.model.Credentials;
import com.codenvy.eclipse.core.model.Token;
import com.codenvy.eclipse.core.services.AuthenticationService;
import com.codenvy.eclipse.core.services.RestServiceFactory;
import com.codenvy.eclipse.core.services.SecureStorageService;

/**
 * {@link AuthenticationService} tests.
 * 
 * @author Kevin Pollet
 */
public class AuthenticationServiceIT extends RestApiBaseIT {
    private static final String          USERNAME  = "codenvy@codenvy.com";
    private static final String          PASSWORD  = "password";
    private static final Token    SDK_TOKEN = new Token("123123");

    private static AuthenticationService authenticationService;
    private static SecureStorageService  secureStorageService;

    @BeforeClass
    public static void initialize() {
        final BundleContext context = FrameworkUtil.getBundle(AuthenticationServiceIT.class).getBundleContext();
        final ServiceReference<RestServiceFactory> restServiceFactoryRef = context.getServiceReference(RestServiceFactory.class);
        Assert.assertNotNull(restServiceFactoryRef);

        final RestServiceFactory restServiceFactory = context.getService(restServiceFactoryRef);
        Assert.assertNotNull(restServiceFactory);

        authenticationService = restServiceFactory.newRestService(AuthenticationService.class, REST_API_URL);
        Assert.assertNotNull(authenticationService);

        final ServiceReference<SecureStorageService> secureStorageServiceRef = context.getServiceReference(SecureStorageService.class);
        Assert.assertNotNull(secureStorageServiceRef);

        secureStorageService = context.getService(secureStorageServiceRef);
        Assert.assertNotNull(secureStorageService);
    }

    @Test(expected = NullPointerException.class)
    public void testLoginWithNullCredentials() {
        authenticationService.login(null);
    }

    @Test(expected = NullPointerException.class)
    public void testLoginWithNullCredentialsStoreOrNot() {
        authenticationService.login(null, true);
    }

    @Test
    public void testLoginDefault() {
        final Token token = authenticationService.login(new Credentials(USERNAME, PASSWORD));

        Assert.assertNotNull(token);
        Assert.assertEquals(SDK_TOKEN, token);
    }

    @Test
    public void testLoginStoreCredentials() {
        secureStorageService.deleteCredentials(REST_API_URL, USERNAME);

        final Token tokenPostAuthentication = authenticationService.login(new Credentials(USERNAME, PASSWORD), true);

        Assert.assertNotNull(tokenPostAuthentication);
        Assert.assertEquals(SDK_TOKEN, tokenPostAuthentication);

        Token token = secureStorageService.getToken(REST_API_URL, USERNAME);
        assertNotNull(token);
        assertEquals(tokenPostAuthentication, token);

        String password = secureStorageService.getPassword(REST_API_URL, USERNAME);
        assertNotNull(password);
        assertEquals(PASSWORD, password);
    }

    @Test
    public void testLoginDonNotStoreCredentials() {
        secureStorageService.deleteCredentials(REST_API_URL, USERNAME);

        final Token tokenPostAuthentication = authenticationService.login(new Credentials(USERNAME, PASSWORD), false);

        Assert.assertNotNull(tokenPostAuthentication);
        Assert.assertEquals(SDK_TOKEN, tokenPostAuthentication);

        Token token = secureStorageService.getToken(REST_API_URL, USERNAME);
        assertNull(token);

        String password = secureStorageService.getPassword(REST_API_URL, USERNAME);
        assertNull(password);

    }
}
