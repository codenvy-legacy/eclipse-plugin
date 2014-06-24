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
package com.codenvy.eclipse.client;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Test;

import com.codenvy.eclipse.client.model.Credentials;
import com.codenvy.eclipse.client.model.Token;
import com.codenvy.eclipse.client.store.DataStore;

/**
 * {@link CredentialsProvider} tests.
 * 
 * @author Kevin Pollet
 */
public class CredentialsProviderIT extends RestClientBaseIT {
    @Test
    public void testAuthorizeWithNullDataStoreAndNullCredentials() {
        final CredentialsProvider credentialsProvider = new CredentialsProvider(REST_API_URL, null);

        Assert.assertNull(credentialsProvider.authorize(null));
    }

    @Test
    public void testAuthorizeWithNullDataStoreAndCredentials() {
        final CredentialsProvider credentialsProvider = new CredentialsProvider(REST_API_URL, null);

        Assert.assertNotNull(credentialsProvider.authorize(new Credentials(DUMMY_USERNAME, DUMMY_PASSWORD)));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAuthorizeWithDataStoreAndNullCredentials() {
        final DataStore<String, Credentials> credentialsStore = mock(DataStore.class);
        final CredentialsProvider credentialsProvider = new CredentialsProvider(REST_API_URL, credentialsStore);

        Assert.assertNull(credentialsProvider.authorize(null));
        verifyZeroInteractions(credentialsStore);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAuthorizeWithDataStoreAndCredentials() {
        final DataStore<String, Credentials> credentialsStore = mock(DataStore.class);
        final CredentialsProvider credentialsProvider = new CredentialsProvider(REST_API_URL, credentialsStore);
        final Token token = credentialsProvider.authorize(new Credentials(DUMMY_USERNAME, DUMMY_PASSWORD));

        Assert.assertNotNull(token);
        verify(credentialsStore, times(1)).put(eq(DUMMY_USERNAME), eq(new Credentials(DUMMY_PASSWORD, token)));
    }

    @Test
    public void testGetTokenWithNullDataStoreAndNullUsername() {
        final CredentialsProvider credentialsProvider = new CredentialsProvider(REST_API_URL, null);

        Assert.assertNull(credentialsProvider.getToken(null));
    }

    @Test
    public void testGetTokenWithNullDataStoreAndUsername() {
        final CredentialsProvider credentialsProvider = new CredentialsProvider(REST_API_URL, null);

        Assert.assertNull(credentialsProvider.getToken(DUMMY_USERNAME));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetTokenWithDataStoreAndNullUsername() {
        final DataStore<String, Credentials> credentialsStore = mock(DataStore.class);
        when(credentialsStore.get(null)).thenReturn(null);

        final CredentialsProvider credentialsProvider = new CredentialsProvider(REST_API_URL, credentialsStore);
        final Token token = credentialsProvider.getToken(null);

        Assert.assertNull(token);
        verify(credentialsStore, times(1)).get(null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetTokenWithDataStoreAndUsername() {
        final DataStore<String, Credentials> credentialsStore = mock(DataStore.class);
        when(credentialsStore.get(DUMMY_USERNAME)).thenReturn(new Credentials(DUMMY_PASSWORD, new Token(SDK_TOKEN_VALUE)));

        final CredentialsProvider credentialsProvider = new CredentialsProvider(REST_API_URL, credentialsStore);
        final Token token = credentialsProvider.getToken(DUMMY_USERNAME);

        Assert.assertEquals(new Token(SDK_TOKEN_VALUE), token);
        verify(credentialsStore, times(1)).get(DUMMY_USERNAME);
    }

    @Test
    public void testRefreshTokenWithNullDataStoreAndNullUsername() {
        final CredentialsProvider credentialsProvider = new CredentialsProvider(REST_API_URL, null);

        Assert.assertNull(credentialsProvider.refreshToken(null));
    }

    @Test
    public void testRefreshTokenWithNullDataStoreAndUsername() {
        final CredentialsProvider credentialsProvider = new CredentialsProvider(REST_API_URL, null);

        Assert.assertNull(credentialsProvider.refreshToken(DUMMY_USERNAME));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRefreshTokenWithDataStoreAndNullUsername() {
        final DataStore<String, Credentials> credentialsStore = mock(DataStore.class);
        when(credentialsStore.get(null)).thenReturn(null);

        final CredentialsProvider credentialsProvider = new CredentialsProvider(REST_API_URL, credentialsStore);

        Assert.assertNull(credentialsProvider.refreshToken(null));
        verify(credentialsStore, times(1)).get(null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRefreshTokenWithDataStoreAndUsername() {
        final DataStore<String, Credentials> credentialsStore = mock(DataStore.class);
        when(credentialsStore.get(DUMMY_USERNAME)).thenReturn(new Credentials(DUMMY_PASSWORD, new Token(SDK_TOKEN_VALUE)));

        final CredentialsProvider credentialsProvider = new CredentialsProvider(REST_API_URL, credentialsStore);
        final Token token = credentialsProvider.refreshToken(DUMMY_USERNAME);

        Assert.assertEquals(new Token(SDK_TOKEN_VALUE), token);
        verify(credentialsStore, times(1)).get(DUMMY_USERNAME);
        verify(credentialsStore, times(1)).put(eq(DUMMY_USERNAME), eq(new Credentials(DUMMY_PASSWORD, new Token(SDK_TOKEN_VALUE))));
    }
}
