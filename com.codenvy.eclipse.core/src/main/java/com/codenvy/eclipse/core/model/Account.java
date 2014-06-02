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

import static com.codenvy.eclipse.core.utils.StringHelper.isEmpty;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The account resource on Codenvy.
 * 
 * @author Kevin Pollet
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Account {
    public final String id;

    /**
     * Constructs an instance of {@link Account}.
     * 
     * @param id the account id.
     * @throws NullPointerException if id parameter is {@code null}.
     * @throws IllegalArgumentException if id parameter is an empty {@link String} or contains only whitespace.
     */
    @JsonCreator
    public Account(@JsonProperty("id") String id) {
        checkNotNull(id);
        checkArgument(!isEmpty(id));

        this.id = id;
    }
}
