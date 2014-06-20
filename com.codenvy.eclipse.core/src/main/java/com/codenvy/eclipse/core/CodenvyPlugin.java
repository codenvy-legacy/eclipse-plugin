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

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

import com.codenvy.eclipse.client.Codenvy;

/**
 * The activator class controls the plug-in life cycle.
 * 
 * @author Kevin Pollet
 */
public class CodenvyPlugin extends Plugin {
    // the plug-in ID
    public static final String   PLUGIN_ID      = "com.codenvy.eclipse.core"; //$NON-NLS-1$

    /**
     * Constant identifying the job family identifier for Codenvy jobs.
     */
    public static final Object   FAMILY_CODENVY = new Object();

    // the shared instance
    private static CodenvyPlugin plugin;

    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
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
     * Returns a Codenvy builder for the given url and username.
     * 
     * @param url the Codenvy platform url.
     * @param username the username.
     * @return an instance of the Codenvy API builder.
     */
    public Codenvy.Builder getCodenvyBuilder(String url, String username) {
        return new Codenvy.Builder(url, username, SecureStorageDataStoreFactory.INSTANCE);
    }
}
