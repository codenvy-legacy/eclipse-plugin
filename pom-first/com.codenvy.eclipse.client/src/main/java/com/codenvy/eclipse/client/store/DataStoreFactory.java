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

/**
 * Data store factory contract used to store user credentials.
 * 
 * @author Kevin Pollet
 * @param <K> the key {@linkplain java.lang.reflect.Type Type}.
 * @param <V> the value {@linkplain java.lang.reflect.Type Type}.
 */
public interface DataStoreFactory<K, V> {
    /**
     * Returns an instance of the data store associated to the given id.
     * 
     * @param id the data store id.
     * @return the associated data store.
     * @throws NullPointerException if id parameter is {@code null}.
     */
    DataStore<K, V> getDataStore(String id);
}
