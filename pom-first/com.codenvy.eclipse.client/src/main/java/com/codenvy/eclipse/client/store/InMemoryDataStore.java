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

import com.codenvy.eclipse.client.model.Credentials;

/**
 * {@link DataStore} implementation which stores user credentials in memory.
 * 
 * @author Kevin Pollet
 */
public class InMemoryDataStore implements DataStore<String, Credentials> {
    private final ConcurrentMap<String, Credentials> data;

    InMemoryDataStore() {
        this.data = new ConcurrentHashMap<>();
    }

    @Override
    public Credentials get(String key) {
        checkNotNull(key);

        return data.get(key);
    }

    @Override
    public Credentials put(String key, Credentials value) {
        checkNotNull(key);
        checkNotNull(value);

        return data.putIfAbsent(key, value);
    }

    @Override
    public Credentials delete(String key) {
        checkNotNull(key);

        return data.remove(key);
    }
}
