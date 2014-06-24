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

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;

import com.codenvy.eclipse.client.auth.AuthenticationException;
import com.codenvy.eclipse.client.auth.Credentials;
import com.codenvy.eclipse.client.auth.CredentialsProvider;
import com.codenvy.eclipse.client.auth.Token;

/**
 * {@link AbstractClient} tests.
 * 
 * @author Kevin Pollet
 */
public class AbstractClientIT extends RestClientBaseIT {
    @Test(expected = NullPointerException.class)
    public void testNewAbstractClientWithNullURL() {
        final Credentials credentials = new Credentials(DUMMY_USERNAME, DUMMY_PASSWORD);
        final CredentialsProvider credentialsProvider = new CredentialsProvider(REST_API_URL, null);

        new AbstractClient(null, "dummyAPI", DUMMY_USERNAME, credentials, credentialsProvider) {
        };
    }

    @Test(expected = NullPointerException.class)
    public void testNewAbstractClientWithNullAPIName() {
        final Credentials credentials = new Credentials(DUMMY_USERNAME, DUMMY_PASSWORD);
        final CredentialsProvider credentialsProvider = new CredentialsProvider(REST_API_URL, null);

        new AbstractClient(REST_API_URL, null, DUMMY_USERNAME, credentials, credentialsProvider) {
        };
    }

    @Test(expected = NullPointerException.class)
    public void testNewAbstractClientWithNullUsername() {
        final Credentials credentials = new Credentials(DUMMY_USERNAME, DUMMY_PASSWORD);
        final CredentialsProvider credentialsProvider = new CredentialsProvider(REST_API_URL, null);

        new AbstractClient(REST_API_URL, "dummyAPI", null, credentials, credentialsProvider) {
        };
    }

    @Test
    public void testNewAbstractClientWithNullCredendials() {
        final CredentialsProvider credentialsProvider = new CredentialsProvider(REST_API_URL, null);

        new AbstractClient(REST_API_URL, "dummyAPI", DUMMY_USERNAME, null, credentialsProvider) {
        };
    }

    @Test(expected = NullPointerException.class)
    public void testNewAbstractClientWithNullCredendialsProvider() {
        final Credentials credentials = new Credentials(DUMMY_USERNAME, DUMMY_PASSWORD);

        new AbstractClient(REST_API_URL, "dummyAPI", DUMMY_USERNAME, credentials, null) {
        };
    }

    @Test
    public void testAbstractClientAuthenticationFilterWithStoredCredentials() {
        final CredentialsProvider credentialsProvider = mock(CredentialsProvider.class);
        when(credentialsProvider.getToken(DUMMY_USERNAME)).thenReturn(new Token(SDK_TOKEN_VALUE));

        final AbstractClient client = new AbstractClient(REST_API_URL, "user", DUMMY_USERNAME, null, credentialsProvider) {
        };

        // dummy request
        client.getWebTarget()
              .path("current")
              .request()
              .accept(APPLICATION_JSON)
              .get();

        verify(credentialsProvider, times(1)).getToken(DUMMY_USERNAME);
        verify(credentialsProvider, times(0)).authorize(eq(new Credentials(DUMMY_USERNAME, DUMMY_PASSWORD)));
    }

    @Test
    public void testAbstractClientAuthenticationFilterWithNoCredentialsStoredButProvided() {
        final Credentials credentials = new Credentials(DUMMY_USERNAME, DUMMY_PASSWORD);
        final CredentialsProvider credentialsProvider = mock(CredentialsProvider.class);
        when(credentialsProvider.getToken(DUMMY_USERNAME)).thenReturn(null);
        when(credentialsProvider.authorize(credentials)).thenReturn(new Token(SDK_TOKEN_VALUE));

        final AbstractClient client = new AbstractClient(REST_API_URL, "user", DUMMY_USERNAME, credentials, credentialsProvider) {
        };

        // dummy request
        client.getWebTarget()
              .path("current")
              .request()
              .accept(APPLICATION_JSON)
              .get();

        verify(credentialsProvider, times(1)).getToken(DUMMY_USERNAME);
        verify(credentialsProvider, times(1)).authorize(eq(new Credentials(DUMMY_USERNAME, DUMMY_PASSWORD)));
    }

    @Test(expected = AuthenticationException.class)
    public void testAbstractClientAuthenticationFilterWithNoCredentialsStoredAndNoProvided() {
        final CredentialsProvider credentialsProvider = mock(CredentialsProvider.class);
        when(credentialsProvider.getToken(DUMMY_USERNAME)).thenReturn(null);

        final AbstractClient client = new AbstractClient(REST_API_URL, "user", DUMMY_USERNAME, null, credentialsProvider) {
        };

        // dummy request
        client.getWebTarget()
              .path("current")
              .request()
              .accept(APPLICATION_JSON)
              .get();
    }
}
