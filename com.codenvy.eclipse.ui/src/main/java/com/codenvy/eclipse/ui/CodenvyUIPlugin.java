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
package com.codenvy.eclipse.ui;

import static com.codenvy.eclipse.ui.Images.RUN_MAIN_TAB_ICON;
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
public final class CodenvyUIPlugin extends AbstractUIPlugin {
    public static final String     PLUGIN_ID = "com.codenvy.eclipse.ui"; //$NON-NLS-1$

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
        registry.put(RUN_MAIN_TAB_ICON, ImageDescriptor.createFromFile(CodenvyUIPlugin.class, "/images/RunMainTab.gif"));
    }
}
