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
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Codenvy authentication token class.
 * 
 * @author Kevin Pollet
 */
public class CodenvyToken {
    public final String value;

    /**
     * Constructs an instance of {@linkplain CodenvyToken}.
     * 
     * @param value the authentication token.
     * @throws NullPointerException if the value parameter is {@code null}.
     */
    @JsonCreator
    public CodenvyToken(@JsonProperty("value") String value) {
        checkNotNull(value);
        
        this.value = value;
    }
}
