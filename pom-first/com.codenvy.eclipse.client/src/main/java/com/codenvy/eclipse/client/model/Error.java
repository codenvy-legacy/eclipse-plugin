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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The codenvy error envelope object model.
 * 
 * @author Kevin Pollet
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Error {
    public final String message;

    /**
     * Constructs an instance of {@link Error}.
     * 
     * @param message the error message.
     * @throws NullPointerException if message is {@code null}.
     */
    @JsonCreator
    public Error(@JsonProperty("status") int status, @JsonProperty("message") String message) {
        checkNotNull(message);

        this.message = message;
    }
}
