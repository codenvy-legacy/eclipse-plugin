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
package com.codenvy.eclipse.core.model;

import static com.google.common.base.Preconditions.checkNotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class represents the user resource on Codenvy.
 * 
 * @author Kevin Pollet
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    public final String id;
    public final String password;
    public final String email;

    /**
     * Construct an instance of {@linkplain User}.
     * 
     * @param id the user id.
     * @param password the user password.
     * @param email the user email.
     * @throws NullPointerException if id, password or email parameter is {@code null}.
     */
    @JsonCreator
    public User(@JsonProperty("id") String id, @JsonProperty("password") String password, @JsonProperty("email") String email) {
        checkNotNull(id);
        checkNotNull(password);
        checkNotNull(email);

        this.id = id;
        this.email = email;
        this.password = password;
    }
}
