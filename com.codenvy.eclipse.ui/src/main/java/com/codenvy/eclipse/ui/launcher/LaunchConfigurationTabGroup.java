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
package com.codenvy.eclipse.ui.launcher;

import static org.eclipse.debug.core.ILaunchManager.RUN_MODE;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

/**
 * The codenvy launch configuration tab group.
 * 
 * @author Kevin Pollet
 */
public class LaunchConfigurationTabGroup extends AbstractLaunchConfigurationTabGroup {
    @Override
    public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
        final List<ILaunchConfigurationTab> configurationTabs = new ArrayList<>();

        if (RUN_MODE.equals(mode)) {
            configurationTabs.add(new RunMainConfigurationTab());
        }

        setTabs(configurationTabs.toArray(new ILaunchConfigurationTab[0]));
    }
}
