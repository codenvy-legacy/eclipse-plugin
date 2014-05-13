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
package com.codenvy.eclipse.ui;

import static com.codenvy.eclipse.ui.Images.WIZARD_LOGO;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 * 
 * @author Kevin Pollet
 */
public class CodenvyUIPlugin extends AbstractUIPlugin {

    // the plug-in ID
    public static final String     PLUGIN_ID = "com.codenvy.eclipse.ui"; //$NON-NLS-1$

    // the shared instance
    private static CodenvyUIPlugin plugin;

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
    public static CodenvyUIPlugin getDefault() {
        return plugin;
    }

    @Override
    protected void initializeImageRegistry(ImageRegistry registry) {
        registry.put(WIZARD_LOGO, ImageDescriptor.createFromFile(CodenvyUIPlugin.class, "/images/WizardLogo.png"));
    }
}
