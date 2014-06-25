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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.client.ClientRequestContext;

import org.junit.Test;
import org.mockito.Mockito;

import com.codenvy.eclipse.client.AbstractIT;

/**
 * {@link AuthenticationFilter} tests.
 * 
 * @author Kevin Pollet
 */
public class AuthenticationFilterIT extends AbstractIT {
    @Test(expected = NullPointerException.class)
    public void testNewAuthenticationFilterWithNullAuthenticationManager() {
        new AuthenticationFilter(null);
    }

    @Test
    public void testFilterWithStoredCredentials() throws AuthenticationException, IOException, URISyntaxException {
        final AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        when(authenticationManager.getToken()).thenReturn(new Token(SDK_TOKEN_VALUE));

        final ClientRequestContext clientRequestContext = Mockito.mock(ClientRequestContext.class);
        when(clientRequestContext.getUri()).thenReturn(new URI("http://dummy.com"));

        final AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager);

        authenticationFilter.filter(clientRequestContext);

        verify(authenticationManager, times(1)).getToken();
        verify(authenticationManager, times(0)).authorize();
    }

    @Test
    public void testFilterWithoutStoredCredentials() throws AuthenticationException, IOException, URISyntaxException {
        final AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        when(authenticationManager.getToken()).thenReturn(null);
        when(authenticationManager.authorize()).thenReturn(new Token(SDK_TOKEN_VALUE));

        final ClientRequestContext clientRequestContext = Mockito.mock(ClientRequestContext.class);
        when(clientRequestContext.getUri()).thenReturn(new URI("http://dummy.com"));

        final AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager);

        authenticationFilter.filter(clientRequestContext);

        verify(authenticationManager, times(1)).getToken();
        verify(authenticationManager, times(1)).authorize();
    }
}
