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
package com.codenvy.eclipse.ui.launcher;

import static com.codenvy.eclipse.core.launcher.LaunchConstants.BUILDER_LAUNCH_CONFIGURATION_NAME_PREFIX;
import static com.codenvy.eclipse.core.launcher.LaunchConstants.BUILDER_LAUNCH_DELEGATE_ID;
import static com.codenvy.eclipse.core.launcher.LaunchConstants.CODENVY_PROJECT_NAME_ATTRIBUTE_NAME;
import static com.codenvy.eclipse.core.launcher.LaunchConstants.LAUNCH_CONFIGURATION_TYPE_ID;
import static java.util.Collections.singleton;
import static org.eclipse.core.resources.IResource.ROOT;
import static org.eclipse.debug.core.ILaunchManager.RUN_MODE;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.IConsoleConstants;

/**
 * The codenvy build shortcut implementation.
 * 
 * @author Kevin Pollet
 */
public class BuildOnCodenvyShortcut implements ILaunchShortcut {
    @Override
    public void launch(ISelection selection, String mode) {
        if (selection instanceof IStructuredSelection && RUN_MODE.equalsIgnoreCase(mode)) {
            final IStructuredSelection structuredSelection = (IStructuredSelection)selection;
            if (!structuredSelection.isEmpty()) {
                final IAdaptable adaptable = (IAdaptable)structuredSelection.getFirstElement();
                final IResource resource = (IResource)adaptable.getAdapter(IResource.class);

                if (resource != null && resource.getType() != ROOT) {
                    buildProjectOnCodenvy(resource.getProject(), mode);
                }
            }
        }
    }

    @Override
    public void launch(IEditorPart editor, String mode) {
        if (RUN_MODE.equalsIgnoreCase(mode)) {
            final IEditorInput editorInput = editor.getEditorInput();
            final IResource resource = (IResource)editorInput.getAdapter(IResource.class);

            if (resource != null && resource.getType() != ROOT) {
                buildProjectOnCodenvy(resource.getProject(), mode);
            }
        }
    }

    private void buildProjectOnCodenvy(IProject project, final String mode) {
        final ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
        final ILaunchConfigurationType launchConfigurationType = launchManager.getLaunchConfigurationType(LAUNCH_CONFIGURATION_TYPE_ID);
        if (launchConfigurationType != null) {
            try {

                final ILaunchConfiguration launchConfiguration =
                                                                 getLaunchConfiguration(launchManager, launchConfigurationType, project,
                                                                                        mode);
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(IConsoleConstants.ID_CONSOLE_VIEW);
                DebugUITools.launch(launchConfiguration, mode);

            } catch (PartInitException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private ILaunchConfiguration getLaunchConfiguration(ILaunchManager launchManager,
                                                        ILaunchConfigurationType launchConfigurationType,
                                                        IProject project,
                                                        String mode) {

        ILaunchConfiguration launchConfiguration = null;
        final String launchConfigurationName = BUILDER_LAUNCH_CONFIGURATION_NAME_PREFIX + project.getName();

        try {

            final ILaunchConfiguration[] launchConfigurations = launchManager.getLaunchConfigurations(launchConfigurationType);
            for (ILaunchConfiguration oneLaunchConfiguration : launchConfigurations) {
                if (oneLaunchConfiguration.supportsMode(mode) && oneLaunchConfiguration.getName().equals(launchConfigurationName)) {
                    launchConfiguration = oneLaunchConfiguration;
                    break;
                }
            }

            if (launchConfiguration == null) {
                final ILaunchConfigurationWorkingCopy workingCopy = launchConfigurationType.newInstance(null, launchConfigurationName);
                workingCopy.setPreferredLaunchDelegate(singleton(RUN_MODE), BUILDER_LAUNCH_DELEGATE_ID);
                workingCopy.setAttribute(CODENVY_PROJECT_NAME_ATTRIBUTE_NAME, project.getName());

                launchConfiguration = workingCopy.doSave();
            }

            return launchConfiguration;

        } catch (CoreException e) {
            throw new RuntimeException(e);
        }
    }
}
