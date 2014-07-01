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

import static com.codenvy.eclipse.client.auth.TokenInjectorFilter.TOKEN_PROPERTY_NAME;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.client.ClientRequestContext;

import org.junit.Assert;
import org.junit.Test;

/**
 * {@link TokenInjectorFilter} tests.
 * 
 * @author Kevin Pollet
 */
public class TokenInjectorFilterTest {
    private final URI dummyUri;
    private final URI dummyUriWithToken;

    public TokenInjectorFilterTest() throws URISyntaxException {
        dummyUri = new URI("http://dummy.com");
        dummyUriWithToken = new URI("http://dummy.com/?token=123123");
    }

    @Test
    public void testFilterWithoutTokenProperty() throws IOException {
        final ClientRequestContext clientRequestContext = mock(ClientRequestContext.class);
        when(clientRequestContext.getProperty(TOKEN_PROPERTY_NAME)).thenReturn(null);
        when(clientRequestContext.getUri()).thenReturn(dummyUri);

        final TokenInjectorFilter tokenInjectorFilter = new TokenInjectorFilter();
        tokenInjectorFilter.filter(clientRequestContext);

        verify(clientRequestContext, times(0)).getUri();
        verify(clientRequestContext, times(0)).setUri(any(URI.class));
        verify(clientRequestContext, times(1)).getProperty(TOKEN_PROPERTY_NAME);

        Assert.assertEquals(dummyUri, clientRequestContext.getUri());
    }

    @Test
    public void testFilterWithTokenProperty() throws IOException {
        final ClientRequestContext clientRequestContext = mock(ClientRequestContext.class);
        when(clientRequestContext.getProperty(TOKEN_PROPERTY_NAME)).thenReturn(new Token("123123"));
        when(clientRequestContext.getUri()).thenReturn(dummyUri);

        final TokenInjectorFilter tokenInjectorFilter = new TokenInjectorFilter();
        tokenInjectorFilter.filter(clientRequestContext);

        verify(clientRequestContext, times(1)).getUri();
        verify(clientRequestContext, times(1)).setUri(dummyUriWithToken);
        verify(clientRequestContext, times(1)).getProperty(TOKEN_PROPERTY_NAME);
    }
}
