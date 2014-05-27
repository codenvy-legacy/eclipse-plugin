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
