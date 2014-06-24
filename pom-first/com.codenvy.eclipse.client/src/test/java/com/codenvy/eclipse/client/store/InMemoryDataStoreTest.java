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
import org.junit.Test;

import com.codenvy.eclipse.client.model.Credentials;
import com.codenvy.eclipse.client.model.Token;

/**
 * {@link InMemoryDataStore} tests.
 * 
 * @author Kevin Pollet
 */
public class InMemoryDataStoreTest {
    private final DataStoreFactory<String, Credentials> dataStoreFactory;
    private final DataStore<String, Credentials>        dataStore;

    public InMemoryDataStoreTest() {
        dataStoreFactory = new InMemoryDataStoreFactory();
        dataStore = dataStoreFactory.getDataStore("dummy");
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
        dataStore.put("testGetWithExistingKey", new Credentials("dummy", new Token("dummy")));

        Assert.assertNotNull(dataStore.get("testGetWithExistingKey"));
        Assert.assertEquals(new Credentials("dummy", new Token("dummy")), dataStore.get("testGetWithExistingKey"));
    }

    @Test(expected = NullPointerException.class)
    public void testPutWithNullKey() {
        dataStore.put(null, new Credentials("dummy", new Token("dummy")));
    }

    @Test(expected = NullPointerException.class)
    public void testPutWithNullCredentials() {
        dataStore.put("dummy", null);
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
        dataStore.put("testDeleteWithExistingKey", new Credentials("dummy", new Token("dummy")));
        final Credentials credentials = dataStore.delete("testDeleteWithExistingKey");

        Assert.assertNotNull(credentials);
        Assert.assertEquals(new Credentials("dummy", new Token("dummy")), credentials);
        Assert.assertNull(dataStore.delete("testDeleteWithExistingKey"));
    }
}
