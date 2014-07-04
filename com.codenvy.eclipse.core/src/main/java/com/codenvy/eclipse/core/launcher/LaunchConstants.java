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
package com.codenvy.eclipse.core.launcher;

/**
 * The launch constants.
 * 
 * @author Kevin Pollet
 */
public final class LaunchConstants {
    public static final String CODENVY_PROJECT_NAME_ATTRIBUTE_NAME      = "codenvy.project.name";

    public static final String LAUNCH_CONFIGURATION_TYPE_ID             = "com.codenvy.eclipse.core.launcher.launchConfigurationType";

    public static final String RUNNER_LAUNCH_DELEGATE_ID                = "com.codenvy.eclipse.core.launcher.runLaunchDelegate";
    public static final String RUNNER_LAUNCH_CONFIGURATION_NAME_PREFIX  = "Run_";

    public static final String BUILDER_LAUNCH_DELEGATE_ID               = "com.codenvy.eclipse.core.launcher.buildLaunchDelegate";
    public static final String BUILDER_LAUNCH_CONFIGURATION_NAME_PREFIX = "Build_";

    /**
     * Disable instantiation.
     */
    private LaunchConstants() {
    }
}
