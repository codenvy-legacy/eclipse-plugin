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
package com.codenvy.eclipse.client;

import org.junit.Assert;
import org.junit.Test;

import com.codenvy.eclipse.client.model.User;

/**
 * {@linkplain com.codenvy.eclipse.client.UserClient UserService} tests.
 * 
 * @author Kevin Pollet
 * @author Stéphane Daviet
 */
public class UserClientIT extends RestClientBaseIT {
    @Test
    public void testGetCurrentUser() {
        final User currentUser = codenvy.user()
                                        .current()
                                        .execute();

        Assert.assertNotNull(currentUser);
        Assert.assertNotNull(currentUser.id);
        Assert.assertNotNull(currentUser.password);
        Assert.assertNotNull(currentUser.email);
    }
}
