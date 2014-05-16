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
package com.codenvy.eclipse.core.services;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.ws.rs.client.WebTarget;

import com.codenvy.eclipse.core.model.CodenvyToken;

/**
 * Abstract rest service with authentication implementation.
 * 
 * @author Kevin Pollet
 * @see RestServiceFactory
 */
public class AbstractRestServiceWithAuth extends AbstractRestService {
    private static final String TOKEN_PARAMETER_NAME = "token";

    private final CodenvyToken  codenvyToken;

    /**
     * Constructs an instance of {@linkplain AbstractRestServiceWithAuth}.
     * 
     * @param url the Codenvy platform url.
     * @param rootPath the rest service root path
     * @param codenvyToken the Codenvy authentication token.
     * @throws NullPointerException if url, rootPath or codenvyToken parameter is {@code null}.
     * @throws IllegalArgumentException if url parameter is an empty {@linkplain String}.
     */
    public AbstractRestServiceWithAuth(String url, String rootPath,
                                       CodenvyToken codenvyToken) {
        super(url, rootPath);

        checkNotNull(codenvyToken);

        this.codenvyToken = codenvyToken;
    }

    public CodenvyToken getCodenvyToken() {
        return codenvyToken;
    }

    @Override
    public WebTarget getWebTarget() {
        return super.getWebTarget().queryParam(TOKEN_PARAMETER_NAME, codenvyToken.value);
    }
}
