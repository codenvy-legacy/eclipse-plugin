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
package com.codenvy.eclipse.ui.test.mocks;

import com.codenvy.eclipse.core.client.UserClient;
import com.codenvy.eclipse.core.client.model.User;

/**
 * {@link UserClient} mock.
 * 
 * @author Kevin Pollet
 */
public class UserServiceMock implements UserClient {
    public static final String MOCK_USER_ID  = "user-codenvy-id";
    public static final String MOCK_USERNAME = "johndoe";

    public UserServiceMock(String url, String username) {
    }

    @Override
    public User current() {
        return new User(MOCK_USER_ID, "<none>", "johndoe");
    }
}
