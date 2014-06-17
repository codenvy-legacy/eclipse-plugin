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
package com.codenvy.eclipse.client.model;

import static com.google.common.base.Preconditions.checkNotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class represents the user credentials used for authentication.
 * 
 * @author Kevin Pollet
 */
public class Credentials {
    public final String username;
    public final String password;

    /**
     * Construct an instance of {@linkplain Credentials}.
     * 
     * @param username the user username.
     * @param password the user password.
     * @throws NullPointerException if username parameter is {@code null}.
     */
    @JsonCreator
    public Credentials(@JsonProperty("username") String username, @JsonProperty("password") String password) {
        checkNotNull(username);

        this.username = username;
        this.password = password;
    }
}
