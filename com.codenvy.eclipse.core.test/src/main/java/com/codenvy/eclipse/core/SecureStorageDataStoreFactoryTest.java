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
