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
package com.codenvy.eclipse.core.test.utils;


import org.junit.Assert;
import org.junit.Test;

import com.codenvy.eclipse.core.exceptions.ServiceUnavailableException;
import com.codenvy.eclipse.core.services.AuthenticationService;
import com.codenvy.eclipse.core.services.RestServiceFactory;
import com.codenvy.eclipse.core.utils.ServiceHelper;
import com.codenvy.eclipse.core.utils.ServiceHelper.ServiceInvoker;

/**
 * {@link ServiceHelper} test.
 * 
 * @author Kevin Pollet
 */
public class ServiceHelperTest {
    @Test(expected = NullPointerException.class)
    public void testInvokeAnOSGiServiceWithANullIServiceClass() throws ServiceUnavailableException {
        ServiceHelper.forService(null);
    }

    @Test(expected = NullPointerException.class)
    public void testInvokeAnOSGiServiceWithANullInvoker() throws ServiceUnavailableException {
        ServiceHelper.forService(RestServiceFactory.class)
                     .invoke(null);
    }

    @Test(expected = ServiceUnavailableException.class)
    public void testInvokeAnUnavailableOSGiService() throws ServiceUnavailableException {
        ServiceHelper.forService(AuthenticationService.class)
                     .invoke(new ServiceInvoker<AuthenticationService, Void>() {
                         @Override
                         public Void run(AuthenticationService service) {
                             return null;
                         }
                     });
    }

    @Test
    public void testInvokeAnAvailableOSGiService() throws ServiceUnavailableException {
        ServiceHelper.forService(RestServiceFactory.class)
                     .invoke(new ServiceInvoker<RestServiceFactory, Void>() {
                         @Override
                         public Void run(RestServiceFactory service) {
                             Assert.assertNotNull(service);
                             return null;
                         }
                     });
    }
}
