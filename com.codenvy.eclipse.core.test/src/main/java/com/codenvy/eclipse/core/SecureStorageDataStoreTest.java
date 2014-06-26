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

import static com.codenvy.eclipse.core.SecureStorageDataStore.CODENVY_PASSWORD_KEY_NAME;
import static com.codenvy.eclipse.core.SecureStorageDataStore.CODENVY_TOKEN_KEY_NAME;
import static com.codenvy.eclipse.core.SecureStorageDataStoreFactory.CODENVY_NODE_NAME;
import static org.eclipse.equinox.security.storage.EncodingUtils.encodeSlashes;

import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.codenvy.eclipse.client.auth.Credentials;
import com.codenvy.eclipse.client.auth.Token;
import com.codenvy.eclipse.client.store.DataStore;

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

    public SecureStorageDataStoreTest() {
        final ISecurePreferences root = SecurePreferencesFactory.getDefault();
        this.codenvyNode = root.node(CODENVY_NODE_NAME);
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
        Assert.assertEquals(BAR_PASSWORD, storedCredentials.password);
        Assert.assertEquals(new Token(BAR_TOKEN), storedCredentials.token);
    }

    @Test(expected = NullPointerException.class)
    public void testPutWithNullUsername() {
        final Credentials credentials = new Credentials.Builder().withPassword(BAR_PASSWORD)
                                                                 .withToken(new Token(BAR_TOKEN))
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
        final Credentials credentials = new Credentials.Builder().withPassword(FOO_PASSWORD)
                                                                 .withToken(new Token(FOO_TOKEN))
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
        final Credentials credentials = new Credentials.Builder().withPassword(FOO_PASSWORD)
                                                                 .withToken(new Token(FOO_TOKEN))
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
        final Credentials fooCredentials = new Credentials.Builder().withPassword(FOO_PASSWORD)
                                                                    .withToken(new Token(FOO_TOKEN))
                                                                    .build();

        final Credentials barCredentials = new Credentials.Builder().withPassword(BAR_PASSWORD)
                                                                    .withToken(new Token(BAR_TOKEN))
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
        final Credentials credentials = new Credentials.Builder().withPassword(BAR_PASSWORD)
                                                                 .withToken(new Token(BAR_TOKEN))
                                                                 .build();

        Assert.assertNotNull(storedCredentials);
        Assert.assertFalse(urlNode.nodeExists(BAR_USERNAME));
        Assert.assertEquals(credentials, storedCredentials);
    }
}
