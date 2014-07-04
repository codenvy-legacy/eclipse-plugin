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
