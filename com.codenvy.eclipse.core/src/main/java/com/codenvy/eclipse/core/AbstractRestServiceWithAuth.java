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
package com.codenvy.eclipse.core;

import static com.google.common.base.Preconditions.checkNotNull;

import com.codenvy.eclipse.core.model.CodenvyToken;

/**
 * Abstract rest service with authentication implementation.
 * 
 * @author Kevin Pollet
 * @see RestServiceFactory
 */
public class AbstractRestServiceWithAuth extends AbstractRestService {
    private final CodenvyToken codenvyToken;

    /**
     * Constructs an instance of {@linkplain AbstractRestServiceWithAuth}.
     * 
     * @param url the Codenvy platform url.
     * @param codenvyToken the Codenvy authentication token.
     * @throws NullPointerException if url or codenvyToken parameter is {@code null}.
     * @throws IllegalArgumentException if url parameter is an empty {@linkplain String}.
     */
    public AbstractRestServiceWithAuth(String url, CodenvyToken codenvyToken) {
        super(url);

        checkNotNull(codenvyToken);

        this.codenvyToken = codenvyToken;
    }

    public CodenvyToken getCodenvyToken() {
        return codenvyToken;
    }
}
