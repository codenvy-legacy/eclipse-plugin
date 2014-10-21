/*******************************************************************************
 * Copyright (c) 2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.eclipse.core.store;

import static com.codenvy.eclipse.core.store.SecureStorageDataStore.CODENVY_PASSWORD_KEY_NAME;
import static com.codenvy.eclipse.core.store.SecureStorageDataStore.CODENVY_TOKEN_KEY_NAME;
import static com.codenvy.eclipse.core.store.SecureStorageDataStoreFactory.CODENVY_NODE_NAME;
import static org.eclipse.equinox.security.storage.EncodingUtils.encodeSlashes;

import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.codenvy.client.CodenvyAPI;
import com.codenvy.client.CodenvyClient;
import com.codenvy.client.auth.Credentials;
import com.codenvy.client.store.DataStore;
import com.codenvy.eclipse.core.store.SecureStorageDataStore;

/**
 * {@link SecureStorageDataStore} tests.
 * 
 * @author Kevin Pollet
 */
public class SecureStorageDataStoreTest {
    private static final String      FOO_DATA_STORE_ID = "http://foo.com";

    private static final String      FOO_USERNAME      = "foo";
    private static final String      FOO_PASSWORD      = "fooPassword";
    private static final String      FOO_TOKEN         = "fooToken";

    private static final String      BAR_USERNAME      = "bar";
    private static final String      BAR_PASSWORD      = "barPassword";
    private static final String      BAR_TOKEN         = "barToken";

    private final ISecurePreferences codenvyNode;
    private ISecurePreferences       urlNode;
    private CodenvyClient            codenvyClient;

    public SecureStorageDataStoreTest() {
        final ISecurePreferences root = SecurePreferencesFactory.getDefault();
        this.codenvyNode = root.node(CODENVY_NODE_NAME);
        this.codenvyClient = CodenvyAPI.getClient();
    }

    @Before
    public void before() throws StorageException {
        urlNode = codenvyNode.node(encodeSlashes(FOO_DATA_STORE_ID));
        urlNode.node(BAR_USERNAME).put(CODENVY_PASSWORD_KEY_NAME, BAR_PASSWORD, true);
        urlNode.node(BAR_USERNAME).put(CODENVY_TOKEN_KEY_NAME, BAR_TOKEN, true);
    }

    @After
    public void after() {
        urlNode.removeNode();
    }

    @Test(expected = NullPointerException.class)
    public void testNewWithNullURLNode() {
        new SecureStorageDataStore(null);
    }

    @Test(expected = NullPointerException.class)
    public void testGetWithNullUsername() {
        new SecureStorageDataStore(urlNode).get(null);
    }

    @Test
    public void testGetWithMissingUsername() {
        final DataStore<String, Credentials> dataStore = new SecureStorageDataStore(urlNode);

        Assert.assertNull(dataStore.get(FOO_USERNAME));
    }

    @Test
    public void testGet() {
        final DataStore<String, Credentials> dataStore = new SecureStorageDataStore(urlNode);
        final Credentials storedCredentials = dataStore.get(BAR_USERNAME);

        Assert.assertNotNull(storedCredentials);
        Assert.assertEquals(BAR_PASSWORD, storedCredentials.password());
        Assert.assertEquals(codenvyClient.newTokenBuilder(BAR_TOKEN).build(), storedCredentials.token());
    }

    @Test(expected = NullPointerException.class)
    public void testPutWithNullUsername() {
        final Credentials credentials = codenvyClient.newCredentialsBuilder().withPassword(BAR_PASSWORD)
                                                                 .withToken(codenvyClient.newTokenBuilder(BAR_TOKEN).build())
                                                                 .build();

        new SecureStorageDataStore(urlNode).put(null, credentials);
    }

    @Test(expected = NullPointerException.class)
    public void testPutWithNullCredentials() {
        new SecureStorageDataStore(urlNode).put(BAR_USERNAME, null);
    }

    @Test
    public void testPut() throws StorageException {
        final DataStore<String, Credentials> dataStore = new SecureStorageDataStore(urlNode);
        final Credentials credentials = codenvyClient.newCredentialsBuilder().withPassword(FOO_PASSWORD)
                                                                 .withToken(codenvyClient.newTokenBuilder(FOO_TOKEN).build())
                                                                 .build();

        final Credentials storedCredentials = dataStore.put(FOO_USERNAME, credentials);

        Assert.assertNull(storedCredentials);
        Assert.assertTrue(urlNode.nodeExists(FOO_USERNAME));
        Assert.assertEquals(FOO_PASSWORD, urlNode.node(FOO_USERNAME).get(CODENVY_PASSWORD_KEY_NAME, (String)null));
        Assert.assertEquals(FOO_TOKEN, urlNode.node(FOO_USERNAME).get(CODENVY_TOKEN_KEY_NAME, (String)null));
    }

    @Test
    public void testPutWithStoreOnlyToken() throws StorageException {
        final DataStore<String, Credentials> dataStore = new SecureStorageDataStore(urlNode);
        final Credentials credentials = codenvyClient.newCredentialsBuilder().withPassword(FOO_PASSWORD)
                                                                 .withToken(codenvyClient.newTokenBuilder(FOO_TOKEN).build())
                                                                 .storeOnlyToken(true)
                                                                 .build();

        final Credentials storedCredentials = dataStore.put(FOO_USERNAME, credentials);

        Assert.assertNull(storedCredentials);
        Assert.assertTrue(urlNode.nodeExists(FOO_USERNAME));
        Assert.assertNull(urlNode.node(FOO_USERNAME).get(CODENVY_PASSWORD_KEY_NAME, (String)null));
        Assert.assertEquals(FOO_TOKEN, urlNode.node(FOO_USERNAME).get(CODENVY_TOKEN_KEY_NAME, (String)null));

    }

    @Test
    public void testPutOnExitingNode() throws StorageException {
        final DataStore<String, Credentials> dataStore = new SecureStorageDataStore(urlNode);
        final Credentials fooCredentials = codenvyClient.newCredentialsBuilder().withUsername(FOO_USERNAME)
                                                                    .withPassword(FOO_PASSWORD)
                                                                    .withToken(codenvyClient.newTokenBuilder(FOO_TOKEN).build())
                                                                    .build();

        final Credentials barCredentials = codenvyClient.newCredentialsBuilder().withUsername(BAR_USERNAME)
                                                                    .withPassword(BAR_PASSWORD)
                                                                    .withToken(codenvyClient.newTokenBuilder(BAR_TOKEN).build())
                                                                    .build();

        final Credentials storedCredentials = dataStore.put(BAR_USERNAME, fooCredentials);

        Assert.assertNotNull(storedCredentials);
        Assert.assertEquals(barCredentials, storedCredentials);
        Assert.assertEquals(FOO_PASSWORD, urlNode.node(BAR_USERNAME).get(CODENVY_PASSWORD_KEY_NAME, (String)null));
        Assert.assertEquals(FOO_TOKEN, urlNode.node(BAR_USERNAME).get(CODENVY_TOKEN_KEY_NAME, (String)null));
    }

    @Test(expected = NullPointerException.class)
    public void testDeleteWithNullUsername() {
        new SecureStorageDataStore(urlNode).delete(null);
    }

    @Test
    public void testDeleteWithMissingUsername() {
        final DataStore<String, Credentials> dataStore = new SecureStorageDataStore(urlNode);
        final Credentials storedCredentials = dataStore.delete(FOO_USERNAME);

        Assert.assertNull(storedCredentials);
    }

    @Test
    public void testDelete() {
        final DataStore<String, Credentials> dataStore = new SecureStorageDataStore(urlNode);
        final Credentials storedCredentials = dataStore.delete(BAR_USERNAME);
        final Credentials credentials = codenvyClient.newCredentialsBuilder().withUsername(BAR_USERNAME)
                                                                 .withPassword(BAR_PASSWORD)
                                                                 .withToken(codenvyClient.newTokenBuilder(BAR_TOKEN).build())
                                                                 .build();

        Assert.assertNotNull(storedCredentials);
        Assert.assertFalse(urlNode.nodeExists(BAR_USERNAME));
        Assert.assertEquals(credentials, storedCredentials);
    }
}
