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
package com.codenvy.eclipse.core.client.store.secure;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.eclipse.equinox.security.storage.EncodingUtils.encodeSlashes;

import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;

import com.codenvy.eclipse.core.client.store.DataStore;
import com.codenvy.eclipse.core.client.store.DataStoreFactory;
import com.codenvy.eclipse.core.client.store.StoredCredentials;

/**
 * The Eclipse secure storage data store factory.
 * 
 * @author Kevin Pollet
 */
public enum SecureStorageDataStoreFactory implements DataStoreFactory<String, StoredCredentials> {
    INSTANCE;

    public static final String       CODENVY_PREFERENCE_STORAGE_NODE_NAME = "Codenvy";
    private final ISecurePreferences codenvyNode;

    private SecureStorageDataStoreFactory() {
        final ISecurePreferences root = SecurePreferencesFactory.getDefault();
        this.codenvyNode = root.node(CODENVY_PREFERENCE_STORAGE_NODE_NAME);
    }

    @Override
    public DataStore<String, StoredCredentials> getDataStore(String id) {
        checkNotNull(id);
        return new SecureStorageDataStore(codenvyNode.node(encodeSlashes(id)));
    }
}
