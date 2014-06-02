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
import com.codenvy.eclipse.core.services.TokenProvider;

/**
 * {@link TokenProvider} tests.
 * 
 * @author Stéphane Daviet
 */
public class TokenProviderIT extends RestApiBaseIT {
    private static TokenProvider         tokenProvider;
    private static AuthenticationService authenticationService;
    private static SecureStorageService  secureStorageService;

    private static final String          DUMMY_URL      = "http://www.dummy.com";
    private static final String          DUMMY_USERNAME = "dummyUsername";
    private static final String          DUMMY_PASSWORD = "dummyPassword";

    @BeforeClass
    public static void initialize() {
        final BundleContext context = FrameworkUtil.getBundle(WorkspaceServiceIT.class).getBundleContext();
        final ServiceReference<TokenProvider> tokenProviderRef = context.getServiceReference(TokenProvider.class);
        Assert.assertNotNull(tokenProviderRef);

        tokenProvider = context.getService(tokenProviderRef);
        Assert.assertNotNull(tokenProvider);

        final ServiceReference<SecureStorageService> secureStorageServiceRef = context.getServiceReference(SecureStorageService.class);
        Assert.assertNotNull(secureStorageServiceRef);

        secureStorageService = context.getService(secureStorageServiceRef);
        Assert.assertNotNull(secureStorageService);

        final ServiceReference<RestServiceFactory> restServiceFactoryRef = context.getServiceReference(RestServiceFactory.class);
        Assert.assertNotNull(restServiceFactoryRef);

        final RestServiceFactory restServiceFactory = context.getService(restServiceFactoryRef);
        Assert.assertNotNull(restServiceFactory);

        authenticationService = restServiceFactory.newRestService(AuthenticationService.class, REST_API_URL);
        Assert.assertNotNull(authenticationService);
    }

    @Test(expected = NullPointerException.class)
    public void testGetTokenWithNullURL() {
        tokenProvider.getToken(null, DUMMY_USERNAME);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTokenWithEmptyURL() {
        tokenProvider.getToken("", DUMMY_USERNAME);
    }

    @Test(expected = NullPointerException.class)
    public void testGetTokenWithNullUsername() {
        tokenProvider.getToken(DUMMY_URL, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTokenWithEmptyUsername() {
        tokenProvider.getToken(DUMMY_URL, "");
    }

    @Test(expected = NullPointerException.class)
    public void testRenewTokenWithNullURL() {
        tokenProvider.renewToken(null, DUMMY_USERNAME);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRenewTokenWithEmptyURL() {
        tokenProvider.renewToken("", DUMMY_USERNAME);
    }

    @Test(expected = NullPointerException.class)
    public void testRenewTokenWithNullUsername() {
        tokenProvider.renewToken(DUMMY_URL, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRenewTokenWithEmptyUsername() {
        tokenProvider.renewToken(DUMMY_URL, "");
    }

    @Test
    public void testGetToken() {
        final Token tokenPostAuthentication = authenticationService.login(new Credentials(DUMMY_USERNAME, DUMMY_PASSWORD));

        Token token = tokenProvider.getToken(REST_API_URL, DUMMY_USERNAME);
        assertNotNull(token);
        assertEquals(tokenPostAuthentication, token);
    }

    @Test
    public void testRenewToken() {
        secureStorageService.deleteToken(REST_API_URL, DUMMY_USERNAME);

        Token token = tokenProvider.renewToken(REST_API_URL, DUMMY_USERNAME);
        assertNotNull(token);
    }
}
