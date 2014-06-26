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

import static com.codenvy.eclipse.core.SecureStorageDataStoreFactory.CODENVY_NODE_NAME;
import static org.eclipse.equinox.security.storage.EncodingUtils.encodeSlashes;

import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.junit.Assert;
import org.junit.Test;

import com.codenvy.eclipse.client.auth.Credentials;
import com.codenvy.eclipse.client.store.DataStore;
import com.codenvy.eclipse.client.store.DataStoreFactory;

/**
 * {@link SecureStorageDataStoreFactory} tests.
 * 
 * @author Kevin Pollet
 */
public class SecureStorageDataStoreFactoryTest {
    private static final String                         FOO_DATA_STORE_ID = "http://foo.com";

    private final DataStoreFactory<String, Credentials> dataStoreFactory;
    private final ISecurePreferences                    root;

    public SecureStorageDataStoreFactoryTest() {
        dataStoreFactory = SecureStorageDataStoreFactory.INSTANCE;
        root = SecurePreferencesFactory.getDefault();
    }

    @Test(expected = NullPointerException.class)
    public void testGetDataStoreWithNullId() {
        dataStoreFactory.getDataStore(null);
    }

    @Test
    public void testGetDataStore() {
        final DataStore<String, Credentials> dataStore = dataStoreFactory.getDataStore(FOO_DATA_STORE_ID);

        Assert.assertNotNull(dataStore);
        Assert.assertTrue(root.nodeExists(CODENVY_NODE_NAME));
        Assert.assertTrue(root.node(CODENVY_NODE_NAME).nodeExists(encodeSlashes(FOO_DATA_STORE_ID)));
    }
}
