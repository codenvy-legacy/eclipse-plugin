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

/**
 * {@link InMemoryDataStoreFactory} tests.
 * 
 * @author Kevin Pollet
 */
public class InMemoryDataStoreFactoryTest {
    @Test(expected = NullPointerException.class)
    public void testGetDataStoreWithNullId() {
        new InMemoryDataStoreFactory().getDataStore(null);
    }

    @Test
    public void testGetDataStoreId() {
        final DataStoreFactory<String, StoredCredentials> dataStoreFactory = new InMemoryDataStoreFactory();

        Assert.assertNotNull(dataStoreFactory.getDataStore("dummy"));
        Assert.assertEquals(dataStoreFactory.getDataStore("equals"), dataStoreFactory.getDataStore("equals"));
        Assert.assertNotEquals(dataStoreFactory.getDataStore("equals"), dataStoreFactory.getDataStore("equals2"));
    }
}
