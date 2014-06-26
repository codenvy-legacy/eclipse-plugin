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
package com.codenvy.eclipse.client.store;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.codenvy.eclipse.client.auth.Credentials;
import com.codenvy.eclipse.client.auth.Token;

/**
 * {@link InMemoryDataStore} tests.
 * 
 * @author Kevin Pollet
 */
public class InMemoryDataStoreTest {
    private static final String                   DUMMY_DATASTORE_KEY = "dummyKey";
    private static final String                   DUMMY_USERNAME      = "dummyUsername";
    private static final String                   DUMMY_PASSWORD      = "dummyPassword";
    private static final String                   DUMMY_TOKEN         = "dummyToken";

    private DataStoreFactory<String, Credentials> dataStoreFactory;
    private DataStore<String, Credentials>        dataStore;
    private Credentials                           credentials;

    @Before
    public void before() {
        this.dataStoreFactory = new InMemoryDataStoreFactory();
        this.dataStore = dataStoreFactory.getDataStore(DUMMY_DATASTORE_KEY);
        this.credentials = new Credentials.Builder().withPassword(DUMMY_PASSWORD)
                                                    .withToken(new Token(DUMMY_TOKEN))
                                                    .build();
    }

    @Test(expected = NullPointerException.class)
    public void testGetWithNullKey() {
        dataStore.get(null);
    }

    @Test
    public void testGetWithMissingKey() {
        Assert.assertNull(dataStore.get("missing"));
    }

    @Test
    public void testGetWithExistingKey() {
        dataStore.put("testGetWithExistingKey", credentials);

        Assert.assertNotNull(dataStore.get("testGetWithExistingKey"));
        Assert.assertEquals(credentials, dataStore.get("testGetWithExistingKey"));
    }

    @Test(expected = NullPointerException.class)
    public void testPutWithNullKey() {
        dataStore.put(null, credentials);
    }

    @Test(expected = NullPointerException.class)
    public void testPutWithNullCredentials() {
        dataStore.put(DUMMY_USERNAME, null);
    }

    @Test
    public void testPutWithStoreOnlyTokenCredentials() {
        final Credentials credentials = new Credentials.Builder().withPassword(DUMMY_PASSWORD)
                                                                 .withToken(new Token(DUMMY_TOKEN))
                                                                 .storeOnlyToken(true)
                                                                 .build();

        dataStore.put(DUMMY_USERNAME, credentials);

        Assert.assertNotNull(dataStore.get(DUMMY_USERNAME));
        Assert.assertEquals(new Credentials.Builder().withToken(new Token(DUMMY_TOKEN)).build(), dataStore.get(DUMMY_USERNAME));
    }

    @Test(expected = NullPointerException.class)
    public void testDeleteWithNullKey() {
        dataStore.delete(null);
    }

    @Test
    public void testDeleteWithMissingKey() {
        Assert.assertNull(dataStore.delete("missing"));
    }

    @Test
    public void testDeleteWithExistingKey() {
        dataStore.put("testDeleteWithExistingKey", credentials);
        final Credentials deletedCredentials = dataStore.delete("testDeleteWithExistingKey");

        Assert.assertNotNull(credentials);
        Assert.assertEquals(credentials, deletedCredentials);
        Assert.assertNull(dataStore.delete("testDeleteWithExistingKey"));
    }
}
