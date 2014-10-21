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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

import com.codenvy.client.CodenvyAPI;
import com.codenvy.client.CodenvyBuilder;
import com.codenvy.client.auth.CredentialsProvider;
import com.codenvy.eclipse.core.store.SecureStorageDataStoreFactory;

/**
 * The activator class controls the plug-in life cycle.
 * 
 * @author Kevin Pollet
 */
public final class CodenvyPlugin extends Plugin {
    public static final String   PLUGIN_ID                         = "com.codenvy.eclipse.core";        //$NON-NLS-1$
    public static final String   CREDENTIALS_PROVIDER_EXTENSION_ID = PLUGIN_ID + ".credentialsProvider"; //$NON-NLS-1$

    /**
     * Constant identifying the job family identifier for Codenvy jobs.
     */
    public static final Object   FAMILY_CODENVY                    = new Object();

    private static CodenvyPlugin plugin;

    private CredentialsProvider  credentialsProvider;

    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;

        final IExtensionRegistry registry = Platform.getExtensionRegistry();
        final IConfigurationElement[] configurationElements = registry.getConfigurationElementsFor(CREDENTIALS_PROVIDER_EXTENSION_ID);

        for (IConfigurationElement oneConfigurationElement : configurationElements) {
            try {

                credentialsProvider = (CredentialsProvider)oneConfigurationElement.createExecutableExtension("class");

            } catch (CoreException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance.
     * 
     * @return the shared instance
     */
    public static CodenvyPlugin getDefault() {
        return plugin;
    }

    /**
     * Returns a Codenvy builder for the given URL and username.
     * 
     * @param url the Codenvy platform URL.
     * @param username the username.
     * @return an instance of the {@link CodenvyBuilder}.
     */
    public CodenvyBuilder getCodenvyBuilder(String url, String username) {
        return CodenvyAPI.getClient()
                         .newCodenvyBuilder(url, username)
                         .withCredentialsStoreFactory(SecureStorageDataStoreFactory.INSTANCE)
                         .withCredentialsProvider(credentialsProvider);
    }
}
