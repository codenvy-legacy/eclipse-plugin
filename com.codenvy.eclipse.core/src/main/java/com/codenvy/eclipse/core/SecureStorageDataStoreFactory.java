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

import static com.google.common.base.Preconditions.checkNotNull;
import static org.eclipse.equinox.security.storage.EncodingUtils.encodeSlashes;

import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;

import com.codenvy.eclipse.client.auth.Credentials;
import com.codenvy.eclipse.client.store.DataStore;
import com.codenvy.eclipse.client.store.DataStoreFactory;

/**
 * The Eclipse secure storage data store factory.
 * 
 * @author Kevin Pollet
 */
public enum SecureStorageDataStoreFactory implements DataStoreFactory<String, Credentials> {
    INSTANCE;

    public static final String CODENVY_NODE_NAME = "Codenvy";

    @Override
    public DataStore<String, Credentials> getDataStore(String id) {
        checkNotNull(id);

        final ISecurePreferences root = SecurePreferencesFactory.getDefault();
        final ISecurePreferences codenvyNode = root.node(CODENVY_NODE_NAME);

        return new SecureStorageDataStore(codenvyNode.node(encodeSlashes(id)));
    }
}
