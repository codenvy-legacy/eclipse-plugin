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
package com.codenvy.eclipse.core.client.store;

import static com.google.common.base.Preconditions.checkNotNull;

import com.codenvy.eclipse.core.client.model.Token;

/**
 * The credentials stored in the data store.
 * 
 * @author Kevin Pollet
 */
public class StoredCredentials {
    public final String password;
    public final Token  token;

    /**
     * Constructs an instance {@link StoredCredentials}.
     * 
     * @param password the user password.
     * @param token the user token.
     * @throws NullPointerException if passord or token parameter is {@code null}.
     */
    public StoredCredentials(String password, Token token) {
        checkNotNull(password);
        checkNotNull(token);

        this.password = password;
        this.token = token;
    }
}
