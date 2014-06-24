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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.codenvy.eclipse.client.auth.Credentials;

/**
 * {@link DataStoreFactory} implementation providing {@link DataStore} which stores user credentials in memory.
 * 
 * @author Kevin Pollet
 */
public class InMemoryDataStoreFactory implements DataStoreFactory<String, Credentials> {
    private final ConcurrentMap<String, DataStore<String, Credentials>> dataStores;

    public InMemoryDataStoreFactory() {
        this.dataStores = new ConcurrentHashMap<>();
    }

    @Override
    public DataStore<String, Credentials> getDataStore(String id) {
        checkNotNull(id);

        DataStore<String, Credentials> store = dataStores.get(id);
        if (store == null) {
            final DataStore<String, Credentials> dataStore = new InMemoryDataStore();
            store = dataStores.putIfAbsent(id, dataStore);
            if (store == null) {
                store = dataStore;
            }
        }
        return store;
    }
}
