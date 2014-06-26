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
package com.codenvy.eclipse.client.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * User credentials used for authentication.
 * 
 * @author Kevin Pollet
 */
@JsonIgnoreProperties(ignoreUnknown = true, value = "token")
public class Credentials {
    public final String  username;
    public final String  password;
    public final Token   token;
    public final boolean storeOnlyToken;

    /**
     * Constructs an instance of {@link Credentials}.
     * 
     * @param username the user name.
     * @param password the user password.
     * @param token the user authentication {@link Token}.
     * @param storeOnlyToken {@code true} if only the authentication {@link Token} must be stored.
     */
    private Credentials(String username, String password, Token token, boolean storeOnlyToken) {
        this.username = username;
        this.password = password;
        this.token = token;
        this.storeOnlyToken = storeOnlyToken;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((password == null) ? 0 : password.hashCode());
        result = prime * result + ((token == null) ? 0 : token.hashCode());
        result = prime * result + ((username == null) ? 0 : username.hashCode());
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
        Credentials other = (Credentials)obj;
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
        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username))
            return false;
        return true;
    }

    /**
     * Builder used to build a {@link Credentials} object.
     * 
     * @author Kevin Pollet
     */
    public static class Builder {
        private String  username;
        private String  password;
        private Token   token;
        private boolean storeOnlyToken;

        /**
         * Defines the user name.
         * 
         * @param username the user name.
         * @return the {@link Builder} instance.
         */
        public Builder withUsername(String username) {
            this.username = username;
            return this;
        }

        /**
         * Defines the user password.
         * 
         * @param password the user password.
         * @return the {@link Builder} instance.
         */
        public Builder withPassword(String password) {
            this.password = password;
            return this;
        }

        /**
         * Defines the authentication {@link Token}.
         * 
         * @param token the authentication {@link Token}.
         * @return the {@link Builder} instance.
         */
        public Builder withToken(Token token) {
            this.token = token;
            return this;
        }

        /**
         * Defines if only the authentication {@link Token} must be stored.
         * 
         * @param storeOnlyToken {@code true} if only the authentication {@link Token} must be stored.
         * @return the {@link Builder} instance.
         */
        public Builder storeOnlyToken(boolean storeOnlyToken) {
            this.storeOnlyToken = storeOnlyToken;
            return this;
        }

        /**
         * Builds the {@link Credentials} object.
         * 
         * @return the created {@link Credentials} object.
         */
        public Credentials build() {
            return new Credentials(username, password, token, storeOnlyToken);
        }
    }
}
