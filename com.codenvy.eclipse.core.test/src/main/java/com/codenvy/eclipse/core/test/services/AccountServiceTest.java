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

import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import com.codenvy.eclipse.core.model.CodenvyAccount;
import com.codenvy.eclipse.core.services.AccountService;
import com.codenvy.eclipse.core.services.RestServiceFactory;

/**
 * {@link AccountService} test.
 * 
 * @author Kevin Pollet
 */
public class AccountServiceTest extends RestApiBaseTest {
    private static final String   DUMMY_USERNAME  = "dummyUsername";
    private static final String   DUMMY_PASSWORD  = "dummyPassword";
    private static final String   SDK_TOKEN_VALUE = "123123";

    private static AccountService accountService;

    @BeforeClass
    public static void initialize() {
        final BundleContext context = FrameworkUtil.getBundle(AccountServiceTest.class).getBundleContext();
        final ServiceReference<RestServiceFactory> restServiceFactoryRef = context.getServiceReference(RestServiceFactory.class);
        Assert.assertNotNull(restServiceFactoryRef);

        final RestServiceFactory restServiceFactory = context.getService(restServiceFactoryRef);
        Assert.assertNotNull(restServiceFactory);

        accountService = restServiceFactory.newRestServiceWithAuth(AccountService.class, REST_API_URL, DUMMY_USERNAME);
        Assert.assertNotNull(accountService);
    }

    @Test
    @Ignore("The account API is not availbale in the sdk")
    public void testGetCurrentUserAccount() {
        final List<CodenvyAccount> currentUserAccounts = accountService.getCurrentUserAccounts();

        Assert.assertNotNull(currentUserAccounts);
        Assert.assertFalse(currentUserAccounts.isEmpty());
    }
}
