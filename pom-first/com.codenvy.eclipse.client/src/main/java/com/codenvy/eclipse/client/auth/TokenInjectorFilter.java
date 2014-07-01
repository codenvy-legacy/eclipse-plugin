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

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.Provider;

/**
 * Filter used to inject {@link Token} in client request.
 * 
 * @author Kevin Pollet
 */
@Provider
public class TokenInjectorFilter implements ClientRequestFilter {
    public static final String TOKEN_PROPERTY_NAME = "token";

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        final Token token = (Token)requestContext.getProperty(TOKEN_PROPERTY_NAME);
        if (token != null && token.value != null) {

            requestContext.setUri(UriBuilder.fromUri(requestContext.getUri())
                                            .queryParam(TOKEN_PROPERTY_NAME, token.value)
                                            .build());
        }
    }
}
