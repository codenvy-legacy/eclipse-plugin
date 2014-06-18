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
package com.codenvy.eclipse.client.store;

import static com.google.common.base.Preconditions.checkNotNull;

import com.codenvy.eclipse.client.model.Token;

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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((password == null) ? 0 : password.hashCode());
        result = prime * result + ((token == null) ? 0 : token.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        StoredCredentials other = (StoredCredentials)obj;
        if (password == null) {
            if (other.password != null)
                return false;
        } else if (!password.equals(other.password))
            return false;
        if (token == null) {
            if (other.token != null)
                return false;
        } else if (!token.equals(other.token))
            return false;
        return true;
    }
}
