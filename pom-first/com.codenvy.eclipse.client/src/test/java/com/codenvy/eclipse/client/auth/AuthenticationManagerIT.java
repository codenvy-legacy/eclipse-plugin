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

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Test;

import com.codenvy.eclipse.client.RestClientBaseIT;
import com.codenvy.eclipse.client.store.DataStore;

/**
 * {@link AuthenticationManager} tests.
 * 
 * @author Kevin Pollet
 */
public class AuthenticationManagerIT extends RestClientBaseIT {
    @Test
    public void testAuthorizeWithNullDataStoreAndNullCredentials() {
        final AuthenticationManager authenticationManager = new AuthenticationManager(REST_API_URL, null);

        Assert.assertNull(authenticationManager.authorize(null));
    }

    @Test
    public void testAuthorizeWithNullDataStoreAndCredentials() {
        final AuthenticationManager authenticationManager = new AuthenticationManager(REST_API_URL, null);

        Assert.assertNotNull(authenticationManager.authorize(new Credentials(DUMMY_USERNAME, DUMMY_PASSWORD)));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAuthorizeWithDataStoreAndNullCredentials() {
        final DataStore<String, Credentials> credentialsStore = mock(DataStore.class);
        final AuthenticationManager authenticationManager = new AuthenticationManager(REST_API_URL, credentialsStore);

        Assert.assertNull(authenticationManager.authorize(null));
        verifyZeroInteractions(credentialsStore);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAuthorizeWithDataStoreAndCredentials() {
        final DataStore<String, Credentials> credentialsStore = mock(DataStore.class);
        final AuthenticationManager authenticationManager = new AuthenticationManager(REST_API_URL, credentialsStore);
        final Token token = authenticationManager.authorize(new Credentials(DUMMY_USERNAME, DUMMY_PASSWORD));

        Assert.assertNotNull(token);
        verify(credentialsStore, times(1)).put(eq(DUMMY_USERNAME), eq(new Credentials(DUMMY_PASSWORD, token)));
    }

    @Test
    public void testGetTokenWithNullDataStoreAndNullUsername() {
        final AuthenticationManager authenticationManager = new AuthenticationManager(REST_API_URL, null);

        Assert.assertNull(authenticationManager.getToken(null));
    }

    @Test
    public void testGetTokenWithNullDataStoreAndUsername() {
        final AuthenticationManager authenticationManager = new AuthenticationManager(REST_API_URL, null);

        Assert.assertNull(authenticationManager.getToken(DUMMY_USERNAME));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetTokenWithDataStoreAndNullUsername() {
        final DataStore<String, Credentials> credentialsStore = mock(DataStore.class);
        when(credentialsStore.get(null)).thenReturn(null);

        final AuthenticationManager authenticationManager = new AuthenticationManager(REST_API_URL, credentialsStore);
        final Token token = authenticationManager.getToken(null);

        Assert.assertNull(token);
        verify(credentialsStore, times(1)).get(null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetTokenWithDataStoreAndUsername() {
        final DataStore<String, Credentials> credentialsStore = mock(DataStore.class);
        when(credentialsStore.get(DUMMY_USERNAME)).thenReturn(new Credentials(DUMMY_PASSWORD, new Token(SDK_TOKEN_VALUE)));

        final AuthenticationManager authenticationManager = new AuthenticationManager(REST_API_URL, credentialsStore);
        final Token token = authenticationManager.getToken(DUMMY_USERNAME);

        Assert.assertEquals(new Token(SDK_TOKEN_VALUE), token);
        verify(credentialsStore, times(1)).get(DUMMY_USERNAME);
    }

    @Test
    public void testRefreshTokenWithNullDataStoreAndNullUsername() {
        final AuthenticationManager authenticationManager = new AuthenticationManager(REST_API_URL, null);

        Assert.assertNull(authenticationManager.refreshToken(null));
    }

    @Test
    public void testRefreshTokenWithNullDataStoreAndUsername() {
        final AuthenticationManager authenticationManager = new AuthenticationManager(REST_API_URL, null);

        Assert.assertNull(authenticationManager.refreshToken(DUMMY_USERNAME));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRefreshTokenWithDataStoreAndNullUsername() {
        final DataStore<String, Credentials> credentialsStore = mock(DataStore.class);
        when(credentialsStore.get(null)).thenReturn(null);

        final AuthenticationManager authenticationManager = new AuthenticationManager(REST_API_URL, credentialsStore);

        Assert.assertNull(authenticationManager.refreshToken(null));
        verify(credentialsStore, times(1)).get(null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRefreshTokenWithDataStoreAndUsername() {
        final DataStore<String, Credentials> credentialsStore = mock(DataStore.class);
        when(credentialsStore.get(DUMMY_USERNAME)).thenReturn(new Credentials(DUMMY_PASSWORD, new Token(SDK_TOKEN_VALUE)));

        final AuthenticationManager authenticationManager = new AuthenticationManager(REST_API_URL, credentialsStore);
        final Token token = authenticationManager.refreshToken(DUMMY_USERNAME);

        Assert.assertEquals(new Token(SDK_TOKEN_VALUE), token);
        verify(credentialsStore, times(1)).get(DUMMY_USERNAME);
        verify(credentialsStore, times(1)).put(eq(DUMMY_USERNAME), eq(new Credentials(DUMMY_PASSWORD, new Token(SDK_TOKEN_VALUE))));
    }
}
