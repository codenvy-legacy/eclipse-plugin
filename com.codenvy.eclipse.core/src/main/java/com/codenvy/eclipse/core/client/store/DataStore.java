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
package com.codenvy.eclipse.core.client.store;

/**
 * Store contract used to store user credentials.
 * 
 * @author Kevin Pollet
 * @param <K> the key {@linkplain java.lang.reflect.Type Type}.
 * @param <V> the value {@linkplain java.lang.reflect.Type Type}.
 */
public interface DataStore<K, V> {
    /**
     * Returns the value associated to the given key.
     * 
     * @param key the key.
     * @return the associated value or {@code null} if none.
     * @throws NullPointerException if implementation doesn't support {@code null} keys.
     */
    V get(K key);

    /**
     * Stores the given value with the given key.
     * 
     * @param key the key.
     * @param value the value.
     * @return the previous stored value or {@code null} if none.
     * @throws NullPointerException if implementation doesn't support {@code null} keys or values.
     */
    V put(K key, V value);

    /**
     * Deletes the value associated to the given key.
     * 
     * @param key the key.
     * @return the deleted value or {@code null} if none.
     * @throws NullPointerException if implementation doesn't support {@code null} keys.
     */
    V delete(K key);
}
